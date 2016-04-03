package org.inanme.integration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.integration.annotation.IntegrationComponentScan;
import org.springframework.integration.config.EnableIntegration;


@Configuration
@ComponentScan("org.inanme")    //@Component
@IntegrationComponentScan("org.inanme")    //@MessagingGateway
@EnableIntegration
@ImportResource("classpath:org/inanme/it.xml")
class TestConfig {

}
