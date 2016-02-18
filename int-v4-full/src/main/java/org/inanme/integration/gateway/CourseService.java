package org.inanme.integration.gateway;

import org.inanme.integration.Channels;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway(defaultRequestChannel = Channels.REQUEST_CHANNEL)
public interface CourseService {
    String findCourse(String courseId);
}
