package com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain;

import lombok.*;

/**
 * "id":"scsmbstgra", "state":"STARTED", "type":"APPLICATION_LOG", "host":"12345", "timestamp":1491377495212
 */
@Getter @Setter @NoArgsConstructor @ToString @Builder @AllArgsConstructor
public class LogEntry implements Comparable<LogEntry> {

    private String id;
    private TransactionState state;
    private Long timestamp;

    private LogType type;
    private String host;

    @Override
    public int compareTo(LogEntry toCompare) {
        return getState().compareTo(toCompare.getState());
    }
}
