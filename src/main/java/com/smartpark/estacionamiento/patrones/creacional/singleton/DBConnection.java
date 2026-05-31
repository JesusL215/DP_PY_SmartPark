package com.smartpark.estacionamiento.patrones.creacional.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    private static volatile DBConnection instance;
    private Connection connection;

    private static final String URL = "jdbc:postgresql://localhost:5432/gestion_estacionamiento_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root215";

    private DBConnection() {
        try {
            Class.forName("org.postgresql.Driver");
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