package com.example.pcproject;

public class Post {
    private String nume,puls;

    public Post() {
    }

    public Post(String nume, String puls) {
        this.nume = nume;
        this.puls = puls;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public String getPuls() {
        return puls;
    }

    public void setPuls(String puls) {
        this.puls = puls;
    }
}
