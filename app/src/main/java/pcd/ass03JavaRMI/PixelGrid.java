package pcd.ass03JavaRMI;

import java.awt.*;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;


public class PixelGrid extends UnicastRemoteObject implements PixelGridService {
    private final int nRows;
    private final int nColumns;
    private final int[][] grid;

    public PixelGrid(int nRows, int nColumns) throws RemoteException {
        super();
        this.nRows = nRows;
        this.nColumns = nColumns;
        grid = new int[nRows][nColumns];
    }

    @Override
    public void setColor(int x, int y, int color) throws RemoteException {
        grid[y][x] = color;
    }

    @Override
    public int getColor(int x, int y) throws RemoteException {
        return grid[y][x];
    }

    @Override
    public int getNumRows() throws RemoteException {
        return nRows;
    }

    @Override
    public int getNumColumns() throws RemoteException {
        return nColumns;
    }

    //New method for the GUI. It's called by the server when a client has selected a cell
    @Override
    public void selectedCells(int col, int row) throws RemoteException {
        if (col >= 0 && col < nColumns && row >= 0 && row < nRows) {
                setColor(col, row, Color.BLACK.getRGB());

        }
    }





}
