package gui;

import cpu.GanttSlice;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Draws a Gantt chart on a time scale, with idle time shown in gray. */
class GanttPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private static final Color[] PALETTE = {
        new Color(175, 169, 236), new Color(93, 202, 165), new Color(240, 153, 123),
        new Color(133, 183, 235), new Color(237, 147, 177), new Color(151, 196, 89),
        new Color(239, 159, 39), new Color(204, 203, 246), new Color(159, 225, 203),
        new Color(245, 196, 179),
    };
    private static final Color IDLE_COLOR = new Color(220, 220, 215);

    private List<GanttSlice> slices = new ArrayList<GanttSlice>();
    private final Map<String, Color> colors = new HashMap<String, Color>();

    GanttPanel() {
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        setPreferredSize(new Dimension(600, 112));
    }

    void setResult(List<GanttSlice> newSlices) {
        slices = new ArrayList<GanttSlice>(newSlices);
        colors.clear();
        for (GanttSlice s : slices) {
            if (!s.isIdle() && !colors.containsKey(s.getPid())) {
                colors.put(s.getPid(), PALETTE[colors.size() % PALETTE.length]);
            }
        }
        repaint();
    }

    void clear() {
        slices = new ArrayList<GanttSlice>();
        colors.clear();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int left = 30;
        int right = 30;
        FontMetrics fm = g2.getFontMetrics();

        if (slices.isEmpty()) {
            g2.setColor(Color.GRAY);
            g2.drawString("The Gantt chart appears here after you click Run.", left, 56);
            g2.dispose();
            return;
        }

        int total = slices.get(slices.size() - 1).getEnd();
        double scale = (getWidth() - left - right) / (double) total;
        int barY = 22;
        int barH = 40;

        for (GanttSlice s : slices) {
            int x1 = left + (int) Math.round(s.getStart() * scale);
            int x2 = left + (int) Math.round(s.getEnd() * scale);
            g2.setColor(s.isIdle() ? IDLE_COLOR : colors.get(s.getPid()));
            g2.fillRect(x1, barY, x2 - x1, barH);
            g2.setColor(new Color(70, 70, 70));
            g2.drawRect(x1, barY, x2 - x1, barH);

            String label = s.isIdle() ? "Idle" : s.getPid();
            int labelWidth = fm.stringWidth(label);
            if (labelWidth + 6 <= x2 - x1) {
                g2.drawString(label, x1 + (x2 - x1 - labelWidth) / 2,
                        barY + (barH + fm.getAscent() - fm.getDescent()) / 2);
            }
        }

        // Time marks at the start of every block and at the very end.
        g2.setColor(new Color(70, 70, 70));
        int lastRight = Integer.MIN_VALUE;
        for (int i = 0; i <= slices.size(); i++) {
            int time = i < slices.size() ? slices.get(i).getStart() : total;
            int x = left + (int) Math.round(time * scale);
            g2.drawLine(x, barY + barH, x, barY + barH + 5);
            String text = String.valueOf(time);
            int w = fm.stringWidth(text);
            int textX = x - w / 2;
            if (textX > lastRight + 4) {
                g2.drawString(text, textX, barY + barH + 19);
                lastRight = textX + w;
            }
        }

        String caption = "Time (ms)";
        g2.drawString(caption, (getWidth() - fm.stringWidth(caption)) / 2, barY + barH + 40);
        g2.dispose();
    }
}
