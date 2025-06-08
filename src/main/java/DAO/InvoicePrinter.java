package DAO;

import MODEL.FactureItem;

import java.awt.*;
import java.awt.print.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import javax.swing.*;


public class InvoicePrinter implements Printable {

    private Connection connection;
    private List<Invoice> invoicesToPrint;
    private static ConfigManager configManager=ConfigManager.getInstance();
    private static final String COMPANY_NAME = configManager.getCompanyName();
    private static final String COMPANY_PHONE = "Phone :" +configManager.getPhoneNumber();

    // Using existing FactureItem class instead of InvoiceItem subclass

    public static class Invoice {
        public int invoiceId;
        public int clientId;
        public String clientName;
        public String notes;
        public List<FactureItem> items;
        public double totalAmount;

        public Invoice(int invoiceId, int clientId, String clientName, String notes) {
            this.invoiceId = invoiceId;
            this.clientId = clientId;
            this.clientName = clientName;
            this.notes = notes != null ? notes : "";
            this.items = new ArrayList<>();
            this.totalAmount = 0.0;
        }

        public void addItem(FactureItem item) {
            this.items.add(item);
            this.totalAmount += item.getTotal();
        }
    }

    public InvoicePrinter() {
        this.connection = DBConnection.getInstance();
        this.invoicesToPrint = new ArrayList<>();
    }

