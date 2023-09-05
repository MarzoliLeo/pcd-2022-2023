package pcd.Assignment3JavaRMI.view;

import pcd.Assignment3JavaRMI.view.services.PixelGridService;

import java.rmi.RemoteException;
import java.util.Arrays;

public class PixelGrid implements PixelGridService {
	private final int nRows;
	private final int nColumns;
	private final int[][] grid;
	
	public PixelGrid(final int nRows, final int nColumns) {
		this.nRows = nRows;
		this.nColumns = nColumns;
		grid = new int[nRows][nColumns];
	}

	public void clear() throws RemoteException {
		for (int i = 0; i < nRows; i++) {
			Arrays.fill(grid[i], 0);
		}
	}
	
	synchronized public void set(final int x, final int y, final int color) throws RemoteException {
		grid[y][x] = color;
	}
	
	public int get(int x, int y) throws RemoteException {
		return grid[y][x];
	}
	
	public int getNumRows() throws RemoteException {
		return this.nRows;
	}

	public int getNumColumns() throws RemoteException {
		return this.nColumns;
	}
	
}
