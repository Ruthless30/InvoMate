package GUI;

import DAO.ProduitDAO;
import MODEL.TableProduit;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ProduitGUI extends JFrame {
    JButton ajouter = new JButton("Ajouter");
    JButton modifier = new JButton("Modifier");
    JButton supprimer = new JButton("Supprimer");
    JButton retour = new JButton("Retour");

    TableProduit tc = new TableProduit();
    JTable table=new JTable(tc);
    JScrollPane jsp = new JScrollPane(table);

    JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel main_panel = new JPanel(new BorderLayout());

    ProduitDAO cd = new ProduitDAO();

    public ProduitGUI(HomeGUI previousFrame) {
        setTitle("Produit");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        // Load icon from resources
        ImageIcon icon = new ImageIcon(getClass().getResource("/boxes.png"));
        setIconImage(icon.getImage());

        tc.chargIt(cd.getAllProduit());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }


        button_panel.add(ajouter);
        button_panel.add(modifier);
        button_panel.add(supprimer);
        button_panel.add(retour);
        main_panel.add(button_panel, BorderLayout.NORTH);
        main_panel.add(jsp, BorderLayout.CENTER);

        retour.addActionListener(e -> {
            this.setVisible(false);
            previousFrame.setVisible(true);
        });

        ajouter.addActionListener(e -> {
            JDialog jd = new JDialog();

            jd.setTitle("Ajouter Produit");
            jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);


            JButton confirmer = new JButton("confirmer");

            JLabel labelName = new JLabel("Nom Produit");
            JTextField textFieldProduit = new JTextField();
            JLabel labelProduit = new JLabel("Prix");
            JTextField textFieldPrix = new JTextField();

            textFieldProduit.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();

                    // Allow backspace and delete
                    if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                        return;
                    }

                    // Allow letters (all Unicode letters including Arabic, Russian, etc.)
                    if (Character.isLetter(c)) {
                        return;
                    }

                    // Allow spaces and common punctuation
                    if (Character.isWhitespace(c) || "'-.,".contains(String.valueOf(c))) {
                        return;
                    }

                    // Block all other characters (including numbers)
                    e.consume();
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

            formPanel.add(labelName);
            formPanel.add(textFieldProduit);
            formPanel.add(labelProduit);
            formPanel.add(textFieldPrix);
            buttonPanel.add(confirmer);

            main_panel.add(formPanel, BorderLayout.NORTH);
            main_panel.add(buttonPanel, BorderLayout.SOUTH);
            jd.add(main_panel);

            confirmer.addActionListener(confirmer_action->{
                if(cd.addProduit(textFieldProduit.getText(),Double.parseDouble(textFieldPrix.getText()))){
                    tc.chargIt(cd.getAllProduit());
                    jd.dispose();
                }
                else{
                    JOptionPane.showMessageDialog(null, "Produit deja existe!", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            });


            jd.pack();
            jd.setLocationRelativeTo(null);
            jd.setVisible(true);
        });

        modifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null, "Sélectionner un Produit, svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JDialog jd_modifer = new JDialog();
                jd_modifer.setTitle("Modifier Produit");
                jd_modifer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JButton confirmer_modifier = new JButton("confirmer");

                JLabel labelName = new JLabel("Nom Produit");
                JTextField textFieldProduit = new JTextField();
                JLabel labelPrix = new JLabel("Prix");
                JTextField textFieldPrix = new JTextField();


                textFieldProduit.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();

                        // Allow backspace and delete
                        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                            return;
                        }

                        // Allow letters (all Unicode letters including Arabic, Russian, etc.)
                        if (Character.isLetter(c)) {
                            return;
                        }

                        // Allow spaces and common punctuation
                        if (Character.isWhitespace(c) || "'-.,".contains(String.valueOf(c))) {
                            return;
                        }

                        // Block all other characters (including numbers)
                        e.consume();
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
                textFieldProduit.setText((String) tc.getValueAt(row, 1));
                textFieldPrix.setText(String.valueOf(tc.getValueAt(row, 2)));


                formPanel.add(labelName);
                formPanel.add(textFieldProduit);
                formPanel.add(labelPrix);
                formPanel.add(textFieldPrix);
                buttonPanel.add(confirmer_modifier);

                main_panel.add(formPanel, BorderLayout.NORTH);
                main_panel.add(buttonPanel, BorderLayout.SOUTH);

                confirmer_modifier.addActionListener(confirmer_modifier_action->{
                    if(cd.updateProduit((int) tc.getValueAt(row, 0),textFieldProduit.getText(), Double.parseDouble(textFieldPrix.getText()))){
                        tc.chargIt(cd.getAllProduit());
                        jd_modifer.dispose();
                    }else {
                        JOptionPane.showMessageDialog(null, "Erreur de modification", "Erreur", JOptionPane.INFORMATION_MESSAGE);
                    }});

                jd_modifer.add(main_panel);
                jd_modifer.pack();
                jd_modifer.setLocationRelativeTo(null);
                jd_modifer.setVisible(true);

            }

        });

        supprimer.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,"Sélectionner un Produit, svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "Vous êtes sûr de supprimer un produit?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    cd.deleteProduit((int) tc.getValueAt(row, 0));
                    tc.chargIt(cd.getAllProduit());
                } else if (option == JOptionPane.NO_OPTION) {
                    System.out.println("Closed");
                }
            }
        });




        add(main_panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }
}
