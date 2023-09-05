package pcd.Assignment3JavaRMI.view.services;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PixelGridService extends Remote {
    void set(int x, int y, int color) throws RemoteException;

    int getNumColumns() throws RemoteException;

    int getNumRows() throws RemoteException;

    int get(int column, int row) throws RemoteException;
}
