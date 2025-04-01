package com.example.javafxprojrct;

public class Ausleihe {
    Benutzer benutzer;
    Buch buch;

    public Ausleihe(Benutzer benutzer, Buch buch) {
        this.benutzer = benutzer;
        this.buch = buch;
    }

    public Benutzer getBenutzer() {
        return benutzer;
    }

    public void setBenutzer(Benutzer benutzer) {
        this.benutzer = benutzer;
    }

    public Buch getBuch() {
        return buch;
    }

    public void setBuch(Buch buch) {
        this.buch = buch;
    }

    @Override
    public String toString() {
        return "Ausleihe{" +
                "benutzer=" + benutzer +
                ", buch=" + buch +
                '}';
    }
}
