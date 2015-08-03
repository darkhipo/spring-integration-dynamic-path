package com.calamp.messaging.spring.integration.poc;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@MessageEndpoint
public class ServiceActivators {

    private static final Logger log = Logger.getLogger(ServiceActivators.class.getName());
    
    @ServiceActivator(inputChannel = CalAmpSIConfig.targetChannelName)
    public void printObject(Object obj){
	String preamble = "[Message at Completion]: ";
	log.info(preamble + obj);
	//System.out.println(obj);
    }
}
