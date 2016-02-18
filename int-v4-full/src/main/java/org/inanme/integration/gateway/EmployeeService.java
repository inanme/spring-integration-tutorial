package org.inanme.integration.gateway;

import org.inanme.integration.Channels;
import org.inanme.integration.domain.Employee;
import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface EmployeeService {

    @Gateway(requestChannel = Channels.SEND_REQUEST)
    void process(Employee emp);
}
