package pcd.ex3.view;

import pcd.ex3.view.services.BrushManagerService;
import pcd.ex3.view.services.PixelGridService;
import pcd.ex3.view.listeners.ColorChangeListener;
import pcd.ex3.view.listeners.MouseMovedListener;
import pcd.ex3.view.listeners.PixelGridEventListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;


public class PixelGridView extends JFrame {
    private final VisualiserPanel panel;
    private final PixelGridService grid;
    private final int w, h;
    private final List<PixelGridEventListener> pixelListeners;
	private final List<MouseMovedListener> movedListener;
	private final List<ColorChangeListener> colorChangeListeners;
	private final String clientID;
    
    public PixelGridView(String clientID, PixelGridService grid, BrushManagerService brushManager, int w, int h){
		setTitle(".:: PixelArt ::." + "Client:" + clientID);
		this.clientID = clientID;
		this.grid = grid;
		this.w = w;
		this.h = h;
		pixelListeners = new ArrayList<>();
		movedListener = new ArrayList<>();
		colorChangeListeners = new ArrayList<>();
		setResizable(false);
        panel = new VisualiserPanel(grid, brushManager, w, h);
        panel.addMouseListener(createMouseListener());
		panel.addMouseMotionListener(createMotionListener());
		var colorChangeButton = new JButton("Change color");
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
		// add panel and a button to the button to change color
		add(panel, BorderLayout.CENTER);
		var buttonPanel = new JPanel();
		buttonPanel.add(colorChangeButton);
		add(buttonPanel, BorderLayout.SOUTH);
        getContentPane().add(panel);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
    
    public void refresh(){
        panel.repaint();
    }
        
    public void display() {
		SwingUtilities.invokeLater(() -> {
			this.pack();
			this.setVisible(true);
		});
    }
    
    public void addPixelGridEventListener(PixelGridEventListener l) { pixelListeners.add(l); }

	public void addMouseMovedListener(MouseMovedListener l) { movedListener.add(l); }

	public void addColorChangedListener(ColorChangeListener l) { colorChangeListeners.add(l); }

	private void hideCursor() {
		var cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		var blankCursor = Toolkit.getDefaultToolkit()
				.createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
		// Set the blank cursor to the JFrame.
		this.getContentPane().setCursor(blankCursor);
	}

	private MouseListener createMouseListener () {
		return new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int dx = 0;
				try {
					dx = w / grid.getNumColumns();
				} catch (RemoteException ex) {
					throw new RuntimeException(ex);
				}
				int dy = 0;
				try {
					dy = h / grid.getNumRows();
				} catch (RemoteException ex) {
					throw new RuntimeException(ex);
				}
				int col = e.getX() / dx;
				int row = e.getY() / dy;
				pixelListeners.forEach(l -> {
					try {
						l.selectedCell(col, row);
					} catch (RemoteException ex) {
						throw new RuntimeException(ex);
					}
				});
			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}
		};
	}

	private MouseMotionListener createMotionListener() {
		return new MouseMotionListener() {
			@Override
			public void mouseDragged(MouseEvent e) {}

			@Override
			public void mouseMoved(MouseEvent e) {
				movedListener.forEach(l -> {
					try {
						l.mouseMoved(e.getX(), e.getY());
					} catch (RemoteException ex) {
						throw new RuntimeException(ex);
					}
				});
			}
		};
	}
}
