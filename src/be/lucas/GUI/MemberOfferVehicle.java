package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;
import be.lucas.DAO.RideDAO;
import be.lucas.DAO.VehicleDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MemberOfferVehicle extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;
    private RideDAO rideDAO = new RideDAO();
    private VehicleDAO vehicleDAO = new VehicleDAO();

    public MemberOfferVehicle(Member member) {
        this.member = member;
        setTitle("Proposer véhicule - " + member.getFirstName() + " " + member.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Rides où vous êtes inscrit (sans être conducteur)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel ridesPanel = new JPanel();
        ridesPanel.setLayout(new GridLayout(0, 1, 10, 10));

        try {
            List<Ride> eligibleRides = rideDAO.getRidesForVehicleOffer(member.getId());

            if (eligibleRides.isEmpty()) {
                JLabel noRides = new JLabel("Aucun ride éligible. Vous êtes déjà conducteur partout où vous êtes inscrit.", SwingConstants.CENTER);
                ridesPanel.add(noRides);
            } else {
                for (Ride ride : eligibleRides) {
                    JButton rideButton = createRideButton(ride);
                    ridesPanel.add(rideButton);
                }
            }
        } catch (SQLException e) {
            JLabel error = new JLabel("Erreur DB : " + e.getMessage());
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
            Vehicle vehicle = vehicleDAO.getVehicleByDriverId(member.getId());
            if (vehicle == null) {
                JOptionPane.showMessageDialog(this, "Vous n'avez pas de véhicule enregistré.", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }

            boolean alreadyDriver = ride.getVehicles().stream()
                .anyMatch(v -> v.getDriver() != null && v.getDriver().getId() == member.getId());

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
                boolean success = rideDAO.assignVehicleToRide(vehicle.getId(), ride.getId());
                if (success) {
                    JOptionPane.showMessageDialog(this, "Véhicule ajouté au Ride ID " + ride.getId() + " !");
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Échec : déjà proposé ou erreur DB.");
                }
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur DB : " + ex.getMessage());
        }
    }
}