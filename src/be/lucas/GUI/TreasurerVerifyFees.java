package be.lucas.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import be.lucas.Model.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class TreasurerVerifyFees extends JFrame {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Treasurer treasurer;

    public TreasurerVerifyFees(Treasurer treasurer) {
        this.treasurer = treasurer;
        setTitle("Verify Membership Fees - " + treasurer.getFirstName() + " " + treasurer.getName());
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Données simulées
        List<Member> members = new ArrayList<>();
        members.add(new Member("Doe", "John", "123456789", 1, "pass123", 100.0));
        members.get(0).setMembershipPaid(true);
        members.add(new Member("Smith", "Bob", "987654321", 4, "pass456", 50.0));

        JPanel panel = new JPanel(new BorderLayout());
        JTextArea reportText = new JTextArea();
        reportText.setEditable(false);

        StringBuilder report = new StringBuilder("Membership Status:\n");
        for (Member member : members) {
            treasurer.verifyMembershipFees(member);
            report.append(member.getFirstName()).append(" ").append(member.getName())
                  .append(": ").append(member.isMembershipPaid() ? "Paid" : "Not Paid").append("\n");
        }
        reportText.setText(report.toString());

        panel.add(new JScrollPane(reportText), BorderLayout.CENTER);
        add(panel);
    }
}
