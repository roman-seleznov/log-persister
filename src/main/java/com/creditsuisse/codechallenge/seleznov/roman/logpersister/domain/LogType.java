package com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LogType {

    @JsonProperty("APPLICATION_LOG")
    APPLICATION_SERVER_LOG("APPLICATION_LOG");

    @Getter private String value;

}
