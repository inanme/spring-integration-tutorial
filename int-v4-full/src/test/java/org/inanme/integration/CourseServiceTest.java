package org.inanme.integration;

import org.inanme.integration.configuration.InfrastructureConfiguration;
import org.inanme.integration.gateway.CourseService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {InfrastructureConfiguration.class})
public class CourseServiceTest {

    @Autowired
    private CourseService service;

    @Test
    public void testFlow() {
//        String courseName1 = service.findCourse("BC-45");
//        assertNotNull(courseName1);
//        assertEquals("Introduction to Java", courseName1);

        String courseName2 = service.findCourse("DF-21");
        assertNotNull(courseName2);
        assertEquals("Functional Programming Principles in Scala", courseName2);
    }
}
