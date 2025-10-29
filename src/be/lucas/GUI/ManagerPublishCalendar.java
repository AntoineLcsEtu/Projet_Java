package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;

public class ManagerPublishCalendar extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Manager manager;

    public ManagerPublishCalendar(Manager manager) {
        this.manager = manager;
        setTitle("Publish Ride - " + manager.getFirstName() + " " + manager.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2));

        panel.add(new JLabel("Ride ID:"));
        JTextField rideIdField = new JTextField();
        panel.add(rideIdField);

        panel.add(new JLabel("Start Date (yyyy-MM-dd):"));
        JTextField dateField = new JTextField();
        panel.add(dateField);

        JButton publishButton = new JButton("Publish Ride");
        panel.add(publishButton);

        publishButton.addActionListener(e -> {
            try {
                int rideId = Integer.parseInt(rideIdField.getText());
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date date = sdf.parse(dateField.getText());
                Ride ride = new Ride(rideId, "Club Address", date, 0.0);
                manager.publishCalendar(ride);
                JOptionPane.showMessageDialog(this, "Ride published: ID " + rideId);
                dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Invalid input");
            }
        });

        add(panel);
    }
}
