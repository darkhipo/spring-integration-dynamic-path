package com.calamp.messaging.spring.integration.poc;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Transforms {
    static Logger log = Logger.getLogger(Transforms.class.getName());
    static Map<String, CalAmpSIStage> stageMap;
    
    static{
        stageMap = new HashMap<String, CalAmpSIStage>();
    }

    public static void registerStage(CalAmpSIStage newStage){
	stageMap.put(newStage.getStageIdentifer(), newStage);
    }
    
    @Transformer
    public Message<CalAmpSIWrapper> transform(Message<CalAmpSIWrapper> messageIn) throws InterruptedException {
	CalAmpSIWrapper inboundPayload = messageIn.getPayload();
	CalAmpSIStage stage = stageMap.get(inboundPayload.nextStepPeek());
	CalAmpSIWrapper outboundPayload = stage.enact(inboundPayload);
	String nextHop = outboundPayload.nextStepPeek();
	if( nextHop != null ){
	    String logstr = inboundPayload.getSiIdent() + " AT: " ;
	    logstr += inboundPayload.nextStepPeek() + " NEXT-HOP: " + nextHop;
	    log.info( logstr );
	}
	Message<CalAmpSIWrapper> m1;
	m1 = MessageBuilder.withPayload(outboundPayload).setHeader("calamp-next-hop", nextHop).build();
	//Thread.sleep(1000);
	return m1;
    }
}