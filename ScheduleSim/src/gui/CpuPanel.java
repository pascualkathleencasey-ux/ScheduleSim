package gui;

import cpu.CPUResult;
import cpu.CPUScheduler;
import cpu.SimProcess;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Screen for one CPU scheduling algorithm. The same panel serves SRTF and
 * Round Robin: the scheduler passed in decides the behavior, and the time
 * quantum field is shown only when the scheduler needs it.
 */
class CpuPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final int MIN_ROWS = 10;      // project sheet: support at least 10 processes
    private static final int MAX_ROWS = 50;

    private final CPUScheduler scheduler;

    private final DefaultTableModel inputModel =
            new DefaultTableModel(new Object[] {"PID", "Arrival time (AT)", "Burst time (BT)"}, 0);
    private final JTable inputTable = new JTable(inputModel);
    private final JSpinner quantumSpinner =
            new JSpinner(new SpinnerNumberModel(2, 1, InputParser.MAX_VALUE, 1));

    private final GanttPanel ganttPanel = new GanttPanel();
    private final DefaultTableModel resultModel = new DefaultTableModel(
            new Object[] {"PID", "AT", "BT", "CT", "TAT", "WT", "RT"}, 0);
    private final JTable resultTable = new JTable(resultModel);
    private final JLabel avgTat = UiKit.valueLabel();
    private final JLabel avgWt = UiKit.valueLabel();
    private final JLabel avgRt = UiKit.valueLabel();
    private final JLabel utilization = UiKit.valueLabel();
    private final JLabel throughput = UiKit.valueLabel();

    CpuPanel(final CPUScheduler scheduler, Navigator navigator) {
        this.scheduler = scheduler;
        setLayout(new BorderLayout());
        add(UiKit.header("CPU Scheduling - " + scheduler.getName(), navigator), BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(0, 14, 14, 14));

        content.add(UiKit.section("Input", buildInputArea()));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Gantt chart", ganttPanel));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Results per process", UiKit.tableBlock(resultTable)));
        content.add(Box.createVerticalStrut(8));
        content.add(UiKit.section("Summary", buildSummary()));

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        add(scroll, BorderLayout.CENTER);

        UiKit.centerCells(inputTable);
        UiKit.centerCells(resultTable);
        resultTable.setEnabled(false);
        inputTable.putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);
        inputTable.setRowHeight(24);
        resetInputRows();
    }

    private JPanel buildInputArea() {
        JPanel area = new JPanel(new BorderLayout(0, 8));

        JPanel tableArea = new JPanel(new BorderLayout());
        JScrollPane inputScroll = new JScrollPane(inputTable);
        inputScroll.setPreferredSize(new java.awt.Dimension(600, 170));
        tableArea.add(inputScroll, BorderLayout.CENTER);
        area.add(tableArea, BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        if (scheduler.needsTimeQuantum()) {
            controls.add(new JLabel("Time quantum:"));
            controls.add(quantumSpinner);
        }
        controls.add(button("Add process", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { addRow(); }
        }));
        controls.add(button("Remove selected", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { removeSelected(); }
        }));
        controls.add(Box.createHorizontalStrut(12));
        controls.add(button("Load sample data", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { loadSample(); }
        }));
        JButton run = button("Run", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { run(); }
        });
        controls.add(run);
        controls.add(button("Reset", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) { reset(); }
        }));
        area.add(controls, BorderLayout.SOUTH);
        return area;
    }

    private JPanel buildSummary() {
        JPanel grid = new JPanel(new GridLayout(2, 6, 12, 2));
        grid.add(new JLabel("Avg turnaround"));
        grid.add(new JLabel("Avg waiting"));
        grid.add(new JLabel("Avg response"));
        grid.add(new JLabel("CPU utilization"));
        grid.add(new JLabel("Throughput"));
        grid.add(new JLabel(""));
        grid.add(avgTat);
        grid.add(avgWt);
        grid.add(avgRt);
        grid.add(utilization);
        grid.add(throughput);
        grid.add(new JLabel(""));
        return grid;
    }

    private JButton button(String text, ActionListener listener) {
        JButton b = new JButton(text);
        b.addActionListener(listener);
        return b;
    }

    // ----- actions -----

    private void addRow() {
        stopEditing();
        if (inputModel.getRowCount() >= MAX_ROWS) {
            UiKit.warn(this, "You can enter at most " + MAX_ROWS + " processes.");
            return;
        }
        inputModel.addRow(new Object[] {"P" + (inputModel.getRowCount() + 1), "", ""});
    }

    private void removeSelected() {
        stopEditing();
        int[] rows = inputTable.getSelectedRows();
        if (rows.length == 0) {
            UiKit.warn(this, "Select a row to remove first.");
            return;
        }
        for (int i = rows.length - 1; i >= 0; i--) {
            inputModel.removeRow(rows[i]);
        }
    }

    private void loadSample() {
        stopEditing();
        inputModel.setRowCount(0);
        for (String[] row : SampleData.CPU_ROWS) {
            inputModel.addRow(new Object[] {row[0], row[1], row[2]});
        }
        quantumSpinner.setValue(SampleData.QUANTUM);
        clearResults();
    }

    private void run() {
        stopEditing();
        try {
            List<SimProcess> processes = InputParser.readProcesses(inputModel);
            int quantum = 1;
            if (scheduler.needsTimeQuantum()) {
                quantum = InputParser.parseInt(String.valueOf(quantumSpinner.getValue()),
                        "Time quantum", 1, InputParser.MAX_VALUE);
            }
            showResult(scheduler.schedule(processes, quantum));
        } catch (InputException ex) {
            UiKit.warn(this, ex.getMessage());
        }
    }

    private void reset() {
        stopEditing();
        resetInputRows();
        quantumSpinner.setValue(2);
        clearResults();
    }

    /** Back to MIN_ROWS empty rows, so at least 10 processes can be entered. */
    private void resetInputRows() {
        inputModel.setRowCount(0);
        for (int i = 1; i <= MIN_ROWS; i++) {
            inputModel.addRow(new Object[] {"P" + i, "", ""});
        }
    }

    private void showResult(CPUResult result) {
        ganttPanel.setResult(result.getGantt());
        resultModel.setRowCount(0);
        for (SimProcess p : result.getProcesses()) {
            resultModel.addRow(new Object[] {p.getPid(), p.getArrival(), p.getBurst(),
                    p.getCompletion(), p.getTurnaround(), p.getWaiting(), p.getResponse()});
        }
        avgTat.setText(UiKit.fmt(result.getAverageTurnaround()) + " ms");
        avgWt.setText(UiKit.fmt(result.getAverageWaiting()) + " ms");
        avgRt.setText(UiKit.fmt(result.getAverageResponse()) + " ms");
        utilization.setText(UiKit.fmt(result.getCpuUtilization()) + " %");
        throughput.setText(UiKit.fmt(result.getThroughput()) + " process/ms");
    }

    private void clearResults() {
        ganttPanel.clear();
        resultModel.setRowCount(0);
        avgTat.setText(UiKit.DASH);
        avgWt.setText(UiKit.DASH);
        avgRt.setText(UiKit.DASH);
        utilization.setText(UiKit.DASH);
        throughput.setText(UiKit.DASH);
    }

    private void stopEditing() {
        if (inputTable.isEditing()) {
            inputTable.getCellEditor().stopCellEditing();
        }
    }

    /** Used by the headless self-test to fill the table and click Run. */
    Component[] testHooks() {
        return new Component[] {inputTable, ganttPanel};
    }
}
