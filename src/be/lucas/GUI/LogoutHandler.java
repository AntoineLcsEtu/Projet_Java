package be.lucas.GUI;

import javax.swing.*;

public class LogoutHandler {
    public static void logout(JFrame currentFrame) {
        currentFrame.dispose(); 
        
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