package com.calamp.messaging.spring.integration.poc;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;

public class Startup {

    public static void main(String[] args) throws IOException {
	ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
		"/META-INF/spring/si-components.xml");
	String propertyPath = "si-ident.properties";

	InputStream propStream = new FileInputStream(propertyPath);
	Properties props = new Properties();
	props.load(propStream);
	Integer dataByteCount = 10;

	String sourceChannelName = props.getProperty("source_channel_name");

	MessageChannel channel = context.getBean(sourceChannelName,
		MessageChannel.class);

	Integer currentIndex = 0;
	List<String> outPath = new ArrayList<String>();
	outPath.add("A");
	outPath.add("B");
	outPath.add("C");
	outPath.add("D");
	outPath.add("E");
	outPath.add("F");

	boolean doStop = false;
	int seq = 0;
	while (!doStop) {
	    // Context Components interact as needed.
	    String data = RandomStringUtils.randomAlphanumeric(dataByteCount);
	    String payload = "Mseq[" + "Seq[" + seq + "]" + "Payload[" + data
		    + "]" + "]";

	    Message<String> m1 = MessageBuilder.withPayload(payload)
		    .setHeader("calamp-path", outPath)
		    .setHeader("calamp-next-hop", outPath.get(0))
		    .setHeader("calamp-path-index", currentIndex).build();

	    channel.send(m1);
	    seq++;

	    if (seq > 100) {
		doStop = true;
	    }
	}
	context.close();
    }
}
