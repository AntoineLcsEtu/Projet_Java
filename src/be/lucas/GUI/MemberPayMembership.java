package be.lucas.GUI;

import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberPayMembership extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;
    private JLabel balanceLabel;
    private JLabel feeLabel;
    private JButton payButton;
    private JTextField amountField;
    private JButton creditButton;

    public MemberPayMembership(Member member) {
        this.member = member;
        setTitle("Cotisation & Solde - " + member.getFirstName() + " " + member.getName());
        setSize(750, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refreshInfo();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("GESTION COTISATION & SOLDE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(new Color(248, 249, 250));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        balanceLabel = new JLabel();
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        centerPanel.add(balanceLabel, gbc);

        feeLabel = new JLabel();
        feeLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        feeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        centerPanel.add(feeLabel, gbc);

        payButton = new JButton("Payer la cotisation");
        payButton.setFont(new Font("Arial", Font.BOLD, 16));
        payButton.setPreferredSize(new Dimension(320, 50));
        gbc.gridy = 2;
        centerPanel.add(payButton, gbc);
        payButton.addActionListener(e -> payMembership());

        JSeparator sep = new JSeparator();
        gbc.gridy = 3; gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(30, 50, 30, 50);
        centerPanel.add(sep, gbc);

        JLabel creditTitle = new JLabel("Recharger mon solde", SwingConstants.CENTER);
        creditTitle.setFont(new Font("Arial", Font.BOLD, 18));
        creditTitle.setForeground(new Color(0, 102, 204));
        gbc.gridy = 4; gbc.insets = new Insets(10, 10, 10, 10);
        centerPanel.add(creditTitle, gbc);

        JLabel lblAmount = new JLabel("Montant (€) :");
        lblAmount.setFont(new Font("Arial", Font.PLAIN, 16));
        gbc.gridy = 5; gbc.gridwidth = 1; gbc.anchor = GridBagConstraints.EAST;
        centerPanel.add(lblAmount, gbc);

        amountField = new JTextField(12);
        amountField.setFont(new Font("Arial", Font.PLAIN, 16));
        amountField.setHorizontalAlignment(JTextField.RIGHT);
        gbc.gridx = 1; gbc.anchor = GridBagConstraints.WEST;
        centerPanel.add(amountField, gbc);

        creditButton = new JButton("Créditer le solde");
        creditButton.setFont(new Font("Arial", Font.BOLD, 16));
        creditButton.setBackground(Color.WHITE);
        creditButton.setForeground(new Color(0, 102, 204));
        creditButton.setPreferredSize(new Dimension(320, 50));
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        centerPanel.add(creditButton, gbc);

        creditButton.addActionListener(e -> creditBalanceFromUI());

        mainPanel.add(centerPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private void refreshInfo() {
        try {
            int catCount = member.getCategoryCount();
            refreshInfo(catCount);
        } catch (Exception ex) {
            feeLabel.setText("<html><center><span style='color:red;'>Erreur de calcul</span></center></html>");
            payButton.setEnabled(false);
        }
    }

    private void refreshInfo(int catCount) {
        balanceLabel.setText("Solde actuel : " + String.format("%.2f", member.getBalance()) + " €");

        double fee = member.calculateMembershipFee(catCount);

        feeLabel.setText(
        	    "<html><center>Cotisation : 20 € + " + catCount + " catégorie(s) × 5 €<br>" +
        	    "<b style='font-size:18px; color:#0066CC;'>Total : " + String.format("%.2f", fee) + " €</b></center></html>"
        	);

        if (member.isMembershipPaid()) {
            payButton.setEnabled(false);
            payButton.setText("Cotisation déjà payée ");
            payButton.setBackground(new Color(200, 200,200));
        } else {
            payButton.setEnabled(true);
            payButton.setText("Payer la cotisation");
            payButton.setBackground(Color.WHITE);
        }
    }

    private void payMembership() {
        try {
            int catCount = member.getCategoryCount();

            if (!member.canPayMembership(catCount)) {
                JOptionPane.showMessageDialog(this,
                    "<html><b>Solde insuffisant !</b><br>" +
                    "Requis : " + String.format("%.2f", member.calculateMembershipFee(catCount)) + " €<br>" +
                    "Disponible : " + String.format("%.2f", member.getBalance()) + " €</html>",
                    "Paiement impossible", JOptionPane.WARNING_MESSAGE);
                return;
            }

            boolean ok = member.payMembership(catCount);
            if (ok) {
                refreshInfo(catCount);
                JOptionPane.showMessageDialog(this,
                    "Cotisation payée avec succès !\nNouveau solde : " + String.format("%.2f", member.getBalance()) + " €",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void creditBalanceFromUI() {
        String text = amountField.getText().trim();
        if (text.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez saisir un montant.", "Champ vide", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(text.replace(',', '.'));
            if (amount <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Montant invalide.", "Erreur", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "<html>Créditer <b>" + String.format("%.2f", amount) + " €</b> sur le solde de<br>" +
            member.getFirstName() + " " + member.getName() + " ?</html>",
            "Confirmation", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = member.creditBalance(amount); 
                if (success) {
                    refreshInfo();
                    amountField.setText("");
                    JOptionPane.showMessageDialog(this,
                        "Crédit de " + String.format("%.2f", amount) + " € effectué !\nNouveau solde : " +
                        String.format("%.2f", member.getBalance()) + " €",
                        "Succès", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "Le crédit a été refusé (solde négatif interdit).", "Info", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}