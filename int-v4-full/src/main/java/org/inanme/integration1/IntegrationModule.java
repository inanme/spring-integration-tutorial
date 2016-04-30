package org.inanme.integration1;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.*;
import org.springframework.integration.channel.*;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.IntStream;

import static org.inanme.integration1.IntegrationModuleSupport.sleep;
import static org.springframework.integration.IntegrationMessageHeaderAccessor.*;

@Configuration
@ImportResource("classpath:org/inanme/it.xml")
@EnableIntegration
@ComponentScan
@IntegrationComponentScan
public class IntegrationModule {

    @MessagingGateway
    interface Add {
        @Gateway(requestChannel = "add-channel", replyChannel = "add-reply-channel")
        Future<Integer> add(Integer val1);
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
    public static class ProducerConsumer {
        private final Logger logger = Logger.getLogger(getClass());
        private static final String CHANNEL = "produce-consume-jms-channel";

        @Autowired
        @Qualifier(CHANNEL)
        private MessageChannel channel;

        public void produce() {
            IntStream.range(0, 10).boxed().forEach(item -> {
                channel.send(MessageBuilder.withPayload(item).build());
                logger.debug("Produced " + item);
            });
        }

        @ServiceActivator(inputChannel = CHANNEL)
        public void consume1(@Payload Object item) {
            sleep();
            logger.debug("consumer1 : " + item);
        }

        @ServiceActivator(inputChannel = CHANNEL)
        public void consume2(@Payload Object item) {
            sleep();
            logger.debug("consumer2 : " + item);
        }

        @ServiceActivator(inputChannel = CHANNEL)
        public void consume3(@Payload Object item) {
            sleep();
            logger.debug("consumer3 : " + item);
        }
    }

    @MessagingGateway
    interface BookingService {
        @Gateway(requestChannel = "booking")
        void book(Integer message);
    }

    @Component
    public static class ServiceActivators {

        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "booking", outputChannel = "charging")
        public Message<Integer> bill(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }

        @ServiceActivator(inputChannel = "charging", outputChannel = "notification")
        public Message<Integer> charge(Message<Integer> message) {
            return MessageBuilder.withPayload(message.getPayload() + 1).copyHeaders(message.getHeaders()).build();
        }

        @ServiceActivator(inputChannel = "sms")
        public void sms(@Payload Integer payload) {
            logger.debug("SMS : " + payload);
        }

        @ServiceActivator(inputChannel = "phone")
        public void phone(@Payload Integer payload) {
            logger.debug("Phone : " + payload);
        }

        @ServiceActivator(inputChannel = "email")
        public void email(@Payload Integer payload) {
            logger.debug("Email : " + payload);
        }

        @ServiceActivator(inputChannel = "http-inbound-get-1")
        public String hello() {
            return "hello";
        }

        @ServiceActivator(inputChannel = "add-channel")
        public Integer add(@Payload Integer val1) {
            sleep();
            return val1 + 2;
        }
    }

    @Component
    public static class MyWireTab {
        private final Logger logger = Logger.getLogger(getClass());

        @ServiceActivator(inputChannel = "my-wire-tab")
        public void m1(@Payload Integer payload) {
            logger.debug("my-wire-tab : " + payload);
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
