package be.lucas.GUI;

import be.lucas.Model.Manager;
import be.lucas.Model.Ride;
import be.lucas.Model.Category;

import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;

public class ManagerPublishCalendar extends JFrame {
    private static final long serialVersionUID = 1L;
    private Manager manager;
    private JTextField placeField;
    private JTextField dateField;
    private JTextField feeField;
    private final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    public ManagerPublishCalendar(Manager manager) {
        this.manager = manager;
        setTitle("Publier un Ride - " + manager.getFirstName() + " " + manager.getName());
        setSize(500, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        String categoryName = manager.getCategory() != null && manager.getCategory().getType() != null
            ? manager.getCategory().getType().name().replace("_", " ")
            : "Inconnue";
        JLabel title = new JLabel("PUBLIER UN RIDE - " + categoryName.toUpperCase(), SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        formPanel.add(new JLabel("Lieu de départ :"));
        placeField = new JTextField("Club House");
        formPanel.add(placeField);

        formPanel.add(new JLabel("Date (jj/mm/aaaa) :"));
        dateField = new JTextField(sdf.format(new Date()));
        formPanel.add(dateField);

        formPanel.add(new JLabel("Frais initiaux (€) :"));
        feeField = new JTextField("0.0");
        formPanel.add(feeField);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton publishButton = new JButton("PUBLIER LE RIDE");
        publishButton.setFont(new Font("Arial", Font.BOLD, 14));
        publishButton.setBackground(new Color(0, 128, 0));
        publishButton.setForeground(Color.BLACK);
        buttonPanel.add(publishButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        publishButton.addActionListener(e -> publishRide());

        add(mainPanel);
    }

    private void publishRide() {
        try {
            String place = placeField.getText().trim();
            if (place.isEmpty()) {
                showError("Le lieu de départ est obligatoire.");
                return;
            }

            Date startDate;
            try {
                startDate = sdf.parse(dateField.getText().trim());
            } catch (ParseException ex) {
                showError("Format de date invalide (jj/mm/aaaa).");
                return;
            }

            double fee;
            try {
                fee = Double.parseDouble(feeField.getText().trim());
                if (fee < 0) throw new NumberFormatException();
            } catch (Exception ex) {
                showError("Frais invalides.");
                return;
            }

            Category category = manager.getCategory();
            if (category == null) {
                showError("Aucune catégorie associée.");
                return;
            }

            Ride ride = new Ride(0, place, startDate, fee);
            ride.setCategory(category);

            int generatedId = manager.publishRideWithDB(ride, category.getId());

            if (generatedId > 0) {
                ride.setId(generatedId); 
                manager.publishCalendar(ride); 

                JOptionPane.showMessageDialog(this,
                    "Ride publié avec succès !\n" +
                    "ID: " + generatedId + "\n" +
                    "Lieu: " + place + "\n" +
                    "Date: " + sdf.format(startDate),
                    "Succès", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                showError("Échec de l'insertion en base de données.");
            }

        } catch (Exception ex) {
            showError("Erreur : " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Erreur", JOptionPane.ERROR_MESSAGE);
    }
}