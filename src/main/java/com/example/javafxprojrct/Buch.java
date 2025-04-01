package com.example.javafxprojrct;

public class Buch {
    int id;
    String name;

    public Buch(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Buch{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
