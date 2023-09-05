package pcd.ass03JavaRMI;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.UUID;

public class PixelArtMainClient {
    public static int randomColor() {
        Random rand = new Random();
        return rand.nextInt(256 * 256 * 256);
    }

    public static void main(String[] args) {
        try {
            // Retrive the rmiregistry of the server.
            Registry registry = LocateRegistry.getRegistry("localhost", Registry.REGISTRY_PORT);

            PixelGridService gridService = new PixelGrid(40, 40);
            Naming.rebind("PixelGridService", gridService);

            BrushManagerService brushManagerService = new BrushManager();
            Naming.rebind("BrushManagerService", brushManagerService);

            // Look up the remote object by its binding name
            /*PixelGridService gridService = (PixelGridService) registry.lookup("PixelGridService");
            BrushManagerService brushManagerService = (BrushManagerService) registry.lookup("BrushManagerService");*/


            String id = UUID.randomUUID().toString();
            var localBrush = new Brush(id, 0, 0, randomColor());
            brushManagerService.addBrush(localBrush);  // Utilizza brushManagerService anziché brushManager

            PixelGrid grid = new PixelGrid(40, 40);
            PixelGridView view = new PixelGridView(gridService, brushManagerService, 800, 800);
            view.addMouseMovedListener((x, y) -> {
                localBrush.updatePosition(x, y);
                view.refresh();
            });
            view.addPixelGridEventListener((x, y) -> {
                grid.setColor(x, y, localBrush.getColor());
                view.refresh();
            });
            view.addColorChangedListener(localBrush::setColor);
            view.display();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
