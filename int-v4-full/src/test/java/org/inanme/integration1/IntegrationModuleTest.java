package org.inanme.integration1;

import org.inanme.integration1.IntegrationModule.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.function.Function;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.is;
import static org.inanme.integration1.IntegrationModuleSupport.randomSleep;
import static org.inanme.integration1.IntegrationModuleSupport.sleep;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = IntegrationModule.class, loader = AnnotationConfigWebContextLoader.class)
@WebAppConfiguration
public class IntegrationModuleTest {

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    @Qualifier("booking")
    private MessageChannel messageChannel;

    @Test
    public void testWithChannel() throws InterruptedException {
        messageChannel.send(MessageBuilder.withPayload(2).build());
    }

    @Autowired
    private BookingService bookingService;

    @Test
    public void testWithGateWay() throws InterruptedException {
        bookingService.book(3);
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    @Qualifier("aggregator-stream")
    private MessageChannel aggregatorStream;

    @Test
    public void aggregator() {
        int max = 4;
        IntStream.rangeClosed(0, max).forEach(it -> {
            randomSleep();
            aggregatorStream.send(MessageBuilder.withPayload(it).setCorrelationId(it).build());
            randomSleep();
            aggregatorStream.send(MessageBuilder.withPayload(max - it).setCorrelationId(max - it).build());
        });
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    @Qualifier("resequencer-stream")
    private MessageChannel resequencerStream;

    @Test
    public void resequencer() {
        int max = 10;
        IntStream.rangeClosed(0, max).forEach(it -> {
            resequencerStream.send(MessageBuilder.withPayload(max - it)
                    .setCorrelationId("X")
                    .setSequenceNumber(max - it)
                    .setSequenceSize(11)
                    .build());
            randomSleep();
        });
        sleep();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    private ProducerConsumer producerConsumer;

    @Test
    public synchronized void producerConsumer() throws InterruptedException {
        producerConsumer.produce();
        wait();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    private Add add;

    @Test
    public void add() throws ExecutionException, InterruptedException {
        Future<Integer> add = this.add.add(1);
        assertThat(add.get(), is(3));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    @Qualifier("scatter-gather")
    private Function<Double, Double> scatterGather;

    @Test
    public void scatterGather() {
        Double apply = scatterGather.apply(100d);
        assertThat(apply, is(3d));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext wac;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void shouldReceiveMessage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/service").
                header("param", "1")).
                andExpect(status().isOk()).andExpect(content().string("hello"));
    }
    //////////////////////////////////////////////////////////////////////////////////////////////////////
}
