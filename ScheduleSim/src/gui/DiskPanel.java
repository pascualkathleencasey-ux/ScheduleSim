package gui;

import disk.DiskResult;
import disk.DiskScheduler;
import disk.DiskStep;
import disk.Direction;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Screen for one disk scheduling algorithm. The same panel serves SCAN and
 * C-LOOK; the scheduler passed in decides the behavior.
 */
class DiskPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_DISK_SIZE = 200;

    private final DiskScheduler scheduler;

    private final JTextField requestsField = new JTextField(40);
    private final JTextField headField = new JTextField("", 6);
    private final JTextField sizeField = new JTextField(String.valueOf(DEFAULT_DISK_SIZE), 6);
    private final JComboBox<Direction> directionBox = new JComboBox<Direction>(Direction.values());

    private final HeadPathPanel pathPanel = new HeadPathPanel();
    private final DefaultTableModel stepModel = new DefaultTableModel(
            new Object[] {"Step", "From", "To", "Movement", "Type"}, 0);
    private final JTable stepTable = new JTable(stepModel);
    private final JLabel sequenceLabel = UiKit.valueLabel();
    private final JLabel totalLabel = UiKit.valueLabel();
    private final JLabel averageLabel = UiKit.valueLabel();

    DiskPanel(DiskScheduler scheduler, Navigator navigator) {
        this.scheduler = scheduler;
        setLayout(new BorderLayout());
        add(UiKit.header("Disk Scheduling - " + scheduler.getName(), navigator), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));

        content.add(UiKit.section("Input", buildInputArea()));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Summary", buildSummary()));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Head path", pathPanel));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Head movement per step", UiKit.tableBlock(stepTable)));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        UiKit.centerCells(stepTable);
        stepTable.setEnabled(false);
    }

    private JPanel buildInputArea() {
        JPanel area = new JPanel();
        area.setLayout(new BoxLayout(area, BoxLayout.Y_AXIS));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row1.add(new JLabel("Request queue (cylinders, separated by commas):"));
        area.add(row1);
        JPanel row2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row2.add(requestsField);
        area.add(row2);

        JPanel row3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        row3.add(new JLabel("Initial head position:"));
        row3.add(headField);
        row3.add(new JLabel("Disk size (cylinders):"));
        row3.add(sizeField);
        row3.add(new JLabel("Direction:"));
        row3.add(directionBox);
        area.add(row3);

        JPanel row4 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row4.add(button("Load sample data", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { loadSample(); }
        }));
        row4.add(button("Run", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { run(); }
        }));
        row4.add(button("Reset", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { reset(); }
        }));
        area.add(row4);
        return area;
    }

    private JPanel buildSummary() {
        JPanel grid = new JPanel(new GridLayout(3, 2, 12, 4));
        grid.add(new JLabel("Seek sequence"));
        grid.add(sequenceLabel);
        grid.add(new JLabel("Total head movement"));
        grid.add(totalLabel);
        grid.add(new JLabel("Average seek distance"));
        grid.add(averageLabel);
        return grid;
    }

    private JButton button(String text, ActionListener listener) {
        JButton b = new JButton(text);
        b.addActionListener(listener);
        return b;
    }

    // ----- actions -----

    private void loadSample() {
        requestsField.setText(SampleData.DISK_REQUESTS);
        headField.setText(String.valueOf(SampleData.DISK_HEAD));
        sizeField.setText(String.valueOf(SampleData.DISK_SIZE));
        directionBox.setSelectedItem(SampleData.DISK_DIRECTION);
        clearResults();
    }

    private void run() {
        try {
            int size = InputParser.parseInt(sizeField.getText(), "Disk size", 2, InputParser.MAX_VALUE);
            int head = InputParser.parseInt(headField.getText(), "Initial head position", 0, size - 1);
            List<Integer> requests = InputParser.parseRequests(requestsField.getText(), size);
            Direction direction = (Direction) directionBox.getSelectedItem();
            showResult(scheduler.schedule(requests, head, size, direction));
        } catch (InputException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }

    private void reset() {
        requestsField.setText("");
        headField.setText("");
        sizeField.setText(String.valueOf(DEFAULT_DISK_SIZE));
        directionBox.setSelectedIndex(0);
        clearResults();
    }

    private void showResult(DiskResult result) {
        StringBuilder seq = new StringBuilder(String.valueOf(result.getStartHead()));
        for (int cylinder : result.getServiceOrder()) {
            seq.append(" → ").append(cylinder);
        }
        sequenceLabel.setText("<html>" + seq + "</html>");
        totalLabel.setText(result.getTotalMovement() + " cylinders");
        averageLabel.setText(UiKit.fmt(result.getAverageSeek()));

        stepModel.setRowCount(0);
        int n = 0;
        for (DiskStep s : result.getSteps()) {
            n++;
            stepModel.addRow(new Object[] {n, s.getFrom(), s.getTo(), s.getDistance(),
                    s.getType().getLabel()});
        }
        pathPanel.setResult(result);
    }

    private void clearResults() {
        sequenceLabel.setText(UiKit.DASH);
        totalLabel.setText(UiKit.DASH);
        averageLabel.setText(UiKit.DASH);
        stepModel.setRowCount(0);
        pathPanel.clear();
    }
}
