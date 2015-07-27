package com.calamp.messaging.spring.integration.poc;

import java.io.IOException;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

public class Startup {
    static Logger log = Logger.getLogger(Startup.class.getName());
    
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, TimeoutException, InvalidAlgorithmParameterException {
	//ClassPathXmlApplicationContext context = contextFromXmlConfig();
	ApplicationContext context = contextFromJavaConfig();

	MessageChannel channel;
	channel = context.getBean(CalAmpSIConfig.sourceChannelName, MessageChannel.class);

	runTest( channel );
	( ( ConfigurableApplicationContext) context ).close();
    }
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
    private static Message<CalAmpSIWrapper> toStages(byte[] calAmpDataBytes, List<String> initialPathPlan) {
	UUID siId = UUID.randomUUID();

	CalAmpSIWrapper payload = new CalAmpSIWrapper(siId, calAmpDataBytes, initialPathPlan);

	Message<CalAmpSIWrapper> m1;
	m1 = MessageBuilder.withPayload(payload)
	    .setHeader(CalAmpSIConfig.nextHopHeaderName, initialPathPlan.get(0))
	    .build();

	log.info("Inject: " + payload);
	return m1;
    }
    private static void runTest( MessageChannel channel) throws InvalidAlgorithmParameterException {
	boolean doStop = false;
	int dataByteCount = 4;
	int testCount = 10;
	int seq = 1;
	
	/*
	 * Setup the test environment.
	 */
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
	    Message<CalAmpSIWrapper> m1 = toStages(calAmpDataBytes, initialPathPlan);
	    channel.send(m1);
	    seq++;

	    if (seq > testCount) {
		doStop = true;
	    }
	}
    }
}