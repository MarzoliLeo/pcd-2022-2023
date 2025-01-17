package pcd.ass03JavaRMI;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.rmi.RemoteException;



public class VisualiserPanel extends JPanel {
    private static final int BRUSH_SIZE = 10;
    private static final int STROKE_SIZE = 1;
    private final PixelGridService gridService;
    private final BrushManagerService brushManagerService;
    private final int w,h;

    public VisualiserPanel(PixelGridService gridService, BrushManagerService brushManagerService, int w, int h){
        setSize(w,h);
        this.gridService = gridService;
        this.brushManagerService = brushManagerService;
        this.w = w;
        this.h = h;
        this.setPreferredSize(new Dimension(w, h));
    }

    public void paint(Graphics g){
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, this.getWidth(), this.getHeight());

        try {
            int dx = w / gridService.getNumColumns();
            int dy = h / gridService.getNumRows();
            g2.setStroke(new BasicStroke(STROKE_SIZE));
            for (int i = 0; i < gridService.getNumRows(); i++) {
                int y = i * dy;
                g2.drawLine(0, y, w, y);
            }

            for (int i = 0; i < gridService.getNumColumns(); i++) {
                int x = i * dx;
                g2.drawLine(x, 0, x, h);
            }

            for (int row = 0; row < gridService.getNumRows(); row++) {
                int y = row * dy;
                for (int column = 0; column < gridService.getNumColumns(); column++) {
                    int x = column * dx;
                    int color = gridService.getColor(column, row);
                    if (color != 0) {
                        g2.setColor(new Color(color));
                        g2.fillRect(x + STROKE_SIZE, y + STROKE_SIZE, dx - STROKE_SIZE, dy - STROKE_SIZE);
                    }
                }
            }

            this.draw(g2);

        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }

    }

    public void draw(final Graphics2D g) throws RemoteException {
        brushManagerService.getBrushes().forEach(brush -> {
            try {
                g.setColor(new Color(brush.getColor()));
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            Ellipse2D.Double circle = null;
            try {
                circle = new Ellipse2D.Double(brush.getX() - BRUSH_SIZE / 2.0, brush.getY() - BRUSH_SIZE / 2.0, BRUSH_SIZE, BRUSH_SIZE);
            } catch (RemoteException e) {
                throw new RuntimeException(e);
            }
            // draw the polygon
            g.fill(circle);
            g.setStroke(new BasicStroke(STROKE_SIZE));
            g.setColor(Color.BLACK);
            g.draw(circle);
        });
    }
}
