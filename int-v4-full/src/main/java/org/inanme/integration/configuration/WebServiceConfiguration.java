package org.inanme.integration.configuration;

import org.inanme.integration.Channels;
import org.inanme.spring.ws.types.Course;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.ws.MarshallingWebServiceOutboundGateway;
import org.springframework.messaging.MessageHandler;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

@Configuration
public class WebServiceConfiguration {

    @Bean
    @ServiceActivator(inputChannel = Channels.INVOCATION_CHANNEL)
    public MessageHandler wsOutboundGateway() {
        MarshallingWebServiceOutboundGateway gw =
            new MarshallingWebServiceOutboundGateway("http://localhost:8080/courses", jaxb2Marshaller());
        gw.setOutputChannelName(Channels.RESPONSE_CHANNEL);
        return gw;
    }

    @Bean
    public Jaxb2Marshaller jaxb2Marshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath(Course.class.getPackage().getName());
        return marshaller;
    }
}
