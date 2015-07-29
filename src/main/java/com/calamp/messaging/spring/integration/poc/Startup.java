package com.calamp.messaging.spring.integration.poc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.List;
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
	
	runTest(context);
	( ( ConfigurableApplicationContext) context ).close();
    }
    @SuppressWarnings("unused")
    private static ClassPathXmlApplicationContext contextFromXmlConfig() {
	ClassPathXmlApplicationContext context;
	context= new ClassPathXmlApplicationContext("/META-INF/spring/si-components.xml");
	return context;
    }
    @SuppressWarnings("unused")
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
    private static void runTest(ApplicationContext context) throws InvalidAlgorithmParameterException, InterruptedException, ExecutionException, TimeoutException {
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
	    System.out.println( "Service Request: " + payload);
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
}