package pcd.ass03JavaRMI;

import pcd.ass03JavaRMI.BrushManager;
import pcd.ass03JavaRMI.BrushManagerService;
import pcd.ass03JavaRMI.PixelGrid;
import pcd.ass03JavaRMI.PixelGridService;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class PixelArtMainServer {
    public static void main(String[] args) {
        try {
            // Start the RMI Registry.
            LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            PixelGridService gridService = new PixelGrid(40, 40);
            Naming.rebind("PixelGridService", gridService);

            BrushManagerService brushManagerService = new BrushManager();
            Naming.rebind("BrushManagerService", brushManagerService);

            System.out.println("Oggetti caricati sul server e pronti per l'uso.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
