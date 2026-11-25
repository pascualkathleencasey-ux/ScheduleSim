package gui;

import disk.DiskResult;
import disk.DiskStep;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;

/**
 * Draws the head's path: cylinders across the top, one row per head position,
 * so the path runs from the top of the panel down.
 */
class HeadPathPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color SERVE_COLOR = new Color(83, 74, 183);
    private static final Color END_COLOR = new Color(95, 94, 90);
    private static final Color JUMP_COLOR = new Color(216, 90, 48);
    private static final int LEFT = 40;
    private static final int RIGHT = 50;
    private static final int TOP = 56;
    private static final int ROW = 26;

    private DiskResult result;

    HeadPathPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        setPreferredSize(new Dimension(600, 120));
    }

    void setResult(DiskResult newResult) {
        result = newResult;
        int points = result.getSteps().size() + 1;
        setPreferredSize(new Dimension(600, TOP + ROW * (points - 1) + 56));
        revalidate();
        repaint();
    }

    void clear() {
        result = null;
        setPreferredSize(new Dimension(600, 120));
        revalidate();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        FontMetrics fm = g2.getFontMetrics();

        if (result == null) {
            g2.setColor(Color.GRAY);
            g2.drawString("The head path appears here after you click Run.", LEFT, 60);
            g2.dispose();
            return;
        }

        int usable = getWidth() - LEFT - RIGHT;
        int maxCylinder = Math.max(1, result.getDiskSize() - 1);
        double scale = usable / (double) maxCylinder;

        // Cylinder axis with marks.
        g2.setColor(new Color(120, 120, 120));
        g2.drawLine(LEFT, 36, LEFT + usable, 36);
        int[] marks = {0, maxCylinder / 4, maxCylinder / 2, (3 * maxCylinder) / 4, maxCylinder};
        for (int i = 0; i < marks.length; i++) {
            if (i > 0 && marks[i] == marks[i - 1]) {
                continue;
            }
            int x = xOf(marks[i], scale);
            g2.drawLine(x, 32, x, 40);
            String text = String.valueOf(marks[i]);
            g2.drawString(text, x - fm.stringWidth(text) / 2, 28);
        }
        g2.drawString("Cylinder", LEFT, 14);

        // Path segments.
        Stroke solid = new BasicStroke(1.8f);
        Stroke dashed = new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[] {6f, 4f}, 0f);
        int row = 0;
        int prevX = xOf(result.getStartHead(), scale);
        int prevY = TOP;
        for (DiskStep step : result.getSteps()) {
            row++;
            int x = xOf(step.getTo(), scale);
            int y = TOP + ROW * row;
            if (step.getType() == DiskStep.Type.SERVICE) {
                g2.setColor(SERVE_COLOR);
                g2.setStroke(solid);
            } else if (step.getType() == DiskStep.Type.JUMP) {
                g2.setColor(JUMP_COLOR);
                g2.setStroke(dashed);
            } else {
                g2.setColor(END_COLOR);
                g2.setStroke(dashed);
            }
            g2.drawLine(prevX, prevY, x, y);
            prevX = x;
            prevY = y;
        }
        g2.setStroke(solid);

        // Points and labels.
        int startX = xOf(result.getStartHead(), scale);
        drawPoint(g2, fm, startX, TOP, result.getStartHead() + " (start)", Color.GRAY, true);
        row = 0;
        for (DiskStep step : result.getSteps()) {
            row++;
            boolean serve = step.getType() != DiskStep.Type.SWEEP_TO_END;
            drawPoint(g2, fm, xOf(step.getTo(), scale), TOP + ROW * row,
                    String.valueOf(step.getTo()), serve ? SERVE_COLOR : END_COLOR, serve);
        }

        // Legend.
        int legendY = TOP + ROW * result.getSteps().size() + 32;
        int x = LEFT;
        x = legendItem(g2, fm, x, legendY, SERVE_COLOR, false, "Serve request");
        x = legendItem(g2, fm, x + 24, legendY, END_COLOR, true, "Move to disk end");
        legendItem(g2, fm, x + 24, legendY, JUMP_COLOR, true, "Return jump (counted)");
        g2.dispose();
    }

    private int xOf(int cylinder, double scale) {
        return LEFT + (int) Math.round(cylinder * scale);
    }

    private void drawPoint(Graphics2D g2, FontMetrics fm, int x, int y, String label,
                           Color color, boolean filled) {
        g2.setColor(color);
        if (filled) {
            g2.fillOval(x - 4, y - 4, 8, 8);
        } else {
            g2.setColor(Color.WHITE);
            g2.fillOval(x - 4, y - 4, 8, 8);
            g2.setColor(color);
            g2.drawOval(x - 4, y - 4, 8, 8);
        }
        g2.setColor(new Color(50, 50, 50));
        int w = fm.stringWidth(label);
        int textX = x + 9;
        if (textX + w > getWidth() - 4) {
            textX = x - 9 - w;
        }
        g2.drawString(label, textX, y + 4);
    }

    private int legendItem(Graphics2D g2, FontMetrics fm, int x, int y, Color color,
                           boolean dashed, String text) {
        g2.setColor(color);
        g2.setStroke(dashed
                ? new BasicStroke(1.8f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                        10f, new float[] {6f, 4f}, 0f)
                : new BasicStroke(1.8f));
        g2.drawLine(x, y, x + 24, y);
        g2.setStroke(new BasicStroke(1f));
        g2.setColor(new Color(50, 50, 50));
        g2.drawString(text, x + 30, y + 4);
        return x + 30 + fm.stringWidth(text);
    }
}
