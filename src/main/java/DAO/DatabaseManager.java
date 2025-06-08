package DAO;// DatabaseManager.java
import java.sql.*;
import java.io.File;

public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection connection;
    private String appDataFolder;
    private String dbUrl;
    private String dbPath;

    private DatabaseManager() {
        // Initialize app data folder
        this.appDataFolder = getAppDataFolder();

        // Initialize with default database name if no company name is set yet
        String companyName = ConfigManager.getInstance().getCompanyName();
        if (companyName != null && !companyName.isEmpty()) {
            initializeDatabase(companyName);
        }
        // Note: Database will be initialized later after company setup is complete
    }

    public static DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private String getAppDataFolder() {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");

        // Get company name for folder, or use default
        String companyName = ConfigManager.getInstance().getCompanyName();
        String appFolder = sanitizeCompanyNameForFolder(companyName);

        String dataPath;
        if (os.contains("win")) {
            // Windows: %APPDATA%/CompanyName
            dataPath = System.getenv("APPDATA") + File.separator + appFolder;
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/CompanyName
            dataPath = userHome + File.separator + "Library" + File.separator +
                    "Application Support" + File.separator + appFolder;
        } else {
            // Linux: ~/.config/CompanyName
            dataPath = userHome + File.separator + ".config" + File.separator + appFolder;
        }

        // Create directory if it doesn't exist
        File dir = new File(dataPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dataPath;
    }

    private String sanitizeCompanyNameForFolder(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            return "AndalousProject"; // Default fallback for when company name is not set yet
        }

        // Keep original case but replace spaces and special characters
        return companyName.trim()
                .replaceAll("[\\s\\-]+", "_")  // Replace spaces and dashes with underscores
                .replaceAll("[^a-zA-Z0-9_]", ""); // Remove special characters except underscores
    }

    public void initializeDatabase(String companyName) {
        // Update app data folder based on company name
        this.appDataFolder = getAppDataFolderForCompany(companyName);

        // Create database name based on company name
        String sanitizedName = sanitizeCompanyNameForFolder(companyName);
        String dbName = sanitizedName.toLowerCase() + ".db";
        this.dbPath = this.appDataFolder + File.separator + dbName;
        this.dbUrl = "jdbc:sqlite:" + this.dbPath;

        System.out.println("Database initialized for company: " + companyName);
        System.out.println("Database path: " + this.dbPath);
    }

    private String getAppDataFolderForCompany(String companyName) {
        String os = System.getProperty("os.name").toLowerCase();
        String userHome = System.getProperty("user.home");
        String appFolder = sanitizeCompanyNameForFolder(companyName);

        String dataPath;
        if (os.contains("win")) {
            // Windows: %APPDATA%/CompanyName
            dataPath = System.getenv("APPDATA") + File.separator + appFolder;
        } else if (os.contains("mac")) {
            // macOS: ~/Library/Application Support/CompanyName
            dataPath = userHome + File.separator + "Library" + File.separator +
                    "Application Support" + File.separator + appFolder;
        } else {
            // Linux: ~/.config/CompanyName
            dataPath = userHome + File.separator + ".config" + File.separator + appFolder;
        }

        // Create directory if it doesn't exist
        File dir = new File(dataPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        return dataPath;
    }

    public boolean isDatabaseExists() {
        if (dbPath == null) {
            return false;
        }
        File dbFile = new File(dbPath);
        return dbFile.exists();
    }

    public String getDatabasePath() {
        return dbPath;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            if (dbUrl == null) {
                throw new SQLException("Database not initialized. Call initializeDatabase() first.");
            }
            connection = DriverManager.getConnection(dbUrl);
        }
        return connection;
    }

    public void createDatabase() throws SQLException {
        Connection conn = getConnection();

        // Create clients table
        String createClientsTable = """
            CREATE TABLE IF NOT EXISTS "clients" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "name" TEXT NOT NULL UNIQUE,
                "credit" REAL DEFAULT 0,
                "deliveryTime" TEXT CHECK("deliveryTime" IN ("matin", "apres-midi", "nuit"))
            )
        """;

        // Create products table
        String createProductsTable = """
            CREATE TABLE IF NOT EXISTS "products" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "name" TEXT NOT NULL,
                "price" REAL NOT NULL
            )
        """;

        // Create invoices table
        String createInvoicesTable = """
            CREATE TABLE IF NOT EXISTS "invoices" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "client_id" INTEGER NOT NULL,
                "client_name" TEXT NOT NULL,
                "ordre_index" INTEGER,
                "notes" TEXT,
                "created_at" TEXT,
                "print" TEXT DEFAULT 'YES',
                FOREIGN KEY("client_id") REFERENCES "clients"("id"),
                FOREIGN KEY("client_name") REFERENCES "clients"("name")
            )
        """;

        // Create invoice_items table
        String createInvoiceItemsTable = """
            CREATE TABLE IF NOT EXISTS "invoice_items" (
                "id" INTEGER PRIMARY KEY AUTOINCREMENT,
                "invoice_id" INTEGER NOT NULL,
                "product_id" INTEGER NOT NULL,
                "product_name" TEXT NOT NULL,
                "quantity" INTEGER NOT NULL,
                "price" REAL NOT NULL,
                "total" REAL NOT NULL,
                FOREIGN KEY("invoice_id") REFERENCES "invoices"("id"),
                FOREIGN KEY("product_id") REFERENCES "products"("id"),
                FOREIGN KEY("product_name") REFERENCES "products"("name")
            )
        """;

        // Create triggers
        String createInvoiceTrigger = """
            CREATE TRIGGER IF NOT EXISTS create_invoice_after_client_insert
            AFTER INSERT ON clients
            FOR EACH ROW
            BEGIN
                INSERT INTO invoices (client_id, client_name, notes, created_at, print, ordre_index)
                VALUES (
                    NEW.id,
                    NEW.name,
                    '',
                    datetime('now'),
                    'YES',
                    COALESCE(
                        (SELECT MAX(ordre_index) + 1 
                         FROM invoices 
                         WHERE print = 'YES'), 
                        1
                    )
                );
            END
        """;

        String createDeleteTrigger = """
            CREATE TRIGGER IF NOT EXISTS delete_client_cascade
            BEFORE DELETE ON clients
            FOR EACH ROW
            BEGIN
                DELETE FROM invoice_items 
                WHERE invoice_id IN (
                    SELECT id FROM invoices WHERE client_id = OLD.id
                );
                
                DELETE FROM invoices 
                WHERE client_id = OLD.id;
            END
        """;

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(createClientsTable);
            stmt.execute(createProductsTable);
            stmt.execute(createInvoicesTable);
            stmt.execute(createInvoiceItemsTable);
            stmt.execute(createInvoiceTrigger);
            stmt.execute(createDeleteTrigger);

            System.out.println("Database and tables created successfully for: " +
                    ConfigManager.getInstance().getCompanyName());
        }
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}