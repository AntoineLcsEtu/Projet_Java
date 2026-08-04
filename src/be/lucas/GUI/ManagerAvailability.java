package be.lucas.GUI;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.stream.Collectors;

public class ManagerAvailability extends JFrame {

    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private Manager manager;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

    public ManagerAvailability(Manager manager) {
        this.manager = manager;
        setTitle("Disponibilités - " + manager.getFirstName() + " " + manager.getName());
        setSize(700, 500);
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

        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setBackground(new Color(248, 249, 250));
        textArea.setMargin(new Insets(10, 10, 10, 10));

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════\n");
        sb.append("           DISPONIBILITÉS DES RIDES - ").append(categoryName.toUpperCase()).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════\n\n");

        try {
            if (manager.getCategory() == null) {
                sb.append("Aucune catégorie associée au manager.\n");
            } else {
                int categoryId = manager.getCategory().getId();

                List<Ride> allRides = manager.getAllRides();

                List<Ride> categoryRides = allRides.stream()
                    .filter(r -> r.getCategoryId() == categoryId)
                    .collect(Collectors.toList());

                if (categoryRides.isEmpty()) {
                    sb.append("Aucun ride publié dans votre catégorie.\n");
                } else {
                    for (int i = 0; i < categoryRides.size(); i++) {
                        Ride r = categoryRides.get(i);

                        sb.append(String.format("%d. RIDE ID: %d\n", i + 1, r.getId()));
                        sb.append(String.format("   Lieu de départ : %s\n", r.getStartPlace()));
                        sb.append(String.format("   Date/Heure     : %s\n", sdf.format(r.getStartDate())));
                        sb.append(String.format("   Frais          : %.2f €\n", r.getFee()));
                        sb.append(String.format("   Sièges         : %d disponibles / %d total\n", 
                            r.getAvailableSeatNumber(), r.getTotalSeatNumber()));
                        sb.append(String.format("   Places vélo    : %d disponibles / %d total\n", 
                            r.getAvailableBikeSpotNumber(), r.getTotalBikeSpotNumber()));
                        sb.append(String.format("   Inscrits       : %d passager(s) + %d vélo(s)\n", 
                            r.getNeededSeatNumber(), r.getNeededBikeSpotNumber()));
                        int missingDrivers = r.getMissingDriversCount();
                        String driverStatus = missingDrivers > 0
                            ? "Besoin de " + missingDrivers + " conducteur(s)"
                            : "Tous les véhicules ont un conducteur";
                        sb.append(String.format("   Conducteurs    : %s\n", driverStatus));
                        sb.append("   " + "─".repeat(70) + "\n");
                    }
                }
            }
        } catch (Exception e) {
            sb.append("ERREUR : ").append(e.getMessage()).append("\n");
            e.printStackTrace();
        }

        textArea.setText(sb.toString());
        mainPanel.add(new JScrollPane(textArea), BorderLayout.CENTER);
        add(mainPanel);
    }
}