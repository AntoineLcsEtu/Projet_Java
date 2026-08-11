package be.lucas.GUI;

import be.lucas.Model.Member;
import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {
    private static final long serialVersionUID = 1L;

    public MemberDashboard(Member member) {  
        setTitle("Member Dashboard - " + member.getFirstName() + " " + member.getName());
        setSize(700, 560);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLocationRelativeTo(null);

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                LogoutHandler.logout(MemberDashboard.this);
            }
        });

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        mainPanel.setBackground(Color.WHITE);

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));
        headerPanel.setBackground(Color.WHITE);

        JLabel title = new JLabel("TABLEAU DE BORD MEMBRE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 102, 204));
        headerPanel.add(title, BorderLayout.NORTH);

        JLabel welcomeLabel = new JLabel(
            "Bienvenue, " + member.getFirstName() + " " + member.getName(),
            SwingConstants.CENTER
        );
        welcomeLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        welcomeLabel.setForeground(Color.GRAY);
        headerPanel.add(welcomeLabel, BorderLayout.CENTER);

        JLabel balanceLabel = new JLabel(
            "Solde actuel : " + String.format("%.2f", member.getBalance()) + " €",
            SwingConstants.CENTER
        );
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 15));
        balanceLabel.setForeground(new Color(0, 140, 60));
        balanceLabel.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        headerPanel.add(balanceLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(6, 1, 10, 12));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(25, 60, 20, 60));
        buttonPanel.setBackground(Color.WHITE);

        JButton reserveButton = createDashboardButton("Réserver un trajet", new Color(0, 102, 204));
        JButton addVehicleButton = createDashboardButton("Ajouter un véhicule", new Color(0, 102, 204));
        JButton offerVehicleButton = createDashboardButton("Proposer un véhicule", new Color(0, 102, 204));
        JButton payMembershipButton = createDashboardButton("Payer la cotisation", new Color(0, 102, 204));
        JButton availabilityButton = createDashboardButton("Vérifier les disponibilités", new Color(0, 102, 204));
        JButton logoutButton = createDashboardButton("Se déconnecter", Color.RED.darker());

        reserveButton.addActionListener(e -> new MemberReserveRide(member).setVisible(true));
        addVehicleButton.addActionListener(e -> new MemberAddVehicle(member).setVisible(true));
        offerVehicleButton.addActionListener(e -> new MemberOfferVehicle(member).setVisible(true));
        payMembershipButton.addActionListener(e -> new MemberPayMembership(member).setVisible(true));
        availabilityButton.addActionListener(e -> new MemberAvailability(member).setVisible(true));
        logoutButton.addActionListener(e -> LogoutHandler.logout(this));

        buttonPanel.add(reserveButton);
        buttonPanel.add(addVehicleButton);
        buttonPanel.add(offerVehicleButton);
        buttonPanel.add(payMembershipButton);
        buttonPanel.add(availabilityButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createDashboardButton(String text, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setBackground(Color.WHITE);
        button.setForeground(textColor);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(textColor, 1),
            BorderFactory.createEmptyBorder(8, 15, 8, 15)
        ));
        return button;
    }
}