package com.calamp.messaging.spring.integration.poc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

public class Startup {
    static Logger log = Logger.getLogger(Startup.class.getName());
    
    public static void main(String[] args) throws IOException {
	ClassPathXmlApplicationContext context;
	context= new ClassPathXmlApplicationContext("/META-INF/spring/si-components.xml");
	String propertyPath = "si-ident.properties";

	InputStream propStream = new FileInputStream(propertyPath);
	Properties props = new Properties();
	props.load(propStream);

	String sourceChannelName = props.getProperty("source_channel_name");

	MessageChannel channel;
	channel = context.getBean(sourceChannelName, MessageChannel.class);

	runTest( channel );
	context.close();
    }

    private static void runTest( MessageChannel channel) {
	boolean doStop = false;
	int dataByteCount = 2048;
	int testCount = 100;
	int seq = 0;
	
	/*
	 * Setup the test environment.
	 */
	List<String> stageLabels = Arrays.asList("A","B","C","D","E","F");
	for (String label : stageLabels){
	    Transforms.registerStage( new DummyStage(label) );
	}
	List<String> initialPathPlan =  Arrays.asList("C","A","B","D","E","F");

	/*
	 * Run the test.
	 */
	while (!doStop) {
	    UUID siId = UUID.randomUUID();
	    String simCalAmpDataString = RandomStringUtils.randomAlphanumeric(dataByteCount);
	    byte[] calAmpDataBytes = simCalAmpDataString.getBytes(Charset.forName("UTF-8"));
	    CalAmpSIWrapper payload = new CalAmpSIWrapper(siId, calAmpDataBytes, initialPathPlan);

	    Message<CalAmpSIWrapper> m1;
	    m1 = MessageBuilder.withPayload(payload).setHeader("calamp-next-hop", initialPathPlan.get(0)).build();

	    log.info("Inject: " + payload);
	    channel.send(m1);
	    seq++;

	    if (seq > testCount) {
		doStop = true;
	    }
	}
    }
}