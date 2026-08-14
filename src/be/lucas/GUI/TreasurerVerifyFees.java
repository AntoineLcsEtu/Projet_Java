package be.lucas.GUI;

import be.lucas.Model.Member;
import java.util.List;
import be.lucas.Model.Treasurer;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;

public class TreasurerVerifyFees extends JFrame {
    private static final long serialVersionUID = 1L;
    private Treasurer treasurer;
    private JTable table;
    private DefaultTableModel model;
    private JLabel statusLabel;

    public TreasurerVerifyFees(Treasurer treasurer) throws Exception {
        this.treasurer = treasurer;
        setTitle("Vérification des Cotisations");
        setSize(700, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initUI();
        refreshReport();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("RAPPORT DE VÉRIFICATION DES COTISATIONS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = { "Membre", "Solde actuel", "Statut" };
        model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);

        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.GRAY);

        JButton reminderBtn = new JButton("Envoyer les rappels");
        reminderBtn.setFont(new Font("Arial", Font.BOLD, 18));
        reminderBtn.setBackground(Color.RED);
        reminderBtn.setForeground(new Color(220, 53, 69));
        reminderBtn.setPreferredSize(new Dimension(400, 55));
        reminderBtn.setFocusPainted(false);
        reminderBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reminderBtn.addActionListener(e -> sendReminders());

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnWrap.add(reminderBtn);
        bottomPanel.add(btnWrap, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }

    private void refreshReport() {
        try {
            List<Member> members = treasurer.getMembershipReport();
            refreshReport(members);
        } catch (Exception ex) {
            statusLabel.setText("Erreur lors du chargement du rapport : " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshReport(List<Member> members) {
        model.setRowCount(0);

        if (members.isEmpty()) {
            statusLabel.setText("Aucun membre trouvé.");
            statusLabel.setForeground(Color.GRAY);
            return;
        }

        int paidCount = 0, unpaidCount = 0;

        for (Member m : members) {
            boolean isPaid = m.isMembershipPaid();
            if (isPaid) paidCount++;
            else unpaidCount++;

            model.addRow(new Object[] {
                m.getFirstName() + " " + m.getName(),
                String.format("%.2f €", m.getBalance()),
                isPaid ? "Payée" : "Non payée"
            });
        }

        statusLabel.setText(paidCount + " membre(s) à jour  •  " + unpaidCount + " membre(s) non à jour");
        statusLabel.setForeground(Color.GRAY);
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