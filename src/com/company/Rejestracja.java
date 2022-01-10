package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class Rejestracja extends JFrame {
    private JPanel panel1;
    private JTextField imie;
    private JTextField nazwisko;
    private JTextField pesel;
    private JTextField dataigodzina;
    private JButton OKButton;
    private JLabel wynik;
    private JLabel formatDaty;

    private static final String URL = "jdbc:mysql://localhost:3306/rejestracja";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public Rejestracja() {
        super("Rejestracja");
        this.setContentPane(panel1);
        this.pack();
        wynik.setText("");
        formatDaty.setFont(new Font("Serif", Font.PLAIN, 12));


        ActionListener actionListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = e.getSource();  //sprzawdzam gdzie zostalo klikniete
                if (obj == OKButton) {

                    if (!imie.getText().equals("") &&
                            !nazwisko.getText().equals("") &&
                            !pesel.getText().equals("") &&
                            !dataigodzina.getText().equals("") &&
                            //sprawdzamy czy dlugosc pola jest = 16 i czy jest pauza na 10 pozycji
                            dataigodzina.getText().length() == 16 &&
                            dataigodzina.getText().charAt(10) == '-') {

                        if (sprawdzGodzineIDzien(dataigodzina.getText())) {
                            //obiekt tworzymy żeby go zapisac calego do bazy
                            ModelRejestracji modelRejestracji = new ModelRejestracji(imie.getText(), nazwisko.getText(), pesel.getText(), dataigodzina.getText());
                            try {
                                Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
                                String query = "INSERT INTO rejestracje VALUES (null, '%s', '%s', '%s', '%s');";
                                Statement stmt = conn.createStatement();
                                stmt.executeUpdate(String.format(query, modelRejestracji.getImie(), modelRejestracji.getNazwisko(), modelRejestracji.getPesel(), modelRejestracji.getDataigodzina()));
                                conn.close();
                                wynik.setText("dodano pomyślnie");
                            } catch (SQLException sqlException) {
                                sqlException.printStackTrace();
                            }
                        } else {
                            wynik.setText("Niepoprawne dane, sprawdź poprawność pól lub zmien termin");
                        }
                    } else {
                        wynik.setText("WYPEŁNIJ POPRAWNIE WSZYSTKIE POLA");
                    }
                }
            }
        };

        OKButton.addActionListener(actionListener);

    }

    private Boolean sprawdzGodzineIDzien(String dzienIGodzina) {
        int count = 0;
        //sprawdzenie czy istnieje już rezerwacja na tą godzinę
        try {
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT COUNT(ID) AS DUPLIKAT FROM rejestracje WHERE DATA_I_GODZINA like '%s'";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(String.format(query, dzienIGodzina));
            while (rs.next()) {
                count = rs.getInt("DUPLIKAT");
            }
            conn.close();
        } catch (SQLException e) {
            System.out.println("Error");
        }

        String godzina = dzienIGodzina.substring(11, 13);

        //Sprawdzenie czy zarejestrowano się na godziny 12-17 i czy nie ma juz takiej rezerwacji
        if (count == 0 && Integer.parseInt(godzina) >= 12 && Integer.parseInt(godzina) <= 17) {
            return true;
        } else {
            return false;
        }
    }
}

