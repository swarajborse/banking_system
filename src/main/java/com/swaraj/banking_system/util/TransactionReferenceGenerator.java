package com.swaraj.banking_system.util;

import java.time.Year;
import java.util.concurrent.atomic.AtomicLong;

public class TransactionReferenceGenerator {

    private static final AtomicLong COUNTER = new AtomicLong(1);

    public static String generate() {

        return "TXN"
                + Year.now().getValue()
                + String.format("%06d", COUNTER.getAndIncrement());

    }
}