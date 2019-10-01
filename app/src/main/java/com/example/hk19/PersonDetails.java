package com.example.hk19;

public class PersonDetails {
    private String personName;
    private String personNumber;
    public PersonDetails() {
    }
    public PersonDetails(String personName, String personNumber) {
        this.personName = personName;
        this.personNumber = personNumber;
    }
    public String getPersonName() {
        return personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    public String getPersonNumber() {
        return personNumber;
    }
    public void setPersonNumber(String personNumber) {
        this.personNumber = personNumber;
    }
}
