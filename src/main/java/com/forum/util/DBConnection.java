package com.forum.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBConnection — JDBC utility class.
 *
 * Provides a single static method to obtain a MySQL connection.
 * For production you would swap this for a JNDI DataSource / connection
 * pool (e.g. Apache DBCP or HikariCP), but this simpler approach is
 * sufficient for the capstone project.
 *
 * Module coverage: Module 4 — Server-Side (JDBC)
 */
public class DBConnection {

    // ── Connection parameters ─────────────────────────────────────────────
    private static final String URL  =
            "jdbc:mysql://localhost:3306/forum_db?useSSL=false&serverTimezone=UTC";
    private static final String USER = "forum_user";   // change to your DB user
    private static final String PASS = "secret";       // change to your DB password

    // Load the MySQL JDBC driver once when the class is first used
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(
                "MySQL JDBC driver not found. Add mysql-connector-j to pom.xml.", e);
        }
    }

    /**
     * Returns a new Connection from DriverManager.
     * Callers are responsible for closing the connection (use try-with-resources).
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // Prevent instantiation — this is a utility class
    private DBConnection() {}
}
