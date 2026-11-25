package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Main menu. All four algorithms are one click away, grouped as CPU
 * Scheduling and Disk Scheduling, plus About and Exit.
 */
class MenuPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    MenuPanel(final Navigator navigator) {
        setLayout(new GridBagLayout());

        JPanel box = new JPanel();
        box.setLayout(new javax.swing.BoxLayout(box, javax.swing.BoxLayout.Y_AXIS));

        JLabel title = new JLabel(AppInfo.TITLE, SwingConstants.CENTER);
        title.setFont(title.getFont().deriveFont(Font.BOLD, 24f));
        title.setAlignmentX(0.5f);
        JLabel sub = new JLabel(AppInfo.COURSE, SwingConstants.CENTER);
        sub.setAlignmentX(0.5f);
        box.add(title);
        box.add(Box.createVerticalStrut(4));
        box.add(sub);
        box.add(Box.createVerticalStrut(22));

        box.add(group("CPU Scheduling",
                algorithmButton("Shortest Remaining Time First (SRTF)", MainFrame.CARD_SRTF, navigator),
                algorithmButton("Round Robin (RR)", MainFrame.CARD_RR, navigator)));
        box.add(Box.createVerticalStrut(14));
        box.add(group("Disk Scheduling",
                algorithmButton("C-LOOK", MainFrame.CARD_CLOOK, navigator),
                algorithmButton("SCAN", MainFrame.CARD_SCAN, navigator)));
        box.add(Box.createVerticalStrut(14));

        JPanel bottom = new JPanel(new GridLayout(1, 2, 10, 0));
        bottom.add(algorithmButton("About", MainFrame.CARD_ABOUT, navigator));
        JButton exit = new JButton("Exit");
        exit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        bottom.add(exit);
        bottom.setAlignmentX(0.5f);
        bottom.setMaximumSize(new Dimension(420, 40));
        bottom.setPreferredSize(new Dimension(420, 40));
        box.add(bottom);

        add(box, new GridBagConstraints());
    }

    private JPanel group(String title, JButton first, JButton second) {
        JPanel panel = new JPanel(new GridLayout(1, 2, 10, 0));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(6, 8, 8, 8)));
        panel.add(first);
        panel.add(second);
        panel.setAlignmentX(0.5f);
        panel.setPreferredSize(new Dimension(520, 86));
        panel.setMaximumSize(new Dimension(520, 86));
        return panel;
    }

    private JButton algorithmButton(String text, final String card, final Navigator navigator) {
        JButton button = new JButton("<html><center>" + text + "</center></html>");
        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigator.show(card);
            }
        });
        return button;
    }
}
