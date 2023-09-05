package pcd.ass03JavaRMI;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

public class Brush implements Remote, Serializable, MousePointerService {
    private int x, y;
    private int color;
    private String clientId;

    public Brush(final String clientId, int x, int y, int color) throws RemoteException {
        super();
        this.clientId = clientId;
        this.x = x;
        this.y = y;
        this.color = color;
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
        return this.color;
    }

    @Override
    public void setColor(int color) throws RemoteException {
        this.color = color;
    }

    public String getClientId() {
        return clientId;
    }
}

