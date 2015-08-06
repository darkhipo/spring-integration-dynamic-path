package com.calamp.messaging.spring.integration.poc;

import java.util.concurrent.Future;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.messaging.handler.annotation.Header;

@MessagingGateway(defaultRequestChannel = CalAmpSIConfig.sourceChannelName, defaultReplyChannel = CalAmpSIConfig.targetChannelName, errorChannel = CalAmpSIConfig.errorChannelName, defaultRequestTimeout = CalAmpSIConfig.requestTimeoutMillis, defaultReplyTimeout = CalAmpSIConfig.replyTimeoutMillis)
public interface CalAmpSIRouteAndProcessService {

    @Gateway(requestChannel = CalAmpSIConfig.sourceChannelName, replyChannel = CalAmpSIConfig.targetChannelName)
    Future<CalAmpSIWrapper> processMessage(CalAmpSIWrapper message,
            @Header(CalAmpSIConfig.nextHopHeaderName) String nextHop);

    @Gateway(requestChannel = CalAmpSIConfig.sourceChannelName, replyChannel = CalAmpSIConfig.targetChannelName)
    Future<CalAmpSIWrapper> processMessage(CalAmpSIWrapper message);
}
