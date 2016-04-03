package org.inanme.integration1;

import org.inanme.integration1.IntegrationModule.BookingService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.concurrent.TimeUnit;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:org/inanme/it.xml")
public class IntegrationModuleTest {

    @Autowired
    @Qualifier("bookingConfirmationRequests")
    private MessageChannel messageChannel;

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
}
