package org.inanme.integration1;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.springframework.integration.IntegrationMessageHeaderAccessor.*;

@Configuration
@ImportResource("classpath:org/inanme/it.xml")
public class IntegrationModule {

    interface BookingService {
        void book(Integer message);
    }

    @Bean(name = "some-channel2")
    public MessageChannel someChannel() {
        int x = 2;
        switch (x) {
            case 1:
                return new QueueChannel();
            case 2:
                return new QueueChannel(1);
            case 3:
                return new PriorityChannel();
            case 4:
                return new RendezvousChannel();
            case 5:
                return new ExecutorChannel(Executors.newCachedThreadPool());
            case 9:
                return new PublishSubscribeChannel();
            case 10:
                return new PublishSubscribeChannel(Executors.newCachedThreadPool());
            default:
                return new DirectChannel();
        }
    }

    @Component
    public static class SomeProducer {
        private final Logger logger = Logger.getLogger(getClass());

        @Autowired
        @Qualifier("some-channel")
        private MessageChannel channel;

        public void produce() {
            IntStream.range(0, 5).boxed().forEach(item -> {
                logger.debug(item);
                channel.send(MessageBuilder.withPayload(item).build());
            });
        }
    }

    @Component
    public static class SomeConsumer1 implements Consumer {
        private final Logger logger = Logger.getLogger(getClass());

        @Override
        @ServiceActivator(inputChannel = "some-channel")
        public void accept(@Payload Object item) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug(item);
        }
    }

    @Component
    public static class SomeConsumer2 implements Consumer {
        private final Logger logger = Logger.getLogger(getClass());

        @Override
        @ServiceActivator(inputChannel = "some-channel")
        public void accept(@Payload Object item) {
            try {
                TimeUnit.MILLISECONDS.sleep(1000l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            logger.debug(item);
        }
    }

    @Component
    public static class BillForBookingService {
        @ServiceActivator(inputChannel = "bookingConfirmationRequests", outputChannel = "chargedBookings")
        public Message<Integer> m1(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }
    }

    @Component("seatAvailabilityService")
    public static class SeatAvailabilityService {
        @ServiceActivator(inputChannel = "chargedBookings", outputChannel = "seatNotifications")
        public Message<Integer> m1(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }
    }

    @Component
    public static class EmailConfirmationService {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "email")
        public void m1(@Payload Integer email) {
            logger.debug("Email : " + email);
        }
    }

    @Component
    public static class SMSConfirmationService {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "sms")
        public void m1(@Payload Integer email) {
            logger.debug("SMS : " + email);
        }
    }

    @Component
    public static class PhoneConfirmationService {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "phone")
        public void m1(@Payload Integer email) {
            logger.debug("Phone : " + email);
        }
    }

    @Component
    public static class Error {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "error-channel")
        public void error(Message<Integer> message) {
            logger.debug("error:" + message.getPayload());
        }
    }

    @Component("out")
    public static class Out {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "out-channel")
        public void out(Message<Integer> message) {
            logger.debug("out:" + message.getPayload());
        }
    }

    @Component("myAggregator")
    public static class MyAggregator {
        private final Logger logger = Logger.getLogger(getClass());

        @CorrelationStrategy
        public String correlateBy(@Header(CORRELATION_ID) String correlationId) {
            return correlationId;
        }

        public boolean canRelease1(List<Message<Integer>> messages) {
            List<Integer> payload = messages.stream().map(Message::getPayload).collect(Collectors.toList());
            logger.debug("release:" + payload);
            return messages.size() == 2;
        }

        @ReleaseStrategy
        public boolean canRelease(@Payloads List<Integer> payload) {
            logger.debug("canRelease:" + payload);
            return payload.size() == 2;
        }

        @Aggregator
        public int aggreate(@Payloads List<Integer> payload) {
            logger.debug("aggreate:" + payload);
            return payload.stream().mapToInt(Integer::intValue).sum();
        }
    }
}


