package com.codebreeze.reactor.services;

import java.util.Date;


public class EchoService {

    public String echo(final String request) {
        spin(10);
        return new Date() + ":" + request;
    }

    private static void spin(int milliseconds) {
        long sleepTime = milliseconds*1000000L; // convert to nanoseconds
        long startTime = System.nanoTime();
        while ((System.nanoTime() - startTime) < sleepTime) {}
    }
}
