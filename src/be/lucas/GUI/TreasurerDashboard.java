package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TreasurerDashboard extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Treasurer treasurer;

    public TreasurerDashboard(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Treasurer Dashboard - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(2, 1));

        JButton verifyFeesButton = new JButton("Verify Membership Fees");
        JButton payDriverButton = new JButton("Manage Payments");

        verifyFeesButton.addActionListener(e -> new TreasurerVerifyFees(treasurer).setVisible(true));
        payDriverButton.addActionListener(e -> new TreasurerManagePayments(treasurer).setVisible(true));

        panel.add(verifyFeesButton);
        panel.add(payDriverButton);

        add(panel);
    }
}