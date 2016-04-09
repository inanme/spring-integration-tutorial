package org.inanme.integration1;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.*;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.integration.IntegrationMessageHeaderAccessor.*;

@Configuration
@ImportResource("classpath:org/inanme/it.xml")
public class IntegrationModule {
    private static final Logger logger = Logger.getLogger(IntegrationModule.class);

    interface BookingService {
        void book(Integer message);
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
        @ServiceActivator(inputChannel = "email")
        public void m1(@Payload Integer email) {
            logger.debug("Email : " + email);
        }
    }

    @Component
    public static class SMSConfirmationService {
        @ServiceActivator(inputChannel = "sms")
        public void m1(@Payload Integer email) {
            logger.debug("SMS : " + email);
        }
    }

    @Component
    public static class PhoneConfirmationService {
        @ServiceActivator(inputChannel = "phone")
        public void m1(@Payload Integer email) {
            logger.debug("Phone : " + email);
        }
    }

    @Component
    public static class Error {
        @ServiceActivator(inputChannel = "error-channel")
        public void error(Message<Integer> message) {
            logger.debug("error:" + message.getPayload());
        }
    }

    @Component("out")
    public static class Out {
        @ServiceActivator(inputChannel = "out-channel")
        public void out(Message<Integer> message) {
            logger.debug("out:" + message.getPayload());
        }
    }

    @Component("myAggregator")
    public static class MyAggregator {

        @CorrelationStrategy
        public String correlateBy(
                @Header(CORRELATION_ID)
                        String correlationId) {
            return correlationId;
        }

        public boolean canRelease1(List<Message<Integer>> messages) {
            List<Integer> payload = messages.stream().map(Message::getPayload).collect(Collectors.toList());
            logger.debug("release:" + payload);
            return messages.size() == 2;
        }

        @ReleaseStrategy
        public boolean canRelease(
                @Payloads
                        List<Integer> payload) {
            logger.debug("canRelease:" + payload);
            return payload.size() == 2;
        }

        @Aggregator
        public int aggreate(
                @Payloads
                        List<Integer> payload) {
            logger.debug("aggreate:" + payload);
            return payload.stream().mapToInt(Integer::intValue).sum();
        }
    }
}


