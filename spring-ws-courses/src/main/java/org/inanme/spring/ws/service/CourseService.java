package org.inanme.spring.ws.service;

import org.inanme.spring.ws.types.Course;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public interface CourseService {

    Map<String, Course> getCourses();

    Optional<Course> getCourse(String courseId);
}

@Component
class CourseServiceImpl implements CourseService {

    Course course1 = new Course();

    {
        course1.setCourseId("BC-45");
        course1.setDescription("An introduction to Java");
        course1.setName("Introduction to Java");
        course1.setSubscriptors(new BigInteger("25"));
    }

    Course course2 = new Course();

    {
        course2.setCourseId("DF-21");
        course2.setDescription("Learn about functional programming");
        course2.setName("Functional Programming Principles in Scala");
        course2.setSubscriptors(new BigInteger("12"));
    }

    private final List<Course> courses = Arrays.asList(course1, course2);

    private final Map<String, Course> courseDb =
        courses.stream().collect(Collectors.toMap(Course::getCourseId, Function.identity()));

    @Override
    public Map<String, Course> getCourses() {
        return courseDb;
    }

    @Override
    public Optional<Course> getCourse(String courseId) {
        if (course2.getCourseId().equals(courseId)) {
            try {
                Thread.sleep(4000); //added sleep in order to test client timeout
            } catch (InterruptedException e) {
            }
        }
        return Optional.ofNullable(courseDb.get(courseId));
    }
}
