package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;

public class MemberOfferVehicle extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Member member;

    public MemberOfferVehicle(Member member) {
        this.member = member;
        setTitle("Offer a Vehicle - " + member.getFirstName() + " " + member.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Number of Seats:"));
        JTextField seatsField = new JTextField();
        panel.add(seatsField);

        panel.add(new JLabel("Number of Bike Spots:"));
        JTextField bikeSpotsField = new JTextField();
        panel.add(bikeSpotsField);

        JButton offerButton = new JButton("Offer Vehicle");
        panel.add(offerButton);

        offerButton.addActionListener(e -> {
            try {
                int seats = Integer.parseInt(seatsField.getText());
                int bikeSpots = Integer.parseInt(bikeSpotsField.getText());
                Vehicle vehicle = new Vehicle(1, seats, bikeSpots);
                vehicle.setDriver(member);
                member.setDrivenVehicle(vehicle);
                JOptionPane.showMessageDialog(this, "Vehicle offered: " + seats + " seats, " + bikeSpots + " bike spots");
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid input for seats or bike spots");
            }
        });

        add(panel);
    }
}
