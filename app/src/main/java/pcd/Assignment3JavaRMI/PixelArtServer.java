package pcd.Assignment3JavaRMI;

import pcd.Assignment3JavaRMI.view.*;
import pcd.Assignment3JavaRMI.view.services.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PixelArtServer {

    public static void main(String args[]) {

        try {
            BrushManagerService brushManager = new BrushManager();
            BrushManagerService brushManagerStub = (BrushManagerService) UnicastRemoteObject.exportObject(brushManager, 0);

            PixelGridService grid = new PixelGrid(40,40);
            PixelGridService gridStub = (PixelGridService) UnicastRemoteObject.exportObject(grid, 0);

            Registry registry = LocateRegistry.createRegistry(1099);
            registry.rebind("brushManager", brushManagerStub);
            registry.rebind("pixelGrid", gridStub);

            System.out.println("Server ready to execute.");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
