package be.lucas.GUI;

import be.lucas.Model.Member;
import be.lucas.Model.Manager;
import be.lucas.Model.Treasurer;
import be.lucas.DAO.PersonDAO;
import be.lucas.DAO.MemberDAO;
import be.lucas.DAO.ManagerDAO;
import be.lucas.DAO.TreasurerDAO;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;

public class LoginWindow extends JFrame {

    /**
	 * 
	 */
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

            // DAO pour login
            PersonDAO personDAO = new PersonDAO();
            String role = personDAO.login(id, password);

            if (role != null) {
                switch (role) {
                    case "MEMBER" -> {
                        Member member = new MemberDAO().getMemberByPersonId(id);
                        if (member != null) {
                            new MemberDashboard(member).setVisible(true);
                        }
                    }
                    case "MANAGER" -> {
                        Manager manager = new ManagerDAO().getManagerByPersonId(id);
                        if (manager != null) {
                            new ManagerDashboard(manager).setVisible(true);
                        }
                    }
                    case "TREASURER" -> {
                        Treasurer treasurer = new TreasurerDAO().getTreasurerByPersonId(id);
                        if (treasurer != null) {
                            new TreasurerDashboard(treasurer).setVisible(true);
                        }
                    }
                }
                dispose(); 
            } else {
                JOptionPane.showMessageDialog(this, "Identifiants incorrects", "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "L'ID doit être un nombre", "Erreur", JOptionPane.ERROR_MESSAGE);
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Erreur base de données : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Erreur inattendue : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
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