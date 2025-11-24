package be.lucas.GUI;

import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberPayMembership extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;
    private JLabel balanceLabel;
    private JLabel feeLabel;

    public MemberPayMembership(Member member) {
        this.member = member;
        setTitle("Payer Cotisation - " + member.getFirstName() + " " + member.getName());
        setSize(450, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Payer votre cotisation annuelle", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel);

        balanceLabel = new JLabel("", SwingConstants.CENTER);
        balanceLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        panel.add(balanceLabel);

        feeLabel = new JLabel("", SwingConstants.CENTER);
        feeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        panel.add(feeLabel);

        JButton payButton = new JButton("Payer la cotisation");
        payButton.setFont(new Font("Arial", Font.BOLD, 14));
        payButton.setBackground(new Color(0, 128, 0));
        payButton.setForeground(Color.WHITE);
        panel.add(payButton);

        payButton.addActionListener(e -> payMembership());

        add(panel);
        refreshInfo();
    }

    private void refreshInfo() {
        balanceLabel.setText("Solde actuel : " + String.format("%.2f", member.getBalance()) + " €");

        try {
            int categoryCount = member.getCategoryCount();
            double totalFee = member.calculateMembershipFee();

            feeLabel.setText(
                "<html><center>" +
                    "Cotisation : 20 € + " + categoryCount + " catégorie(s) × 5 €<br>" +
                    "<b style='font-size:16px; color:#006400;'>Total à payer : " + 
                    String.format("%.2f", totalFee) + " €</b>" +
                "</center></html>"
            );
        } catch (Exception e) {
            feeLabel.setText("<html><center><span style='color:red;'>Erreur de calcul</span></center></html>");
            e.printStackTrace();
        }
    }

    private void payMembership() {
        try {
            double totalFee = member.calculateMembershipFee();

            if (!member.canPayMembership()) {
                JOptionPane.showMessageDialog(this,
                    "<html><center><b>Solde insuffisant !</b><br>" +
                    "Montant requis : <b>" + String.format("%.2f", totalFee) + " €</b><br>" +
                    "Votre solde : <b>" + String.format("%.2f", member.getBalance()) + " €</b></center></html>",
                    "Paiement impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean success = member.payMembership();

            if (success) {
                refreshInfo();
                JOptionPane.showMessageDialog(this,
                    "<html><center>Cotisation payée avec succès !<br>" +
                    "Montant débité : <b>" + String.format("%.2f", totalFee) + " €</b><br>" +
                    "Nouveau solde : <b>" + String.format("%.2f", member.getBalance()) + " €</b></center></html>",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec du paiement.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors du paiement : " + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}