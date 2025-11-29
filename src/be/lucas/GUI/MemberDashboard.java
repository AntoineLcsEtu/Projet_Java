package be.lucas.GUI;

import be.lucas.Model.Member;
import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {
    private static final long serialVersionUID = 1L;

    public MemberDashboard(Member member) {  
        setTitle("Member Dashboard - " + member.getFirstName() + " " + member.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); 
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("TABLEAU DE BORD MEMBRE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridLayout(5, 1, 10, 15));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        buttonPanel.setBackground(new Color(248, 249, 250));

        JButton reserveButton = new JButton("Réserver un trajet");
        JButton offerVehicleButton = new JButton("Proposer un véhicule");
        JButton payMembershipButton = new JButton("Payer la cotisation");
        JButton availabilityButton = new JButton("Vérifier les disponibilités");
        JButton logoutButton = new JButton("Se déconnecter");

        logoutButton.setForeground(Color.RED.darker());

        reserveButton.addActionListener(e -> new MemberReserveRide(member).setVisible(true));
        offerVehicleButton.addActionListener(e -> new MemberOfferVehicle(member).setVisible(true));
        payMembershipButton.addActionListener(e -> new MemberPayMembership(member).setVisible(true));
        availabilityButton.addActionListener(e -> new MemberAvailability(member).setVisible(true));
        
        logoutButton.addActionListener(e -> LogoutHandler.logout(this));

        buttonPanel.add(reserveButton);
        buttonPanel.add(offerVehicleButton);
        buttonPanel.add(payMembershipButton);
        buttonPanel.add(availabilityButton);
        buttonPanel.add(logoutButton);

        mainPanel.add(buttonPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}