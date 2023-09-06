package pcd.ex3.view;


import pcd.ex3.view.services.BrushService;
import pcd.ex3.view.services.BrushManagerService;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BrushManager implements BrushManagerService {

    private Map<String, BrushService> brushes = new HashMap<>();

    public void addBrush(final String clientID, final BrushService brush) {
        brushes.put(clientID, brush);
    }

    @Override
    public void removeBrush(String clientID) throws RemoteException {
        brushes.remove(clientID);
    }

    @Override
    public List<BrushService>getBrushes() {
        return brushes.values().stream().toList();
    }

    @Override
    public int getColor(String clientID) throws RemoteException {
        return brushes.get(clientID).getColor();
    }

    @Override
    public void updateBrushPosition(String clientID, int x, int y) throws RemoteException {
        brushes.get(clientID).updatePosition(x, y);
    }

}
