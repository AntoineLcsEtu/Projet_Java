package be.lucas.GUI;

import be.lucas.Model.*;
import be.lucas.DAO.InscriptionDAO;
import be.lucas.DAO.RideDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class MemberAvailability extends JFrame {
    private static final long serialVersionUID = 1L;

    public MemberAvailability(Member member) {  
        setTitle("Mes Réservations - " + member.getFirstName() + " " + member.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));

        try {
            InscriptionDAO inscriptionDAO = new InscriptionDAO();
            List<Ride> reservedRides = inscriptionDAO.getRidesByMemberId(member.getId());

            StringBuilder sb = new StringBuilder("=== MES RÉSERVATIONS ===\n\n");
            if (reservedRides.isEmpty()) {
                sb.append("Vous n'avez réservé aucun ride pour le moment.\n");
            } else {
                RideDAO rideDAO = new RideDAO();
                for (int i = 0; i < reservedRides.size(); i++) {
                    Ride ride = rideDAO.getRideWithDetails(reservedRides.get(i).getId());
                    if (ride == null) continue;

                    sb.append("Réservation ").append(i + 1).append(" :\n");
                    sb.append("  Ride ID: ").append(ride.getId()).append("\n");
                    sb.append("  Lieu: ").append(ride.getStartPlace()).append("\n");
                    sb.append("  Date: ").append(ride.getStartDate()).append("\n");
                    sb.append("  Frais: ").append(ride.getFee()).append(" €\n");
                    sb.append("  Sièges disponibles: ").append(ride.getAvailableSeatNumber()).append("\n");
                    sb.append("  Places vélo disponibles: ").append(ride.getAvailableBikeSpotNumber()).append("\n");
                    sb.append("  Conducteurs: ").append(ride.checkDriverNeeds()).append("\n");
                    sb.append("----------------------------------------\n");
                }
            }
            textArea.setText(sb.toString());

        } catch (SQLException e) {
            textArea.setText("Erreur DB : " + e.getMessage());
            e.printStackTrace();
        }

        panel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(panel);
    }
}