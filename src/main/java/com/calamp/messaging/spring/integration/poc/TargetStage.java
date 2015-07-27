package com.calamp.messaging.spring.integration.poc;

public class TargetStage extends CalAmpSIStage {

    public TargetStage(String ident, Boolean isFinalStage) {
	super(ident, isFinalStage);
    }

    @Override
    public CalAmpSIWrapper enact(CalAmpSIWrapper inPayload) {
	return null;
    }

}
