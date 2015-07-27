package com.calamp.messaging.spring.integration.poc;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
@MessageEndpoint
public class TargetServiceActivator {
	
	@ServiceActivator(inputChannel = CalAmpSIConfig.targetChannelName )
	public void printObject(Object obj){
		System.out.println(obj);
	}
}
