package be.lucas.GUI;

import be.lucas.Model.Treasurer;
import javax.swing.*;
import java.awt.*;

public class TreasurerVerifyFees extends JFrame {
    private static final long serialVersionUID = 1L;
    @SuppressWarnings("unused")
    private final Treasurer treasurer;

    public TreasurerVerifyFees(Treasurer treasurer) throws Exception {
        this.treasurer = treasurer;
        setTitle("Vérification des Cotisations");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel title = new JLabel("RAPPORT DE VÉRIFICATION DES COTISATIONS", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 20));
        title.setForeground(new Color(0, 102, 0));
        mainPanel.add(title, BorderLayout.NORTH);

        JTextArea reportText = new JTextArea();
        reportText.setEditable(false);
        reportText.setFont(new Font("Consolas", Font.PLAIN, 14));
        reportText.setBackground(new Color(248, 255, 248));
        reportText.setMargin(new Insets(10, 10, 10, 10));

        Object[] result = treasurer.verifyMembershipFees();  
        String   report = (String) result[0];
        

        reportText.setText(report);
        mainPanel.add(new JScrollPane(reportText), BorderLayout.CENTER);
        add(mainPanel);    
    }
}