package pcd.ex3.view.services;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BrushManagerService extends Remote {

    void addBrush(final String clientID, final BrushService brush) throws RemoteException;

    void removeBrush(final String clientID) throws RemoteException;

    List<BrushService> getBrushes() throws RemoteException;

    int getColor(String clientID) throws RemoteException;

    void updateBrushPosition(String clientID, int x, int y) throws RemoteException;
}
