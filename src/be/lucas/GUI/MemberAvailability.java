package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class MemberAvailability extends JFrame {
    private static final long serialVersionUID = 1L;
    private static final java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy HH:mm");

    public MemberAvailability(Member member) {
        setTitle("Mes Réservations - " + member.getFirstName() + " " + member.getName());
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("MES RÉSERVATIONS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = { "Ride ID", "Lieu", "Date", "Frais", "Sièges dispo.", "Vélo dispo.", "Statut conducteurs" };
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            private static final long serialVersionUID = 1L;
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JLabel statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        statusLabel.setForeground(Color.GRAY);

        try {
            List<Ride> reservedRides = member.getReservedRides();

            if (reservedRides.isEmpty()) {
                statusLabel.setText("Vous n'avez réservé aucun ride pour le moment.");
            } else {
                for (Ride ride : reservedRides) {
                	int missingDrivers = ride.getMissingDriversCount();
                	String driverStatus = missingDrivers > 0
                	    ? missingDrivers + " véhicule(s) sans conducteur"
                	    : "Conducteur OK";

                    model.addRow(new Object[] {
                        ride.getId(),
                        ride.getStartPlace(),
                        sdf.format(ride.getStartDate()),
                        String.format("%.2f €", ride.getFee()),
                        ride.getAvailableSeatNumber(),
                        ride.getAvailableBikeSpotNumber(),
                        driverStatus
                    });
                }
                statusLabel.setText(reservedRides.size() + " réservation(s)");
            }
        } catch (Exception e) {
            statusLabel.setText("Erreur : " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            e.printStackTrace();
        }

        JTable table = new JTable(model);
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 13));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        mainPanel.add(new JScrollPane(table), BorderLayout.CENTER);
        mainPanel.add(statusLabel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}