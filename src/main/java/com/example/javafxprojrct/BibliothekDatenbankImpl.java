package com.example.javafxprojrct;

import com.password4j.BcryptFunction;
import com.password4j.Hash;
import com.password4j.Password;
import com.password4j.types.Bcrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BibliothekDatenbankImpl implements BibliothekFunktion{
    private Connection connection;

    public BibliothekDatenbankImpl() {
        String url = "jdbc:mysql://127.0.0.1:3306/buecherei";
        String username = "root";
        String password = "root";
        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fehler bei der Verbindung zur Datenbank.");
        }
    }

    @Override
    public boolean isBenutzerExist(String benutzerName) {
        String sql = "SELECT COUNT(*) FROM user WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, benutzerName);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int anzahl = result.getInt(1);
                    if (anzahl > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fehler: " + e.getMessage());
        }

        return false;
    }

    @Override
    public Benutzer addBenutzer(String benutzerName, String password) {
        if (isBenutzerExist(benutzerName)) {
            return getBenutzerBeiName(benutzerName);
        }

        String sql = "INSERT INTO user (name, password) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, benutzerName);
            stmt.setString(2, password);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        return new Benutzer(generatedId, benutzerName, password);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Fehler: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Benutzer checkPassword(String benutzerName, String password) {
        String sql = "SELECT * FROM user WHERE password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, password);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    if (result.getString("name").equals(benutzerName)) {
                        return new Benutzer(result.getInt("id"), result.getString("name"), result.getString("password"));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return null;
    }

    @Override
    public Benutzer getBenutzer(int benutzerId) {
        String sql = "SELECT * FROM user WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, benutzerId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Benutzer(result.getInt("id"), result.getString("name"), result.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return null;
    }


    public Benutzer getBenutzerBeiName(String benutzerName) {
        String sql = "SELECT * FROM user WHERE name = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, benutzerName);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Benutzer(result.getInt("id"), result.getString("name"), result.getString("password"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return null;
    }

    @Override
    public boolean isBuchAusgeliehen(Buch buch) {
        String sql = "SELECT COUNT(*) FROM loan WHERE book_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buch.getId());

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int anzahl = result.getInt(1);
                    if (anzahl > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void addAusgelieheneBuecher(int benutzerId, int buchId) {
        if (isBuchAusgeliehen(getBuch(buchId))) {
            System.out.println("Die Bibliothek hat kein Buch mit dieser Nummer oder das Buch ist bereits ausgeliehen.");
            return;
        }

        String sql = "INSERT INTO loan (user_id, book_id) VALUES (?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, benutzerId);
            stmt.setInt(2,buchId);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next() && !isBuchAusgeliehen(getBuch(buchId))) {
                        new Ausleihe(getBenutzer(benutzerId), getBuch(buchId));
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    @Override
    public boolean isAusleiheExist(int benutzerId, int buchId) {
        String sql = "SELECT 1 FROM loan WHERE user_id = ? AND book_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, benutzerId);
            stmt.setInt(2, buchId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    int anzahl = result.getInt(1);
                    if (anzahl > 0) {
                        return true;
                    }
                }
            }
        } catch (SQLException e) {

            System.out.println("Fehler: " + e.getMessage());
        }
        return false;
    }

    @Override
    public void removeAusgelieheneBuecher(int benutzerId, int buchId) {
        if (!isAusleiheExist(benutzerId, buchId)) {
            System.out.println("Das Buch einem anderen Benutzer gehört oder ist bereits zurückgegeben.");
            return;
        }

        String sql = "DELETE FROM loan WHERE user_id = ? AND book_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, benutzerId);
            stmt.setInt(2, buchId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    @Override
    public void addBuch(String buchName) {
        String sql = "INSERT INTO book (name) VALUES (?)";

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, buchName);

            int affectedRows = stmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        new Buch(generatedId, buchName);
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    @Override
    public Buch getBuch(int buchId) {
        String sql = "SELECT * FROM book WHERE id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, buchId);

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return new Buch(result.getInt("id"), result.getString("name"));
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return null;
    }


    @Override
    public List<Buch> getBenutzerBuecher(int benutzerId) {
        if (getBenutzer(benutzerId) == null) {
            return null;
        }

        List<Buch> list = new ArrayList<>();

        String sql = "SELECT b.id, b.name FROM loan l JOIN book b ON l.book_id = b.id WHERE l.user_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, benutzerId);

            try (ResultSet result = stmt.executeQuery()) {

                while (result.next()) {
                    int buchId = result.getInt("id");
                    String buchName = result.getString("name");

                    Buch buch = new Buch(buchId, buchName);
                    list.add(new Buch(buch.getId(), buch.getName()));
                }
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return list;
    }

    @Override
    public void getBuecher() {
        String sql = "SELECT * FROM book";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                int buchId = result.getInt("id");
                String buchName = result.getString("name");

                Buch buch = new Buch(buchId, buchName);
                System.out.println("Buch Nummer: " + buch.getId() + ", Name: " + buch.getName());
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }
    }

    @Override
    public List<Buch> getBuecherVerfuegbar() {
        List<Buch> list = new ArrayList<>();
        String sql = "SELECT b.id, b.name FROM book b LEFT JOIN loan l ON b.id = l.book_id WHERE l.book_id IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {

            while (result.next()) {
                int buchId = result.getInt("id");
                String buchName = result.getString("name");

                list.add(new Buch(buchId ,buchName));
            }
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return list;
    }

    @Override
    public int getAnzahlBuecherVerfuegbar() {
        String sql = "SELECT COUNT(*) FROM book b LEFT JOIN loan l ON b.id = l.book_id WHERE l.book_id IS NULL";

        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet result = stmt.executeQuery()) {
            int anzahl = result.getInt(1);
            return anzahl;
        } catch (SQLException e) {
            System.out.println("Fehler: " + e.getMessage());
        }

        return 0;
    }
}
