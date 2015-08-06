package com.calamp.messaging.spring.integration.poc;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.*;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.scheduling.support.PeriodicTrigger;

@Configuration
@ComponentScan
@EnableIntegration
@IntegrationComponentScan
@Import({ ServiceActivators.class, Transforms.class, Routers.class })
public class CalAmpSIConfig {
    private static final Logger log = Logger.getLogger(CalAmpSIConfig.class
            .getName());

    public static final String stageChannelName = "calAmpStageChannel";
    public static final String sourceChannelName = "calAmpSourceChannel";
    public static final String targetChannelName = "calAmpTargetChannel";
    public static final String errorChannelName = "calAmpErrorChannel";
    public static final String nextHopHeaderName = "calamp-next-hop";
    public static final String terminalStageTag = "!";

    public static final long pollDelayMillis = 64L;
    public static final long requestTimeoutMillis = 10000L;
    public static final long replyTimeoutMillis = 1000L;

    @Bean(name = PollerMetadata.DEFAULT_POLLER)
    public PollerMetadata defaultPoller() {
        log.info("Poll: " + CalAmpSIConfig.pollDelayMillis);
        PollerMetadata pollerMetadata = new PollerMetadata();
        pollerMetadata.setTrigger(new PeriodicTrigger(pollDelayMillis));
        return pollerMetadata;
    }

    @Bean
    public PublishSubscribeChannel calAmpSourceChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public PublishSubscribeChannel calAmpStageChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public PublishSubscribeChannel calAmpTargetChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public PublishSubscribeChannel calAmpReplyChannel() {
        return new PublishSubscribeChannel();
    }
}