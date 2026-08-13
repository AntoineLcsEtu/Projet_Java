package be.lucas.GUI;

import be.lucas.Model.CategoryType;
import be.lucas.Model.Member;

import javax.swing.*;
import java.awt.*;

public class MemberAddBike extends JFrame {
    private static final long serialVersionUID = 1L;
    private Member member;

    public MemberAddBike(Member member) {
        this.member = member;
        setTitle("Ajouter un vélo - " + member.getFirstName() + " " + member.getName());
        setSize(450, 360);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JPanel headerPanel = new JPanel(new BorderLayout(0, 8));

        JLabel title = new JLabel("AJOUTER UN VÉLO", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 204));
        headerPanel.add(title, BorderLayout.NORTH);

        String ownedText;
        try {
            int count = member.getOwnedBikes().size();
            ownedText = count == 0
                ? "Vous n'avez aucun vélo enregistré."
                : "Vous avez déjà " + count + " vélo(s) enregistré(s).";
        } catch (Exception ex) {
            ownedText = " ";
        }
        JLabel ownedLabel = new JLabel(ownedText, SwingConstants.CENTER);
        ownedLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        ownedLabel.setForeground(Color.GRAY);
        headerPanel.add(ownedLabel, BorderLayout.SOUTH);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createTitledBorder("Caractéristiques du vélo"));

        formPanel.add(new JLabel("Type de vélo :"));
        JComboBox<CategoryType> typeCombo = new JComboBox<>(CategoryType.values());
        formPanel.add(typeCombo);

        formPanel.add(new JLabel("Poids (kg) :"));
        JTextField weightField = new JTextField();
        formPanel.add(weightField);

        formPanel.add(new JLabel("Longueur (cm) :"));
        JTextField lengthField = new JTextField();
        formPanel.add(lengthField);

        JButton addButton = new JButton("Ajouter le vélo");
        addButton.setFont(new Font("Arial", Font.BOLD, 14));

        JButton cancelButton = new JButton("Annuler");
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 13));
        cancelButton.addActionListener(e -> dispose());

        addButton.addActionListener(e -> {
            try {
            	double weight = Double.parseDouble(weightField.getText().trim().replace(",", "."));
            	double length = Double.parseDouble(lengthField.getText().trim().replace(",", "."));
            	CategoryType type = (CategoryType) typeCombo.getSelectedItem();

            	boolean success = member.addBike(weight, type, length);
                if (success) {
                    JOptionPane.showMessageDialog(this, "Vélo ajouté avec succès !", "Succès", JOptionPane.INFORMATION_MESSAGE);
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Erreur lors de l'ajout du vélo.", "Erreur", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Merci d'entrer des valeurs numériques valides.", "Format invalide", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Erreur : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
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