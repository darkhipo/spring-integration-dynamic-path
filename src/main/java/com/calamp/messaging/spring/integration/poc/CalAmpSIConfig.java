package com.calamp.messaging.spring.integration.poc;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.messaging.MessageChannel;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
@ComponentScan
@EnableIntegration
@IntegrationComponentScan
@Import( { TargetServiceActivator.class, Transforms.class, Routers.class } )
public class CalAmpSIConfig {
    public final static String sourceChannelName = "calAmpSourceChannel";
    public final static String stageChannelName  = "calAmpStageChannel";
    public final static String targetChannelName = "calAmpTargetChannel";
    public final static String nextHopHeaderName = "calamp-next-hop";
    public final static String terminalStageTag  = "!";
    
    private static Logger log = Logger.getLogger(CalAmpSIConfig.class.getName());
    static final int pollDelay = 128;
    
    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
	log.info("Poll: " + CalAmpSIConfig.pollDelay);
    	PollerMetadata pollerMetadata = new PollerMetadata();
    	pollerMetadata.setTrigger(new PeriodicTrigger(pollDelay));
    	return pollerMetadata;
    }
    @Bean
    public MessageChannel calAmpSourceChannel(){
	return new DirectChannel();
    }
    @Bean
    public MessageChannel calAmpStageChannel(){
	return new DirectChannel();
    }
    @Bean
    public MessageChannel calAmpTargetChannel(){
	return new DirectChannel();
    }
}