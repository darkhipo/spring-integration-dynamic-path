package com.calamp.messaging.spring.integration.poc;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@MessageEndpoint
public class CalAmpSIErrorHandler {
    private static final Logger log = Logger.getLogger(CalAmpSIErrorHandler.class.getName());
    
    @ServiceActivator(inputChannel = CalAmpSIConfig.errorChannelName, outputChannel = CalAmpSIConfig.targetChannelName)
    public Message<CalAmpSIWrapper> handleErrorMessage(Message<Exception> errorMessage) {
	Exception e = errorMessage.getPayload();
        String preamble = "[Error]: ";
	log.info(preamble + e);
	System.out.println(preamble + e);
	
	CalAmpSIWrapper errorWrapper = new CalAmpSIWrapper(e);
	Message<CalAmpSIWrapper> m1;	
	m1 = MessageBuilder
		.withPayload(errorWrapper)
		.copyHeaders(errorMessage.getHeaders())
		.build();
	return m1;
    }
}