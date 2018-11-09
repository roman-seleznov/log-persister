package com.creditsuisse.codechallenge.seleznov.roman.logpersister.aggregator;

import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.LogEntry;
import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.ServerEvent;
import com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain.TransactionState;
import org.junit.Before;
import org.junit.Test;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.util.LinkedList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class LogEntryAggregatorTest {

    private LogEntryAggregator aggregator;

    @Before
    public void setUp() {
        aggregator = new LogEntryAggregator();
    }

    @Test
    public void aggregateWithAlertAndReverseSequence() {

        List<Message<LogEntry>> logEntries = new LinkedList<>();

        LogEntry.LogEntryBuilder builder = LogEntry.builder().id("One");

        logEntries.add(getMessage(builder.state(TransactionState.FINISHED).timestamp(10L).build()));
        logEntries.add(getMessage(builder.state(TransactionState.STARTED).timestamp(1L).build()));

        Message<ServerEvent> serverEventMessage = aggregator.aggregate(logEntries);

        assertThat( serverEventMessage, is(not(equalTo(null))));
        assertThat( serverEventMessage.getPayload().getDuration(), is(equalTo(9L)));
        assertThat( serverEventMessage.getPayload().isAlert(), is(equalTo(true)));

    }

    @Test
    public void aggregateWithNoAlertAndNaturalSequence() {

        List<Message<LogEntry>> logEntries = new LinkedList<>();

        LogEntry.LogEntryBuilder builder = LogEntry.builder().id("Two");

        logEntries.add(getMessage(builder.state(TransactionState.STARTED).timestamp(10L).build()));
        logEntries.add(getMessage(builder.state(TransactionState.FINISHED).timestamp(11L).build()));

        Message<ServerEvent> serverEventMessage = aggregator.aggregate(logEntries);

        assertThat( serverEventMessage, is(not(equalTo(null))));
        assertThat( serverEventMessage.getPayload().getDuration(), is(equalTo(1L)));
        assertThat( serverEventMessage.getPayload().isAlert(), is(equalTo(false)));

    }

    @Test
    public void releaseStrategy() {
        //TODO: Implement more tests
    }

    @Test
    public void correlationStrategy() {
        //TODO: Implement more tests
    }

    private Message<LogEntry> getMessage(LogEntry entry) {
        return MessageBuilder.withPayload(entry).build();
    }

}