package be.lucas.GUI;


import javax.swing.JFrame;
import javax.swing.JPanel;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class MemberDashboard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Member member;

    public MemberDashboard(Member member) {
        this.member = member;
        setTitle("Member Dashboard - " + member.getFirstName() + " " + member.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 1));

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
