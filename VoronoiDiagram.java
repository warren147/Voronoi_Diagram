import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class VoronoiDiagram extends JPanel {
    private static final int CANVAS_SIZE = 800;
    private BufferedImage voronoiImage;
    private List<Point> sites;
    private List<Color> colors;

    public VoronoiDiagram() {
        this.sites = new ArrayList<>();
        this.colors = new ArrayList<>();
        voronoiImage = new BufferedImage(CANVAS_SIZE, CANVAS_SIZE, BufferedImage.TYPE_INT_RGB);
        fillWhiteBackground();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                addSite(e.getPoint());
            }
        });
    }

    private void fillWhiteBackground() {
        Graphics2D g2d = voronoiImage.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        g2d.dispose();
    }

    private void addSite(Point point) {
        sites.add(point);
        generateColors();
        computeVoronoi();
        repaint();
    }

    private void generateColors() {
        colors.clear();
        Random random = new Random();
        for (int i = 0; i < sites.size(); i++) {
            colors.add(new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256)));
        }
        Collections.shuffle(colors);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(voronoiImage, 0, 0, null);
        g.setColor(Color.RED);
        for (Point site : sites) {
            g.fillOval(site.x - 3, site.y - 3, 6, 6);
        }
    }

    private void computeVoronoi() {
        if (sites.isEmpty()) return;

        int width = voronoiImage.getWidth();
        int height = voronoiImage.getHeight();

        int[][] nearestSite = new int[width][height];
        double[][] distance = new double[width][height];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                distance[x][y] = Double.MAX_VALUE;
            }
        }

        for (int i = 0; i < sites.size(); i++) {
            Point site = sites.get(i);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    double d = site.distance(x, y);
                    if (d < distance[x][y]) {
                        distance[x][y] = d;
                        nearestSite[x][y] = i;
                    }
                }
            }
        }

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                int siteIndex = nearestSite[x][y];
                voronoiImage.setRGB(x, y, colors.get(siteIndex).getRGB());
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Voronoi Diagram");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(CANVAS_SIZE, CANVAS_SIZE);
        frame.add(new VoronoiDiagram());
        frame.setVisible(true);
    }
}