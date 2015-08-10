package com.codebreeze.reactor;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

public class AbstractDriver {
    protected static EchoServiceConfiguration parseParamsWithJCommander(final String... args) {
        final EchoServiceConfiguration echoServiceConfiguration = new EchoServiceConfiguration();
        final JCommander jCommander = new JCommander(echoServiceConfiguration);
        jCommander.setAcceptUnknownOptions(true);
        jCommander.parse(args);
        return echoServiceConfiguration;
    }

    @Parameters(separators = "= ")
    protected static class EchoServiceConfiguration {
        @Parameter(
                names = {"--http-port"},
                arity = 1,
                description = "the port number on which the rest service will be listening"
        )
        protected Integer port = 8081;
    }
}
