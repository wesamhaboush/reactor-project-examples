package com.codebreeze.reactor;

import com.google.common.util.concurrent.Uninterruptibles;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;
import reactor.Environment;
import reactor.bus.Event;
import reactor.core.Dispatcher;
import reactor.core.dispatch.RingBufferDispatcher;
import reactor.core.dispatch.ThreadPoolExecutorDispatcher;
import reactor.core.dispatch.WorkQueueDispatcher;
import reactor.fn.Consumer;
import reactor.jarjar.com.lmax.disruptor.YieldingWaitStrategy;
import reactor.jarjar.com.lmax.disruptor.dsl.ProducerType;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.IntStream.range;

public class ReactorTest {
    private static final int BACKLOG = 2048;
    private static final int EVENT_COUNT = 2000;

    @Test
    public void testStuff() {
        final Event<String> event = Event.wrap("Hello World!");
        final AtomicLong counter = new AtomicLong(0);

        final Dispatcher ringBufferDispatcher = new RingBufferDispatcher(
                "ringBufferDispatcher",
                BACKLOG,
                null,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        final Dispatcher workQueueDispatcher = new WorkQueueDispatcher(
                "workQueueDispatcher",
                Environment.PROCESSORS,
                BACKLOG,
                null,
                ProducerType.SINGLE,
                new YieldingWaitStrategy()
        );

        final Dispatcher threadPoolExecutorDispatcher = new ThreadPoolExecutorDispatcher(
                Environment.PROCESSORS,
                BACKLOG,
                "threadPoolExecutorDispatcher"
        );

        final Consumer<Event<String>> consumer = new Consumer<Event<String>>() {
            @Override
            public void accept(final Event<String> event) {
                range(0, 100000).forEach(i -> event.getData().contains("Worl"));
                counter.incrementAndGet();
            }
        };
        range(0, 100).forEach(i -> {
            testDispatcher(workQueueDispatcher, event, consumer);
            testDispatcher(threadPoolExecutorDispatcher, event, consumer);
            testDispatcher(ringBufferDispatcher, event, consumer);
            System.out.println();
        });
    }

    private static void testDispatcher(final Dispatcher dispatcher,
                                       final Event<String> event,
                                       final Consumer<Event<String>> consumer){
        StopWatch sw = new StopWatch();
        sw.start();
        range(0, EVENT_COUNT).forEach(i -> dispatcher.dispatch(event, consumer, null));
        sw.stop();
        System.out.println(dispatcher.getClass().getSimpleName() + ":" + sw.getTime());
    }

}
