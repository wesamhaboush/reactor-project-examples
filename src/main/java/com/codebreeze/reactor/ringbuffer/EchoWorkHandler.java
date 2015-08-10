package com.codebreeze.reactor.ringbuffer;

import com.codebreeze.reactor.services.EchoService;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.WorkHandler;
import org.springframework.beans.factory.annotation.Autowired;

public class EchoWorkHandler implements WorkHandler<EchoEvent> {

    @Autowired
    private EchoService echoService;

    public void onEvent(EchoEvent event) throws Exception {
//        System.out.println("handling event: " + event);
        event.getDeferredResult().setResult(echoService.echo(event.getText()));
    }
}
