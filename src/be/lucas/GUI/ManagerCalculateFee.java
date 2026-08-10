package be.lucas.GUI;


import javax.swing.*;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;
import java.awt.*;

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

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(20, 20));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel title = new JLabel("CALCULER LE PRIX D'UN TRAJET", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 90, 180));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Informations du trajet"));

        formPanel.add(new JLabel("ID du trajet (Ride ID) :"));
        JTextField rideIdField = new JTextField();
        rideIdField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(rideIdField);

        JButton calculateButton = new JButton("Calculer le prix");
        calculateButton.setFont(new Font("Arial", Font.BOLD, 16));
        calculateButton.setBackground(Color.WHITE);
        calculateButton.setForeground(new Color(0, 120, 215));

        calculateButton.addActionListener(e -> {
            String input = rideIdField.getText().trim();
            if (input.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez entrer un ID de trajet.", "Champ vide", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                int rideId = Integer.parseInt(input);

                Ride ride = manager.getRideById(rideId);

                if (ride == null) {
                    JOptionPane.showMessageDialog(this, "Aucun trajet trouvé avec l'ID " + rideId, "Introuvable", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                manager.calculateRideFee(ride);

                int passengers = ride.getNeededSeatNumber();
                int bikes = ride.getNeededBikeSpotNumber();
                double total = ride.getFee();

                String message = String.format(
                	    "<html><h2>Prix calculé pour le trajet n°%d</h2>" +
                	    "<b>Passagers inscrits :</b> %d × %.2f € = %.2f €<br>" +
                	    "<b>Vélos inscrits :</b> %d × %.2f € = %.2f €<br><br>" +
                	    "<h3>Total à facturer : %.2f €</h3></html>",
                	    rideId, passengers, Vehicle.SEAT_FEE, ride.getPassengerFeeTotal(),
                	    bikes, Vehicle.BIKE_FEE, ride.getBikeFeeTotal(), total
                	);
                JOptionPane.showMessageDialog(
                    this,
                    message,
                    "Prix du trajet calculé",
                    JOptionPane.INFORMATION_MESSAGE
                );

                dispose();

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "L'ID doit être un nombre entier.", "Format invalide", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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