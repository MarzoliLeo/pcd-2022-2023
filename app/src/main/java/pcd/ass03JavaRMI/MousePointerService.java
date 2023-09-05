package pcd.ass03JavaRMI;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MousePointerService extends Remote {
    void updatePosition(int x, int y) throws RemoteException;
    int getX() throws RemoteException;
    int getY() throws RemoteException;
    int getColor() throws RemoteException;
    void setColor(int color) throws RemoteException;

}
