package be.lucas.GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

public class ManagerCalculateFee extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@SuppressWarnings("unused")
	private Manager manager;

    public ManagerCalculateFee(Manager manager) {
        this.manager = manager;
        setTitle("Calculate Ride Fee - " + manager.getFirstName() + " " + manager.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("CALCULER LE PRIX D'UN TRAJET", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2));
        formPanel.setBackground(new Color(248, 249, 250));

        formPanel.add(new JLabel("Ride ID:"));
        JTextField rideIdField = new JTextField();
        formPanel.add(rideIdField);

        JButton calculateButton = new JButton("Calculate Fee");
        formPanel.add(calculateButton);

        calculateButton.addActionListener(e -> {
            try {
                int rideId = Integer.parseInt(rideIdField.getText());
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

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);
    }
}