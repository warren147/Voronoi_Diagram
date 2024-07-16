import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class VoronoiPanel extends JPanel {

    private final List<Point2D> points = new ArrayList<>();
    private List<List<Point2D>> voronoiCells;

    public VoronoiPanel() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(new Point2D.Double(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    public void generateVoronoi() {
        Voronoi voronoi = new Voronoi();
        Voronoi.VoronoiDiagram diagram = voronoi.compute(points, 0.5);
        voronoiCells = diagram.getCells();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw points
        g2d.setColor(Color.RED);
        for (Point2D point : points) {
            g2d.fill(new Ellipse2D.Double(point.getX() - 2, point.getY() - 2, 4, 4));
        }

        // Draw Voronoi cells
        if (voronoiCells != null) {
            g2d.setColor(Color.BLACK);
            for (List<Point2D> cell : voronoiCells) {
                if (cell != null) {
                    Path2D path = new Path2D.Double();
                    boolean first = true;
                    for (Point2D point : cell) {
                        if (first) {
                            path.moveTo(point.getX(), point.getY());
                            first = false;
                        } else {
                            path.lineTo(point.getX(), point.getY());
                        }
                    }
                    path.closePath();
                    g2d.draw(path);
                }
            }
        }
    }
}