package be.lucas.GUI;

import be.lucas.Model.Member;
import javax.swing.*;
import java.awt.*;

public class MemberDashboard extends JFrame {
    private static final long serialVersionUID = 1L;

    public MemberDashboard(Member member) {  
        setTitle("Member Dashboard - " + member.getFirstName() + " " + member.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton reserveButton = new JButton("Reserve a Ride");
        JButton offerVehicleButton = new JButton("Offer a Vehicle");
        JButton payMembershipButton = new JButton("Pay Membership");
        JButton availabilityButton = new JButton("Check Availability");

        reserveButton.addActionListener(e -> new MemberReserveRide(member).setVisible(true));
        offerVehicleButton.addActionListener(e -> new MemberOfferVehicle(member).setVisible(true));
        payMembershipButton.addActionListener(e -> new MemberPayMembership(member).setVisible(true));
        availabilityButton.addActionListener(e -> new MemberAvailability(member).setVisible(true));

        panel.add(reserveButton);
        panel.add(offerVehicleButton);
        panel.add(payMembershipButton);
        panel.add(availabilityButton);

        add(panel);
    }
}