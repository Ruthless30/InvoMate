package GUI;

import DAO.InvoicePrinter;
import DAO.ProductionPlanningReport;

import javax.swing.*;
import java.awt.*;

public class HomeGUI extends JFrame {
    JButton client = new JButton("Clients");
    JButton facture = new JButton("Factures");
    JButton produit  = new JButton("Produits");
    JButton impression = new JButton("Imprimer");
    JButton calcul = new JButton("Calcul");
    JPanel panel = new JPanel(new GridLayout(5, 1));

    public HomeGUI() {
        setTitle("InvoMate");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 250, 300);
        setLocationRelativeTo(null);
        setResizable(false);
        // Load icon from resources
        ImageIcon icon = new ImageIcon(getClass().getResource("/invoice.png"));
        setIconImage(icon.getImage());

        panel.setBackground(Color.WHITE);
        panel.add(client);
        panel.add(produit);
        panel.add(facture);
        panel.add(impression);
        panel.add(calcul);

        client.addActionListener(e -> {
            new ClientGUI(this);
            this.setVisible(false);
        });
        facture.addActionListener(e -> {
            new FactureGUI(this);
            this.setVisible(false);
        });
        produit.addActionListener(e -> {
            new ProduitGUI(this);
            this.setVisible(false);
        });
        impression.addActionListener(e -> InvoicePrinter.printInvoices());
        calcul.addActionListener(e -> ProductionPlanningReport.printProductionReport());

        add(panel);
        setVisible(true);

    }
}
