package pcd.ass03JavaRMI;

import pcd.ass03JavaRMI.ColorChangeListener;
import pcd.ass03JavaRMI.MouseMovedListener;
import pcd.ass03JavaRMI.PixelGridEventListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;

import javax.swing.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PixelGridView extends JFrame {
    private final VisualiserPanel panel;
    private final int w, h;
    private final PixelGridService gridService;
    private final BrushManagerService brushManagerService;

    private final List<PixelGridEventListener> pixelListeners;
    private final List<MouseMovedListener> movedListener;
    private final List<ColorChangeListener> colorChangeListeners;

    public PixelGridView(PixelGridService gridService, BrushManagerService brushManagerService, int w, int h) {
        this.gridService = gridService;
        this.brushManagerService = brushManagerService;
        this.w = w;
        this.h = h;
        pixelListeners = new ArrayList<>();
        movedListener = new ArrayList<>();
        colorChangeListeners = new ArrayList<>();
        setTitle(".:: PixelArt ::.");
        setResizable(false);
        panel = new VisualiserPanel(gridService, brushManagerService, w, h);
        panel.addMouseListener(createMouseListener());
        panel.addMouseMotionListener(createMotionListener());
        var colorChangeButton = new JButton("Change color");
        /*colorChangeButton.addActionListener(e -> {
            var color = JColorChooser.showDialog(this, "Choose a color", Color.BLACK);
            if (color != null) {
                try {
                    brushManagerService.broadcastColorChange(color.getRGB());
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });*/
        colorChangeButton.addActionListener(e -> {
            var color = JColorChooser.showDialog(this, "Choose a color", Color.BLACK);
            if (color != null) {
                colorChangeListeners.forEach(l -> {
                    try {
                        l.colorChanged(color.getRGB());
                    } catch (RemoteException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        });
        add(panel, BorderLayout.CENTER);
        add(colorChangeButton, BorderLayout.SOUTH);
        getContentPane().add(panel);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        hideCursor();
    }

    // Event listeners.
    public void addPixelGridEventListener(PixelGridEventListener l) {
        pixelListeners.add(l);
    }

    public void addMouseMovedListener(MouseMovedListener l) {
        movedListener.add(l);
    }

    public void addColorChangedListener(ColorChangeListener l) {
        colorChangeListeners.add(l);
    }

    public void refresh() {
        panel.repaint();
    }

    public void display() {
        SwingUtilities.invokeLater(() -> {
            this.pack();
            this.setVisible(true);
        });
    }

    private void hideCursor() {
        var cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
        var blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
        this.getContentPane().setCursor(blankCursor);
    }

    private MouseListener createMouseListener() {
        return new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent e) {
                try {
                    int dx = w / gridService.getNumColumns();
                    int dy = h / gridService.getNumRows();
                    int col = e.getX() / dx;
                    int row = e.getY() / dy;
                    System.out.println("Mouse clicked on cell (" + col + ", " + row + ")");
                    gridService.selectedCells(col, row);
                } catch (RemoteException ex) {
                    throw new RuntimeException(ex);
                }
            }

            @Override
            public void mousePressed(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        };
    }

    private MouseMotionListener createMotionListener() {
        return new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                movedListener.forEach(l -> {
                    try {
                        l.mouseMoved(e.getX(), e.getY());
                        brushManagerService.mouseMoved(e.getX(), e.getY());
                        //System.out.println("Mouse moved: e.getX() = " + e.getX() + ", e.getY() = " + e.getY() + "");

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
            }
        };
    }
}

