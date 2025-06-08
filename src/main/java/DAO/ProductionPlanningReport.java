package DAO;

import java.awt.*;
import java.awt.print.*;
import java.sql.*;
import java.util.*;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ProductionPlanningReport implements Printable {

    private Map<String, Map<String, Integer>> deliveryTimeProducts;
    private Map<String, Integer> totalProducts;
    private String reportDate;
    private static final int MARGIN = 50;
    private static final int LINE_HEIGHT = 20;
    private static final int SECTION_SPACING = 40;

    public ProductionPlanningReport() {
        this.reportDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));
    }

    public void generateAndPrintReport() {
        try {
            System.out.println("Generating production planning report...");

            // Fetch data from database
            fetchReportData();

            // Create print job
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(this);

            // Set up page format for A4
            PageFormat pageFormat = printJob.defaultPage();
            pageFormat.setOrientation(PageFormat.PORTRAIT);

            Paper paper = new Paper();
            // A4 size: 8.27 × 11.69 inches = 595 × 842 points
            paper.setSize(595, 842);
            paper.setImageableArea(50, 50, 495, 742); // margins
            pageFormat.setPaper(paper);

            printJob.setPrintable(this, pageFormat);

            // Set job name for better identification in print dialog
            printJob.setJobName("Production Planning Report - " + reportDate);

            // Always show print dialog (allows choosing printer, PDF, etc.)
            if (printJob.printDialog()) {
                try {
                    printJob.print();
                    System.out.println("Production planning report processed successfully!");
                } catch (PrinterException e) {
                    System.err.println("Printing failed: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Print operation cancelled by user.");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Alternative method that shows print dialog
    public void generateAndPrintReportWithDialog() {
        try {
            System.out.println("Generating production planning report...");

            // Fetch data from database
            fetchReportData();

            // Create print job
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(this);

            // Set up page format for A4
            PageFormat pageFormat = printJob.defaultPage();
            pageFormat.setOrientation(PageFormat.PORTRAIT);

            Paper paper = new Paper();
            paper.setSize(595, 842);
            paper.setImageableArea(50, 50, 495, 742);
            pageFormat.setPaper(paper);

            printJob.setPrintable(this, pageFormat);

            // Show print dialog
            if (printJob.printDialog()) {
                try {
                    printJob.print();
                    System.out.println("Production planning report sent to printer successfully!");
                } catch (PrinterException e) {
                    System.err.println("Printing failed: " + e.getMessage());
                    e.printStackTrace();
                }
            } else {
                System.out.println("Printing cancelled by user.");
            }

        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void fetchReportData() throws SQLException {
        deliveryTimeProducts = getProductsByDeliveryTime();
        totalProducts = calculateTotalProducts();
    }

    private Map<String, Map<String, Integer>> getProductsByDeliveryTime() throws SQLException {
        String query = """
            SELECT c.deliveryTime, ii.product_name, SUM(ii.quantity) as total_quantity
            FROM invoices i
            JOIN clients c ON i.client_id = c.id
            JOIN invoice_items ii ON i.id = ii.invoice_id
            WHERE i.print = 'YES'
            GROUP BY c.deliveryTime, ii.product_name
            ORDER BY c.deliveryTime, ii.product_name
        """;

        Map<String, Map<String, Integer>> result = new LinkedHashMap<>();
        result.put("matin", new LinkedHashMap<>());
        result.put("apres-midi", new LinkedHashMap<>());
        result.put("nuit", new LinkedHashMap<>());

        Connection connection = DBConnection.getInstance();
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String deliveryTime = rs.getString("deliveryTime");
                String productName = rs.getString("product_name");
                int quantity = rs.getInt("total_quantity");

                if (deliveryTime != null) {
                    result.get(deliveryTime).put(productName, quantity);
                }
            }
        }

        return result;
    }

    private Map<String, Integer> calculateTotalProducts() {
        Map<String, Integer> totalProducts = new LinkedHashMap<>();

        for (Map<String, Integer> products : deliveryTimeProducts.values()) {
            for (Map.Entry<String, Integer> entry : products.entrySet()) {
                String productName = entry.getKey();
                int quantity = entry.getValue();
                totalProducts.merge(productName, quantity, Integer::sum);
            }
        }

        return totalProducts;
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {
        if (pageIndex > 0) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Set up fonts
        Font titleFont = new Font("Arial", Font.BOLD, 16);
        Font headerFont = new Font("Arial", Font.BOLD, 12);
        Font normalFont = new Font("Arial", Font.PLAIN, 10);
        Font smallFont = new Font("Arial", Font.PLAIN, 9);

        int x = (int) pageFormat.getImageableX() + MARGIN;
        int y = (int) pageFormat.getImageableY() + MARGIN;
        int pageWidth = (int) pageFormat.getImageableWidth() - 2 * MARGIN;

        // Title
        g2d.setFont(titleFont);
        g2d.setColor(Color.BLACK);
        String title = "RAPPORT DE PLANIFICATION DE PRODUCTION";
        FontMetrics fm = g2d.getFontMetrics();
        int titleWidth = fm.stringWidth(title);
        g2d.drawString(title, x + (pageWidth - titleWidth) / 2, y);
        y += 30;

        // Date and time
        g2d.setFont(normalFont);
        String dateStr = "Date: " + reportDate;
        g2d.drawString(dateStr, x, y);
        y += 30;

        // Draw line separator
        g2d.drawLine(x, y, x + pageWidth, y);
        y += 30; // Increased spacing after separator

        // Draw delivery time sections
        String[] periods = {"matin", "apres-midi", "nuit"};
        String[] periodTitles = {"MATIN ", "APRÈS-MIDI ", "NUIT "};

        for (int i = 0; i < periods.length; i++) {
            String period = periods[i];
            String periodTitle = periodTitles[i];
            Map<String, Integer> products = deliveryTimeProducts.get(period);

            // Section header
            g2d.setFont(headerFont);
            g2d.setColor(Color.BLACK);

            // Draw section background
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(x, y - 15, pageWidth, 20);
            g2d.setColor(Color.BLACK);
            g2d.drawRect(x, y - 15, pageWidth, 20);

            g2d.drawString(periodTitle, x + 10, y);
            y += 25;

            // Table headers
            g2d.setFont(normalFont);
            g2d.drawString("PRODUIT", x + 10, y);
            g2d.drawString("QUANTITÉ", x + pageWidth - 100, y);
            y += 5;

            // Draw line under headers
            g2d.drawLine(x, y, x + pageWidth, y);
            y += 15;

            // Store starting position for border calculation
            int sectionStartY = y - 45;
            int contentStartY = y;

            // Table content
            g2d.setFont(smallFont);
            if (products.isEmpty()) {
                g2d.drawString("Aucun produit nécessaire", x + 10, y);
                y += LINE_HEIGHT;
            } else {
                for (Map.Entry<String, Integer> entry : products.entrySet()) {
                    String productName = entry.getKey();
                    if (productName.length() > 50) {
                        productName = productName.substring(0, 47) + "...";
                    }
                    g2d.drawString(productName, x + 10, y);
                    g2d.drawString(String.valueOf(entry.getValue()), x + pageWidth - 100, y);
                    y += LINE_HEIGHT;
                }
            }

            // Draw section border
            int sectionHeight = y - sectionStartY + 10;
            g2d.drawRect(x, sectionStartY, pageWidth, sectionHeight);

            // Add spacing between sections
            y += SECTION_SPACING;
        }

        // Total summary section with extra spacing
        y += 10; // Additional space before total section

        g2d.setFont(headerFont);
        g2d.setColor(new Color(200, 200, 200));
        g2d.fillRect(x, y - 15, pageWidth, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y - 15, pageWidth, 20);
        g2d.drawString("RÉSUMÉ TOTAL", x + 10, y);
        y += 25;

        // Total table headers
        g2d.setFont(normalFont);
        g2d.drawString("PRODUIT", x + 10, y);
        g2d.drawString("QUANTITÉ TOTALE", x + pageWidth - 120, y);
        y += 5;
        g2d.drawLine(x, y, x + pageWidth, y);
        y += 15;

        // Store starting position for total section border
        int totalSectionStartY = y - 45;

        // Total table content
        g2d.setFont(smallFont);
        if (totalProducts.isEmpty()) {
            g2d.drawString("Aucun produit à préparer", x + 10, y);
            y += LINE_HEIGHT;
        } else {
            List<Map.Entry<String, Integer>> sortedProducts = new ArrayList<>(totalProducts.entrySet());
            sortedProducts.sort(Map.Entry.comparingByKey());

            for (Map.Entry<String, Integer> entry : sortedProducts) {
                String productName = entry.getKey();
                if (productName.length() > 50) {
                    productName = productName.substring(0, 47) + "...";
                }
                g2d.drawString(productName, x + 10, y);
                g2d.drawString(String.valueOf(entry.getValue()), x + pageWidth - 120, y);
                y += LINE_HEIGHT;
            }
        }

        // Draw total section border
        int totalSectionHeight = y - totalSectionStartY + 10;
        g2d.drawRect(x, totalSectionStartY, pageWidth, totalSectionHeight);
        y += 40; // Increased spacing after total section

        // Summary statistics
        int totalItems = totalProducts.values().stream().mapToInt(Integer::intValue).sum();
        int uniqueProducts = totalProducts.size();

        g2d.setFont(normalFont);
        g2d.drawString("Produits uniques: " + uniqueProducts, x, y);
        g2d.drawString("Articles totaux à préparer: " + totalItems, x + 200, y);

        // Footer
        y = (int) (pageFormat.getImageableY() + pageFormat.getImageableHeight() - 30);
        g2d.setFont(smallFont);
        g2d.setColor(Color.GRAY);
        String footer = "Généré le " + reportDate + " - Page 1";
        FontMetrics footerFm = g2d.getFontMetrics();
        int footerWidth = footerFm.stringWidth(footer);
        g2d.drawString(footer, x + (pageWidth - footerWidth) / 2, y);

        return PAGE_EXISTS;
    }

    // Simple method to call from your button - now shows print dialog
    public static void printProductionReport() {
        ProductionPlanningReport report = new ProductionPlanningReport();
        report.generateAndPrintReport(); // This now shows the dialog
    }

    // Method for direct printing without dialog (if needed)
    public static void printProductionReportDirectly() {
        ProductionPlanningReport report = new ProductionPlanningReport();
        try {
            System.out.println("Generating production planning report...");

            // Fetch data from database
            report.fetchReportData();

            // Create print job
            PrinterJob printJob = PrinterJob.getPrinterJob();
            printJob.setPrintable(report);

            // Set up page format for A4
            PageFormat pageFormat = printJob.defaultPage();
            pageFormat.setOrientation(PageFormat.PORTRAIT);

            Paper paper = new Paper();
            paper.setSize(595, 842);
            paper.setImageableArea(50, 50, 495, 742);
            pageFormat.setPaper(paper);

            printJob.setPrintable(report, pageFormat);
            printJob.setJobName("Production Planning Report - " + report.reportDate);

            // Print directly without dialog
            try {
                printJob.print();
                System.out.println("Production planning report sent to printer successfully!");
            } catch (PrinterException e) {
                System.err.println("Printing failed: " + e.getMessage());
                e.printStackTrace();
            }

        } catch (SQLException e) {
            System.err.println("Error fetching data: " + e.getMessage());
            e.printStackTrace();
        }
    }

}