import javax.swing.*;
import controllers.MainController;

/**
 * Main entry point for the Crowdfunding System
 * Simple MVC architecture with Java Swing GUI and CSV data storage
 */
public class Main {
    public static void main(String[] args) {
        // Set look and feel for better appearance
        try {
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            // Use default if system look and feel fails
        }

        // Start the application through main controller
        SwingUtilities.invokeLater(() -> {
            new MainController().start();
        });
    }
}