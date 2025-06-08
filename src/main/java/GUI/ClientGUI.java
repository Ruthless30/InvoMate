package GUI;

import DAO.ClientDAO;
import MODEL.TableClient;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class ClientGUI extends JFrame {
    JButton ajouter = new JButton("Ajouter");
    JButton modifier = new JButton("Modifier");
    JButton supprimer = new JButton("Supprimer");
    JButton credit = new JButton("Credit");
    JButton retour = new JButton("Retour");

    TableClient tc = new TableClient();
    JTable table=new JTable(tc);
    JScrollPane jsp = new JScrollPane(table);

    JPanel button_panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
    JPanel main_panel = new JPanel(new BorderLayout());

    ClientDAO cd = new ClientDAO();

    public ClientGUI(HomeGUI previousFrame) {
        setTitle("Clients");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        // Load icon from resources
        ImageIcon icon = new ImageIcon(getClass().getResource("/customers.png"));
        setIconImage(icon.getImage());

        tc.chargIt(cd.getAllClient());

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }


        button_panel.add(ajouter);
        button_panel.add(modifier);
        button_panel.add(supprimer);
        button_panel.add(credit);
        button_panel.add(retour);
        main_panel.add(button_panel, BorderLayout.NORTH);
        main_panel.add(jsp, BorderLayout.CENTER);

        retour.addActionListener(e -> {
            this.setVisible(false);
            previousFrame.setVisible(true);
        });

        ajouter.addActionListener(e -> {
           JDialog jd = new JDialog();

           jd.setTitle("Ajouter Client");
           jd.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);


           JButton confirmer = new JButton("confirmer");

           JLabel labelName = new JLabel("Nom Client");
           JTextField textFieldName = new JTextField();
           JLabel labelClient = new JLabel("Temps Livraison");
           String[] options = {"matin", "apres-midi", "nuit"};
           JComboBox<String> comboBox = new JComboBox<>(options);

            textFieldName.addKeyListener(new KeyAdapter() {
                @Override
                public void keyTyped(KeyEvent e) {
                    char c = e.getKeyChar();

                    // Allow backspace and delete
                    if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                        return;
                    }

                    // Get current text and caret position
                    String currentText = textFieldName.getText();
                    int caretPos = textFieldName.getCaretPosition();

                    // If typing a digit
                    if (Character.isDigit(c)) {
                        // Check if there are any letters after the current position
                        String textAfterCaret = currentText.substring(caretPos);
                        for (char ch : textAfterCaret.toCharArray()) {
                            if (Character.isLetter(ch)) {
                                e.consume(); // Block digit if there are letters after it
                                return;
                            }
                        }
                    }
                    // If typing a letter
                    else if (Character.isLetter(c)) {
                        // Check if there are any digits before the current position
                        String textBeforeCaret = currentText.substring(0, caretPos);
                        for (char ch : textBeforeCaret.toCharArray()) {
                            if (Character.isDigit(ch)) {
                                e.consume(); // Block letter if there are digits before it
                                return;
                            }
                        }
                    }
                    // Allow spaces and common punctuation
                    else if (!Character.isWhitespace(c) && !"'-.,".contains(String.valueOf(c))) {
                        e.consume(); // Block other characters
                    }
                }
            });

           JPanel main_panel = new JPanel(new BorderLayout());
           JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
           JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));

           formPanel.add(labelName);
           formPanel.add(textFieldName);
           formPanel.add(labelClient);
           formPanel.add(comboBox);
           buttonPanel.add(confirmer);

           main_panel.add(formPanel, BorderLayout.NORTH);
           main_panel.add(buttonPanel, BorderLayout.SOUTH);
           jd.add(main_panel);

           confirmer.addActionListener(confirmer_action->{
               if(cd.addClient(textFieldName.getText(), comboBox.getSelectedItem().toString())){
                   tc.chargIt(cd.getAllClient());
                   jd.dispose();
               }
               else{
                   JOptionPane.showMessageDialog(jd, "Client Deja Existe!", "Erreur", JOptionPane.ERROR_MESSAGE);
               }
           });


           jd.pack();
           jd.setLocationRelativeTo(null);
           jd.setVisible(true);
        });

        modifier.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null, "Sélectionner un client, svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }else{
                JDialog jd_modifer = new JDialog();
                jd_modifer.setTitle("Modifier Client");
                jd_modifer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JButton confirmer_modifier = new JButton("confirmer");

                JLabel labelName = new JLabel("Nom Client");
                JTextField textFieldName = new JTextField();
                JLabel labelClient = new JLabel("Temps Livraison");
                String[] options = {"matin", "apres-midi", "nuit"};
                JComboBox<String> comboBox = new JComboBox<>(options);

                textFieldName.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();

                        // Allow backspace and delete
                        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                            return;
                        }

                        // Get current text and caret position
                        String currentText = textFieldName.getText();
                        int caretPos = textFieldName.getCaretPosition();

                        // If typing a digit
                        if (Character.isDigit(c)) {
                            // Check if there are any letters after the current position
                            String textAfterCaret = currentText.substring(caretPos);
                            for (char ch : textAfterCaret.toCharArray()) {
                                if (Character.isLetter(ch)) {
                                    e.consume(); // Block digit if there are letters after it
                                    return;
                                }
                            }
                        }
                        // If typing a letter
                        else if (Character.isLetter(c)) {
                            // Check if there are any digits before the current position
                            String textBeforeCaret = currentText.substring(0, caretPos);
                            for (char ch : textBeforeCaret.toCharArray()) {
                                if (Character.isDigit(ch)) {
                                    e.consume(); // Block letter if there are digits before it
                                    return;
                                }
                            }
                        }
                        // Allow spaces and common punctuation
                        else if (!Character.isWhitespace(c) && !"'-.,".contains(String.valueOf(c))) {
                            e.consume(); // Block other characters
                        }
                    }
                });



                JPanel main_panel = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));

                //Displaying the current data that would be modified
                textFieldName.setText((String) tc.getValueAt(row, 1)); // assuming name is in column 1
                comboBox.setSelectedItem((String) tc.getValueAt(row, 2)); // assuming delivery time is in column 2

                formPanel.add(labelName);
                formPanel.add(textFieldName);
                formPanel.add(labelClient);
                formPanel.add(comboBox);
                buttonPanel.add(confirmer_modifier);

                main_panel.add(formPanel, BorderLayout.NORTH);
                main_panel.add(buttonPanel, BorderLayout.SOUTH);

                confirmer_modifier.addActionListener(confirmer_modifier_action->{
                    if(cd.updateClient((int) tc.getValueAt(row, 0),textFieldName.getText(), comboBox.getSelectedItem().toString())){
                        tc.chargIt(cd.getAllClient());
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
            int id =(int) tc.getValueAt(row, 0);
            if(row==-1){
                JOptionPane.showMessageDialog(null,"Sélectionner un Client svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }
            else{
                int option = JOptionPane.showConfirmDialog(
                        null,
                        "Vous êtes sûr de supprimer Client?",
                        "Confirm",
                        JOptionPane.YES_NO_OPTION
                );

                if (option == JOptionPane.YES_OPTION) {
                    cd.deleteClient(id);
                    tc.chargIt(cd.getAllClient());
                } else if (option == JOptionPane.NO_OPTION) {
                    System.out.println("Closed");
                }
            }
        });

        credit.addActionListener(e -> {
            int row = table.getSelectedRow();
            if(row==-1){
                JOptionPane.showMessageDialog(null,"Sélectionner un Client svp!", "Erreur", JOptionPane.INFORMATION_MESSAGE);
            }else{
                int id =(int) tc.getValueAt(row, 0);
                JDialog jd_modifer = new JDialog();
                jd_modifer.setTitle("Modifier Client");
                jd_modifer.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);

                JButton confirmer_credit = new JButton("confirmer");

                JLabel labelCredit = new JLabel("Credit");
                JTextField textFieldCredit = new JTextField();

                textFieldCredit.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();

                        // Allow backspace and delete
                        if (c == KeyEvent.VK_BACK_SPACE || c == KeyEvent.VK_DELETE) {
                            return;
                        }

                        // Get current text and caret position
                        String currentText = textFieldCredit.getText();
                        int caretPos = textFieldCredit.getCaretPosition();

                        // Allow digits anywhere
                        if (Character.isDigit(c)) {
                            return;
                        }

                        // Allow minus sign only at the beginning and only if there isn't one already
                        if (c == '-') {
                            if (caretPos == 0 && !currentText.contains("-")) {
                                return; // Allow minus at the beginning
                            }
                        }

                        // Allow decimal point for credit amounts (only one decimal point)
                        if (c == '.' || c == ',') {
                            if (!currentText.contains(".") && !currentText.contains(",")) {
                                return; // Allow one decimal separator
                            }
                        }

                        // Block all other characters
                        e.consume();
                    }
                });

                JPanel main_panel = new JPanel(new BorderLayout());
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
                JPanel formPanel = new JPanel(new GridLayout(2, 2, 5, 5));

                //Displaying the current data that would be modified
                textFieldCredit.setText(String.valueOf(tc.getValueAt(row, 3)));

                formPanel.add(labelCredit);
                formPanel.add(textFieldCredit);
                buttonPanel.add(confirmer_credit);

                main_panel.add(formPanel, BorderLayout.NORTH);
                main_panel.add(buttonPanel, BorderLayout.SOUTH);

                confirmer_credit.addActionListener(confirmer_modifier_action->{
                    if(cd.updateCredit(id,Double.parseDouble(textFieldCredit.getText()))){
                        tc.chargIt(cd.getAllClient());
                        jd_modifer.dispose();
                    }else {
                        JOptionPane.showMessageDialog(null, "Erreur de modification", "Erreur", JOptionPane.INFORMATION_MESSAGE);
                    }

                });


                jd_modifer.add(main_panel);
                jd_modifer.pack();
                jd_modifer.setLocationRelativeTo(null);
                jd_modifer.setVisible(true);
            }
        });

        add(main_panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);


    }
}
