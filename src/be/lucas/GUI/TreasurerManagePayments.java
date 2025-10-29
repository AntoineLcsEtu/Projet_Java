package be.lucas.GUI;

import java.awt.EventQueue;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

public class TreasurerManagePayments extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Treasurer treasurer;

    public TreasurerManagePayments(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Manage Payments - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton payDriverButton = new JButton("Pay Drivers");
        panel.add(payDriverButton);

        payDriverButton.addActionListener(e -> {
            treasurer.payDriver();
            JOptionPane.showMessageDialog(this, "Drivers paid");
            dispose();
        });

        add(panel);
    }
}
