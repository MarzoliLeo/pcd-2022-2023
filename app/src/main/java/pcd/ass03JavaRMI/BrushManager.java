package pcd.ass03JavaRMI;

import pcd.ex3.view.services.BrushService;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.List;



public class BrushManager extends UnicastRemoteObject implements BrushManagerService {
    private List<Brush> brushes = new ArrayList<>();
    private List<String> brushesId = new ArrayList<>();

    //Costrutto necessario per l'extends da "UnicastRemoteObject".
    public BrushManager() throws RemoteException {
        super();
    }

    @Override
    public List<Brush>getBrushes() {
        return brushes;
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

