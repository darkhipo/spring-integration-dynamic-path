# spring-integration-dynamic-path
Uses header router to allow for a reconfigurable process path. 

![alt tag](https://github.com/darkhipo/spring-integration-dynamic-path/blob/master/dynamic_route.jpg)

# XML Configuration

The SI network is defined in resources/MET-INF/si-components.xml . This file will need to be modified to account for all known stages of the domain specific Spring Integration network. This file must configure a channel outbound from each such stage (except for the terminating stage) and inbound into an SI header-router. All channel routes for the header-router must be assigned as a bean in the si-components.xml file. The SI header-router serves as a demultiplexer for the system; each incomming message is routed to the next stage based on a 'calamp-next-hop' SI-header field. The router will route the terminating stage to the terminating channel which feeds into a terminating service activator or optionally a terminating adapter thus concluding the flow. The terminating service activator or adapter as well as the terminating channel must also be configured in si-components.xml . 

# JAVA Configuration

## Daemon Config
Startup.java id the entry point for the control flow of the program; it currently implements an example network and injects example messages into the network. At startup the daemon launched to manage the execution flow of the SI-network for inbound CalAmp packets will need to do two things:
* Firstly, each stage of the network (uniquely identified by the string identifier stageIdentifer in the CalAmpStage class) stage must be registed with the call Transforms.registerStage(CalAmpSIStage newStage). Each registerd stage MUST be mirrored by a transformer declared and defined in si-components.xml, the identifiers must be an exact match match with those bound to the CalAmpStage objects that are registered. 
* Secondly, the daemon must determine the initial path of each inbound packet. A path here is defined to be a list of string identifiers matching the stage identifiers referenced in the previous point. The packet will be routed to the stage ( as identified by that stage's string identifier) of the inbound packet's initial path. Each stage may change the subsequent stages.  

## SI Packet Wrapper Config
All corporate packets to be routed through the designated SI network should be wrapped in a CalAmpSIWrapper object (inside the dataBytes field). The bytes of the wrapped object can be reconstructed into the desired object through a serialize/deserialize implementation. The CalAmpSIWrapper object contains routing information for the SI-network.

## Stage Config
Each execution stage required in CalAmp's spring system should inherit from the abstract class CalAmpSIStage and implement an "enact" method. Each such method will perform the required transform on the CalAmpSIWrapper message that it recieves (as needed on the internal CalAmp packet data contained therein). Each such method then returns another CalAmpSIWrapper containing the outbound data from the implementing stage. DummyStage is an example implementation of such a stage.    
