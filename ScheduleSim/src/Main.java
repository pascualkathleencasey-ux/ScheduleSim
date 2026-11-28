import gui.MainFrame;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/** Entry point: opens the main menu window. */
public class Main {

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
          // hi
            // The default look and feel is fine if the system one is unavailable.
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
