package org.inanme.integration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@ComponentScan("org.inanme.integration")    //@Component
@IntegrationComponentScan("org.inanme.integration")    //@MessagingGateway
@EnableIntegration
@Import({MongoDBConfiguration.class, WebServiceConfiguration.class})
public class InfrastructureConfiguration {

    @Bean
    public MessageChannel requestChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel invocationChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel responseChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public MessageChannel storeChannel() {
        return new DirectChannel();
    }
}
