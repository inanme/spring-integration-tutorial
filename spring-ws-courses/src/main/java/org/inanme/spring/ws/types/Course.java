//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, v2.2.4-2 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2014.04.24 at 04:39:37 PM CEST 
//


package org.inanme.spring.ws.types;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "course")
public class Course {

    @XmlAttribute(name = "courseId", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "NCName")
    protected String courseId;

    @XmlAttribute(name = "description", required = true)
    protected String description;

    @XmlAttribute(name = "name", required = true)
    protected String name;

    @XmlAttribute(name = "subscriptors", required = true)
    protected BigInteger subscriptors;

    /**
     * Gets the value of the courseId property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getCourseId() {
        return courseId;
    }

    /**
     * Sets the value of the courseId property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setCourseId(String value) {
        this.courseId = value;
    }

    /**
     * Gets the value of the description property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setDescription(String value) {
        this.description = value;
    }

    /**
     * Gets the value of the name property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the subscriptors property.
     *
     * @return possible object is
     * {@link BigInteger }
     */
    public BigInteger getSubscriptors() {
        return subscriptors;
    }

    /**
     * Sets the value of the subscriptors property.
     *
     * @param value allowed object is
     *              {@link BigInteger }
     */
    public void setSubscriptors(BigInteger value) {
        this.subscriptors = value;
    }
}
