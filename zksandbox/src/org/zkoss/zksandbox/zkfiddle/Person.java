package org.zkoss.zksandbox.zkfiddle;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: payegishemingway
 * Date: 2/19/13
 * Time: 9:48 AM
 * To change this template use File | Settings | File Templates.
 */
public class Person {
    private String name;
    private Date birthdate;

    public Person() {
        super();
    }

    public Person(String name, Date birthdate) {
        this();
        this.name = name;
        this.birthdate = birthdate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

}
