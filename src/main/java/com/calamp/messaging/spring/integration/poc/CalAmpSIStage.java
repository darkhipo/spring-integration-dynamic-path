package com.calamp.messaging.spring.integration.poc;

public abstract class CalAmpSIStage {
    private final String stageIdentifer;
    private final Boolean isFinalStage;
    
    public CalAmpSIStage( String ident, Boolean isFinalStage ){
	this.stageIdentifer = ident;
	this.isFinalStage = isFinalStage; 
    }
    public String getStageIdentifer() {
        return stageIdentifer;
    }
    public Boolean getIsFinalStage() {
        return isFinalStage;
    }
    
    public abstract CalAmpSIWrapper enact( CalAmpSIWrapper inPayload ) throws Exception;
}
