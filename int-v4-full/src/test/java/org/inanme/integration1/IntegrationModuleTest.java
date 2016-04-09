package org.inanme.integration1;

import org.inanme.integration1.IntegrationModule.BookingService;
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
    @Qualifier("number.stream")
    private MessageChannel numberStream;

    @Autowired
    private BookingService bookingService;

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
    public void aggregatorExample() {
        int max = 10;
        IntStream.rangeClosed(0, max).forEach(it -> {
            sleep();
            numberStream.send(MessageBuilder.withPayload(it).setCorrelationId(it).build());
            sleep();
            numberStream.send(MessageBuilder.withPayload(max - it).setCorrelationId(max - it).build());
        });
    }
}
