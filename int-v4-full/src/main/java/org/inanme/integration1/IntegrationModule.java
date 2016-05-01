package org.inanme.integration1;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.stereotype.Component;

import javax.xml.bind.annotation.XmlRootElement;
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

    @Bean
    public Jackson2JsonObjectMapper mapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return new Jackson2JsonObjectMapper(mapper);
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(Data.class);
        return marshaller;
    }

    @MessagingGateway
    interface Add {
        @Gateway(requestChannel = "add-channel")
        Future<Integer> add(Integer val1);
    }

    @MessagingGateway
    interface BookingService {
        @Gateway(requestChannel = "booking")
        void book(Integer message);
    }

    @MessagingGateway
    interface MyChain {
        @Gateway(requestChannel = "my-chain")
        String call(Integer val1);
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

    @XmlRootElement
    public static class Data {
        Integer integer;

        public Data(Integer integer) {
            this.integer = integer;
        }

        public Data() {
        }

        public Integer getInteger() {
            return integer;
        }

        public void setInteger(Integer integer) {
            this.integer = integer;
        }
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

        @ServiceActivator(inputChannel = "buffered-channel-with-queue-size-1")
        public void queueConsumer(@Payload String message) {
            sleep();
            logger.debug("queueConsumer : " + message);
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
