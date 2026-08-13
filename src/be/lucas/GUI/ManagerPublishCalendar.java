package be.lucas.GUI;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;
import be.lucas.Model.Category;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class ManagerPublishCalendar extends JFrame {
    private static final long serialVersionUID = 1L;
    private Manager manager;
    private JTextField dateField;
    private JTextField timeField;
    private JTextField feeField;
    private final SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm");
    private final SimpleDateFormat sdfFull = new SimpleDateFormat("dd/MM/yyyy HH:mm");
    private static final String FIXED_DEPARTURE_PLACE = "Club House";

    public ManagerPublishCalendar(Manager manager) {
        this.manager = manager;
        setTitle("Publier un Ride - " + manager.getFirstName() + " " + manager.getName());
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String categoryName = manager.getCategory() != null && manager.getCategory().getType() != null
            ? manager.getCategory().getType().name().replace("_", " ")
            : "Inconnue";
        JLabel title = new JLabel("PUBLIER UN RIDE - " + categoryName.toUpperCase(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        formPanel.setBackground(new Color(248, 249, 250));

        formPanel.add(new JLabel("Lieu de départ :"));
        JLabel placeLabel = new JLabel(FIXED_DEPARTURE_PLACE);
        placeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        placeLabel.setForeground(new Color(0, 102, 204));
        formPanel.add(placeLabel);

        formPanel.add(new JLabel("Date (jj/mm/aaaa) :"));
        dateField = new JTextField(sdfDate.format(new Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Heure (HH:mm) :"));
        Calendar cal = Calendar.getInstance();
        timeField = new JTextField(String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE)));
        formPanel.add(timeField);

        formPanel.add(new JLabel("Frais initiaux (€) :"));
        feeField = new JTextField("0.0");
        formPanel.add(feeField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton publishButton = new JButton("Publier le ride");
        publishButton.setFont(new Font("Arial", Font.BOLD, 14));
        publishButton.setBackground(Color.WHITE);
        publishButton.setForeground(new Color(0, 102, 204));
        buttonPanel.add(publishButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        publishButton.addActionListener(e -> publishRide());

        add(mainPanel);
    }

    private void publishRide() {
        try {
            String place = FIXED_DEPARTURE_PLACE;

            Date dateOnly;
            try {
                dateOnly = sdfDate.parse(dateField.getText().trim());
            } catch (ParseException ex) {
                showError("Format de date invalide. Utilisez jj/mm/aaaa (ex: 25/12/2024)");
                return;
            }

            Date timeOnly;
            try {
                timeOnly = sdfTime.parse(timeField.getText().trim());
            } catch (ParseException ex) {
                showError("Format d'heure invalide. Utilisez HH:mm (ex: 14:30)");
                return;
            }

            Calendar calDate = Calendar.getInstance();
            calDate.setTime(dateOnly);
            
            Calendar calTime = Calendar.getInstance();
            calTime.setTime(timeOnly);
            
            calDate.set(Calendar.HOUR_OF_DAY, calTime.get(Calendar.HOUR_OF_DAY));
            calDate.set(Calendar.MINUTE, calTime.get(Calendar.MINUTE));
            calDate.set(Calendar.SECOND, 0);
            calDate.set(Calendar.MILLISECOND, 0);
            
            Date startDate = calDate.getTime();

            double fee;
            try {
                fee = Double.parseDouble(feeField.getText().trim().replace(",", ".")); 
            } catch (NumberFormatException ex) {
                showError("Frais invalides. Utilisez un nombre positif.");
                return;
            }

            Category category = manager.getCategory();
            if (category == null) {
                showError("Aucune catégorie associée au manager.");
                return;
            }

            Ride ride = new Ride(0, place, startDate, fee);
            
            int generatedId = manager.publishRideWithDB(ride);

            if (generatedId > 0) {
                ride.setId(generatedId);
                manager.publishCalendar(ride); 

                JOptionPane.showMessageDialog(this,
                    "Ride publié avec succès !\n\n" +
                    "ID: " + generatedId + "\n" +
                    "Lieu: " + place + "\n" +
                    "Date et heure: " + sdfFull.format(startDate) + "\n" +
                    "Frais initiaux: " + String.format("%.2f", fee) + " €",
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showError("Échec de l'insertion en base de données.");
            }

        } catch (Exception ex) {
            showError("Erreur inattendue : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}