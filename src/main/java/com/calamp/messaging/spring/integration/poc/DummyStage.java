package com.calamp.messaging.spring.integration.poc;

import java.util.Collections;

public class DummyStage extends CalAmpSIStage {
    public DummyStage(String ident, Boolean isFinalStage) {
	super(ident, isFinalStage);
    }
    public CalAmpSIWrapper enact( CalAmpSIWrapper inPayload ){
	/* TODO: Actual Work Would Happen Here. 
	 * The outbound is a copy of the inbound with the payload data and 
	 * future path fields modified. 
	*/
	//Copy construction, outboundPayload is deep copy.
	CalAmpSIWrapper outboundPayload = new CalAmpSIWrapper(inPayload);
	//The first element of the future becomes the present.
	outboundPayload.advance( this.getStageIdentifer() ); 
	String dataAsString = inPayload.getDataAsString() + " " + getStageIdentifer();
	//Modify outbound payload data.
	outboundPayload.setDataBytesFromString(dataAsString);
	//The future (at outbound payload) beyond the present is random.
	for( String s : outboundPayload.getTransitPath()){
	    if (!outboundPayload.getFuturePath().contains(s)){
		outboundPayload.getFuturePath().add(s);
	    }
	}
	Collections.shuffle( outboundPayload.getFuturePath() );

	return outboundPayload;
    }
}
