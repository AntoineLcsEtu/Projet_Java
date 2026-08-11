package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MemberOfferVehicle extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;

    public MemberOfferVehicle(Member member) {
        this.member = member;
        setTitle("Proposer véhicule - " + member.getFirstName() + " " + member.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("RIDES OÙ VOUS ÊTES INSCRIT (SANS ÊTRE CONDUCTEUR)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel ridesPanel = new JPanel();
        ridesPanel.setLayout(new GridLayout(0, 1, 10, 10));
        ridesPanel.setBackground(new Color(248, 249, 250));

        try {
            List<Ride> eligibleRides = member.getEligibleRidesForVehicleOffer();

            if (eligibleRides.isEmpty()) {
                JLabel noRides = new JLabel("Aucun ride éligible. Vous êtes déjà conducteur partout où vous êtes inscrit.", SwingConstants.CENTER);
                ridesPanel.add(noRides);
            } else {
                for (Ride ride : eligibleRides) {
                    JButton rideButton = createRideButton(ride);
                    ridesPanel.add(rideButton);
                }
            }
        } catch (Exception e) {
            JLabel error = new JLabel("Erreur : " + e.getMessage());
            error.setForeground(Color.RED);
            ridesPanel.add(error);
            e.printStackTrace();
        }

        JScrollPane scrollPane = new JScrollPane(ridesPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        add(mainPanel);
    }

    private JButton createRideButton(Ride ride) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout());
        button.setPreferredSize(new Dimension(650, 90));

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.add(new JLabel("<html><b>Ride ID: " + ride.getId() + "</b></html>"));
        infoPanel.add(new JLabel("Lieu: " + ride.getStartPlace()));
        infoPanel.add(new JLabel("Date: " + ride.getStartDate()));
        infoPanel.add(new JLabel("Frais: " + ride.getFee() + " €"));

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.add(new JLabel("Sièges libres: " + ride.getAvailableSeatNumber()));
        statusPanel.add(new JLabel(" | Vélo libres: " + ride.getAvailableBikeSpotNumber()));

        button.add(infoPanel, BorderLayout.CENTER);
        button.add(statusPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> offerVehicleForRide(ride));
        return button;
    }

    private void offerVehicleForRide(Ride ride) {
        try {
        	List<Vehicle> ownedVehicles = member.getOwnedVehicles();
        	if (ownedVehicles.isEmpty()) {
        	    JOptionPane.showMessageDialog(this, "Vous n'avez pas de véhicule enregistré.", "Erreur", JOptionPane.ERROR_MESSAGE);
        	    return;
        	}

        	Vehicle vehicle;
        	if (ownedVehicles.size() == 1) {
        	    vehicle = ownedVehicles.get(0);
        	} else {
        	    JPanel selectionPanel = new JPanel();
        	    selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        	    selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        	    JLabel instructionLabel = new JLabel("Vous avez plusieurs véhicules enregistrés :");
        	    instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        	    instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        	    selectionPanel.add(instructionLabel);
        	    selectionPanel.add(Box.createVerticalStrut(15));

        	    ButtonGroup vehicleGroup = new ButtonGroup();
        	    JRadioButton[] radioButtons = new JRadioButton[ownedVehicles.size()];

        	    for (int i = 0; i < ownedVehicles.size(); i++) {
        	        Vehicle v = ownedVehicles.get(i);
        	        String label = String.format(
        	            "<html><b>Véhicule #%d</b> — %d siège(s), %d place(s) vélo</html>",
        	            v.getId(), v.getSeatNumber(), v.getBikeSpotNumber()
        	        );
        	        JRadioButton radio = new JRadioButton(label);
        	        radio.setFont(new Font("Arial", Font.PLAIN, 13));
        	        radio.setAlignmentX(Component.LEFT_ALIGNMENT);
        	        if (i == 0) radio.setSelected(true);

        	        vehicleGroup.add(radio);
        	        radioButtons[i] = radio;
        	        selectionPanel.add(radio);
        	        selectionPanel.add(Box.createVerticalStrut(8));
        	    }

        	    int result = JOptionPane.showConfirmDialog(
        	        this, selectionPanel, "Choisir un véhicule",
        	        JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        	    );

        	    if (result != JOptionPane.OK_OPTION) return;

        	    vehicle = ownedVehicles.get(0);
        	    for (int i = 0; i < radioButtons.length; i++) {
        	        if (radioButtons[i].isSelected()) {
        	            vehicle = ownedVehicles.get(i);
        	            break;
        	        }
        	    }
        	}

            boolean alreadyDriver = ride.isDriver(member);
            if (alreadyDriver) {
                JOptionPane.showMessageDialog(this, "Vous êtes déjà conducteur sur ce ride !", "Déjà inscrit", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(this,
                    "Proposer votre véhicule pour ce ride ?\n" +
                            "Sièges: " + vehicle.getSeatNumber() + " | Vélo: " + vehicle.getBikeSpotNumber() + "\n" +
                            "Autres véhicules seront aussi disponibles.",
                    "Confirmer", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = member.assignVehicleToRide(vehicle, ride);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Véhicule ajouté au Ride ID " + ride.getId() + " !");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Échec : déjà proposé ou erreur.");
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
        }
    }
}