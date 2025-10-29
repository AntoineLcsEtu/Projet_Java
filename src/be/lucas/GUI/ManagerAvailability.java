package be.lucas.GUI;

import javax.swing.JFrame;
import javax.swing.JPanel;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ManagerAvailability extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Manager manager;

    public ManagerAvailability(Manager manager) {
        this.manager = manager;
        setTitle("Check Availability - " + manager.getFirstName() + " " + manager.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Données simulées
        List<Ride> rides = new ArrayList<>();
        Ride ride = new Ride(1, "Club Address", new Date(), 50.0);
        Vehicle vehicle = new Vehicle(1, 4, 2);
        ride.addVehicle(vehicle);
        rides.add(ride);

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea availabilityText = new JTextArea();
        availabilityText.setEditable(false);

        StringBuilder availability = new StringBuilder("Ride Availability:\n");
        for (Ride r : rides) {
            availability.append("Ride ID: ").append(r.getId())
                        .append(", Seats Available: ").append(r.getAvailableSeatNumber())
                        .append(", Bike Spots Available: ").append(r.getAvailableBikeSpotNumber())
                        .append("\n").append(r.checkDriverNeeds()).append("\n");
        }
        availabilityText.setText(availability.toString());

        panel.add(new JScrollPane(availabilityText), BorderLayout.CENTER);
        add(panel);
    }
}
