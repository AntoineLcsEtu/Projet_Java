package be.lucas.GUI;


import javax.swing.JFrame;
import javax.swing.JPanel;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.Date;

import javax.swing.*;
import java.awt.*;

public class ManagerDashboard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Manager manager;

    public ManagerDashboard(Manager manager) {
        this.manager = manager;
        setTitle("Manager Dashboard - " + manager.getFirstName() + " " + manager.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 1));

        JButton publishRideButton = new JButton("Publish Ride");
        JButton calculateFeeButton = new JButton("Calculate Ride Fee");
        JButton availabilityButton = new JButton("Check Availability");

        publishRideButton.addActionListener(e -> new ManagerPublishCalendar(manager).setVisible(true));
        calculateFeeButton.addActionListener(e -> new ManagerCalculateFee(manager).setVisible(true));
        availabilityButton.addActionListener(e -> new ManagerAvailability(manager).setVisible(true));

        panel.add(publishRideButton);
        panel.add(calculateFeeButton);
        panel.add(availabilityButton);

        add(panel);
    }
}
