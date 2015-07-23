package com.calamp.messaging.spring.integration.poc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Transforms {
    Random rand;

    Transforms() {
	this.rand = new Random();
    }

    @Transformer
    public Message<String> transform(Message<String> messageIn) throws InterruptedException {
	String outPayload = messageIn.getPayload();

	Integer currentIndex = (Integer) messageIn.getHeaders().get(
		"calamp-path-index");

	@SuppressWarnings("unchecked")
	List<String> incomingPath = (List<String>) messageIn.getHeaders().get(
		"calamp-path");

	List<String> concretePast = (currentIndex == 0) ? new ArrayList<String>()
		: incomingPath.subList(0, currentIndex);

	String concretePresent = incomingPath.get(currentIndex);
	List<String> variableFuture = (currentIndex == incomingPath.size() -1 ) ? 
		new ArrayList<String>() :
		copyStringList(incomingPath.subList(
		currentIndex + 1, incomingPath.size()));

	List<String> outPath = new ArrayList<String>();
	if (concretePresent.equals("A")) {
	    outPayload = A(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	} else if (concretePresent.equals("B")) {
	    outPayload = B(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	}
	else if (concretePresent.equals("C")) {
	    outPayload = C(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	}
	else if (concretePresent.equals("D")) {
	    outPayload = D(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	}
	else if (concretePresent.equals("E")) {
	    outPayload = E(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	}
	else if (concretePresent.equals("F")) {
	    outPayload = F(concretePast, concretePresent, variableFuture,
		    outPayload, outPath);
	}
	System.out.println("AT: " + outPath.get(currentIndex) + " NEXT-HOP: " + outPath.get(currentIndex + 1));
	Message<String> m1 = MessageBuilder.withPayload(outPayload)
		.setHeader("calamp-path", outPath)
		.setHeader("calamp-path-index", currentIndex + 1)
		.setHeader("calamp-next-hop", outPath.get(currentIndex + 1)).build();
	//Thread.sleep(1000);
	return m1;
    }

    private String A(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "A";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }

    private String B(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "B";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }
    
    private String C(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "C";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }
    
    private String D(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "D";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }
    
    private String E(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "E";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }
    
    private String F(List<String> concretePast, String concretePresent,
	    List<String> variableFuture, String inPayload, List<String> outPath) {
	String myName = "F";

	String outPayload = dummyProcess(concretePast, concretePresent,
		variableFuture, inPayload, myName, outPath);

	return outPayload;
    }

    private String dummyProcess(List<String> concretePast,
	    String concretePresent, List<String> variableFuture,
	    String inPayload, String myName, List<String> outPath) {

	assertIndexCorrect(myName, concretePresent);
	//variableFuture.addAll(concretePast);
	Collections.shuffle(variableFuture);

	outPath.addAll(concretePast);
	outPath.add(myName);
	outPath.addAll(variableFuture);
	
	String outPayload = inPayload + myName;
	return outPayload;
    }

    private static void assertIndexCorrect(String myName, String concretePresent) {
	if (!myName.equals(concretePresent)) {
	    throw new IllegalStateException("In state [" + myName
		    + "] with inconsistent path value: [" + concretePresent
		    + "]");
	}
    }

    private static List<String> copyStringList(List<String> toCopy) {
	ArrayList<String> theCopy = new ArrayList<String>();
	for (String s : toCopy) {
	    theCopy.add(new String(s));
	}
	return theCopy;
    }
}
