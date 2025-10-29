package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;

public class MemberPayMembership extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Member member;

    public MemberPayMembership(Member member) {
        this.member = member;
        setTitle("Pay Membership - " + member.getFirstName() + " " + member.getName());
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1));

        JButton payButton = new JButton("Pay Membership");
        panel.add(payButton);

        JLabel balanceLabel = new JLabel("Current Balance: €" + member.checkBalance());
        panel.add(balanceLabel);

        payButton.addActionListener(e -> {
            member.calculateBalance();
            member.setMembershipPaid(true);
            balanceLabel.setText("Current Balance: €" + member.checkBalance());
            JOptionPane.showMessageDialog(this, "Membership paid");
            dispose();
        });

        add(panel);
    }
}
