package com.calamp.messaging.spring.integration.poc;

public abstract class CalAmpSIStage {
    private final String stageIdentifer;
    
    public CalAmpSIStage( String ident ){
	this.stageIdentifer = ident;
    }
    public String getStageIdentifer() {
        return stageIdentifer;
    }
    public abstract CalAmpSIWrapper enact( CalAmpSIWrapper inPayload );
}
