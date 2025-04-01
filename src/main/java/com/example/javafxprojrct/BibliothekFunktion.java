package com.example.javafxprojrct;

import java.util.List;

public interface BibliothekFunktion {
    boolean isBenutzerExist(String benutzerName);

    Benutzer addBenutzer(String benutzerName, String password);

    Benutzer checkPassword(String benutzerName, String password);

    Benutzer getBenutzer(int benutzerId);

    boolean isBuchAusgeliehen(Buch buch);

    void addAusgelieheneBuecher(int benutzerId, int buchId);

    boolean isAusleiheExist(int benutzerId, int buchId);

    void removeAusgelieheneBuecher(int benutzerId, int buchId);

    void addBuch(String buchName);

    Buch getBuch(int buchId);


    List<Buch> getBenutzerBuecher(int benutzerId);

    void getBuecher();

    List<Buch> getBuecherVerfuegbar();

    int getAnzahlBuecherVerfuegbar();
}
