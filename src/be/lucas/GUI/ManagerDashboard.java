package be.lucas.GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboard extends JFrame {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private Manager manager;

    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        setTitle("Manager Dashboard - " + manager.getFirstName() + " " + manager.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                LogoutHandler.logout(ManagerDashboard.this);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("TABLEAU DE BORD MANAGER", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        headerPanel.add(title, BorderLayout.NORTH);

        String categoryName = manager.getCategory() != null && manager.getCategory().getType() != null
            ? manager.getCategory().getType().name().replace("_", " ")
            : "Aucune catégorie";

        String rideCountText;
        try {
            int rideCount = manager.getRidesInMyCategory().size();
            rideCountText = rideCount + " trajet(s) publié(s)";
        } catch (Exception ex) {
            rideCountText = "Trajets : indisponible";
        }

        JLabel infoLabel = new JLabel(
            "Catégorie : " + categoryName + "  •  " + rideCountText,
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        infoLabel.setForeground(Color.GRAY);
        headerPanel.add(infoLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(4, 1, 10, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        buttonPanel.setBackground(new Color(248, 249, 250));

        JButton publishRideButton = new JButton("Publier un trajet");
        JButton calculateFeeButton = new JButton("Calculer le prix d’un trajet");
        JButton availabilityButton = new JButton("Vérifier les disponibilités");
        JButton logoutButton = new JButton("Se déconnecter");
        logoutButton.setForeground(Color.RED.darker());

        publishRideButton.addActionListener(e -> new ManagerPublishCalendar(manager).setVisible(true));
        calculateFeeButton.addActionListener(e -> new ManagerCalculateFee(manager).setVisible(true));
        availabilityButton.addActionListener(e -> new ManagerAvailability(manager).setVisible(true));
        logoutButton.addActionListener(e -> LogoutHandler.logout(this));

        buttonPanel.add(publishRideButton);
        buttonPanel.add(calculateFeeButton);
        buttonPanel.add(availabilityButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}