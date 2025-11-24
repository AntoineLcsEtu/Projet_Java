package be.lucas.GUI;

import be.lucas.Model.Treasurer;

import javax.swing.*;
import java.awt.*;

public class TreasurerManagePayments extends JFrame {
    private static final long serialVersionUID = 1L;
    private Treasurer treasurer;

    public TreasurerManagePayments(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Paiement Conducteurs - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(500, 350);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("PAIEMENT DES CONDUCTEURS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JTextArea logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        logArea.setBackground(new Color(248, 255, 248));
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton payButton = new JButton("Payer les conducteurs");
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        payButton.setBackground(new Color(0, 128, 0)); 
        payButton.setForeground(Color.WHITE);
        buttonPanel.add(payButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        payButton.addActionListener(e -> payDrivers(logArea));

        add(mainPanel);
    }

    private void payDrivers(JTextArea logArea) {
        logArea.setText("");
        StringBuilder log = new StringBuilder();
        log.append("DÉBUT DU PAIEMENT DES CONDUCTEURS\n");
        log.append("═".repeat(50)).append("\n\n");

        try {
            Object[] result = treasurer.payDriver();
            String logText   = (String) result[0];
            double totalPaid = (Double) result[1];

            int paidCount = countPaidDrivers(logText);

            logArea.setText(logText);

            JOptionPane.showMessageDialog(this,
                    String.format("Paiement terminé !\n%d conducteur(s) payé(s)\nTotal versé : %.2f €",
                            paidCount, totalPaid),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            log.append("ERREUR : ").append(ex.getMessage());
            logArea.setText(log.toString());
            JOptionPane.showMessageDialog(this,
                    "Erreur : " + ex.getMessage(),
                    "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private int countPaidDrivers(String log) {
        return (int) log.lines()
                        .filter(l -> l.trim().startsWith("✓"))
                        .count();
    }

    
}