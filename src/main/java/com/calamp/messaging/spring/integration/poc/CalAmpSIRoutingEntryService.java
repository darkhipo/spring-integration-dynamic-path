package com.calamp.messaging.spring.integration.poc;

import java.util.List;
import java.util.concurrent.Future;

public interface CalAmpSIRoutingEntryService {
    Future<CalAmpSIWrapper> processIncomingMessage(Object message);
    List<Future<CalAmpSIWrapper>> processIncomingMessageList(List<Object> messageList);
}
