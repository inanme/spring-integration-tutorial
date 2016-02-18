package org.inanme.integration.domain;

public class Employee {

    public String phone;

    public String name;

    public String address;

    @Override
    public String toString() {
        return "Employee{" +
               "address='" + address + '\'' +
               ", phone='" + phone + '\'' +
               ", name='" + name + '\'' +
               '}';
    }
}
