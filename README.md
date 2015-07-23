# spring-integration-dynamic-path
Uses header router to allow for a reconfigurable process path. 

The current template is pretty messy but it does work under some reasonable conditions. 
In particular, the messages keep track of the path that the messages has traversed (through the services) they are kept in the message header.

So if the message header gets too long you will get a StackOverflowError. Thus assuming paths of a "reasonable size" 
this code should work out. 

Still quite messy, I'm working on cleaning it up and modularizing.
