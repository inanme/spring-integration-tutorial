package org.inanme.integration.endpoint;

import org.inanme.integration.Channels;
import org.inanme.integration.domain.Employee;
import org.inanme.spring.ws.types.GetCourseRequest;
import org.inanme.spring.ws.types.GetCourseResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Filter;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.annotation.Transformer;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

import static org.inanme.integration.Channels.*;

@Component
public class CourseRequestBuilder {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transformer(inputChannel = REQUEST_CHANNEL, outputChannel = INVOCATION_CHANNEL)
    public GetCourseRequest buildRequest(Message<String> msg) {
        logger.info("Building request for course [{}]", msg.getPayload());
        GetCourseRequest request = new GetCourseRequest();
        request.setCourseId(msg.getPayload());
        return request;
    }

    @Filter(inputChannel = RESPONSE_CHANNEL, outputChannel = STORE_CHANNEL)
    public boolean filterCourse(Message<GetCourseResponse> msg) {
        if (!msg.getPayload().getCourseId().startsWith("BC-")) {
            logger.info("Course [{}] filtered. Not a BF course", msg.getPayload().getCourseId());
            return false;
        }

        logger.info("Course [{}] validated. Storing to database", msg.getPayload().getCourseId());
        return true;
    }

    @ServiceActivator(inputChannel = RESPONSE_CHANNEL)
    public String getResponse(Message<GetCourseResponse> msg) {
        GetCourseResponse course = msg.getPayload();
        logger.info("Course with ID [{}] received: {}", course.getCourseId(), course.getName());
        return course.getName();
    }

    @ServiceActivator(inputChannel = Channels.SEND_REQUEST)
    public void processMessage(Message<Employee> message) {
        Employee employee = message.getPayload();
        System.out.println("Message Received:" + employee);
    }
}
