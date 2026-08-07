package be.lucas.GUI;

import be.lucas.Model.Treasurer;
import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class TreasurerManagePayments extends JFrame {
    private static final long serialVersionUID = 1L;
    private Treasurer treasurer;
    private JTextArea logArea;
    private JButton payButton;
    private boolean paymentAlreadyDone = false;

    public TreasurerManagePayments(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Paiement Conducteurs - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(850, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("PAIEMENT DES CONDUCTEURS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        logArea = new JTextArea();
        logArea.setEditable(false);
        logArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        logArea.setBackground(new Color(248, 249, 250));
        logArea.setMargin(new Insets(15, 15, 15, 15));
        mainPanel.add(new JScrollPane(logArea), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

        payButton = new JButton("PAYER LES CONDUCTEURS MAINTENANT");
        payButton.setFont(new Font("Arial", Font.BOLD, 16));
        payButton.setBackground(Color.WHITE);
        payButton.setForeground(new Color(0, 102, 204));
        payButton.setFocusPainted(false);
        payButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        payButton.setPreferredSize(new Dimension(380, 50));

        buttonPanel.add(payButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        payButton.addActionListener(e -> executePayment());

        add(mainPanel);

        refreshPreview(); 
    }

    private void refreshPreview() {
        StringBuilder preview = new StringBuilder();
        preview.append("═══════════════════════════════════════════════════════════════════════\n");
        preview.append("           PRÉVISUALISATION DES PAIEMENTS À EFFECTUER\n");
        preview.append("═══════════════════════════════════════════════════════════════════════\n\n");

        try {
            List<Member> drivers = treasurer.getDriversToPay();  

            if (drivers.isEmpty()) {
                preview.append("Aucun conducteur à payer pour le moment.\n\n");
                preview.append("Tous les rides terminés ont déjà été réglés.\n");
                payButton.setEnabled(false);
                payButton.setText("AUCUN PAIEMENT NÉCESSAIRE");
                payButton.setBackground(new Color(180, 180, 180));
            } else {
                double totalToPay = 0.0;

                for (int i = 0; i < drivers.size(); i++) {
                    Member driver = drivers.get(i);
                    double fee = driver.getDrivenVehicle().calculateDriverFee();
                    
                    totalToPay += fee;

                    preview.append(String.format("%2d. %s %s\n", i + 1, driver.getFirstName(), driver.getName()));
                    preview.append(String.format("    → Véhicule : %d places + %d empl. vélo\n",
                            driver.getDrivenVehicle().getSeatNumber(),
                            driver.getDrivenVehicle().getBikeSpotNumber()));
                    preview.append(String.format("    → Gain prévu : %.2f €\n", fee));
                    preview.append("    " + "─".repeat(60) + "\n");
                }

                preview.append("\n");
                preview.append("═".repeat(75)).append("\n");
                preview.append(String.format("   TOTAL À VERSER : %.2f €\n", totalToPay));
                preview.append(String.format("   CONDUCTEURS CONCERNÉS : %d\n", drivers.size()));
                preview.append("═".repeat(75)).append("\n\n");
                preview.append("Cliquez sur le bouton vert pour valider le paiement.\n");
                preview.append("Cette action est irréversible et crédite immédiatement les soldes.\n");
            }

        } catch (Exception ex) {
            preview.append("ERREUR lors du chargement :\n");
            preview.append(ex.getMessage() + "\n");
            payButton.setEnabled(false);
            ex.printStackTrace();
        }

        logArea.setText(preview.toString());
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

        logArea.append("\nPaiement en cours, veuillez patienter...\n\n");
        logArea.setCaretPosition(logArea.getDocument().getLength());

        try {
        	Object[] result = treasurer.payDriver();
        	@SuppressWarnings("unchecked")
        	List<Member> paidDrivers = (List<Member>) result[0];
        	@SuppressWarnings("unchecked")
        	List<Member> failedDrivers = (List<Member>) result[1];
        	double totalPaid = (Double) result[2];

        	StringBuilder log = new StringBuilder();
        	if (paidDrivers.isEmpty() && failedDrivers.isEmpty()) {
        	    log.append("Aucun conducteur à payer (aucun ride terminé).\n");
        	} else {
        	    for (Member driver : paidDrivers) {
        	        double fee = driver.getDrivenVehicle().calculateDriverFee();
        	        log.append(String.format("✓ %s %s : +%.2f € (sièges: %d, vélo: %d)\n",
        	                driver.getFirstName(), driver.getName(), fee,
        	                driver.getDrivenVehicle().getSeatNumber(),
        	                driver.getDrivenVehicle().getBikeSpotNumber()));
        	    }
        	    for (Member driver : failedDrivers) {
        	        log.append(String.format("✗ ÉCHEC pour %s %s\n", driver.getFirstName(), driver.getName()));
        	    }
        	    log.append("\n").append("═".repeat(50)).append("\n");
        	    log.append(String.format("RÉSUMÉ : %d conducteur(s) payé(s) | Total : %.2f €\n", paidDrivers.size(), totalPaid));
        	}

        	String logText = log.toString();
        	logArea.setText(logText);

            payButton.setText("PAIEMENT EFFECTUÉ ");
            payButton.setBackground(new Color(0, 153, 76));
            payButton.setEnabled(false);

            paymentAlreadyDone = true;

            JOptionPane.showMessageDialog(this,
                "<html><h2>Paiement effectué avec succès !</h2>" +
                "<b>" + String.format("%.2f €", totalPaid) + "</b> versés aux conducteurs.<br><br>" +
                "Les soldes sont à jour.</html>",
                "Succès", JOptionPane.INFORMATION_MESSAGE);

            refreshPreview();

        } catch (Exception ex) {
            logArea.append("\nERREUR FATALE : " + ex.getMessage() + "\n");
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