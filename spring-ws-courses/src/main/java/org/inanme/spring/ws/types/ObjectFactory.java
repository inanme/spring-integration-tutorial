package org.inanme.spring.ws.types;

import javax.xml.bind.annotation.XmlRegistry;

@XmlRegistry
public class ObjectFactory {

    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Course }
     */
    public Course createCourse() {
        return new Course();
    }

    /**
     * Create an instance of {@link GetCourseListResponse }
     */
    public GetCourseListResponse createGetCourseListResponse() {
        return new GetCourseListResponse();
    }

    /**
     * Create an instance of {@link GetCourseListRequest }
     */
    public GetCourseListRequest createGetCourseListRequest() {
        return new GetCourseListRequest();
    }

    /**
     * Create an instance of {@link GetCourseResponse }
     */
    public GetCourseResponse createGetCourseResponse() {
        return new GetCourseResponse();
    }

    /**
     * Create an instance of {@link GetCourseRequest }
     */
    public GetCourseRequest createGetCourseRequest() {
        return new GetCourseRequest();
    }
}
