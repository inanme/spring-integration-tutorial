package org.inanme.integration1;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.config.ConsumerEndpointFactoryBean;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

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

    @Component("monitor")
    public static class Monitor {

        public void m1(Message<Integer> message) {
            logger.debug(message.getPayload());
        }
    }

}


