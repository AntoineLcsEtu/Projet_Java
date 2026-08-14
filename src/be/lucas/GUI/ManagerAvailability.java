package be.lucas.GUI;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class ManagerAvailability extends JFrame {

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private Manager manager;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ManagerAvailability(Manager manager) {
        this.manager = manager;
        setTitle("Disponibilités - " + manager.getFirstName() + " " + manager.getName());
        setSize(1400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String categoryName = manager.getCategory() != null && manager.getCategory().getType() != null
            ? manager.getCategory().getType().name().replace("_", " ")
            : "Inconnue";
        JLabel title = new JLabel("RIDES DE LA CATÉGORIE : " + categoryName.toUpperCase(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        String[] columns = { "Ride ID", "Lieu", "Date", "Frais", "Sièges", "Vélo", "Inscrits", "Statut conducteurs" };
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
            if (manager.getCategory() == null) {
                statusLabel.setText("Aucune catégorie associée au manager.");
            } else {
                List<Ride> categoryRides = manager.getRidesInMyCategory();

                if (categoryRides.isEmpty()) {
                    statusLabel.setText("Aucun ride publié dans votre catégorie.");
                } else {
                    for (Ride r : categoryRides) {
                        int missingDrivers = r.getMissingDriversCount();
                        String driverStatus = missingDrivers > 0
                            ? missingDrivers + " manquant(s)"
                            : "Conducteurs OK";

                        model.addRow(new Object[] {
                            r.getId(),
                            r.getStartPlace(),
                            sdf.format(r.getStartDate()),
                            String.format("%.2f €", r.getFee()),
                            r.getAvailableSeatNumber() + " / " + r.getTotalSeatNumber(),
                            r.getAvailableBikeSpotNumber() + " / " + r.getTotalBikeSpotNumber(),
                            r.getNeededSeatNumber() + " pass. + " + r.getNeededBikeSpotNumber() + " vélo(s)",
                            driverStatus
                        });
                    }
                    statusLabel.setText(categoryRides.size() + " ride(s) publié(s)");
                }
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