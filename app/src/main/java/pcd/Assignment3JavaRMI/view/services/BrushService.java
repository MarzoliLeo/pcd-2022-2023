package pcd.Assignment3JavaRMI.view.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BrushService extends Remote {

    void updatePosition(final int x, final int y) throws RemoteException;
    int getX() throws RemoteException;
    int getY() throws RemoteException;
    int getColor() throws RemoteException;
    void setColor(int color) throws RemoteException;
}
