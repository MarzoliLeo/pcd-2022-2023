package pcd.Assignment3JavaRMI.view;

import pcd.Assignment3JavaRMI.view.services.BrushService;

import java.io.Serializable;
import java.rmi.RemoteException;

public class Brush implements Serializable, BrushService {

    private int x, y;
    private int brushColor;
    private final String clientID;

    public Brush(String clientID, int brushColor) {
        this.x = 0;
        this.y = 0;
        this.brushColor = brushColor;
        this.clientID = clientID;
    }

    @Override
    public void updatePosition(int x, int y) throws RemoteException {
        this.x = x;
        this.y = y;
    }

    @Override
    public int getX() throws RemoteException {
        return this.x;
    }

    @Override
    public int getY() throws RemoteException {
        return this.y;
    }

    @Override
    public int getColor() throws RemoteException {
        return this.brushColor;
    }

    @Override
    public void setColor(int brushColor) {
        this.brushColor = brushColor;
    }
}
