package com.example.javafxprojrct;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginController {

    BibliothekDatenbankImpl bibliothekDatenbankImpl = new BibliothekDatenbankImpl();

    @FXML
    private TextField nameFeld;

    @FXML
    private TextField passwordFeld;

    @FXML
    private Label loginFehler;

    @FXML
    public void goToBibliothek(String name, String password) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("buecherei.fxml"));
        Parent root = loader.load();

        BibliothekController bibliothekController = loader.getController();

        bibliothekController.getData(name, password);

        Stage stage = (Stage) this.nameFeld.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }

    @FXML
    public void submit() throws IOException {
        String name = nameFeld.getText();
        String password = passwordFeld.getText();

        nameFeld.setText("");
        passwordFeld.setText("");

        if (password.contains(" ")) {
            loginFehler.setText("Das Passwort darf keine Leerzeichen enthalten!");
            return;
        }

        if (!name.isBlank() && !password.isBlank()) {
            if (bibliothekDatenbankImpl.getBenutzerBeiName(name) == null) {
                if (bibliothekDatenbankImpl.checkPassword(name, password) == null) {
                    goToBibliothek(name, password);
                } else {
                    loginFehler.setText("Passwort.");
                }
            } else {
                loginFehler.setText("Benutzer mit name " + name + " ist bereits registriert.");
            }
        } else {
            loginFehler.setText("Benutzername und Passwort dürfen nicht leer sein!");
        }
    }

    @FXML
    public void cancel() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("eingang.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) this.loginFehler.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }
}
