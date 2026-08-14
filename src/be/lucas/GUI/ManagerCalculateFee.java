package be.lucas.GUI;


import javax.swing.*;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class ManagerCalculateFee extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Manager manager;

    public ManagerCalculateFee(Manager manager) {
        this.manager = manager;
        setTitle("Calculer le prix d'un trajet - " + manager.getFirstName() + " " + manager.getName());
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private JComboBox<Ride> rideCombo;

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("CALCULER LE PRIX D'UN TRAJET", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 90, 180));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du trajet"));

        formPanel.add(new JLabel("Trajet à facturer :"));

        rideCombo = new JComboBox<>();
        rideCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        rideCombo.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 1L;
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                    boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Ride ride) {
                    setText(String.format("#%d — %s — %s (%.2f €)",
                            ride.getId(), ride.getStartPlace(), sdf.format(ride.getStartDate()), ride.getFee()));
                }
                return this;
            }
        });

        JButton calculateButton = new JButton("Calculer le prix");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 16));
        calculateButton.setBackground(Color.WHITE);
        calculateButton.setForeground(new Color(0, 120, 215));

        try {
            List<Ride> rides = manager.getRidesInMyCategory();
            if (rides.isEmpty()) {
                rideCombo.setEnabled(false);
                calculateButton.setEnabled(false);
                formPanel.add(new JLabel("Aucun trajet publié dans votre catégorie."));
            } else {
                for (Ride r : rides) {
                    rideCombo.addItem(r);
                }
                formPanel.add(rideCombo);
            }
        } catch (Exception ex) {
            rideCombo.setEnabled(false);
            calculateButton.setEnabled(false);
            formPanel.add(new JLabel("Erreur de chargement : " + ex.getMessage()));
            ex.printStackTrace();
        }

        calculateButton.addActionListener(e -> {
            Ride ride = (Ride) rideCombo.getSelectedItem();
            if (ride == null) {
                JOptionPane.showMessageDialog(this, "Veuillez sélectionner un trajet.", "Aucune sélection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                manager.calculateRideFee(ride);

                int passengers = ride.getNeededSeatNumber();
                int bikes = ride.getNeededBikeSpotNumber();
                double total = ride.getFee();

                String message = String.format(
                        "<html><h2>Prix calculé pour le trajet n°%d</h2>" +
                        "<b>Passagers inscrits :</b> %d × %.2f € = %.2f €<br>" +
                        "<b>Vélos inscrits :</b> %d × %.2f € = %.2f €<br><br>" +
                        "<h3>Total à facturer : %.2f €</h3></html>",
                        ride.getId(), passengers, Vehicle.SEAT_FEE, ride.getPassengerFeeTotal(),
                        bikes, Vehicle.BIKE_FEE, ride.getBikeFeeTotal(), total
                );
                JOptionPane.showMessageDialog(this, message, "Prix du trajet calculé", JOptionPane.INFORMATION_MESSAGE);

                dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(calculateButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }
}