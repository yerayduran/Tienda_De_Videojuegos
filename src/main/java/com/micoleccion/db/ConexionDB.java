package com.micoleccion.db;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public final class ConexionDB {

    private static final String RESOURCE = "com/micoleccion/properties/config.properties";
    private static Properties props;

    private ConexionDB() {
    }

    private static void cargarProperties() throws SQLException {
        if (props != null) {
            return;
        }

        props = new Properties();

        /*
        En esta parte es una especie de grifo de cierre automatico para no estar que asegurarme todo el rato de comprobar si esta abierto
         */
        try (InputStream is = ConexionDB.class.getClassLoader().getResourceAsStream(RESOURCE)) {
            if (is == null) {
                throw new SQLException("No se encontró el archivo de configuración en el classpath: " + RESOURCE);
            }
            props.load(is);
        } catch (IOException e) {
            throw new SQLException("Error al leer el archivo de configuración: " + RESOURCE, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        cargarProperties();

        String url = props.getProperty("URL");
        String user = props.getProperty("USER");
        String password = props.getProperty("PASS");

        return DriverManager.getConnection(url, user, password);
    }
}