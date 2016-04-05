package org.inanme.integration1;

import org.apache.log4j.Logger;
import org.springframework.integration.annotation.Aggregator;
import org.springframework.integration.annotation.CorrelationStrategy;
import org.springframework.integration.annotation.Header;
import org.springframework.integration.annotation.ReleaseStrategy;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

public class IntegrationModule {
    private static final Logger logger = Logger.getLogger(IntegrationModule.class);

    interface BookingService {
        void book(Integer message);
    }

    @Component("billForBookingService")
    public static class BillForBookingService {
        public Message<Integer> m1(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }
    }

    @Component("seatAvailabilityService")
    public static class SeatAvailabilityService {
        public Message<Integer> m1(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }
    }

    @Component("emailConfirmationService")
    public static class EmailConfirmationService {

        public void m1(Message<Integer> message) {
            logger.debug(message.getPayload());
        }
    }

    @Component("error")
    public static class Error {

        public void error(Message<Integer> message) {
            logger.debug("error:" + message.getPayload());
        }
    }

    @Component("out")
    public static class Out {

        public void out(Message<Integer> message) {
            logger.debug("out:" + message.getPayload());
        }
    }

    @Component("myAggregator")
    public static class MyAggregator {

        @CorrelationStrategy
        public String correlateBy(
            @Header("CORRELATION_ID")
                String correlationId) {
            return correlationId;
        }

        @ReleaseStrategy
        public boolean release(List<Message<Integer>> messages) {
            List<Integer> payload = messages.stream().map(Message::getPayload).collect(Collectors.toList());
            logger.debug("release:" + payload);
            return messages.size() == 2;
        }

        @Aggregator
        public int aggreate(List<Message<Integer>> messages) {
            List<Integer> payload = messages.stream().map(Message::getPayload).collect(Collectors.toList());
            logger.debug("aggreate:" + payload);
            return payload.stream().mapToInt(Integer::intValue).sum();
        }
    }
}


