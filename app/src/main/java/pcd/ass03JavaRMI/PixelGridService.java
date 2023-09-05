package pcd.ass03JavaRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface PixelGridService extends Remote {
    void setColor(int x, int y, int color) throws RemoteException;
    int getColor(int x, int y) throws RemoteException;
    int getNumRows() throws RemoteException;
    int getNumColumns() throws RemoteException;

    //New method for the GUI. It's called by the server when a client has selected a cell
    void selectedCells(int col, int row) throws RemoteException;
}
