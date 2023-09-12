package pcd.ass03JavaRMI;

import java.awt.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface BrushManagerService extends Remote {
    void addBrush(Brush brush) throws RemoteException;
    void removeBrush(Brush brush) throws RemoteException;
    void mouseMoved(int x, int y) throws IOException;
    List<Brush> getBrushes() throws RemoteException;
}
