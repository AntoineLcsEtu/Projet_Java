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
        setSize(450, 320);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("AJOUTER UN VÉLO", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 18));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 15));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

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

        addButton.addActionListener(e -> {
            try {
                double weight = Double.parseDouble(weightField.getText().trim().replace(",", "."));
                double length = Double.parseDouble(lengthField.getText().trim().replace(",", "."));
                CategoryType type = (CategoryType) typeCombo.getSelectedItem();

                if (weight <= 0 || length <= 0) {
                    JOptionPane.showMessageDialog(this, "Le poids et la longueur doivent être positifs.", "Erreur", JOptionPane.WARNING_MESSAGE);
                    return;
                }

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

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
    }
}