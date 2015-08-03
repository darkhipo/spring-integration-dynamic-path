# spring-integration-dynamic-path
Uses custon router or header router to allow for a reconfigurable process path. 

![alt tag](https://github.com/darkhipo/spring-integration-dynamic-path/blob/master/dynamic_route.jpg)

## Configurations

The project comes with both a pure java configuration and an XML configuration. Both configurations work, and either can be used. The choice is made in the Startup.java file. 

# Overview

## Settings 
Some meta-parameters are set in the file CalAmpSIConfig.java, this includes header values, channel names, timeout values, and that sort of thing. 

## System Interface
The service is intended to be accessed through a CalAmpSIRouteAndProcessService bean. 
Here is an example of fetching this bean from Startup.java:

```
CalAmpSIRouteAndProcessService service = context.getBean(serviceName, CalAmpSIRouteAndProcessService.class);
```

A user will fetch the bytes of the packet to be processed and wrap them in a CalAmpSIWrapper object using the method below:

```
private static CalAmpSIWrapper wrapData(byte[] calAmpDataBytes, List<String> initialPathPlan) {
  UUID siId = UUID.randomUUID();
  CalAmpSIWrapper payload = new CalAmpSIWrapper(siId, calAmpDataBytes, initialPathPlan);
  return payload;
}
```

Then each message may be serviced thus:

```
Future<CalAmpSIWrapper> asynchReply = service.processMessage(payload);
```

This will execute the asynchronous process of enacting the stages (with dynamic path changes). 
To retrieve the reply:

```
CalAmpSIWrapper reply = asynchReply.get()
```

The reply wrapper may either be a valid reply containing relavent data, or it wraps an exception that was thrown durring the processing of the request. 

## Daemon Config
Startup.java id the entry point for the control flow of the program; it currently implements an example network and injects example messages into the network. At startup the daemon launched to manage the execution flow of the SI-network for inbound CalAmp packets will need to do two things:
* Firstly, each stage of the network (uniquely identified by the string identifier stageIdentifer in the CalAmpStage class) stage must be registed with the call ```Transforms.registerStage(CalAmpSIStage newStage, Boolean isFinalStage)```, where final stage indicates whether the stage registered terminates the path. 
* Secondly, the daemon must determine the initial path of each inbound packet. A path here is defined to be a list of string identifiers matching the stage identifiers referenced in the previous point. The packet will be routed to the stage ( as identified by that stage's string identifier) of the inbound packet's initial path. Each stage may change the subsequent stages.  

I'll give an example from Startup.java:

```
List<String> stageLabels = Arrays.asList("A","B","C","D","E");
String finalStageTag = "F";
for (String label : stageLabels){
    Transforms.registerStage( new DummyStage(label, false) );
}
Transforms.registerStage( new DummyStage(finalStageTag, true) );
List<String> initialPathPlan =  Arrays.asList("C","A","B","D","E","F");

String simCalAmpDataString = RandomStringUtils.randomAlphanumeric(dataByteCount);
byte[] calAmpDataBytes = simCalAmpDataString.getBytes(Charset.forName("UTF-8"));
CalAmpSIWrapper payload = wrapData( calAmpDataBytes, initialPathPlan );
System.out.println( "Service Request: " + payload);
Future<CalAmpSIWrapper> asynchReply = service.processMessage(payload);
System.out.println( "Service Reply: "  + asynchReply.get() );
```

## SI Packet Wrapper Config
All corporate packets to be routed through the designated SI network should be wrapped in a CalAmpSIWrapper object (inside the dataBytes field). The bytes of the wrapped object can be reconstructed into the desired object through a serialize/deserialize implementation. The CalAmpSIWrapper object contains routing and exception information for the SI-network.

## Stage Config
Each execution stage required in CalAmp's spring system should inherit from the abstract class CalAmpSIStage and implement an "enact" method. Each such method will perform the required transform on the CalAmpSIWrapper message that it recieves (as needed on the internal CalAmp packet data contained therein). Each such method then returns another CalAmpSIWrapper containing the outbound data from the implementing stage. DummyStage is an example implementation of such a stage.   

## Exception Handling Config
Exceptions resulting from execution of the service are handled by the exception handler in CalAmpSIErrorHandler.java, it can be extended as desired. Exceptions will still be returned as a reply to the calling service (wrapped in a CalAmpSIWrapper) this is to prevent blocking on a service request terminated due to exception.

## End Path Handling Config
All messages upon completion of their path arrive at TargetStage.java, the class therein may be extended beyond the current logging function to perform other useful work needed upon path completion.

