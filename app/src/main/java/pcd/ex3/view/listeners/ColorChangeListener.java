package pcd.ex3.view.listeners;

import java.rmi.RemoteException;

public interface ColorChangeListener {
    void colorChanged(int color) throws RemoteException;
}
