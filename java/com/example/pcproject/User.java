package com.example.pcproject;

public class User {
    String first_name, last_name, email, password;

    public void setFirst_name(String first_name){
        this.first_name=first_name;
    }

    public String getFirst_name(){
        return first_name;
    }

    public void setLast_name(String last_name){
        this.last_name=last_name;
    }

    public String getLast_name(){
        return last_name;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public String getEmail(){
        return email;
    }

    public void setPassword(String password){
        this.password=password;
    }

    public String getPassword(){
        return password;
    }
}
