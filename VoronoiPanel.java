import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class VoronoiPanel extends JPanel {

    private final List<Point2D> points = new ArrayList<>(); // List to store points added by the user
    private List<List<Point2D>> voronoiCells; // List to store the computed Voronoi cells

    // Constructor to set up the panel and mouse listener
    public VoronoiPanel() {
        // Add a mouse listener to capture clicks and add points
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Add the point where the user clicked to the list of points
                points.add(new Point2D.Double(e.getX(), e.getY()));
                System.out.println("Point added: " + e.getX() + ", " + e.getY()); // Debug statement
                repaint(); // Repaint the panel to show the new point
            }
        });
    }

    // Method to generate the Voronoi diagram from the points
    public void generateVoronoi() {
        if (points.isEmpty()) {
            System.out.println("No points to generate Voronoi diagram."); // Debug statement
            return;
        }

        // Create an instance of VoronoiAlgorithm and compute the Voronoi diagram
        VoronoiAlgorithm voronoi = new VoronoiAlgorithm();
        VoronoiAlgorithm.VoronoiDiagram diagram = voronoi.compute(points, 0.5);
        voronoiCells = diagram.getCells();

        // Debug statements to print the Voronoi cells
        System.out.println("Voronoi cells computed:");
        for (List<Point2D> cell : voronoiCells) {
            System.out.println(cell);
        }

        repaint(); // Repaint the panel to show the Voronoi cells
    }

    // Override the paintComponent method to draw points and Voronoi cells
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw points in red
        g2d.setColor(Color.RED);
        int pointSize = 10;
        for (Point2D point : points) {
            g2d.fill(new Ellipse2D.Double(point.getX() - (double) pointSize / 2, point.getY() - (double) pointSize / 2, pointSize, pointSize));
        }

        // Draw Voronoi cells in black
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
                    g2d.draw(path); // Draw the path of the Voronoi cell
                }
            }
        }
    }
}