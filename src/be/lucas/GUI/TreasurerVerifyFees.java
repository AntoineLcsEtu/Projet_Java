package be.lucas.GUI;

import be.lucas.Model.Member;
import java.util.List;
import be.lucas.Model.Treasurer;
import javax.swing.*;
import java.awt.*;

public class TreasurerVerifyFees extends JFrame {
    private static final long serialVersionUID = 1L;
    private Treasurer treasurer;
    private JTextArea reportText;

    public TreasurerVerifyFees(Treasurer treasurer) throws Exception {
        this.treasurer = treasurer;
        setTitle("Vérification des Cotisations");
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refreshReport(); 
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("RAPPORT DE VÉRIFICATION DES COTISATIONS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 24));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        reportText = new JTextArea();
        reportText.setEditable(false);
        reportText.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportText.setBackground(new Color(248, 249, 250));
        reportText.setMargin(new Insets(15, 15, 15, 15));
        mainPanel.add(new JScrollPane(reportText), BorderLayout.CENTER);

        JButton reminderBtn = new JButton("Envoyer les rappels");
        reminderBtn.setFont(new Font("Arial", Font.BOLD, 18));
        reminderBtn.setBackground(Color.RED);
        reminderBtn.setForeground(new Color(220, 53, 69));
        reminderBtn.setPreferredSize(new Dimension(400, 55));
        reminderBtn.setFocusPainted(false);
        reminderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reminderBtn.addActionListener(e -> sendReminders());

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        bottomPanel.add(reminderBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshReport() {
        try {
            List<Member> members = treasurer.getMembershipReport();
            refreshReport(members);
        } catch (Exception ex) {
            reportText.setText("Erreur lors du chargement du rapport :\n" + ex.getMessage());
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void refreshReport(List<Member> members) {
        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("           RAPPORT DE VÉRIFICATION DES COTISATIONS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        if (members.isEmpty()) {
            sb.append("Aucun membre trouvé.\n");
        } else {
            int paidCount = 0, unpaidCount = 0;

            for (int i = 0; i < members.size(); i++) {
                Member m = members.get(i);
                boolean isPaid = m.isMembershipPaid();

                if (isPaid) paidCount++;
                else unpaidCount++;

                sb.append(String.format("%3d. %s %s\n", i + 1, m.getFirstName(), m.getName()));
                sb.append(String.format("     Solde actuel   : %.2f €\n", m.getBalance()));
                sb.append(String.format("     Statut         : %s\n",
                        isPaid ? "PAYÉE " : "NON PAYÉE"));
                sb.append("     " + "-".repeat(60) + "\n");
            }

            sb.append(String.format("\nRÉSUMÉ : %d membre(s) à jour | %d membre(s) non à jour\n", paidCount, unpaidCount));
        }

        reportText.setText(sb.toString());
    }

    private void sendReminders() {
        try {
            List<Member> members = treasurer.getMembershipReport();
            List<Member> unpaidMembers = treasurer.getUnpaidMembers(members);

            String resultMessage;
            if (unpaidMembers.isEmpty()) {
                resultMessage = "Tous les membres sont à jour !\nAucun rappel à envoyer.";
            } else {
                StringBuilder names = new StringBuilder();
                for (int i = 0; i < unpaidMembers.size(); i++) {
                    if (i > 0) names.append(", ");
                    Member m = unpaidMembers.get(i);
                    names.append(m.getFirstName()).append(" ").append(m.getName());
                }
                int count = unpaidMembers.size();
                String memberWord = count == 1 ? "membre" : "membres";
                resultMessage = "Rappel envoyé à :\n" + names + "\n\n(" + count + " " + memberWord + " non à jour)";
            }

            JOptionPane.showMessageDialog(
                this,
                "<html><div style='text-align:center; font-size:15px; line-height:1.6;'>" +
                "<b style='font-size:18px; color:#DC3545;'>Rappels envoyés !</b><br><br>" +
                resultMessage.replace("\n", "<br>") +
                "</div></html>",
                "Rappels de cotisation",
                JOptionPane.INFORMATION_MESSAGE
            );

            refreshReport(members);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Erreur lors de l'envoi des rappels :\n" + ex.getMessage(),
                "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}