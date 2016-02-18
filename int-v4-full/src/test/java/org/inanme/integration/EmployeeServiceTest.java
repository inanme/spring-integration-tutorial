package org.inanme.integration;

import org.inanme.integration.domain.Employee;
import org.inanme.integration.gateway.EmployeeService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class EmployeeServiceTest {

    @Autowired
    private EmployeeService request;

    @Test
    public void testIntegration() {
        Employee emp = new Employee();
        emp.name = "mert";
        request.process(emp);
    }
}
