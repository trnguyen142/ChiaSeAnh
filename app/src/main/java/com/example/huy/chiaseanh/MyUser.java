package com.example.huy.chiaseanh;

import java.io.Serializable;

public class MyUser implements Serializable {
    private String email;
    private String name;

    public MyUser() {

    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public String getName()
    {
        return name;
    }
}
