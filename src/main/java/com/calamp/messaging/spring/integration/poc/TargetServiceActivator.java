package com.calamp.messaging.spring.integration.poc;

import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.stereotype.Component;

@Component
public class TargetServiceActivator {
	
	@ServiceActivator
	public void printObject(Object obj){
		System.out.println(obj);
	}
}
