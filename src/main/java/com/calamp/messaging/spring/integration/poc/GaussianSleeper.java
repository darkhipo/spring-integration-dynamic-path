package com.calamp.messaging.spring.integration.poc;

import java.util.Random;

public class GaussianSleeper extends CalAmpSIStage {
    private Double mu = null;
    private Double sigma = null;
    private Random rand;
    public GaussianSleeper(String ident, Boolean isFinalStage, Double mu, Double sigma) {
	super(ident, isFinalStage);
	this.mu = mu;
	this.sigma = sigma;
	this.rand = new Random();
    }
    public CalAmpSIWrapper enact( CalAmpSIWrapper inPayload ) throws InterruptedException{
	CalAmpSIWrapper outboundPayload = new CalAmpSIWrapper(inPayload);
	outboundPayload.advance( this.getStageIdentifer() ); 
	Integer myDelay = (int) Math.round( this.rand.nextGaussian() * this.sigma + this.mu );
	Thread.sleep(myDelay);
	String dataAsString = inPayload.getDataAsString() + System.currentTimeMillis() + ";";
	outboundPayload.setDataBytesFromString(dataAsString);
	return outboundPayload;
    }
}