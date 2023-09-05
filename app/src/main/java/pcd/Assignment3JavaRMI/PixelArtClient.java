package pcd.Assignment3JavaRMI;

import pcd.Assignment3JavaRMI.view.PixelGridView;
import pcd.Assignment3JavaRMI.view.Brush;
import pcd.Assignment3JavaRMI.view.services.*;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.UUID;

public class PixelArtClient {

    public static int randomColor() {
        Random rand = new Random();
        return rand.nextInt(256 * 256 * 256);
    }

    public static void main(String args[]) throws RemoteException, NotBoundException {

        String clientID = UUID.randomUUID().toString();
        Registry registry = LocateRegistry.getRegistry("localhost", 0);

        // Look up the remote object by its binding name
        BrushManagerService brushManager = (BrushManagerService) registry.lookup("brushManager");
        PixelGridService pixelGrid = (PixelGridService) registry.lookup("pixelGrid");

        PixelGridView view = new PixelGridView(clientID, pixelGrid, brushManager, 600, 600);

        BrushService localBrush = new Brush(clientID, randomColor());
        brushManager.addBrush(clientID, localBrush);

        view.addMouseMovedListener((x, y) -> {
            brushManager.updateBrushPosition(clientID, x, y);
            view.refresh();
        });

        view.addPixelGridEventListener((x, y) -> {
            pixelGrid.set(x, y, brushManager.getColor(clientID));
            view.refresh();
        });

        view.addColorChangedListener( color -> {
            localBrush.setColor(color);
            brushManager.addBrush(clientID, localBrush);
        });

        view.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        view.display();
    }

}
