package com.calamp.messaging.spring.integration.poc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

public class Startup {
    static Logger log = Logger.getLogger(Startup.class.getName());

    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException, InvalidAlgorithmParameterException {
	//ClassPathXmlApplicationContext context = contextFromXmlConfig();
	ApplicationContext context = contextFromJavaConfig();

	timingTest(context);
	( ( ConfigurableApplicationContext) context ).close();
    }
    @SuppressWarnings("unused")
    private static ClassPathXmlApplicationContext contextFromXmlConfig() {
	ClassPathXmlApplicationContext context;
	context= new ClassPathXmlApplicationContext("/META-INF/spring/si-components.xml");
	return context;
    }
    private static ApplicationContext contextFromJavaConfig() {
	ApplicationContext context; 
	context = new AnnotationConfigApplicationContext(CalAmpSIConfig.class);
	return context;
    }
    private static CalAmpSIWrapper wrapData(byte[] calAmpDataBytes, List<String> initialPathPlan) {
	UUID siId = UUID.randomUUID();
	CalAmpSIWrapper payload = new CalAmpSIWrapper(siId, calAmpDataBytes, initialPathPlan);
	return payload;
    }
    private static String getDefaultBeanName(@SuppressWarnings("rawtypes") Class aClass) {
	String beanName = CalAmpSIRouteAndProcessService.class.getSimpleName();
	beanName = Character.toLowerCase(beanName.charAt(0)) + (beanName.length() > 1 ? beanName.substring(1) : "");
	return beanName;
    }
    @SuppressWarnings("unused")
    private static void runTest(ApplicationContext context) throws InterruptedException, ExecutionException {
	boolean doStop    = false;
	int dataByteCount = 4;
	int testCount	  = 10;
	int seq		  = 1;

	String serviceName = getDefaultBeanName( CalAmpSIRouteAndProcessService.class );
	CalAmpSIRouteAndProcessService service = context.getBean(serviceName, CalAmpSIRouteAndProcessService.class);

	List<String> stageLabels = Arrays.asList("A","B","C","D","E");
	String finalStageTag = "F";
	for (String label : stageLabels){
	    Transforms.registerStage( new DummyStage(label, false) );
	}
	Transforms.registerStage( new DummyStage(finalStageTag, true) );
	List<String> initialPathPlan =  Arrays.asList("C","A","B","D","E","F");

	/*
	 * Run the test.
	 */
	while (!doStop) {
	    String simCalAmpDataString = RandomStringUtils.randomAlphanumeric(dataByteCount);
	    byte[] calAmpDataBytes = simCalAmpDataString.getBytes(Charset.forName("UTF-8"));
	    CalAmpSIWrapper payload = wrapData( calAmpDataBytes, initialPathPlan );
	    System.out.println( "Service Request: " + payload );
	    Future<CalAmpSIWrapper> asynchReply = service.processMessage(payload);
	    //The next line synchronizes. If no reply is necessary simply do not execute .get(). 
	    //If synchronization should happen in batches then collect the futures and synchronize as desired.
	    System.out.println( "Service Reply: "  + asynchReply.get() );
	    seq++;

	    if (seq > testCount) {
		doStop = true;
	    }
	}
    }
    private static void timingTest(ApplicationContext context) throws InterruptedException, ExecutionException {
	boolean doStop    = false;
	int dataByteCount = 2048;
	int testCount	  = 50000;
	int seq		  = 1;
	double mu 	  = 1000.0;
	double sigma      = 167;
	Random r = new Random();
	String serviceName = getDefaultBeanName( CalAmpSIRouteAndProcessService.class );
	CalAmpSIRouteAndProcessService service = context.getBean(serviceName, CalAmpSIRouteAndProcessService.class);

	List<String> stageLabels = Arrays.asList("A","B","C","D","E","F","G","H","I");
	for (String label : stageLabels){
	    Transforms.registerStage( new GaussianSleeper(label, false, mu, sigma) );
	}
	Transforms.registerStage( new GaussianSleeper(stageLabels.get(stageLabels.size()-1), true, mu, sigma) );
	
	/*
	 * Run the test.
	 */
	List<Future<CalAmpSIWrapper>> results = new ArrayList<Future<CalAmpSIWrapper>> ();
	long start = System.currentTimeMillis(); 
	while (!doStop) {
	    ArrayList<String> currentLabels  =  new ArrayList<String>();
	    Integer stageCount = r.nextInt( stageLabels.size() - 1 );
	    for (int i=0; i<stageCount; i++){
		currentLabels.add( stageLabels.get(i) );
	    }
	    currentLabels.add(stageLabels.get(stageLabels.size()-1));
		
	    //Some random data.
	    String simCalAmpDataString = RandomStringUtils.randomAlphanumeric(dataByteCount);
	    simCalAmpDataString += "#" + System.currentTimeMillis() + ";";
	    byte[] calAmpDataBytes = simCalAmpDataString.getBytes(Charset.forName("UTF-8"));
	    CalAmpSIWrapper payload = wrapData( calAmpDataBytes, currentLabels );
	    //System.out.println( "Service Request: " + payload );
	    Future<CalAmpSIWrapper> asynchReply = service.processMessage(payload);
	    //The next line synchronizes. If no reply is necessary simply do not execute .get(). 
	    //If synchronization should happen in batches then collect the futures and synchronize as desired.
	    //System.out.println( "Service Reply: "  + asynchReply.get() );
	    results.add(asynchReply);
	    seq++;

	    if (seq > testCount) {
		doStop = true;
	    }
	}
	double avgStageTime = 0;
	double avgFlowTime = 0;
	List<CalAmpSIWrapper> finished = new ArrayList<CalAmpSIWrapper>();
	
	for(Future<CalAmpSIWrapper> x : results){
	    finished.add(x.get());
	}
	long end = System.currentTimeMillis();
	System.out.println("TOTAL-TIME["+ (end-start) +"]");
	for(CalAmpSIWrapper y : finished){
	    String rez = new String(y.getDataBytes());
	    int metaStart = rez.lastIndexOf("#")+1;
	    String meta = rez.substring(metaStart);
	    List<Long> longList = new ArrayList<Long>();
	    StringTokenizer tok = new StringTokenizer(meta, ";");
	    Long prev = Long.valueOf( tok.nextToken() );
	    Long first = prev;
	    Long last = prev;
	    //System.out.println(meta);
	    double acc = 0.0;
	    while(tok.hasMoreTokens()){
		Long t = Long.valueOf(tok.nextToken());
		longList.add(t-prev);
		//System.out.println(t-prev);
		acc += (t-prev);
		prev = t;
		last = t;
	    }
	    avgStageTime += acc / longList.size();
	    avgFlowTime  += last - first;
	}
	
	avgStageTime /= results.size();
	avgFlowTime /= results.size();
	System.out.println("AVG-STAGE["+ avgStageTime+"]");
	System.out.println("AVG-FLOW["+ avgFlowTime+"]");
    }
}