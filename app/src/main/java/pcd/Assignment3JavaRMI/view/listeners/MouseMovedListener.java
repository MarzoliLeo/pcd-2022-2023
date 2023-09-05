package pcd.Assignment3JavaRMI.view.listeners;

import java.rmi.RemoteException;

public interface MouseMovedListener {
    void mouseMoved(int x, int y) throws RemoteException;
}
