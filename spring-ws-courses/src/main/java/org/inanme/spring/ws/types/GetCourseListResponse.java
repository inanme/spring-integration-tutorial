package org.inanme.spring.ws.types;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {"course"})
@XmlRootElement(name = "getCourseListResponse")
public class GetCourseListResponse {

    @XmlElement(required = true)
    protected List<Course> course;


    public List<Course> getCourse() {
        if (course == null) {
            course = new ArrayList<Course>();
        }
        return this.course;
    }
}
