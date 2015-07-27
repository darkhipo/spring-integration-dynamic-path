package com.calamp.messaging.spring.integration.poc;

import java.security.InvalidAlgorithmParameterException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Transforms {
    private static Logger log = Logger.getLogger(Transforms.class.getName());
    private static Map<String, CalAmpSIStage> stageMap;
    
    static{
	Transforms.stageMap = new HashMap<String, CalAmpSIStage>();
	Transforms.stageMap.put(CalAmpSIConfig.terminalStageTag, new TargetStage(CalAmpSIConfig.terminalStageTag, true) );
    }
    public static void registerStage(CalAmpSIStage newStage) throws InvalidAlgorithmParameterException{
	if( newStage.getStageIdentifer().equals(CalAmpSIConfig.terminalStageTag) ){
	    throw new InvalidAlgorithmParameterException("\""+ CalAmpSIConfig.terminalStageTag + "\" is not a valid stage identifier.");
	}
	Transforms.stageMap.put(newStage.getStageIdentifer(), newStage);
    }
    public static Set<String> getStageKeys( ){
	return Transforms.stageMap.keySet();
    }
    public static final CalAmpSIStage resolveStage( String key ){
	if ( Transforms.stageMap.containsKey(key) ){
	    return Transforms.stageMap.get(key);    
	}
	return null;
    }
    
    @Transformer(inputChannel = CalAmpSIConfig.stageChannelName, outputChannel = CalAmpSIConfig.sourceChannelName )
    public static Message<CalAmpSIWrapper> transform( Message<CalAmpSIWrapper> messageIn ) throws InterruptedException {
	CalAmpSIWrapper inboundPayload = messageIn.getPayload();
	CalAmpSIStage stage = Transforms.resolveStage(inboundPayload.nextStepPeek());
	CalAmpSIWrapper outboundPayload = stage.enact(inboundPayload);
	String nextHop = outboundPayload.nextStepPeek();
	if( nextHop != null ){
	    String logstr = "Transform " + inboundPayload.getSiIdent() + " AT: " ;
	    logstr += inboundPayload.nextStepPeek() + " NEXT-HOP: " + nextHop;
	    log.info( logstr );
	}
	if ( stage.getIsFinalStage() ) {
	    nextHop = CalAmpSIConfig.terminalStageTag;
	}
	Message<CalAmpSIWrapper> m1;
	m1 = MessageBuilder.withPayload(outboundPayload)
		.setHeader(CalAmpSIConfig.nextHopHeaderName, nextHop)
		.build();
	return m1;
    }
}