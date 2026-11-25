package gui;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;

/** About screen: group number, section, and members with algorithm and role. */
class AboutPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    AboutPanel(Navigator navigator) {
        setLayout(new BorderLayout());
        add(UiKit.header("About", navigator), BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(BorderFactory.createEmptyBorder(10, 30, 20, 30));

        body.add(line(AppInfo.TITLE, Font.BOLD, 20f));
        body.add(line(AppInfo.COURSE, Font.PLAIN, 14f));
        body.add(line(AppInfo.TERM, Font.PLAIN, 14f));
        body.add(Box.createVerticalStrut(10));
        body.add(line("Section: " + AppInfo.SECTION + "      Group No.: " + AppInfo.GROUP_NO,
                Font.BOLD, 15f));
        body.add(Box.createVerticalStrut(12));

        DefaultTableModel model = new DefaultTableModel(
                new Object[] {"Member", "Assigned algorithm", "Role"}, 0);
        for (String[] member : AppInfo.MEMBERS) {
            model.addRow(member);
        }
        JTable table = new JTable(model);
        table.setEnabled(false);
        table.setRowHeight(26);
        JPanel tableBlock = UiKit.tableBlock(table);
        tableBlock.setAlignmentX(Component.LEFT_ALIGNMENT);
        tableBlock.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE,
                tableBlock.getPreferredSize().height));
        body.add(tableBlock);

        body.add(Box.createVerticalStrut(12));
        body.add(line("Facilitator: " + AppInfo.FACILITATOR, Font.PLAIN, 14f));
        body.add(line("Version 1.0", Font.PLAIN, 14f));
        add(body, BorderLayout.CENTER);
    }

    private JLabel line(String text, int style, float size) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(style, size));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(2, 0, 2, 0));
        return label;
    }
}
