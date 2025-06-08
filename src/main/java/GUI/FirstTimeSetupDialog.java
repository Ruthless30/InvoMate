package GUI;

import DAO.ConfigManager;

import javax.swing.*;
import java.awt.*;

public class FirstTimeSetupDialog extends JDialog {
    private JTextField companyNameField;
    private JTextField phoneNumberField;
    private boolean setupCompleted = false;

    public FirstTimeSetupDialog(Frame parent) {
        super(parent, "Configuration - System de Facturation", true);
        initializeComponents();
        setupLayout();
        setupEventHandlers();

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private void initializeComponents() {
        companyNameField = new JTextField(20);
        phoneNumberField = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Header
        JLabel headerLabel = new JLabel("Welcome to your Project", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(headerLabel, BorderLayout.NORTH);

        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0; gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        formPanel.add(new JLabel("Company Name:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(companyNameField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.NONE;
        formPanel.add(new JLabel("Phone Number:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        formPanel.add(phoneNumberField, gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton setupButton = new JButton("Complete Setup");
        JButton exitButton = new JButton("Exit");

        buttonPanel.add(setupButton);
        buttonPanel.add(exitButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Event handlers
        setupButton.addActionListener(e -> completeSetup());
        exitButton.addActionListener(e -> System.exit(0));
    }

    private void setupEventHandlers() {
        // Add input validation if needed
        companyNameField.addActionListener(e -> phoneNumberField.requestFocus());
        phoneNumberField.addActionListener(e -> completeSetup());
    }

    private void completeSetup() {
        String companyName = companyNameField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();

        if (companyName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a company name.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            companyNameField.requestFocus();
            return;
        }

        if (phoneNumber.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a phone number.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE);
            phoneNumberField.requestFocus();
            return;
        }

        // Save configuration
        ConfigManager.getInstance().saveConfig(companyName, phoneNumber);
        setupCompleted = true;

        JOptionPane.showMessageDialog(this,
                "Setup completed successfully!\nCompany: " + companyName + "\nPhone: " + phoneNumber,
                "Setup Complete",
                JOptionPane.INFORMATION_MESSAGE);

        dispose();
    }

    public boolean isSetupCompleted() {
        return setupCompleted;
    }
}
