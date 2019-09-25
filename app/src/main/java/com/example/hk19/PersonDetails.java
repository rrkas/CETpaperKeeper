package com.example.hk19;

public class PersonDetails {
    private String personName;
    private int personNumber;
    public PersonDetails() {
    }
    public PersonDetails(String personName, int personNumber) {
        this.personName = personName;
        this.personNumber = personNumber;
    }
    public String getPersonName() {
        return personName;
    }
    public void setPersonName(String personName) {
        this.personName = personName;
    }
    public int getPersonNumber() {
        return personNumber;
    }
    public void setPersonNumber(int personNumber) {
        this.personNumber = personNumber;
    }
}
