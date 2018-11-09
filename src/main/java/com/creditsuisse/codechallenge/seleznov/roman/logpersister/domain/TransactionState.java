package com.creditsuisse.codechallenge.seleznov.roman.logpersister.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum TransactionState {

    STARTED("STARTED"), FINISHED("FINISHED");

    @Getter private String value;
}
