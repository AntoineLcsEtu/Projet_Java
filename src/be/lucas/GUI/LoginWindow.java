package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Manager;
import be.lucas.Model.Treasurer;
import be.lucas.Model.Person;

import javax.swing.*;
import java.awt.*;

public class LoginWindow extends JFrame {

    private static final long serialVersionUID = 2L;
    private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;

    public LoginWindow() {
        setTitle("Connexion - Club Vélo");
        setSize(420, 320);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("CONNEXION AU CLUB VÉLO", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 204));
        mainPanel.add(title, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        formPanel.setBackground(new Color(248, 249, 250));

        formPanel.add(new JLabel("ID :"));
        idField = new JTextField();
        formPanel.add(idField);

        formPanel.add(new JLabel("Mot de passe :"));
        passwordField = new JPasswordField();
        formPanel.add(passwordField);

        loginButton = new JButton("Se connecter");
        formPanel.add(new JLabel());
        formPanel.add(loginButton);

        mainPanel.add(formPanel, BorderLayout.CENTER);
        add(mainPanel);

        loginButton.addActionListener(e -> handleLogin());
        idField.addActionListener(e -> passwordField.requestFocusInWindow());
        passwordField.addActionListener(e -> handleLogin());

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                idField.requestFocusInWindow();
            }
        });
    }

    private void handleLogin() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String password = new String(passwordField.getPassword());

            Person person = Person.authenticate(id, password);

            if (person == null) {
                JOptionPane.showMessageDialog(this,
                        "ID ou mot de passe incorrect.",
                        "Échec de connexion",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            switch (person) {
	            case Member member -> new MemberDashboard(member).setVisible(true);
	            case Manager manager -> new ManagerDashboard(manager).setVisible(true);
	            case Treasurer treasurer -> new TreasurerDashboard(treasurer).setVisible(true);
	            default -> {}
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this,
                    "L'ID doit être un nombre",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Erreur inattendue : " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            new LoginWindow().setVisible(true);
        });
    }
}