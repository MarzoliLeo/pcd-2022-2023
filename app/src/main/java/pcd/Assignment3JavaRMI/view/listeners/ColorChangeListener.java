package pcd.Assignment3JavaRMI.view.listeners;

import java.rmi.RemoteException;

public interface ColorChangeListener {
    void colorChanged(int color) throws RemoteException;
}
