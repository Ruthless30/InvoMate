package com.msallem;

import DAO.ConfigManager;
import DAO.DatabaseManager;
import GUI.FirstTimeSetupDialog;
import GUI.HomeGUI;

import javax.swing.*;
import java.sql.SQLException;

public class MainApplication {
    public static void main(String[] args) {
        new MainApplication().start();
    }


    public void start() {
        ConfigManager configManager = ConfigManager.getInstance();
        DatabaseManager dbManager = DatabaseManager.getInstance();

        // Check if this is first time setup
        if (configManager.isFirstTimeSetup()) {
            FirstTimeSetupDialog setupDialog = new FirstTimeSetupDialog(null);
            setupDialog.setVisible(true);

            if (!setupDialog.isSetupCompleted()) {
                System.exit(0); // User cancelled setup
            }

            // After setup is complete, reinitialize database with company name
            dbManager.initializeDatabase(configManager.getCompanyName());

        }
        System.out.println("Configuration File path : " +configManager.getConfigPath());
        // Initialize database
        try {
            if (!dbManager.isDatabaseExists()) {
                System.out.println("Creating database for the first time...");
                System.out.println("Database will be created at: " + dbManager.getDatabasePath());
                dbManager.createDatabase();
            } else {
                System.out.println("Database exists at: " + dbManager.getDatabasePath());
                dbManager.getConnection(); // Just to verify connection
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,
                    "Failed to initialize database: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch main application window
        new HomeGUI();
    }

}