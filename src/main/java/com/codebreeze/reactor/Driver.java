package com.codebreeze.reactor;

public class Driver extends AbstractDriver{
    public static void main(final String... args) throws Exception {
        final EchoServiceConfiguration echoServiceConfiguration = parseParamsWithJCommander(args);
    }
}
