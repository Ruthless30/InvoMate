package GUI;

import DAO.FactureDAO;
import DAO.FactureItemDAO;
import DAO.ProduitDAO;
import MODEL.*;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class FactureGUI extends JFrame {
    //BUTTONS
    JButton retour = new JButton("Retour");
    JButton consulter = new JButton("consulter");
    JButton modifier = new JButton("modifier");
    JButton upButton = new JButton("↑");
    JButton downButton = new JButton("↓");

    //THIS TABLE WILL DISPLAY THE INVOCIES THAT WILL BE PRITNED , FILTERED BY PRINT=YES COLUMN IN INVOICES
    TableFacture modelTableFactureYes = new TableFacture();
    TableComponents componentsFacture = TableBuilder(modelTableFactureYes);
    JTable TablefactureYes = componentsFacture.table;
    JScrollPane ScrollPanefactureYes = componentsFacture.scrollPane;

    //THIS TABLE WILL DISPLAY THE INVOCIES THAT WILL BE PRITNED , FILTERED BY PRINT=NO COLUMN IN INVOICES
    TableFacture modelTableFactureNo = new TableFacture();
    TableComponents componentsFactureNo = TableBuilder(modelTableFactureNo);
    JTable TablefactureNo = componentsFactureNo.table;
    JScrollPane ScrollPanefactureNo = componentsFactureNo.scrollPane;

    //CLIENT TABLE
    TableClient modelTableClient = new TableClient();
    TableComponents componentsClient = TableBuilder(modelTableClient);
    JTable TableClient = componentsClient.table;
    JScrollPane ScrollPaneClient = componentsClient.scrollPane;

    //THIS TABLE IS MADE TO DISPLAY THE ITEMS IN THE INVOICE
    TableFactureItem modelTableFactureItem = new TableFactureItem();
    TableComponents componentsProduit = TableBuilder(modelTableFactureItem);
    JTable TableProduit = componentsProduit.table;
    JScrollPane ScrollPaneProduit = componentsProduit.scrollPane;

    //THIS TABLE IS MADE TO DISPLAY THE PRODUCTS AVAILABLE
    TableProduit modelTableProduitTemp = new TableProduit();
    TableComponents componentsProduitTemp = TableBuilder(modelTableProduitTemp);
    JTable TableProduitTemp = componentsProduitTemp.table;
    JScrollPane ScrollPaneProduitTemp = componentsProduitTemp.scrollPane;

    //LABELS
    JLabel labelYes = new JLabel("Factures à Imprimées", SwingConstants.CENTER);
    JLabel labelNo = new JLabel("Factures Non Imprimées", SwingConstants.CENTER);

    //PANELS
    JPanel Button_panel = new JPanel(new FlowLayout());
    JPanel table_panel = new JPanel(new BorderLayout());
    JPanel up_down_panel = new JPanel(new GridLayout(2, 1, 0, 10));
    JPanel tableCenterPanel = new JPanel(new BorderLayout());
    JPanel tableSouthPanel = new JPanel(new BorderLayout());
    JPanel main_panel = new JPanel(new BorderLayout());

    //NECESSARY SERVICES
    ProduitDAO fp = new ProduitDAO();
    FactureDAO ff = new FactureDAO();
    FactureItemDAO fi = new FactureItemDAO();

    public FactureGUI(HomeGUI previousFrame) {
        setTitle("Factures");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        // Load icon from resources
        ImageIcon icon = new ImageIcon(getClass().getResource("/invoice.png"));
        setIconImage(icon.getImage());

        //Loading data from databse into the tables
        modelTableFactureYes.chargIt(ff.getAllFacture("YES"));
        modelTableFactureNo.chargIt(ff.getAllFacture("NO"));

        Button_panel.add(consulter);
        Button_panel.add(modifier);
        Button_panel.add(retour);


        Button_panel.setPreferredSize(new Dimension(800, 50));

        labelYes.setFont(new Font("Arial", Font.BOLD, 14)); // Optional: styling
        tableCenterPanel.add(labelYes, BorderLayout.NORTH);
        tableCenterPanel.add(ScrollPanefactureYes, BorderLayout.CENTER);
        tableCenterPanel.setPreferredSize(new Dimension(800, 200));

        labelNo.setFont(new Font("Arial", Font.BOLD, 14)); // Optional: styling
        tableSouthPanel.add(labelNo, BorderLayout.NORTH);
        tableSouthPanel.add(ScrollPanefactureNo, BorderLayout.CENTER);
        tableSouthPanel.setPreferredSize(new Dimension(800, 200));


        table_panel.add(tableCenterPanel, BorderLayout.NORTH);
        table_panel.add(tableSouthPanel, BorderLayout.SOUTH);

        up_down_panel.add(upButton, BorderLayout.NORTH);
        up_down_panel.add(downButton, BorderLayout.SOUTH);

        main_panel.add(Button_panel, BorderLayout.NORTH);
        main_panel.add(table_panel, BorderLayout.CENTER);
        main_panel.add(up_down_panel, BorderLayout.EAST);
        main_panel.add(tableSouthPanel, BorderLayout.SOUTH);


        //START RETOUR BUTTON
        retour.addActionListener(e -> {
            this.setVisible(false);
            previousFrame.setVisible(true);
        });
        //END RETOUR BUTTON

        //START MODIFIER BUTTON
        modifier.addActionListener(e -> {
            int selectedRow = TablefactureYes.getSelectedRow();
            if (selectedRow != -1) {
                JDialog jd = new JDialog();
                jd.setSize(250, 200);
                jd.setTitle("Modifier Facture");
                jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JButton confirmer = new JButton("confirmer");

                JLabel labelOrdre = new JLabel("Ordre Index");
                JTextField textFieldIndex = new JTextField();
                JLabel labelNote = new JLabel("Notes");
                JTextArea textFieldNotes = new JTextArea();

                textFieldIndex.setText(TablefactureYes.getValueAt(selectedRow, 2).toString());
                textFieldNotes.setText(TablefactureYes.getValueAt(selectedRow, 3).toString());

                // Input validation for textFieldIndex - only numbers
                textFieldIndex.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        // Allow only digits and backspace/delete
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                            e.consume(); // Ignore the key event
                        }
                    }
                });

                JPanel main_panel = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JPanel formPanel = new JPanel(new BorderLayout());

                // Create sub-panel for the first row (Ordre Index)
                JPanel indexPanel = new JPanel(new GridLayout(1, 2, 5, 5));
                indexPanel.add(labelOrdre);
                indexPanel.add(textFieldIndex);

                // Create panel for notes section with fixed height
                JPanel notesPanel = new JPanel(new BorderLayout());
                notesPanel.add(labelNote, BorderLayout.NORTH);
                JScrollPane scrollPane = new JScrollPane(textFieldNotes);
                scrollPane.setPreferredSize(new Dimension(200, 80)); // Set preferred height to about half
                notesPanel.add(scrollPane, BorderLayout.CENTER);

                // Add components using BorderLayout for better space distribution
                formPanel.add(indexPanel, BorderLayout.NORTH);
                formPanel.add(notesPanel, BorderLayout.CENTER);

                buttonPanel.add(confirmer);

                main_panel.add(formPanel, BorderLayout.NORTH);
                main_panel.add(buttonPanel, BorderLayout.SOUTH);
                jd.add(main_panel);

                confirmer.addActionListener(confirmed -> {
                    int invoiceId = Integer.parseInt(TablefactureYes.getValueAt(selectedRow, 0).toString());
                    int newOrderIndex = Integer.parseInt(textFieldIndex.getText());
                    String notes = textFieldNotes.getText();
                    if(fi.updateInvoiceOrderAndNotes(invoiceId,newOrderIndex,notes)){
                        modelTableFactureYes.chargIt(ff.getAllFacture("YES"));
                        jd.dispose();
                    }else{
                        JOptionPane.showMessageDialog(null, "Erreur.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                    }

                });

                jd.setLocationRelativeTo(null);
                jd.setVisible(true);

            } else {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner une facture.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            }
        });
        //END MODIFIER BUTTON

        //START CONSULTER BUTTON
        consulter.addActionListener(e -> {
            int row = TablefactureYes.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null, "Sélectionner un client, svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }else{
                //RETRIEVING THE INVOICE ID
                int id_facture=Integer.parseInt(TablefactureYes.getValueAt(row, 0).toString());
                //DISPLAYING THE DIALOG WHERE WE CAN SEE THE ITEMS IN THE INVOICE
                JDialog dialogAjouterProduit = new JDialog();
                dialogAjouterProduit.setTitle("Liste Produit de "+TablefactureYes.getValueAt(row, 1).toString());
                dialogAjouterProduit.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JButton ajouterBtn = new JButton("ajouter");
                JButton modifierBtn = new JButton("modifier");
                JButton supprimerBtn = new JButton("supprimer");

                JLabel produitLabel = new JLabel("Produit :");
                JLabel qteLabel = new JLabel("Quantite :");
                JLabel prixLabel = new JLabel("Prix :");

                JTextField textProduit = new JTextField("Choisir un produit");
                JTextField textQte = new JTextField(8);
                JTextField textprix = new JTextField(8);

                textQte.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        // Allow only digits and backspace/delete
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                            e.consume(); // Ignore the key event
                        }
                    }
                });
                textprix.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        // Allow only digits and backspace/delete
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                            e.consume(); // Ignore the key event
                        }
                    }
                });

                //LOADING THE INVOCIE ITEMS DATA FROM DATABASE INTO THE TABLE
                JTable tableProduit = new JTable(modelTableFactureItem);
                JScrollPane ScrollPaneProduit = new JScrollPane(tableProduit);
                modelTableFactureItem.chargIt(fi.getAllFactureItem(id_facture));

                JTable tableProduitTemp = new JTable(modelTableProduitTemp);
                JScrollPane ScrollPaneProduitTemp = new JScrollPane(tableProduitTemp);

                textProduit.setEditable(false);
                textProduit.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                textProduit.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {

                        modelTableProduitTemp.chargIt(fp.getAllProduit());
                        JDialog jd = new JDialog();
                        jd.setTitle("Choisir Produit");
                        jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                        JButton confirmer = new JButton("confirmer");

                        JPanel main_panel = new JPanel(new BorderLayout());
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

                        buttonPanel.add(confirmer);

                        main_panel.add(ScrollPaneProduitTemp, BorderLayout.NORTH);
                        main_panel.add(buttonPanel, BorderLayout.SOUTH);

                        jd.add(main_panel);

                        confirmer.addActionListener(confirmer_action -> {
                            int selectedRow = tableProduitTemp.getSelectedRow();
                            if (selectedRow != -1) {
                                String nomProduit = tableProduitTemp.getValueAt(selectedRow, 1).toString();
                                double prixProduit = (double) tableProduitTemp.getValueAt(selectedRow, 2);
                                textProduit.setText(nomProduit);
                                textprix.setText(String.valueOf(prixProduit));
                                jd.dispose();
                            } else {
                                JOptionPane.showMessageDialog(jd, "Veuillez sélectionner un produit.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                            }
                        });
                        //END OF CONFIRMER BUTTON

                        jd.pack();
                        jd.setLocationRelativeTo(null);
                        jd.setVisible(true);
                    }
                });
                //END OF TEXTPRODUIT BUTTON FIELD

                JPanel Label_panel = new JPanel(new FlowLayout(FlowLayout.CENTER,45,10));
                JPanel TextField_panel  = new JPanel(new FlowLayout());
                JPanel Button_panel = new JPanel(new FlowLayout());
                JPanel main_produit_panel = new JPanel(new BorderLayout());
                JPanel north_panel = new JPanel(new BorderLayout());

                Label_panel.add(produitLabel);
                Label_panel.add(qteLabel);
                Label_panel.add(prixLabel);

                TextField_panel.add(textProduit);
                TextField_panel.add(textQte);
                TextField_panel.add(textprix);

                Button_panel.add(ajouterBtn);
                Button_panel.add(modifierBtn);
                Button_panel.add(supprimerBtn);


                north_panel.add(Label_panel, BorderLayout.NORTH);
                north_panel.add(TextField_panel, BorderLayout.CENTER);
                north_panel.add(Button_panel, BorderLayout.SOUTH);

                main_produit_panel.add(north_panel, BorderLayout.NORTH);
                main_produit_panel.add(ScrollPaneProduit, BorderLayout.SOUTH);

                //START OF AJOUTER BUTTON
                ajouterBtn.addActionListener(ajouter_action->{
                    try {
                        String nomProduit=textProduit.getText();
                        String qteStr=textQte.getText();
                        String prixStr=textprix.getText();

                        int qte = Integer.parseInt(qteStr);
                        double prix = Double.parseDouble(prixStr);
                        int id_produit = fp.getProduitId(nomProduit);
                        double total = prix * qte;
                        System.out.println(total);

                        fi.addFactureItem(id_facture,id_produit,nomProduit,prix,qte,total);
                        modelTableFactureItem.chargIt(fi.getAllFactureItem(id_facture));
                    }
                    catch(NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Veuillez entrer des valeurs numériques valides pour quantité et prix.", "Erreur", JOptionPane.ERROR_MESSAGE);
                    }
                });
                //END OF AJOUTER BUTTON

                //START OF MODIFIER BUTTON
                modifierBtn.addActionListener(modifier_action->{
                    int selectedRow = tableProduit.getSelectedRow();
                    if (selectedRow != -1) {
                        String nomProduit = tableProduit.getValueAt(selectedRow, 0).toString();
                        JDialog jd_modifer = new JDialog();
                        jd_modifer.setTitle("Modifier Produit de Facture");
                        jd_modifer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                        JButton confirmer_modifier = new JButton("confirmer");

                        JLabel labelQte = new JLabel("Qte");
                        JTextField textFieldQte = new JTextField();
                        JLabel labelPrix = new JLabel("Prix");
                        JTextField textFieldPrix = new JTextField();

                        textFieldQte.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                                char c = e.getKeyChar();
                                // Allow only digits and backspace/delete
                                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                                    e.consume(); // Ignore the key event
                                }
                            }
                        });
                        textFieldPrix.addKeyListener(new KeyAdapter() {
                            @Override
                            public void keyTyped(KeyEvent e) {
                                char c = e.getKeyChar();
                                // Allow only digits and backspace/delete
                                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE) {
                                    e.consume(); // Ignore the key event
                                }
                            }
                        });

                        JPanel main_panel = new JPanel(new BorderLayout());
                        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                        JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));

                        //Displaying the current data that would be modified
                        textFieldPrix.setText(modelTableFactureItem.getValueAt(selectedRow, 1).toString());
                        textFieldQte.setText(modelTableFactureItem.getValueAt(selectedRow, 2).toString());


                        formPanel.add(labelQte);
                        formPanel.add(textFieldQte);
                        formPanel.add(labelPrix);
                        formPanel.add(textFieldPrix);
                        buttonPanel.add(confirmer_modifier);

                        main_panel.add(formPanel, BorderLayout.NORTH);
                        main_panel.add(buttonPanel, BorderLayout.SOUTH);

                        confirmer_modifier.addActionListener(confirmer_modifier_action->{
                            if(fi.updateFactureItem(id_facture,nomProduit, Integer.parseInt(textFieldQte.getText()),Double.parseDouble(textFieldPrix.getText()))){
                                textFieldQte.setText("");
                                modelTableFactureItem.chargIt(fi.getAllFactureItem(id_facture));
                                jd_modifer.dispose();
                            }else {
                                JOptionPane.showMessageDialog(null, "Erreur de modification", "Erreur", JOptionPane.INFORMATION_MESSAGE);
                            }});

                        jd_modifer.add(main_panel);
                        jd_modifer.pack();
                        jd_modifer.setLocationRelativeTo(null);
                        jd_modifer.setVisible(true);

                    }else {
                        JOptionPane.showMessageDialog(null, "Veuillez sélectionner un produit.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                    }
                });

                //START OF SUPPRIMER BUTTON
                supprimerBtn.addActionListener(supprimer_action->{
                    int selectedRow = tableProduit.getSelectedRow();
                    if (selectedRow != -1) {
                        fi.deleteFactureItem(id_facture, fp.getProduitId(modelTableFactureItem.getValueAt(selectedRow, 0).toString()));
                        modelTableFactureItem.chargIt(fi.getAllFactureItem(id_facture));
                    }else {
                        JOptionPane.showMessageDialog(null, "Veuillez sélectionner un produit.", "Avertissement", JOptionPane.WARNING_MESSAGE);
                    }
                });
                //END OF SUPPRIMER BUTTON


                dialogAjouterProduit.add(main_produit_panel);
                dialogAjouterProduit.pack();
                dialogAjouterProduit.setLocationRelativeTo(null);
                dialogAjouterProduit.setVisible(true);

            }
            // END OF DIALOG

        });
        //END OF CONSULTER BUTTON


        //START UP ↑ BUTTON
        upButton.addActionListener(up_action -> {
            int row = TablefactureNo.getSelectedRow();
            if (row != -1) {
                int id_facture = (int) TablefactureNo.getValueAt(row, 0);

                // Move the facture to YES
                ff.updateFacturePrint(id_facture, "YES");

                // Reload both lists
                ArrayList<Facture> listeFacture_NO = ff.getAllFacture("NO");
                ArrayList<Facture> listeFacture_YES = ff.getAllFacture("YES");

                // Assign new index to the moved facture: append to end of YES
                int new_index = listeFacture_YES.size();
                ff.updateFactureOrder(id_facture, new_index);

                // Reorder NO table indexes sequentially (1..N)
                for (int i = 0; i < listeFacture_NO.size(); i++) {
                    ff.updateFactureOrder(listeFacture_NO.get(i).getIdFacture(), i + 1);
                }

                // Refresh UI tables
                modelTableFactureYes.chargIt(ff.getAllFacture("YES"));
                modelTableFactureNo.chargIt(ff.getAllFacture("NO"));
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner une Facture.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            }
        });

        //END UP BUTTON

        //START DOWN BUTTON
        downButton.addActionListener(down_action -> {
            int row = TablefactureYes.getSelectedRow();
            if (row != -1) {
                int id_facture = (int) TablefactureYes.getValueAt(row, 0);

                // Move the facture to NO
                ff.updateFacturePrint(id_facture, "NO");

                // Reload both lists
                ArrayList<Facture> listeFacture_NO = ff.getAllFacture("NO");
                ArrayList<Facture> listeFacture_YES = ff.getAllFacture("YES");

                // Assign new index to the moved facture: append to end of NO
                int new_index = listeFacture_NO.size();
                ff.updateFactureOrder(id_facture, new_index);

                // Reorder YES table indexes sequentially (1..N)
                for (int i = 0; i < listeFacture_YES.size(); i++) {
                    ff.updateFactureOrder(listeFacture_YES.get(i).getIdFacture(), i + 1);
                }

                // Refresh UI tables
                modelTableFactureYes.chargIt(ff.getAllFacture("YES"));
                modelTableFactureNo.chargIt(ff.getAllFacture("NO"));
            } else {
                JOptionPane.showMessageDialog(null, "Veuillez sélectionner une Facture.", "Avertissement", JOptionPane.WARNING_MESSAGE);
            }
        });

        //END DOWN BUTTON

        add(main_panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }
    private class TableComponents {
        public JTable table;
        public JScrollPane scrollPane;

        public TableComponents(JTable table, JScrollPane scrollPane) {
            this.table = table;
            this.scrollPane = scrollPane;
        }
    }

    private TableComponents TableBuilder(AbstractTableModel model) {
        JTable table = new JTable(model);

        //Center the contents of the cell
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        JScrollPane scrollPane = new JScrollPane(table);
        return new TableComponents(table,scrollPane);
    }
    
}