    /**
     * Get the appropriate date for printing based on current time
     * If before midnight (00:00), show tomorrow's date
     * If after midnight, show today's date
     */
    private String getPrintDate() {
        Calendar cal = Calendar.getInstance();
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        LocalDate dateToShow;
        if (hour == 0) {
            // It's past midnight (00:00), use today's date
            dateToShow = LocalDate.now();
        } else {
            // It's before midnight, use tomorrow's date
            dateToShow = LocalDate.now().plusDays(1);
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy");
        return dateToShow.format(formatter);
    }

    /**
     * Fetch all invoices marked for printing from database
     */
    private void fetchInvoicesForPrinting() throws SQLException {
        String invoiceQuery = """
            SELECT i.id, i.client_id, i.client_name, i.notes 
            FROM invoices i 
            WHERE i.print = 'YES'
            ORDER BY i.client_id, i.id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(invoiceQuery);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int invoiceId = rs.getInt("id");
                int clientId = rs.getInt("client_id");
                String clientName = rs.getString("client_name");
                String notes = rs.getString("notes");

                Invoice invoice = new Invoice(invoiceId, clientId, clientName, notes);
                fetchInvoiceItems(invoice);
                invoicesToPrint.add(invoice);
            }
        }
    }

    /**
     * Fetch items for a specific invoice
     */
    private void fetchInvoiceItems(Invoice invoice) throws SQLException {
        String itemsQuery = """
            SELECT product_name, quantity, price, total 
            FROM invoice_items 
            WHERE invoice_id = ?
            ORDER BY id
        """;

        try (PreparedStatement stmt = connection.prepareStatement(itemsQuery)) {
            stmt.setInt(1, invoice.invoiceId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    FactureItem item = new FactureItem(
                            rs.getString("product_name"),
                            rs.getDouble("price"),
                            rs.getInt("quantity")
                    );
                    invoice.addItem(item);
                }
            }
        }
    }

    /**
     * Update print status to 'NO' after successful printing
     */
    private void updatePrintStatus() throws SQLException {
        String updateQuery = "UPDATE invoices SET print = 'NO' WHERE print = 'YES'";
        try (PreparedStatement stmt = connection.prepareStatement(updateQuery)) {
            stmt.executeUpdate();
        }
    }

    @Override
    public int print(Graphics graphics, PageFormat pageFormat, int pageIndex) throws PrinterException {

        int invoicesPerPage = 3;
        int totalPages = (int) Math.ceil((double) invoicesToPrint.size() / invoicesPerPage);

        if (pageIndex >= totalPages) {
            return NO_SUCH_PAGE;
        }

        Graphics2D g2d = (Graphics2D) graphics;
        g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

        // Calculate dimensions for 3 invoices per page with more spacing
        double pageWidth = pageFormat.getImageableWidth();
        double pageHeight = pageFormat.getImageableHeight();
        double invoiceWidth = (pageWidth - 40) / 3; // Reserve 40 pixels total for spacing
        double spacingBetween = 20; // 20 pixels between each invoice

        // Print up to 3 invoices on this page
        int startIndex = pageIndex * invoicesPerPage;
        int endIndex = Math.min(startIndex + invoicesPerPage, invoicesToPrint.size());

        for (int i = startIndex; i < endIndex; i++) {
            Invoice invoice = invoicesToPrint.get(i);
            int columnIndex = i - startIndex;
            double xOffset = columnIndex * (invoiceWidth + spacingBetween);

            printSingleInvoice(g2d, invoice, xOffset, 0, invoiceWidth, pageHeight);
        }

        return PAGE_EXISTS;
    }

    /**
     * Print a single invoice within the specified bounds
     */
    private void printSingleInvoice(Graphics2D g2d, Invoice invoice, double x, double y,
                                    double width, double height) {

        Font titleFont = new Font("Arial", Font.BOLD, 12);
        Font headerFont = new Font("Arial", Font.BOLD, 10);
        Font normalFont = new Font("Arial", Font.PLAIN, 8);
        Font smallFont = new Font("Arial", Font.PLAIN, 7);

        int currentY = (int) y + 20;
        int leftMargin = (int) x + 5;
        int rightMargin = (int) (x + width - 5);

        // Company Name
        g2d.setFont(titleFont);
        FontMetrics titleMetrics = g2d.getFontMetrics();
        int titleWidth = titleMetrics.stringWidth(COMPANY_NAME);
        int titleX = leftMargin + (int)((width - 10 - titleWidth) / 2);
        g2d.drawString(COMPANY_NAME, titleX, currentY);
        currentY += 18;

        // Phone Number
        g2d.setFont(normalFont);
        FontMetrics normalMetrics = g2d.getFontMetrics();
        int phoneWidth = normalMetrics.stringWidth(COMPANY_PHONE);
        int phoneX = leftMargin + (int)((width - 10 - phoneWidth) / 2);
        g2d.drawString(COMPANY_PHONE, phoneX, currentY);
        currentY += 12;

        // Date - added under phone number
        String printDate = getPrintDate();
        int dateWidth = normalMetrics.stringWidth(printDate);
        int dateX = leftMargin + (int)((width - 10 - dateWidth) / 2);
        g2d.drawString(printDate, dateX, currentY);
        currentY += 15;

        // Separator line
        g2d.drawLine(leftMargin, currentY, rightMargin, currentY);
        currentY += 10;

        // Client Information
        g2d.setFont(headerFont);
        g2d.drawString("Client ID: " + invoice.clientId, leftMargin, currentY);
        currentY += 12;
        g2d.drawString("Client: " + invoice.clientName, leftMargin, currentY);
        currentY += 12;
        g2d.drawString("Facture #: " + invoice.invoiceId, leftMargin, currentY);
        currentY += 15;

        // Table Header
        g2d.setFont(smallFont);
        g2d.drawLine(leftMargin, currentY, rightMargin, currentY);
        currentY += 2;

        int col1 = leftMargin;
        int col2 = leftMargin + (int)((width - 10) * 0.15);
        int col3 = leftMargin + (int)((width - 10) * 0.60);
        int col4 = leftMargin + (int)((width - 10) * 0.80);

        g2d.drawString("Quantité", col1, currentY + 10);
        g2d.drawString("Produit", col2, currentY + 10);
        g2d.drawString("Prix Unitaire", col3, currentY + 10);
        g2d.drawString("Total", col4, currentY + 10);
        currentY += 12;

        g2d.drawLine(leftMargin, currentY, rightMargin, currentY);
        currentY += 10;

        // Invoice Items
        for (FactureItem item : invoice.items) {
            // Truncate item name if too long
            String itemName = item.getNomProduit().length() > 20 ?
                    item.getNomProduit().substring(0, 20) + "..." : item.getNomProduit();

            g2d.drawString(String.valueOf(item.getQuantity()), col1, currentY);
            g2d.drawString(itemName, col2, currentY);
            g2d.drawString(String.format("%.2f", item.getPrice())+" DT", col3, currentY);
            g2d.drawString(String.format("%.2f", item.getTotal())+" DT", col4, currentY);
            currentY += 10;
        }

        // Total line
        currentY += 5;
        g2d.drawLine(leftMargin, currentY, rightMargin, currentY);
        currentY += 12;

        g2d.setFont(headerFont);
        String totalText = "Total: " + String.format("%.2f", invoice.totalAmount) +" DT";
        int totalWidth = g2d.getFontMetrics().stringWidth(totalText);
        g2d.drawString(totalText, rightMargin - totalWidth, currentY);
        currentY += 15;

        // Notes section
        if (!invoice.notes.isEmpty()) {
            g2d.setFont(smallFont);
            g2d.drawString("NB :", leftMargin, currentY);
            currentY += 10;

            // Word wrap notes if too long
            String[] words = invoice.notes.split(" ");
            StringBuilder currentLine = new StringBuilder();
            int maxWidth = (int)(width - 20);
            FontMetrics fm = g2d.getFontMetrics();

            for (String word : words) {
                String testLine = currentLine.length() > 0 ? currentLine + " " + word : word;
                if (fm.stringWidth(testLine) > maxWidth && currentLine.length() > 0) {
                    g2d.drawString(currentLine.toString(), leftMargin, currentY);
                    currentY += 10;
                    currentLine = new StringBuilder(word);
                } else {
                    currentLine = new StringBuilder(testLine);
                }
            }

            // Draw remaining text
            if (currentLine.length() > 0) {
                g2d.drawString(currentLine.toString(), leftMargin, currentY);
            }
        }
    }

    /**
     * Main method to print invoices using DBConnection singleton
     */
    public static void printInvoices() {
        try {
            InvoicePrinter printer = new InvoicePrinter();
            printer.fetchAndPrintInvoices();
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error printing invoices: " + e.getMessage());
        }
    }

    /**
     * Fetch invoices and handle printing process
     */
    private void fetchAndPrintInvoices() {
        try {
            fetchInvoicesForPrinting();
            if (invoicesToPrint.isEmpty()) {
                JOptionPane.showMessageDialog(null, "No invoices found for printing.");
                return;
            }

            PrinterJob job = PrinterJob.getPrinterJob();

            // Set page format to landscape
            PageFormat pageFormat = job.defaultPage();
            pageFormat.setOrientation(PageFormat.LANDSCAPE);
            job.setPrintable(this, pageFormat);

            if (job.printDialog()) {
                job.print();
                JOptionPane.showMessageDialog(null,
                        "Successfully printed " + invoicesToPrint.size() + " invoices.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error printing invoices: " + e.getMessage());
        }
    }

    public static void printInvoicesFromDatabase(String dbUrl) {
        // This method is now deprecated - use printInvoices() instead
        printInvoices();
    }

    /**
     * Example usage method

     public static void main(String[] args) {
     // Example usage with DBConnection singleton
     InvoicePrinter.printInvoices();
     }
     */
}