package com.calamp.messaging.spring.integration.poc;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
public class Transforms {
    private static Logger log = Logger.getLogger(Transforms.class.getName());
    private static Map<String, CalAmpSIStage> stageMap;

    static {
        Transforms.stageMap = new HashMap<String, CalAmpSIStage>();
        Transforms.stageMap.put(CalAmpSIConfig.terminalStageTag,
                new TargetStage(CalAmpSIConfig.terminalStageTag, true));
    }

    public static void registerStage(CalAmpSIStage newStage) {
        if (!newStage.getStageIdentifer().equals(
                CalAmpSIConfig.terminalStageTag)) {
            Transforms.stageMap.put(newStage.getStageIdentifer(), newStage);
        }
    }

    public static Set<String> getStageKeys() {
        return Transforms.stageMap.keySet();
    }

    public static final CalAmpSIStage resolveStage(String key) {
        if (Transforms.stageMap.containsKey(key)) {
            return Transforms.stageMap.get(key);
        }
        return null;
    }

    private static Message<CalAmpSIWrapper> enrichWithHeader(
            Message<CalAmpSIWrapper> messageIn) {
        CalAmpSIWrapper outboundPayload = messageIn.getPayload();
        String nextHop = outboundPayload.nextStepPeek();
        Message<CalAmpSIWrapper> m1;
        m1 = MessageBuilder.withPayload(outboundPayload)
                .copyHeaders(messageIn.getHeaders())
                .setHeader(CalAmpSIConfig.nextHopHeaderName, nextHop).build();
        return m1;
    }

    @Transformer(inputChannel = CalAmpSIConfig.stageChannelName, outputChannel = CalAmpSIConfig.sourceChannelName)
    public static Message<CalAmpSIWrapper> transform(
            Message<CalAmpSIWrapper> messageIn) throws Exception {
        CalAmpSIWrapper inboundPayload = messageIn.getPayload();
        if (!messageIn.getHeaders().containsKey(
                CalAmpSIConfig.nextHopHeaderName)) {
            messageIn = enrichWithHeader(messageIn);
        }

        CalAmpSIStage stage = Transforms.resolveStage(inboundPayload
                .nextStepPeek());
        CalAmpSIWrapper outboundPayload = stage.enact(inboundPayload);
        String nextHop = outboundPayload.nextStepPeek();

        if (nextHop == null || stage == null || stage.getIsFinalStage()) {
            nextHop = CalAmpSIConfig.terminalStageTag;
        }

        String logstr = "Transform " + inboundPayload.getSiIdent() + " AT: ";
        logstr += inboundPayload.nextStepPeek() + " NEXT-HOP: " + nextHop;
        log.info(logstr);

        Message<CalAmpSIWrapper> m1;
        m1 = MessageBuilder.withPayload(outboundPayload)
                .copyHeaders(messageIn.getHeaders())
                .setHeader(CalAmpSIConfig.nextHopHeaderName, nextHop).build();

        return m1;
    }
}