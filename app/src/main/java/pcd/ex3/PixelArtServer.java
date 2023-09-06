package pcd.ex3;

import pcd.ex3.view.*;
import pcd.ex3.view.services.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class PixelArtServer {

    public static void main(String args[]) {

        try {
            BrushManagerService brushManager = new BrushManager();
            PixelGridService grid = new PixelGrid(40,40);

            BrushManagerService brushManagerServer = (BrushManagerService) UnicastRemoteObject.exportObject(brushManager, 0);
            PixelGridService gridServer = (PixelGridService) UnicastRemoteObject.exportObject(grid, 0);

            Registry registry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            registry.rebind("brushManager", brushManagerServer);
            registry.rebind("pixelGrid", gridServer);

            System.out.println("Server ready to execute.");

        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
