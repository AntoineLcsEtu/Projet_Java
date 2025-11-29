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
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("RIDES DISPONIBLES (NON RÉSERVÉS)", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel balancePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel balanceLabel = new JLabel("Solde actuel : " + String.format("%.2f", member.getBalance()) + " €");
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 14));
        balanceLabel.setForeground(Color.BLUE);
        balancePanel.add(balanceLabel);
        mainPanel.add(balancePanel, BorderLayout.NORTH);

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

        boolean canAfford = member.getBalance() >= ride.getFee();
        Color feeColor = canAfford ? Color.BLACK : Color.RED;

        JPanel availPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        availPanel.add(new JLabel("Sièges: " + ride.getAvailableSeatNumber()));
        availPanel.add(new JLabel(" | Vélo: " + ride.getAvailableBikeSpotNumber()));
        availPanel.add(new JLabel(" | Frais: ", SwingConstants.CENTER));
        JLabel feeLabel = new JLabel(String.format("%.2f", ride.getFee()) + " €");
        feeLabel.setForeground(feeColor);
        availPanel.add(feeLabel);
        
        if (!canAfford) {
            JLabel warning = new JLabel(" (Solde insuffisant)", SwingConstants.CENTER);
            warning.setForeground(Color.RED);
            availPanel.add(warning);
        }

        button.add(infoPanel, BorderLayout.CENTER);
        button.add(availPanel, BorderLayout.SOUTH);

        button.addActionListener(e -> showReservationDialog(ride));
        return button;
    }

    private void showReservationDialog(Ride ride) {
        if (member.getBalance() < ride.getFee()) {
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

            try {
                double balanceBefore = member.getBalance();
                boolean success = member.reserveRide(ride, isPassenger, isBike);

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
                    boolean vehicleOk = member.offerVehicleForRide(ride);
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