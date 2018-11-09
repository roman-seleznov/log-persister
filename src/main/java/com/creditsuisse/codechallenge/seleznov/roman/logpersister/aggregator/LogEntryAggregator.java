package com.creditsuisse.codechallenge.seleznov.roman.logpersister.aggregator;

import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.LogEntry;
import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.ServerEvent;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.List;
import java.util.TreeSet;

@MessageEndpoint
@NoArgsConstructor
public class LogEntryAggregator {

    private Logger logger = LoggerFactory.getLogger(LogEntryAggregator.class);

    @Aggregator(inputChannel = "aggregatorChannel", outputChannel = "persistenceChannel" )
    public Message<ServerEvent> aggregate(List<Message<LogEntry>> messages) {

        TreeSet<LogEntry> logEntries = new TreeSet<>();
        messages.forEach( message -> logEntries.add(message.getPayload()));
        ServerEvent serverEvent = new ServerEvent(logEntries.first(), logEntries.last());

        logger.debug("Aggregated new ServerEvent: {}", serverEvent);

        return MessageBuilder.withPayload(serverEvent).build();
    }

    @ReleaseStrategy
    public boolean releaseStrategy(List<Message<LogEntry>> messageList) {
        return messageList.size() == 2;
    }

    @CorrelationStrategy
    public String correlationStrategy(LogEntry logEntry) {
        return logEntry.getId();
    }


}
