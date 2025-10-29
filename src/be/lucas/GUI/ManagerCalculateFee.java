package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ManagerCalculateFee extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Manager manager;

    public ManagerCalculateFee(Manager manager) {
        this.manager = manager;
        setTitle("Calculate Ride Fee - " + manager.getFirstName() + " " + manager.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 2));

        panel.add(new JLabel("Ride ID:"));
        JTextField rideIdField = new JTextField();
        panel.add(rideIdField);

        JButton calculateButton = new JButton("Calculate Fee");
        panel.add(calculateButton);

        calculateButton.addActionListener(e -> {
            try {
                int rideId = Integer.parseInt(rideIdField.getText());
                // Données simulées
                Ride ride = new Ride(rideId, "Club Address", new Date(), 0.0);
                Vehicle vehicle = new Vehicle(1, 4, 2);
                ride.addVehicle(vehicle);
                manager.calculateRideFee(ride);
                JOptionPane.showMessageDialog(this, "Fee for Ride ID " + rideId + ": €" + ride.getFee());
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Ride ID");
            }
        });

        add(panel);
    }
}
