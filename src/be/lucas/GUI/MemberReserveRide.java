package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class MemberReserveRide extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;

    public MemberReserveRide(Member member) {
        this.member = member;
        setTitle("Réserver un Ride - " + member.getFirstName() + " " + member.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel("Rides disponibles (non réservés)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel ridesPanel = new JPanel();
        ridesPanel.setLayout(new GridLayout(0, 1, 10, 10));

        try {
            List<Ride> availableRides = member.getAvailableRides();

            if (availableRides.isEmpty()) {
                JLabel noRides = new JLabel("Aucun ride disponible pour le moment.", SwingConstants.CENTER);
                ridesPanel.add(noRides);
            } else {
                for (Ride ride : availableRides) {
                    JButton rideButton = createRideButton(ride);
                    ridesPanel.add(rideButton);
                }
            }
        } catch (Exception e) {
            JLabel error = new JLabel("Erreur : " + e.getMessage(), SwingConstants.CENTER);
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
        button.setPreferredSize(new Dimension(650, 80));

        JPanel infoPanel = new JPanel(new GridLayout(3, 1));
        infoPanel.add(new JLabel("<html><b>Ride ID: " + ride.getId() + "</b></html>"));
        infoPanel.add(new JLabel("Lieu: " + ride.getStartPlace()));
        infoPanel.add(new JLabel("Date: " + ride.getStartDate()));

        JPanel availPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availPanel.add(new JLabel("Sièges: " + ride.getAvailableSeatNumber()));
        availPanel.add(new JLabel(" | Vélo: " + ride.getAvailableBikeSpotNumber()));
        availPanel.add(new JLabel(" | Frais: " + ride.getFee() + " €"));

        button.add(infoPanel, BorderLayout.CENTER);
        button.add(availPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> showReservationDialog(ride));
        return button;
    }

    private void showReservationDialog(Ride ride) {
        JCheckBox passengerCheck = new JCheckBox("Passager");
        JCheckBox bikeCheck = new JCheckBox("Vélo");
        JCheckBox driverCheck = new JCheckBox("Conducteur (si vous avez un véhicule)");

        JPanel panel = new JPanel(new GridLayout(0, 1));
        panel.add(new JLabel("Choisissez votre rôle pour le Ride ID " + ride.getId() + ":"));
        panel.add(passengerCheck);
        panel.add(bikeCheck);
        panel.add(driverCheck);

        int result = JOptionPane.showConfirmDialog(this, panel, "Réservation", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            boolean isPassenger = passengerCheck.isSelected();
            boolean isBike = bikeCheck.isSelected();
            boolean isDriver = driverCheck.isSelected();

            if (!isPassenger && !isBike && !isDriver) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner au moins une option.");
                return;
            }

            try {
                if (isPassenger || isBike) {
                    boolean success = member.reserveRide(ride, isPassenger, isBike);
                    if (!success) {
                        JOptionPane.showMessageDialog(this, "Places insuffisantes ou déjà réservé.");
                        return;
                    }
                }

                if (isDriver) {
                    boolean successDriver = member.offerVehicleForRide(ride);
                    if (!successDriver) {
                        JOptionPane.showMessageDialog(this, "Vous n'avez pas de véhicule ou êtes déjà conducteur.");
                        return;
                    }
                }

                JOptionPane.showMessageDialog(this, "Réservation confirmée !");
                dispose();
                new MemberReserveRide(member).setVisible(true);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage());
                ex.printStackTrace();
            }
        }
    }
}
