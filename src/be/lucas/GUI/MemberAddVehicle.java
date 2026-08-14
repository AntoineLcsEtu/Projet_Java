package be.lucas.GUI;

import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberAddVehicle extends JFrame {

    private static final long serialVersionUID = 1L;
    private final Member member;

    public MemberAddVehicle(Member member) {
        this.member = member;
        setTitle("Ajouter un véhicule - " + member.getFirstName() + " " + member.getName());
        setSize(600, 390);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        initUI();
    }

    private void initUI() {
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("AJOUTER UN VÉHICULE", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 22));
        title.setForeground(new Color(0, 90, 180));
        headerPanel.add(title, BorderLayout.NORTH);

        String ownedText;
        try {
            int count = member.getOwnedVehicles().size();
            ownedText = count == 0
                ? "Vous n'avez aucun véhicule enregistré."
                : "Vous avez déjà " + count + " véhicule(s) enregistré(s).";
        } catch (Exception ex) {
            ownedText = " ";
        }
        JLabel ownedLabel = new JLabel(ownedText, SwingConstants.CENTER);
        ownedLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        ownedLabel.setForeground(Color.GRAY);
        headerPanel.add(ownedLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Caractéristiques du véhicule"));

        formPanel.add(new JLabel("Nombre de sièges passager :"));
        JTextField seatField = new JTextField();
        seatField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(seatField);

        formPanel.add(new JLabel("Nombre de places vélo :"));
        JTextField bikeSpotField = new JTextField();
        bikeSpotField.setFont(new Font("Arial", Font.PLAIN, 16));
        formPanel.add(bikeSpotField);

        JButton addButton = new JButton("Ajouter le véhicule");
        addButton.setFont(new Font("Arial", Font.BOLD, 16));
        addButton.setBackground(Color.WHITE);
        addButton.setForeground(new Color(0, 120, 215));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dispose());

        addButton.addActionListener(e -> {
            String seatInput = seatField.getText().trim();
            String bikeSpotInput = bikeSpotField.getText().trim();

            if (seatInput.isEmpty() || bikeSpotInput.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Veuillez remplir les deux champs.", "Champ manquant", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
            	int seatNumber = Integer.parseInt(seatInput);
            	int bikeSpotNumber = Integer.parseInt(bikeSpotInput);

            	boolean success = member.addVehicle(seatNumber, bikeSpotNumber);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Véhicule ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Échec de l'ajout du véhicule.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Les champs doivent être des nombres entiers.", "Format invalide", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        buttonPanel.add(cancelButton);
        buttonPanel.add(addButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}