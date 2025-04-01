package com.example.javafxprojrct;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.controlsfx.control.tableview2.TableView2;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class BibliothekController implements Initializable {
    private BibliothekDatenbankImpl bibliothekDatenbank = new BibliothekDatenbankImpl();

    @FXML
    private VBox adminBox;

    @FXML
    private TextField buchName;

    @FXML
    private TableView benutzerBuchTable;

    @FXML
    private TableColumn benutzerBuchIdColumn;

    @FXML
    private TableColumn benutzerBuchNameColumn;

    @FXML
    private TableColumn benutzerBuchButtonColumn;

    @FXML
    private TableView buchTable;

    @FXML
    private TableColumn buchIdColumn;

    @FXML
    private TableColumn buchNameColumn;

    @FXML
    private TableColumn buchButtonColumn;

    int benutzerId = 0;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        benutzerBuchIdColumn.setCellValueFactory(new PropertyValueFactory<Buch, String>("id"));
        benutzerBuchNameColumn.setCellValueFactory(new PropertyValueFactory<Buch, String>("name"));
        benutzerBuchButtonColumn.setCellFactory(column -> {
            TableCell<Buch, Void> tableCell = new TableCell<>() {
                private final Button button = new Button("Zurückgeben");

                {
                    button.setOnAction(event -> {
                        Buch selectedBook = getTableView().getItems().get(getIndex());
                        zurueckgeben(selectedBook.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setGraphic(button);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return tableCell;
        });

        buchIdColumn.setCellValueFactory(new PropertyValueFactory<Buch, String>("id"));
        buchNameColumn.setCellValueFactory(new PropertyValueFactory<Buch, String>("name"));
        buchButtonColumn.setCellFactory(column -> {
            TableCell<Buch, Void> tableCell = new TableCell<>() {
                private final Button button = new Button("Ausleihen");

                {
                    button.setOnAction(event -> {
                        Buch selectedBook = getTableView().getItems().get(getIndex());
                        ausleihen(selectedBook.getId());
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (!empty) {
                        setGraphic(button);
                    } else {
                        setGraphic(null);
                    }
                }
            };
            return tableCell;
        });
    }

    @FXML
    public void addBuch() {
        if (!buchName.getText().isBlank()) {
            bibliothekDatenbank.addBuch(buchName.getText());
            reload();
        }
    }

    @FXML
    public void getData(String benutzerName, String password) {
        addBenutzer(benutzerName, password);
        benutzerId = bibliothekDatenbank.getBenutzerBeiName(benutzerName).getId();
        reload();
    }

    @FXML
    public void bibliothekAdmin() {
        adminBox.setVisible(true);
        buchButtonColumn.setVisible(false);
        reload();
    }

    @FXML
    public void reload() {
        getBuecherVerfuegbar();
        getBenutzerBuecher();
    }

    @FXML
    public void logout() throws IOException {
        adminBox.setVisible(true);
        buchButtonColumn.setVisible(true);
        FXMLLoader loader = new FXMLLoader(getClass().getResource("eingang.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) this.benutzerBuchTable.getScene().getWindow();
        stage.setScene(new Scene(root, 1000, 800));
        stage.show();
    }

    @FXML
    public void ausleihen(int buchId) {
        bibliothekDatenbank.addAusgelieheneBuecher(benutzerId, buchId);
        reload();
    }

    @FXML
    public void zurueckgeben(int buchId) {
        bibliothekDatenbank.removeAusgelieheneBuecher(benutzerId, buchId);
        reload();
    }

    @FXML
    public void addBenutzer(String benutzerName, String password) {
        Benutzer benutzer = bibliothekDatenbank.addBenutzer(benutzerName, password);
        benutzerId = benutzer.getId();
        reload();
    }

    @FXML
    public void getBenutzerBuecher() {
        List<Buch> list = bibliothekDatenbank.getBenutzerBuecher(benutzerId);

        if (list != null) {
            benutzerBuchTable.getItems().setAll(list);
        }
    }

    @FXML
    public void getBuecherVerfuegbar() {
        List<Buch> list = bibliothekDatenbank.getBuecherVerfuegbar();

        if (list != null) {
            buchTable.getItems().setAll(list);
        }
    }

}