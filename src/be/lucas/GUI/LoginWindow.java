package be.lucas.GUI;

import javax.swing.*;

import be.lucas.Model.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
public class LoginWindow extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField idField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private List<Member> members;
    private List<Manager> managers;
    private List<Treasurer> treasurers;

    public LoginWindow() {
        // Données simulées
        members = new ArrayList<>();
        managers = new ArrayList<>();
        treasurers = new ArrayList<>();

        Calendar calendar = new Calendar(1, null);
        Category category = new Category(1, calendar, CategoryType.ROAD_BIKE);
        calendar.setCategory(category);
        Member member = new Member("Doe", "John", "123456789", 1, "pass123", 100.0);
        member.addCategory(category);
        member.setMembershipPaid(true);
        members.add(member);
        managers.add(new Manager("Smith", "Jane", "987654321", 2, "admin123", category));
        treasurers.add(new Treasurer("Brown", "Alice", "456789123", 3, "treasurer123"));

        setTitle("Club Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(3, 2));

        panel.add(new JLabel("ID:"));
        idField = new JTextField();
        panel.add(idField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        loginButton = new JButton("Login");
        panel.add(loginButton);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    int id = Integer.parseInt(idField.getText());
                    String password = new String(passwordField.getPassword());

                    for (Member member : members) {
                        if (member.login(id, password)) {
                            try {
                                member.validateMembership();
                                new MemberDashboard(member).setVisible(true);
                                dispose();
                                return;
                            } catch (IllegalStateException ex) {
                                JOptionPane.showMessageDialog(LoginWindow.this, "Member must belong to at least one category");
                            }
                            return;
                        }
                    }

                    for (Manager manager : managers) {
                        if (manager.login(id, password)) {
                            new ManagerDashboard(manager).setVisible(true);
                            dispose();
                            return;
                        }
                    }

                    for (Treasurer treasurer : treasurers) {
                        if (treasurer.login(id, password)) {
                            new TreasurerDashboard(treasurer).setVisible(true);
                            dispose();
                            return;
                        }
                    }

                    JOptionPane.showMessageDialog(LoginWindow.this, "Invalid credentials");
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(LoginWindow.this, "ID must be a number");
                }
            }
        });

        add(panel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            LoginWindow loginWindow = new LoginWindow();
            loginWindow.setVisible(true);
        });
    }
}