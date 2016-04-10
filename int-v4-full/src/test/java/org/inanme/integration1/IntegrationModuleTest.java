package org.inanme.integration1;

import org.inanme.integration1.IntegrationModule.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationModule.class, loader = AnnotationConfigContextLoader.class)
public class IntegrationModuleTest {

    private Random random = new Random(System.currentTimeMillis());

    @Autowired
    @Qualifier("bookingConfirmationRequests")
    private MessageChannel messageChannel;

    @Autowired
    @Qualifier("number-stream-1")
    private MessageChannel numberStream1;

    @Autowired
    @Qualifier("number-stream-2")
    private MessageChannel numberStream2;

    @Autowired
    private BookingService bookingService;

    @Autowired
    private SomeProducer someProducer;

    @Test
    public void testWithChannel() throws InterruptedException {
        messageChannel.send(MessageBuilder.withPayload(2).build());
    }

    @Test
    public void testWithGateWay() throws InterruptedException {
        bookingService.book(3);
    }

    private void sleep() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(10) * 100l);
        } catch (InterruptedException e) {

        }
    }

    @Test
    public void aggregator() {
        int max = 10;
        IntStream.rangeClosed(0, max).forEach(it -> {
            sleep();
            numberStream1.send(MessageBuilder.withPayload(it).setCorrelationId(it).build());
            sleep();
            numberStream1.send(MessageBuilder.withPayload(max - it).setCorrelationId(max - it).build());
        });
    }

    @Test
    public void resequencer() {
        int max = 10;
        IntStream.rangeClosed(0, max).forEach(it -> {
            numberStream2.send(MessageBuilder.withPayload(max - it)
                    .setCorrelationId("X")
                    .setSequenceNumber(max - it)
                    .setSequenceSize(11)
                    .build());
            sleep();
        });
    }

    @Test
    public synchronized void producerConsumer() throws InterruptedException {
        someProducer.produce();
        wait();
    }
}
