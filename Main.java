import javax.swing.*;
import controllers.MainController;

/**
 * Main entry point for the Crowdfunding System
**/
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new MainController().start();
        });
    }
}