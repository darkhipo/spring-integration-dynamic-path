package com.calamp.messaging.spring.integration.poc;

import java.security.InvalidAlgorithmParameterException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.Router;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

@SuppressWarnings("unused")
@Component
public class Routers {
    private static Logger log = Logger.getLogger(Routers.class.getName());

    @Router(inputChannel = CalAmpSIConfig.sourceChannelName, defaultOutputChannel = CalAmpSIConfig.targetChannelName, applySequence = "true")  
    public List<String> route( Message<CalAmpSIWrapper> inbound ) throws InterruptedException, InvalidAlgorithmParameterException{
        String nextHop = null;
        if( inbound.getHeaders().containsKey(CalAmpSIConfig.nextHopHeaderName) ){
            nextHop = String.valueOf( inbound.getHeaders().get( CalAmpSIConfig.nextHopHeaderName ) );
        }
        else{
            nextHop = inbound.getPayload().nextStepPeek();
        }
        log.info( "Route [" + "Payload: "+ inbound + " Next-Hop: " + nextHop + "]");
        List<String> routesTo = new ArrayList<String>();
        CalAmpSIStage currentStage = Transforms.resolveStage(nextHop);

        if ( currentStage == null || currentStage.getStageIdentifer().equals(CalAmpSIConfig.terminalStageTag) ){
            routesTo.add( CalAmpSIConfig.targetChannelName );
        }
        else{
            routesTo.add( CalAmpSIConfig.stageChannelName );
        }

        return routesTo;
    }

    //    @Router(inputChannel = CalAmpSIConfig.sourceChannelName, defaultOutputChannel = CalAmpSIConfig.targetChannelName, applySequence = "true")  
    //    public List<String> route(@Header(CalAmpSIConfig.nextHopHeaderName) String nextHop, Message<CalAmpSIWrapper> inbound) throws InterruptedException{
    //	log.info( "Route [" + "Payload: "+ inbound + " Next-Hop: " + nextHop + "]");
    //	List<String> routesTo = new ArrayList<String>();
    //	CalAmpSIStage currentStage = Transforms.resolveStage(nextHop);
    //	
    //	if ( currentStage == null || currentStage.getStageIdentifer().equals(CalAmpSIConfig.terminalStageTag) ){
    //	    routesTo.add( CalAmpSIConfig.targetChannelName );
    //	}
    //	else{
    //	    routesTo.add( CalAmpSIConfig.stageChannelName );
    //	}
    //
    //	return routesTo;
    //    }
}
