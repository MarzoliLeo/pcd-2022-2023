package pcd.ass03JavaRMI;

import java.awt.*;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface BrushManagerService extends Remote {
    void addBrush(Brush brush) throws RemoteException;
    void removeBrush(Brush brush) throws RemoteException;
    void draw(final Graphics2D g) throws RemoteException;
    void mouseMoved(int x, int y) throws IOException;
}
