package com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ToString @Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ServerEvent {

    private @Id String id;
    private Long duration;
    private boolean alert;
    private String type;
    private String host;

    public ServerEvent(LogEntry startEntry, LogEntry endEntry) {

        setId(startEntry.getId());
        setDuration(endEntry.getTimestamp() - startEntry.getTimestamp());
        setAlert(getDuration() > 4);
        setType(startEntry.getType() != null ? startEntry.getType().getValue() : null);
        setHost(startEntry.getHost());

    }

}
