package com.example.zub.fuckgod;

public class PassInfos {

    String id;
    String firstname;
    String lastname;
    String email;
    String password;
    String name;
    String userId;


    PassInfos()
    {

    }

    public PassInfos(String id, String firstname, String lastname, String email, String password,String name, String userId ) {
        this.id = id;
        this.firstname = firstname;
        this.lastname = lastname;
        this.email = email;
        this.password = password;

        this.name = name;
        this.userId =userId;
    }

    public String getId() {
        return id;
    }

    public String getFirstname() {
        return firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getUserId() {
        return userId;
    }
}
