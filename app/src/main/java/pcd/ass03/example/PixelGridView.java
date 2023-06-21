package pcd.ass03.example;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.*;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;



public class PixelGridView extends JFrame {
	private final VisualiserPanel panel;
	private final PixelGrid grid;
	private final int w, h;
	private final List<PixelGridEventListener> pixelListeners;
	private final List<MouseMovedListener> movedListener;
	private final List<ColorChangeListener> colorChangeListeners;

	private final ConnectionFactory factory;
	private Connection connection;
	private Channel channel;
	private final String exchangeName;

	public PixelGridView(PixelGrid grid, BrushManager brushManager, int w, int h, String exchangeName) {
		this.grid = grid;
		this.w = w;
		this.h = h;
		this.exchangeName = exchangeName;
		pixelListeners = new ArrayList<>();
		movedListener = new ArrayList<>();
		colorChangeListeners = new ArrayList<>();
		setTitle(".:: PixelArt ::.");
		setResizable(false);
		panel = new VisualiserPanel(grid, brushManager, w, h);
		panel.addMouseListener(createMouseListener());
		panel.addMouseMotionListener(createMotionListener());
		var colorChangeButton = new JButton("Change color");
		colorChangeButton.addActionListener(e -> {
			var color = JColorChooser.showDialog(this, "Choose a color", Color.BLACK);
			if (color != null) {
				colorChangeListeners.forEach(l -> l.colorChanged(color.getRGB()));
			}
		});
		add(panel, BorderLayout.CENTER);
		add(colorChangeButton, BorderLayout.SOUTH);
		getContentPane().add(panel);
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		hideCursor();

		factory = new ConnectionFactory();
		factory.setHost("localhost");

		try {
			connection = factory.newConnection();
			channel = connection.createChannel();
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}

		// Add a window listener to handle client disconnection
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println("A client has closed.");
			}
		});
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

	public void addPixelGridEventListener(PixelGridEventListener l) {
		pixelListeners.add(l);
	}

	public void addMouseMovedListener(MouseMovedListener l) {
		movedListener.add(l);
	}

	public void addColorChangedListener(ColorChangeListener l) {
		colorChangeListeners.add(l);
	}

	public Channel getChannel() {
		return channel;
	}

	private void hideCursor() {
		var cursorImage = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		var blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(0, 0), "blank cursor");
		// Set the blank cursor to the JFrame.
		this.getContentPane().setCursor(blankCursor);
	}

	private MouseListener createMouseListener() {
		return new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) {
				int dx = w / grid.getNumColumns();
				int dy = h / grid.getNumRows();
				int col = e.getX() / dx;
				int row = e.getY() / dy;
				pixelListeners.forEach(l -> {
					try {
						l.selectedCell(col, row);
					} catch (IOException ex) {
						throw new RuntimeException(ex);
					}
				});
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
				movedListener.forEach(l -> l.mouseMoved(e.getX(), e.getY()));
			}
		};
	}

	public void closeConnection() {
		try {
			channel.close();
			connection.close();
			// Publish a message to notify other clients about the disconnection
			String message = "disconnect";
			channel.basicPublish(exchangeName, "", null, message.getBytes());
		} catch (IOException | TimeoutException e) {
			e.printStackTrace();
		}
	}


}