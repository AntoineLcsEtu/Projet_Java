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
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        panel.add(new JLabel("ID :"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Mot de passe :"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Se connecter");
        panel.add(new JLabel());
        panel.add(loginButton);

        add(panel);

        loginButton.addActionListener(e -> handleLogin());
    }

    private void handleLogin() {
        try {
            int id = Integer.parseInt(idField.getText().trim());
            String password = new String(passwordField.getPassword());

            Person person = Person.authenticate(id, password);

            if (person != null) {

                if (person instanceof Member member) {
                    new MemberDashboard(member).setVisible(true);

                } else if (person instanceof Manager manager) {
                    new ManagerDashboard(manager).setVisible(true);

                } else if (person instanceof Treasurer treasurer) {
                    new TreasurerDashboard(treasurer).setVisible(true);
                }

                dispose();

            } else {
                JOptionPane.showMessageDialog(this,
                        "Identifiants incorrects",
                        "Erreur",
                        JOptionPane.ERROR_MESSAGE);
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
