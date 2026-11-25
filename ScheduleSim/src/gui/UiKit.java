package gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;

/** Small helpers shared by the screens so they look and behave the same. */
final class UiKit {

    static final String DASH = "-";

    private UiKit() { }

    /** A titled box that never stretches taller than its content. */
    static JPanel section(String title, JComponent body) {
        JPanel panel = new JPanel(new BorderLayout()) {
            private static final long serialVersionUID = 1L;

            @Override
            public Dimension getMaximumSize() {
                return new Dimension(Integer.MAX_VALUE, getPreferredSize().height);
            }
        };
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(title),
                BorderFactory.createEmptyBorder(4, 8, 8, 8)));
        panel.add(body, BorderLayout.CENTER);
        return panel;
    }

    /** A table with its header, without a scroll pane (the screen scrolls as a whole). */
    static JPanel tableBlock(JTable table) {
        JPanel block = new JPanel(new BorderLayout());
        block.add(table.getTableHeader(), BorderLayout.NORTH);
        block.add(table, BorderLayout.CENTER);
        return block;
    }

    static void centerCells(JTable table) {
        DefaultTableCellRenderer center = new DefaultTableCellRenderer();
        center.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(center);
        }
        table.setRowHeight(24);
    }

    /** Screen title with a Back button on the right. */
    static JPanel header(String title, final Navigator navigator) {
        JPanel header = new JPanel(new BorderLayout());
        header.setBorder(BorderFactory.createEmptyBorder(10, 14, 6, 14));
        JLabel label = new JLabel(title);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 18f));
        JButton back = new JButton("Back to menu");
        back.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigator.show(MainFrame.CARD_MENU);
            }
        });
        header.add(label, BorderLayout.CENTER);
        header.add(back, BorderLayout.EAST);
        return header;
    }

    static JLabel valueLabel() {
        JLabel label = new JLabel(DASH);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }

    static String fmt(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    static void warn(Component parent, String message) {
        JOptionPane.showMessageDialog(parent, message, "Invalid input", JOptionPane.WARNING_MESSAGE);
    }
}
