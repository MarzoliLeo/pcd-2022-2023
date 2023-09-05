package pcd.ass03JavaRMI;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;



public class BrushManager extends UnicastRemoteObject implements BrushManagerService {
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 2;
    private List<Brush> brushes = new ArrayList<>();
    private List<String> brushesId = new ArrayList<>();

    //Costrutto necessario per l'extends da "UnicastRemoteObject".
    public BrushManager() throws RemoteException {
        super();
    }

    @Override
    public void draw(final Graphics2D g) {
        brushes.forEach(brush -> {
            try {
                g.setColor(new Color(brush.getColor()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            Ellipse2D.Double circle = null;
            try {
                circle = new Ellipse2D.Double(brush.getX() - BRUSH_SIZE / 2.0, brush.getY() - BRUSH_SIZE / 2.0, BRUSH_SIZE, BRUSH_SIZE);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            // draw the polygon
            g.fill(circle);
            g.setStroke(new BasicStroke(STROKE_SIZE));
            g.setColor(Color.BLACK);
            g.draw(circle);
        });
    }

    @Override
    public void addBrush(Brush brush) throws RemoteException {
        brushes.add(brush);
    }

    @Override
    public void removeBrush(Brush brush) throws RemoteException {
        brushes.remove(brush);
    }

    @Override
    public void mouseMoved(int x, int y) throws IOException {
        for (Brush brush : brushes) {
            try {
                brush.updatePosition(x, y);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
        }
    }

}

