package com.calamp.messaging.spring.integration.poc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.apache.commons.codec.binary.Base64;

public class CalAmpSIWrapper {
    private UUID siIdent;
    private byte[] dataBytes;
    private List<String> transitPath;
    private List<String> futurePath;

    public CalAmpSIWrapper(UUID siIdent, byte[] dataBytes, List<String> planPath) {
	super();
	this.siIdent = siIdent;
	this.dataBytes = Base64.encodeBase64(dataBytes);
	this.transitPath = new ArrayList<String> ();
	this.futurePath = planPath;
    }
    
    public CalAmpSIWrapper(CalAmpSIWrapper copyMe) {
	super();
	this.siIdent = UUID.fromString( copyMe.getSiIdent().toString() );
	this.transitPath = CalAmpSIWrapper.copyStringList( copyMe.getTransitPath() );
	this.futurePath = CalAmpSIWrapper.copyStringList( copyMe.getFuturePath() );
	this.dataBytes = Base64.encodeBase64( copyMe.getDataBytes() );
    }

    public String advance(String step){
	CalAmpSIWrapper.assertIndexCorrect(futurePath.get(0), step);
	if(futurePath.size() > 0){
	    String advancingStep = nextStepPop();
	    transitPath.add(advancingStep);
	    return advancingStep;
	}
	return null;
    }
    
    public String nextStepPeek(){
	if(this.futurePath.size() > 0){
	    return this.futurePath.get(0);
	}
	return null;
    }
    
    private String nextStepPop(){
	if(this.futurePath.size() > 0){
	    return this.futurePath.remove(0);
	}
	return null;
    }
    
    public UUID getSiIdent() {
	return siIdent;
    }

    public byte[] getDataBytes() {
	return Base64.decodeBase64(dataBytes);
    }

    public String getDataAsString() {
	return new String(Base64.decodeBase64(dataBytes));
    }
    
    public List<String> getTransitPath() {
        return CalAmpSIWrapper.copyStringList(this.transitPath);
    }

    public List<String> getFuturePath() {
        return futurePath;
    }

    public void setSiIdent(UUID siIdent) {
	this.siIdent = siIdent;
    }

    public void setDataBytes(byte[] dataBytes) {
	this.dataBytes = Base64.encodeBase64(dataBytes);
    }

    public void setDataBytesFromString(String dataString) {
	this.dataBytes = Base64.encodeBase64(dataString.getBytes());
    }

    public void setFuturePath(List<String> futurePath) {
        this.futurePath = futurePath;
    }
    
    @Override
    public String toString() {
	return "CalAmpSIWrapper [siIdent=" + siIdent + ", transitPath="
		+ transitPath + ", futurePath=" + futurePath + ", dataBytes="
		+ Arrays.toString(dataBytes) + "]";
    }
    
    private static List<String> copyStringList(List<String> toCopy) {
	ArrayList<String> theCopy = new ArrayList<String>();
	for (String s : toCopy) {
	    theCopy.add(new String(s));
	}
	return theCopy;
    }
    
    private static void assertIndexCorrect(String myName, String concretePresent) {
	if (!myName.equals(concretePresent)) {
	    throw new IllegalStateException("In state [" + myName
		    + "] with inconsistent path value: [" + concretePresent
		    + "]");
	}
    }
}