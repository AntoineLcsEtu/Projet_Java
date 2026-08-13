package be.lucas.GUI;

import be.lucas.Model.Treasurer;
import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class TreasurerManagePayments extends JFrame {
    private static final long serialVersionUID = 1L;
    private Treasurer treasurer;
    private JTable table;
    private DefaultTableModel model;
    private JLabel statusLabel;
    private JButton payButton;
    private boolean paymentAlreadyDone = false;

    public TreasurerManagePayments(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Paiement Conducteurs - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(750, 550);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("PAIEMENT DES CONDUCTEURS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = { "Conducteur", "Véhicule", "Montant", "Statut" };
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
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statusLabel.setForeground(Color.GRAY);

        payButton = new JButton("PAYER LES CONDUCTEURS MAINTENANT");
        payButton.setFont(new Font("Arial", Font.BOLD, 16));
        payButton.setBackground(Color.WHITE);
        payButton.setForeground(new Color(0, 102, 204));
        payButton.setFocusPainted(false);
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payButton.setPreferredSize(new Dimension(380, 50));
        payButton.addActionListener(e -> executePayment());

        JPanel bottomPanel = new JPanel(new BorderLayout(0, 10));
        bottomPanel.add(statusLabel, BorderLayout.NORTH);
        JPanel btnWrap = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnWrap.add(payButton);
        bottomPanel.add(btnWrap, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        refreshPreview();
    }

    private void refreshPreview() {
        model.setRowCount(0);

        try {
            List<Member> drivers = treasurer.getDriversToPay();

            if (drivers.isEmpty()) {
                statusLabel.setText("Aucun conducteur à payer pour le moment — tous les rides terminés sont réglés.");
                payButton.setEnabled(false);
                payButton.setText("AUCUN PAIEMENT NÉCESSAIRE");
                payButton.setBackground(new Color(180, 180, 180));
                return;
            }

            double totalToPay = 0.0;
            for (Member driver : drivers) {
                double fee = driver.getDrivenVehicle().calculateDriverFee();
                totalToPay += fee;

                model.addRow(new Object[] {
                    driver.getFirstName() + " " + driver.getName(),
                    driver.getDrivenVehicle().getSeatNumber() + " places + " + driver.getDrivenVehicle().getBikeSpotNumber() + " empl. vélo",
                    String.format("%.2f €", fee),
                    "À payer"
                });
            }

            statusLabel.setText(String.format(
                "TOTAL À VERSER : %.2f €  •  %d conducteur(s) concerné(s)  —  action irréversible",
                totalToPay, drivers.size()
            ));

        } catch (Exception ex) {
            statusLabel.setText("Erreur lors du chargement : " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            payButton.setEnabled(false);
            ex.printStackTrace();
        }
    }

    private void executePayment() {
        if (paymentAlreadyDone) {
            JOptionPane.showMessageDialog(this,
                "Le paiement a déjà été effectué lors de cette session.\n\n" +
                "Il n'est pas possible de payer deux fois les mêmes conducteurs.",
                "Paiement déjà effectué", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "<html><body width='400'>" +
            "<h2>Confirmer le paiement définitif ?</h2>" +
            "<p>Vous êtes sur le point de créditer <b>tous</b> les conducteurs concernés.<br><br>" +
            "Cette opération est <b>irréversible</b>.</p>" +
            "</body></html>",
            "Confirmation requise",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );

        if (confirm != JOptionPane.YES_OPTION) return;

        payButton.setEnabled(false);
        payButton.setText("PAIEMENT EN COURS...");
        payButton.setBackground(new Color(255, 153, 0));
        statusLabel.setText("Paiement en cours, veuillez patienter...");

        try {
            Object[] result = treasurer.payDriver();
            @SuppressWarnings("unchecked")
            List<Member> paidDrivers = (List<Member>) result[0];
            @SuppressWarnings("unchecked")
            List<Member> failedDrivers = (List<Member>) result[1];
            double totalPaid = (Double) result[2];

            model.setRowCount(0);

            if (paidDrivers.isEmpty() && failedDrivers.isEmpty()) {
                statusLabel.setText("Aucun conducteur à payer (aucun ride terminé).");
            } else {
                for (Member driver : paidDrivers) {
                    double fee = driver.getDrivenVehicle().calculateDriverFee();
                    model.addRow(new Object[] {
                        driver.getFirstName() + " " + driver.getName(),
                        driver.getDrivenVehicle().getSeatNumber() + " places + " + driver.getDrivenVehicle().getBikeSpotNumber() + " empl. vélo",
                        String.format("%.2f €", fee),
                        "✓ Payé"
                    });
                }
                for (Member driver : failedDrivers) {
                    model.addRow(new Object[] {
                        driver.getFirstName() + " " + driver.getName(),
                        "-",
                        "-",
                        "✗ Échec"
                    });
                }
                statusLabel.setText(String.format(
                    "RÉSUMÉ : %d conducteur(s) payé(s)  •  Total : %.2f €",
                    paidDrivers.size(), totalPaid
                ));
            }

            payButton.setText("PAIEMENT EFFECTUÉ");
            payButton.setBackground(new Color(0, 153, 76));
            payButton.setEnabled(false);

            paymentAlreadyDone = true;

            JOptionPane.showMessageDialog(this,
                "<html><h2>Paiement effectué avec succès !</h2>" +
                "<b>" + String.format("%.2f €", totalPaid) + "</b> versés aux conducteurs.<br><br>" +
                "Les soldes sont à jour.</html>",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            statusLabel.setText("ERREUR FATALE : " + ex.getMessage());
            statusLabel.setForeground(Color.RED);
            payButton.setText("ÉCHEC DU PAIEMENT");
            payButton.setBackground(Color.RED);
            payButton.setEnabled(false);

            JOptionPane.showMessageDialog(this,
                "Le paiement a échoué :\n" + ex.getMessage(),
                "Erreur critique", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
