package com.codebreeze.reactor.services;

import com.google.common.util.concurrent.Uninterruptibles;

import java.util.Date;
import java.util.concurrent.TimeUnit;


public class EchoService {

    public String echo(final String request) {
        Uninterruptibles.sleepUninterruptibly(200, TimeUnit.MILLISECONDS);
        return new Date() + ":" + request;
    }
}
