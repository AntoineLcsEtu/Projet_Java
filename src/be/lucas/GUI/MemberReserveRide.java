package be.lucas.GUI;

import java.awt.EventQueue;
import java.awt.GridLayout;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

public class MemberReserveRide extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Member member;

    public MemberReserveRide(Member member) {
        this.member = member;
        setTitle("Reserve a Ride - " + member.getFirstName() + " " + member.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(4, 2));

        panel.add(new JLabel("Ride ID:"));
        JTextField rideIdField = new JTextField();
        panel.add(rideIdField);

        panel.add(new JLabel("Passenger:"));
        JCheckBox passengerCheck = new JCheckBox();
        panel.add(passengerCheck);

        panel.add(new JLabel("Bike:"));
        JCheckBox bikeCheck = new JCheckBox();
        panel.add(bikeCheck);

        JButton reserveButton = new JButton("Reserve");
        panel.add(reserveButton);

        reserveButton.addActionListener(e -> {
            try {
                int rideId = Integer.parseInt(rideIdField.getText());
                boolean passenger = passengerCheck.isSelected();
                boolean bike = bikeCheck.isSelected();
                Ride ride = new Ride(rideId, "Club Address", new Date(), 0.0); // Simulé
                Inscription registration = new Inscription(1, passenger, bike, ride);
                ride.addRegistration(registration);
                JOptionPane.showMessageDialog(this, "Reservation made for Ride ID " + rideId);
                dispose();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Ride ID");
            }
        });

        add(panel);
    }
}
