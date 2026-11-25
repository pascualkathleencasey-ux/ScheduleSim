package gui;

import cpu.SimProcess;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Turns text typed by the user into validated numbers and lists. Every problem
 * becomes an InputException with a clear message, so bad input never crashes
 * the program.
 */
final class InputParser {

    /** Upper limit for times, sizes, and quanta. Keeps charts readable. */
    static final int MAX_VALUE = 10000;
    static final int MAX_REQUESTS = 100;

    private InputParser() { }

    /** Parses a whole number between min and max (inclusive). */
    static int parseInt(String text, String label, int min, int max) throws InputException {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) {
            throw new InputException(label + " is empty.");
        }
        int value;
        try {
            value = Integer.parseInt(t);
        } catch (NumberFormatException e) {
            throw new InputException(label + " must be a whole number (you entered \"" + t + "\").");
        }
        if (value < min || value > max) {
            throw new InputException(label + " must be between " + min + " and " + max
                    + " (you entered " + value + ").");
        }
        return value;
    }

    /** Reads the process table: PID, arrival time, burst time for every row. */
    static List<SimProcess> readProcesses(DefaultTableModel model) throws InputException {
        List<SimProcess> processes = new ArrayList<SimProcess>();
        Set<String> seen = new HashSet<String>();
        for (int row = 0; row < model.getRowCount(); row++) {
            String where = "Row " + (row + 1) + ": ";
            String pid = cell(model, row, 0).trim();
            if (pid.isEmpty()) {
                throw new InputException(where + "PID is empty.");
            }
            if (!seen.add(pid.toUpperCase())) {
                throw new InputException(where + "PID \"" + pid + "\" is already used.");
            }
            int arrival = parseInt(cell(model, row, 1), where + "arrival time", 0, MAX_VALUE);
            int burst = parseInt(cell(model, row, 2), where + "burst time", 1, MAX_VALUE);
            processes.add(new SimProcess(pid, arrival, burst));
        }
        if (processes.isEmpty()) {
            throw new InputException("Enter at least one process.");
        }
        return processes;
    }

    /** Parses requests separated by commas and/or spaces; each must be on the disk. */
    static List<Integer> parseRequests(String text, int diskSize) throws InputException {
        String t = text == null ? "" : text.trim();
        if (t.isEmpty()) {
            throw new InputException("Request queue is empty. Enter at least one cylinder number.");
        }
        List<Integer> requests = new ArrayList<Integer>();
        for (String token : t.split("[,\\s]+")) {
            if (token.isEmpty()) {
                continue;
            }
            int value;
            try {
                value = Integer.parseInt(token);
            } catch (NumberFormatException e) {
                throw new InputException("Request \"" + token + "\" is not a whole number.");
            }
            if (value < 0 || value > diskSize - 1) {
                throw new InputException("Request " + value + " is outside the disk range (0 to "
                        + (diskSize - 1) + ").");
            }
            requests.add(value);
        }
        if (requests.isEmpty()) {
            throw new InputException("Request queue is empty. Enter at least one cylinder number.");
        }
        if (requests.size() > MAX_REQUESTS) {
            throw new InputException("Enter at most " + MAX_REQUESTS + " requests (you entered "
                    + requests.size() + ").");
        }
        return requests;
    }

    private static String cell(DefaultTableModel model, int row, int col) {
        Object value = model.getValueAt(row, col);
        return value == null ? "" : value.toString();
    }
}
