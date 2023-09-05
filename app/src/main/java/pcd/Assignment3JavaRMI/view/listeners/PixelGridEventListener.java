package pcd.Assignment3JavaRMI.view.listeners;

import java.rmi.RemoteException;

public interface PixelGridEventListener {
	void selectedCell(int x, int y) throws RemoteException;
}
