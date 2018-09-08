package com.example.zub.fuckgod;

public class SearchUsers {

    public String firstname, lastname, userId;

    public SearchUsers(){

    }

    public SearchUsers(String firstname, String lastname, String userId) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.userId = userId;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
