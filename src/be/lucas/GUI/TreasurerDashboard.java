package be.lucas.GUI;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.util.List;

import be.lucas.Model.*;

import java.awt.*;

public class TreasurerDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private Treasurer treasurer;

    public TreasurerDashboard(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Treasurer Dashboard - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                LogoutHandler.logout(TreasurerDashboard.this);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("TABLEAU DE BORD TRÉSORIER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        headerPanel.add(title, BorderLayout.NORTH);

        String infoText;
        try {
            List<Member> members = treasurer.getMembershipReport();
            int unpaidCount = treasurer.getUnpaidMembers(members).size();
            int driversToPayCount = treasurer.getDriversToPay().size();
            infoText = unpaidCount + " cotisation(s) en retard  •  " + driversToPayCount + " conducteur(s) à payer";
        } catch (Exception ex) {
            infoText = "Statistiques indisponibles";
        }

        JLabel infoLabel = new JLabel(infoText, SwingConstants.CENTER);
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(Color.GRAY);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(3, 1, 10, 20));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        buttonPanel.setBackground(new Color(248, 249, 250));

        JButton verifyFeesButton = new JButton("Vérifier les cotisations");
        JButton payDriverButton = new JButton("Gérer les paiements chauffeurs");
        JButton logoutButton = new JButton("Se déconnecter");
        logoutButton.setForeground(Color.RED.darker());

        verifyFeesButton.addActionListener(e -> {
            try {
                new TreasurerVerifyFees(treasurer).setVisible(true);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });
        
        payDriverButton.addActionListener(e -> new TreasurerManagePayments(treasurer).setVisible(true));
        logoutButton.addActionListener(e -> LogoutHandler.logout(this));

        buttonPanel.add(verifyFeesButton);
        buttonPanel.add(payDriverButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}