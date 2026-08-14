package be.lucas.GUI;

import be.lucas.Model.Bike;
import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;

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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("RIDES DISPONIBLES (NON RÉSERVÉS)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        headerPanel.add(title, BorderLayout.NORTH);

        JLabel balanceLabel = new JLabel(
            "Solde actuel : " + String.format("%.2f", member.getBalance()) + " €",
            SwingConstants.CENTER
        );
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(Color.BLUE);
        headerPanel.add(balanceLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel ridesPanel = new JPanel();
        ridesPanel.setLayout(new GridLayout(0, 1, 10, 10));
        ridesPanel.setBackground(new Color(248, 249, 250));

        try {
        	List<Ride> availableRides = member.getAvailableRides();

        	if (availableRides.isEmpty()) {
                JLabel noRides = new JLabel("Aucun ride disponible pour le moment.", SwingConstants.CENTER);
                ridesPanel.add(noRides);
            } else {
            	for (Ride ride : availableRides) {
            	    ridesPanel.add(createRidePanel(ride));
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

    private JPanel createRidePanel(Ride ride) {
        JPanel row = new JPanel(new BorderLayout(15, 0));
        row.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        row.setPreferredSize(new Dimension(650, 100));

        JPanel infoPanel = new JPanel(new GridLayout(4, 1));
        infoPanel.add(new JLabel("<html><b>Ride ID: " + ride.getId() + "</b></html>"));

        String categoryName = "Non spécifiée";
        if (ride.getCategory() != null && ride.getCategory().getType() != null) {
            categoryName = ride.getCategory().getType().name().replace("_", " ");
        }
        infoPanel.add(new JLabel("Catégorie: " + categoryName));
        infoPanel.add(new JLabel("Lieu: " + ride.getStartPlace()));
        infoPanel.add(new JLabel("Date: " + ride.getStartDate()));

        boolean canAfford = member.canAfford(ride.getFee());
        Color feeColor = canAfford ? Color.BLACK : Color.RED;

        JPanel availPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availPanel.add(new JLabel("Sièges: " + ride.getAvailableSeatNumber()));
        availPanel.add(new JLabel(" | Vélo: " + ride.getAvailableBikeSpotNumber()));
        availPanel.add(new JLabel(" | Frais: "));
        JLabel feeLabel = new JLabel(String.format("%.2f", ride.getFee()) + " €");
        feeLabel.setForeground(feeColor);
        availPanel.add(feeLabel);

        if (!canAfford) {
            JLabel warning = new JLabel(" (Solde insuffisant)");
            warning.setForeground(Color.RED);
            availPanel.add(warning);
        }

        JPanel centerBlock = new JPanel(new BorderLayout());
        centerBlock.add(infoPanel, BorderLayout.CENTER);
        centerBlock.add(availPanel, BorderLayout.SOUTH);
        row.add(centerBlock, BorderLayout.CENTER);

        JButton reserveButton = new JButton("Réserver");
        reserveButton.setFont(new Font("Arial", Font.BOLD, 14));
        reserveButton.setFocusPainted(false);
        reserveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        reserveButton.setEnabled(canAfford);
        reserveButton.setToolTipText(canAfford ? "Réserver ce ride" : "Solde insuffisant pour ce ride");
        reserveButton.addActionListener(e -> showReservationDialog(ride));
        row.add(reserveButton, BorderLayout.EAST);

        return row;
    }

    private Vehicle chooseVehicle(List<Vehicle> ownedVehicles) {
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

        if (result != JOptionPane.OK_OPTION) return null;

        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].isSelected()) {
                return ownedVehicles.get(i);
            }
        }
        return ownedVehicles.get(0);
    }

    private Bike chooseBike(List<Bike> ownedBikes) {
        JPanel selectionPanel = new JPanel();
        selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
        selectionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel instructionLabel = new JLabel("Vous avez plusieurs vélos enregistrés :");
        instructionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        instructionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectionPanel.add(instructionLabel);
        selectionPanel.add(Box.createVerticalStrut(15));

        ButtonGroup bikeGroup = new ButtonGroup();
        JRadioButton[] radioButtons = new JRadioButton[ownedBikes.size()];

        for (int i = 0; i < ownedBikes.size(); i++) {
            Bike b = ownedBikes.get(i);
            String typeLabel = b.getType() != null ? b.getType().name().replace("_", " ") : "Type inconnu";
            String label = String.format(
                "<html><b>Vélo #%d</b> — %s, %.1f kg, %.0f cm</html>",
                b.getId(), typeLabel, b.getWeight(), b.getLength()
            );
            JRadioButton radio = new JRadioButton(label);
            radio.setFont(new Font("Arial", Font.PLAIN, 13));
            radio.setAlignmentX(Component.LEFT_ALIGNMENT);
            if (i == 0) radio.setSelected(true);

            bikeGroup.add(radio);
            radioButtons[i] = radio;
            selectionPanel.add(radio);
            selectionPanel.add(Box.createVerticalStrut(8));
        }

        int result = JOptionPane.showConfirmDialog(
            this, selectionPanel, "Choisir un vélo",
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) return null;

        for (int i = 0; i < radioButtons.length; i++) {
            if (radioButtons[i].isSelected()) {
                return ownedBikes.get(i);
            }
        }
        return ownedBikes.get(0);
    }

    private void showReservationDialog(Ride ride) {
    	if (!member.canAfford(ride.getFee())) {
            JOptionPane.showMessageDialog(this, 
                "<html><center><b>Solde insuffisant !</b><br><br>" +
                "Solde actuel : <b>" + String.format("%.2f", member.getBalance()) + " €</b><br>" +
                "Frais du ride : <b>" + String.format("%.2f", ride.getFee()) + " €</b><br><br>" +
                "Veuillez recharger votre solde avant de réserver.</center></html>", 
                "Erreur de paiement", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel(new GridLayout(0, 1));
        ButtonGroup roleGroup = new ButtonGroup();

        JRadioButton passengerRadio = new JRadioButton("Passager");
        JRadioButton driverRadio = new JRadioButton("Conducteur");
        roleGroup.add(passengerRadio);
        roleGroup.add(driverRadio);

        JCheckBox bikeCheck = new JCheckBox("Avec vélo");

        panel.add(new JLabel("Choisir votre rôle :"));
        panel.add(passengerRadio);
        panel.add(driverRadio);
        panel.add(bikeCheck);
        
        panel.add(new JLabel(" "));
        
        JLabel paymentInfo = new JLabel("<html><b>Paiement :</b> " + 
                                      String.format("%.2f", ride.getFee()) + " € " +
                                      "sera déduit de votre solde.</html>");
        paymentInfo.setForeground(Color.BLUE);
        panel.add(paymentInfo);
        
        JLabel infoLabel = new JLabel("<html><small><i>Note : Un conducteur ne peut pas être passager dans le même véhicule.</i></small></html>");
        infoLabel.setForeground(Color.GRAY);
        panel.add(infoLabel);

        int result = JOptionPane.showConfirmDialog(
            this, panel, "Réservation - Ride ID " + ride.getId(),
            JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            boolean isPassenger = passengerRadio.isSelected();
            boolean isDriver = driverRadio.isSelected();
            boolean isBike = bikeCheck.isSelected();

            if (!isPassenger && !isDriver) {
                JOptionPane.showMessageDialog(this, "Vous devez choisir un rôle !", "Erreur", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Vehicle selectedVehicle = null;
            List<Vehicle> ownedVehicles = null;
            if (isDriver) {
                try {
                    ownedVehicles = member.getOwnedVehicles();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ownedVehicles.size() > 1) {
                    selectedVehicle = chooseVehicle(ownedVehicles);
                    if (selectedVehicle == null) {
                        return;
                    }
                } else if (ownedVehicles.size() == 1) {
                    selectedVehicle = ownedVehicles.get(0);
                }
            }

            Bike selectedBike = null;
            List<Bike> ownedBikes = null;
            if (isBike) {
                try {
                    ownedBikes = member.getOwnedBikes();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (ownedBikes.size() > 1) {
                    selectedBike = chooseBike(ownedBikes);
                    if (selectedBike == null) {
                        return;
                    }
                } else if (ownedBikes.size() == 1) {
                    selectedBike = ownedBikes.get(0);
                }
            }

            try {
                double balanceBefore = member.getBalance();
                boolean success = member.reserveRide(ride, isPassenger, isBike, selectedVehicle, selectedBike, ownedVehicles, ownedBikes);

                if (!success) {
                    String msg = isDriver ?
                        "Impossible : vous n'avez pas de véhicule enregistré ou problème d'inscription." :
                        "Plus de places disponibles (sièges ou vélos).";

                    if (isDriver && isBike) {
                        msg = "Votre véhicule n'a plus de place pour un vélo.";
                    }

                    JOptionPane.showMessageDialog(this, 
                        "<html><center><b>Réservation impossible</b><br><br>" + msg + "</center></html>", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (isDriver) {
                    boolean vehicleOk = member.offerVehicleForRide(ride, selectedVehicle);
                    if (!vehicleOk) {
                        JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout de votre véhicule.", "Erreur", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }

                double balanceAfter = member.getBalance();
                double amountPaid = balanceBefore - balanceAfter;
                
                String role = isDriver ? "conducteur" : "passager";
                String bikeMsg = isBike ? " avec vélo" : "";
                JOptionPane.showMessageDialog(this, 
                    "<html><center><b style='color:green;'>✓ Réservation confirmée !</b><br><br>"
                    + "Rôle : <b>" + role + "</b>" + bikeMsg + "<br>" +
                    "Frais payés : <b>" + String.format("%.2f", amountPaid) + " €</b><br>" +
                    "Nouveau solde : <b>" + String.format("%.2f", balanceAfter) + " €</b></center></html>", 
                    "Succès", JOptionPane.INFORMATION_MESSAGE);

                dispose();
                new MemberReserveRide(member).setVisible(true);

            } catch (Exception ex) {
                String errorMsg = ex.getMessage();
                if (errorMsg.contains("Solde insuffisant")) {
                    JOptionPane.showMessageDialog(this, 
                        "<html><center><b>Solde insuffisant !</b><br><br>" +
                        errorMsg + "<br>" +
                        "Votre solde actuel : <b>" + String.format("%.2f", member.getBalance()) + " €</b><br><br>" +
                        "<i>Vous pouvez recharger votre solde via le menu principal.</i></center></html>", 
                        "Erreur de paiement", JOptionPane.ERROR_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "<html><center><b>Erreur lors de la réservation</b><br><br>" + errorMsg + "</center></html>", 
                        "Erreur", JOptionPane.ERROR_MESSAGE);
                }
                ex.printStackTrace();
            }
        }
    }
}