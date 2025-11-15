package com.smartpark.estacionamiento.patrones.creacional.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static volatile DBConnection instance;
    private Connection connection;

    // !!! ACTUALIZA TU CONTRASEÑA AQUÍ !!!
    private static final String URL = "jdbc:mysql://localhost:3306/gestion_estacionamiento_db";
    private static final String USER = "root";
    private static final String PASSWORD = "123"; //

    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("¡Conexión a la base de datos exitosa!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Error al conectar a la base de datos.");
        }
    }

    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }
    public Connection getConnection() {
        return connection;
    }
}