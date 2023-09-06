package pcd.ex1;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Arrays;

import static pcd.BackupFiles.Model.*;

public class Histogram extends JFrame {
    private final int NI; // local number of intervals
    private final int MAXL; // local maximum line count
    private static int[] distribution; // local array that keeps track of the number of sources in a distribution
    private static int[] lineCounts; // local array that keeps track of the number of lines in a distribution.


    public Histogram(int NI, int MAXL) {
        super("Histogram Example");
        this.NI = NI;
        this.MAXL = MAXL;
        this.distribution = new int[NI];
        this.lineCounts = new int[MAXL];
        setSize(NI * 80, MAXL / 10 + 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    // Add elements to the correct distribution range. (its like an API to the histogram class)
    public void incrementDistribution(int range) {
        synchronized (distribution) {
            distribution[range]++;
        }
        synchronized (lineCounts) {
            lineCounts[distribution[range] / (getMAXL() / 10)]++;
        }
        repaint();
    }

    public void paint(Graphics g) {
        super.paint(g);
        int[] ranges = calculateRanges();
        int maxCount = Arrays.stream(distribution).max().orElse(0);
        int maxLineCount = Arrays.stream(lineCounts).max().orElse(0);

        // Create an off-screen buffer to draw the histogram to (to avoid flickering).
        BufferedImage buffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = buffer.createGraphics();

        // Draw the histogram to the buffer.
        g2d.drawLine(50, getHeight() - 50, getWidth() - 50, getHeight() - 50);

        int barHeight = (int) ((getHeight() - 100) / (double) maxCount);
        int barWidth = (int) ((getWidth() - 100) / (double) ranges.length);

        g.drawLine(50, getHeight() - 50, getWidth() - 50, getHeight() - 50);
        g.drawLine(50, getHeight() - 50, 50, 50);

        // Labels in the x-axis.
        FontMetrics fm = g.getFontMetrics();
        int xInterval = (int) (barWidth * 0.8);
        for (int i = 0; i < ranges.length; i++) {
            String label = String.format("%d-%d", i == 0 ? 0 : ranges[i - 1] + 1, ranges[i]);
            int labelWidth = fm.stringWidth(label);
            int x = (int) (50 + (i * barWidth) + (barWidth - labelWidth) / 2.0);
            int y = getHeight() - 40;
            g.drawString(label, x, y);
        }

        // Labels in the y-axis.
        double yInterval = ((double) (getHeight() - 100)) / 10.0;
        for (int i = 0; i <= 10; i++) {
            int y = getHeight() - 50 - (int) (i * yInterval);
            int lineCount = (int) Math.ceil((double) maxLineCount / 10.0 * i);
            String label = Integer.toString(lineCount);
            int labelWidth = fm.stringWidth(label);
            int x = 35 - labelWidth;
            g.drawString(label, x, y + (int) (fm.getAscent() / 2.0));
        }


        // Draw the bars.
        synchronized (distribution) {
            for (int i = 0; i < distribution.length; i++) {
                int x = (int) (50 + (i * barWidth) + (barWidth * 0.1));
                int y = getHeight() - 51;
                int height = distribution[i] * barHeight;
                g.setColor(Color.BLUE);
                g.fillRect(x, y - height, (int) (barWidth * 0.8), height);
            }
        }

        // Copy the buffer to the screen
        g.drawImage(buffer, 0, 0, null);

    }

    // Calculate the ranges for the histogram by putting data in the correct distribution range.
    private int[] calculateRanges() {
        int[] boundaries = new int[NI];
        for (int i = 0; i < NI - 1; i++) {
            boundaries[i] = (i + 1) * MAXL / NI;
            boundaries[i]--;
        }
        boundaries[NI - 1] = MAXL;
        return boundaries;
    }

    // Reset otherwise there is a bug where at every start the Y and X labels gets added.
    public static void resetHistogram() {
        lineCounts = new int[getMAXL()];
        distribution = new int[getNI()];
    }
}

