package org.inanme.integration1;

import com.google.common.collect.ImmutableMap;
import org.apache.log4j.Logger;
import org.inanme.integration1.IntegrationModule.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.MessageRejectedException;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.integration.support.json.JsonObjectMapper;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.AnnotationConfigWebContextLoader;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Map;
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
    private final Logger logger = Logger.getLogger(getClass());

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
    @Qualifier("buffered-channel-with-queue-size-1")
    private MessageChannel bufferedChannelWithQueueSize1;

    @Test
    public void testBufferedChannelWithQueueSize1() throws InterruptedException {
        boolean message1 = bufferedChannelWithQueueSize1.send(MessageBuilder.withPayload("message1").build());
        assertThat(message1, is(true));
        logger.debug("sent message1");
        boolean message2 = bufferedChannelWithQueueSize1.send(MessageBuilder.withPayload("message1").build());
        assertThat(message2, is(true));
        logger.debug("sent message2");
        boolean message3 = bufferedChannelWithQueueSize1.send(MessageBuilder.withPayload("message3").build(), 500l);
        sleep();//wait until message2 gets progressed
        sleep();//wait until message2 gets progressed
        assertThat(message3, is(false));
        logger.debug("sent message3");
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
        assertThat(apply, is(4d));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    private Save save;

    @Test
    public void testSaveVoidReturn() {
        save.save(10);
        logger.debug("save called");
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    @Qualifier("claim")
    private Function<Map<String, String>, String> claim;

    @Test
    public void claim() {
        Map<String, String> of = ImmutableMap.of("name", "x", "surname", "y");
        assertThat(claim.apply(of), is("x and y"));
    }

    //////////////////////////////////////////////////////////////////////////////////////////////////////
    @Autowired
    private MyChain myChain;

    @Autowired
    private JsonObjectMapper jsonObjectMapper;

    @Autowired
    private Jaxb2Marshaller jaxb2Marshaller;

    @Test
    public void myChainTest() throws Exception {
        StringWriter out = new StringWriter();
        jaxb2Marshaller.marshal(new Data(6), new StreamResult(out));
        assertThat(myChain.call(2), is(out.toString()));
    }

    @Test(expected = MessageRejectedException.class)
    public void myChainTestFiltered() throws Exception {
        assertThat(myChain.call(1), is(2));
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
