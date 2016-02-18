package org.inanme.spring.ws.endpoint;

import org.inanme.spring.ws.service.CourseService;
import org.inanme.spring.ws.service.exception.CourseNotFoundException;
import org.inanme.spring.ws.types.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.util.Optional;

@Endpoint
public class CourseEndpoint {

    private static final String NAMESPACE = "http://inanme.org/courses";

    @Autowired
    private CourseService service;

    @PayloadRoot(localPart = "getCourseRequest", namespace = NAMESPACE)
    public
    @ResponsePayload
    GetCourseResponse getCourse(
        @RequestPayload
        GetCourseRequest request) {
        Optional<Course> courseOptional = service.getCourse(request.getCourseId());

        Course course = courseOptional
            .orElseThrow(() -> new CourseNotFoundException("course [" + request.getCourseId() + "] does not exist"));

        GetCourseResponse response = new GetCourseResponse();
        response.setCourseId(course.getCourseId());
        response.setDescription(course.getDescription());
        response.setName(course.getName());
        response.setSubscriptors(course.getSubscriptors());

        return response;
    }

    @PayloadRoot(localPart = "getCourseListRequest", namespace = NAMESPACE)
    public
    @ResponsePayload
    GetCourseListResponse getCourseList(
        @RequestPayload
        GetCourseListRequest request) {
        GetCourseListResponse response = new GetCourseListResponse();
        service.getCourses().forEach((courseId, course) -> response.getCourse().add(course));
        return response;
    }
}
