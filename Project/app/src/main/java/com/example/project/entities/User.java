package com.example.project.entities;

public class User {
    public String id;
    public String name;
    public String username;
    public String password;
    public String email;

    public User(String id, String name, String username, String password, String email) {
        this.id = id;
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
    }
}
