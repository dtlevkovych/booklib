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
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EingangController {
    private BibliothekDatenbankImpl bibliothekDatenbankImpl = new BibliothekDatenbankImpl();

    @FXML
    private Label eingangFehler;

    @FXML
    private TextField nameFeld;

    @FXML
    private TextField passwordFeld;

    @FXML
    private void goToBibliothek(String benutzerName, String password) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("buecherei.fxml"));
        Parent root = loader.load();

        BibliothekController bibliothekController = loader.getController();

        bibliothekController.getData(benutzerName, password);

        Stage stage = (Stage) this.nameFeld.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }

    @FXML
    private void goToBibliothekAdmin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("buecherei.fxml"));
        Parent root = loader.load();

        BibliothekController bibliothekController = loader.getController();

        bibliothekController.bibliothekAdmin();

        Stage stage = (Stage) this.nameFeld.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }

    @FXML
    public void goToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("login.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) this.eingangFehler.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }

    @FXML
    public void submit() throws IOException {
        String name = nameFeld.getText();
        String password = passwordFeld.getText();

        if (Objects.equals(name, "admin") && Objects.equals(password, "123")) {
            goToBibliothekAdmin();
            return;
        }

        nameFeld.setText("");
        passwordFeld.setText("");

        Pattern pattern = Pattern.compile("[^a-zA-Z0-9]+");
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            eingangFehler.setText("Der Benutzername darf keine Sonderzeichen enthalten!");
            return;
        }

        if (password.contains(" ")) {
            eingangFehler.setText("Das Passwort darf keine Leerzeichen enthalten!");
            return;
        }

        if (!name.isBlank() && !password.isBlank()) {
            if (bibliothekDatenbankImpl.getBenutzerBeiName(name) != null) {
                if (checkPassword(name, password) != null && checkPassword(name, password).getPassword().equals(password)) {
                    goToBibliothek(name, password);
                } else {
                    eingangFehler.setText("Falsches Passwort.");
                }
            } else {
                eingangFehler.setText("Benutzer mit name " + name + " ist nicht registriert.");
            }
        } else {
            eingangFehler.setText("Benutzername und Passwort dürfen nicht leer sein!");
        }
    }

    @FXML
    public Benutzer checkPassword(String benutzerName, String password) {
        return bibliothekDatenbankImpl.checkPassword(benutzerName, password);
    }
}
