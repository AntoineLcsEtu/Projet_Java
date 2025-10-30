package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.DAO.MemberDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class MemberPayMembership extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;
    private JLabel balanceLabel;
    private JLabel feeLabel;
    private MemberDAO memberDAO = new MemberDAO();

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
        panel.add(payButton);

        payButton.addActionListener(e -> payMembership());

        add(panel);
        refreshInfo();
    }

    private void refreshInfo() {
        balanceLabel.setText("Solde actuel : " + member.getBalance() + " €");

        try {
            int categoryCount = memberDAO.getCategoryCountForMember(member.getId());
            double baseFee = 20.0;
            double totalFee = baseFee + (categoryCount * 5.0);

            feeLabel.setText(
                "<html><center>" +
                "Cotisation : " + baseFee + " € + " + categoryCount + " catégorie(s) × 5 €<br>" +
                "<b>Total à payer : " + totalFee + " €</b>" +
                "</center></html>"
            );
        } catch (SQLException e) {
            feeLabel.setText("Erreur de chargement des catégories.");
            e.printStackTrace();
        }
    }

    private void payMembership() {
        try {
            int categoryCount = memberDAO.getCategoryCountForMember(member.getId());
            double totalFee = 20.0 + (categoryCount * 5.0);

            if (member.getBalance() < totalFee) {
                JOptionPane.showMessageDialog(this,
                    "Solde insuffisant !\n" +
                    "Montant requis : " + totalFee + " €\n" +
                    "Votre solde : " + member.getBalance() + " €",
                    "Paiement impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double newBalance = member.getBalance() - totalFee;

            boolean success = memberDAO.updateMembershipPaid(
                member.getId(), newBalance, true
            );

            if (success) {
                member.setBalance(newBalance);
                member.setMembershipPaid(true);
                refreshInfo();
                JOptionPane.showMessageDialog(this,
                    "Cotisation payée avec succès !\n" +
                    "Montant débité : " + totalFee + " €\n" +
                    "Nouveau solde : " + newBalance + " €",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Échec de la mise à jour.", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur base de données : " + ex.getMessage(),
                "Erreur DB", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}