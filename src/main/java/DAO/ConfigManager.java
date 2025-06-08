package DAO;

import java.util.Properties;
import java.io.*;

public class ConfigManager {
    private static final String APP_DATA_FOLDER = getAppDataFolder();
    private static final String CONFIG_FILE = APP_DATA_FOLDER + File.separator + "config.properties";

    private static String getAppDataFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String appFolder ="Project";

        String dataPath;
        if (os.contains("win")) {
            // Windows: %APPDATA%/YourProject
            dataPath = System.getenv("APPDATA") + File.separator + appFolder;
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/YourProject
            dataPath = userHome + File.separator + "Library" + File.separator +
                    "Application Support" + File.separator + appFolder;
        } else {
            // Linux: ~/.config/YourProject
            dataPath = userHome + File.separator + ".config" + File.separator + appFolder;
        }

        // Create directory if it doesn't exist
        File dir = new File(dataPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dataPath;
    }

    private static ConfigManager instance;
    private Properties properties;

    private ConfigManager() {
        properties = new Properties();
        loadConfig();
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            instance = new ConfigManager();
        }
        return instance;
    }

    public boolean isFirstTimeSetup() {
        return !new File(CONFIG_FILE).exists() ||
                properties.getProperty("company.name") == null ||
                properties.getProperty("company.phone") == null;
    }

    public void saveConfig(String companyName, String phoneNumber) {
        properties.setProperty("company.name", companyName);
        properties.setProperty("company.phone", phoneNumber);
        properties.setProperty("setup.completed", "true");
        properties.setProperty("setup.date", String.valueOf(System.currentTimeMillis()));

        try (FileOutputStream fos = new FileOutputStream(CONFIG_FILE)) {
            properties.store(fos, "Project Configuration");
            System.out.println("Configuration saved successfully!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadConfig() {
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE)) {
            properties.load(fis);
        } catch (IOException e) {
            // Config file doesn't exist yet, which is fine for first run
            System.out.println("Config file not found, first time setup required.");
        }
    }

    public String getCompanyName() {
        return properties.getProperty("company.name", "");
    }

    public String getPhoneNumber() {
        return properties.getProperty("company.phone", "");
    }

    public boolean isSetupCompleted() {
        return "true".equals(properties.getProperty("setup.completed"));
    }

    public String getConfigPath() {
        return CONFIG_FILE;
    }

    public String getAppDataFolderPath() {
        return APP_DATA_FOLDER;
    }
}
