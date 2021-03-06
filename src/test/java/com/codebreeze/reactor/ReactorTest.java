package com.codebreeze.reactor;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.commons.lang3.tuple.MutablePair;
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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import static java.util.stream.IntStream.range;

public class ReactorTest {
    private static final int BACKLOG = 2048 * 8;
    private static final int EVENT_COUNT = 2000 * 8;

    @Test
    public void testStuff() throws InterruptedException {
        final Event<MutablePair<String, CountDownLatch>> event = Event.wrap(MutablePair.of("Hello World!", new CountDownLatch(EVENT_COUNT)));
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

        final Consumer<Event<MutablePair<String, CountDownLatch>>> consumer = anEvent -> {
            counter.incrementAndGet();
            anEvent.getData().getRight().countDown();
        };

        range(0, 10).forEach(i -> {
                    try {
                        System.out.println("run " + i);
                        testDispatcher(workQueueDispatcher, event, consumer);
                        testDispatcher(threadPoolExecutorDispatcher, event, consumer);
                        testDispatcher(ringBufferDispatcher, event, consumer);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    private static void testDispatcher(final Dispatcher dispatcher,
                                       final Event<MutablePair<String, CountDownLatch>> event,
                                       final Consumer<Event<MutablePair<String, CountDownLatch>>> consumer) throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(EVENT_COUNT);
        event.getData().setRight(latch);
        final StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        range(0, EVENT_COUNT).forEach(j -> dispatcher.dispatch(event, consumer, null));
        latch.await();
        stopWatch.stop();
        final double throughput = Double.valueOf(1000l * EVENT_COUNT) / stopWatch.getTime();
        System.out.println(dispatcher.getClass().getSimpleName() + " throughput :" + throughput);
    }

}
