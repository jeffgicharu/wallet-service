package com.digitalwallet.walletservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import jakarta.annotation.PostConstruct;
import java.sql.Connection;
import java.sql.SQLException;

@Component
public class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @PostConstruct
    public void testConnection() {
        try (Connection connection = dataSource.getConnection()) {
            System.out.println("Database connection successful!");
            System.out.println("Database URL: " + connection.getMetaData().getURL());
        } catch (SQLException e) {
            System.err.println("Database connection failed: " + e.getMessage());
        }
    }
}
