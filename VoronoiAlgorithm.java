import java.awt.geom.*;
import java.util.*;
import java.util.List;

public class VoronoiAlgorithm {

    // Class representing a Voronoi Diagram with sites and their corresponding cells
    public static class VoronoiDiagram {
        private final List<Point2D> sites;
        private final List<List<Point2D>> cells;

        public VoronoiDiagram(List<Point2D> sites, List<List<Point2D>> cells) {
            this.sites = sites;
            this.cells = cells;
        }

        public List<Point2D> getSites() {
            return sites;
        }

        public List<List<Point2D>> getCells() {
            return cells;
        }
    }

    // Computes the Voronoi diagram for the given sites
    public VoronoiDiagram compute(List<Point2D> sites, double pad) {
        double[] boundary = pointsBoundary(sites);

        double xLeft = boundary[0] - pad;
        double yBottom = boundary[1] - pad;
        double xRight = boundary[2] + pad;
        double yTop = boundary[3] + pad;

        List<Point2D> newSites = preprocessSites(sites);

        int n = newSites.size();

        // Define the bounding box for the Voronoi diagram
        List<Point2D> voronoiBox = Arrays.asList(
                new Point2D.Double(0, 795),
                new Point2D.Double(795, 795),
                new Point2D.Double(795, 0),
                new Point2D.Double(0, 0)
        );

        List<List<Point2D>> cells = new ArrayList<>();

        // Compute the Voronoi cell for each site
        for (int i = 0; i < n; i++) {
            List<Point2D> cell = new ArrayList<>(voronoiBox);
            Point2D currentSite = newSites.get(i);

            for (int j = 0; j < n; j++) {
                if (i == j) continue;

                List<Point2D> newCell = new ArrayList<>();
                Point2D nextSite = newSites.get(j);
                double[] bisector = twoPointsBisector(currentSite, nextSite);

                if (bisector[0] == 0 && bisector[1] == 0) continue;

                for (int k = 0; k < cell.size(); k++) {
                    Point2D currentVertex = cell.get(k);
                    Point2D nextVertex = cell.get((k + 1) % cell.size());
                    Point2D firstIntersection = lineAndSegmentIntersection(bisector, currentVertex, nextVertex);

                    System.out.println("Current Vertex: " + currentVertex + ", Next Vertex: " + nextVertex);
                    if (firstIntersection != null) {
                        System.out.println("First Intersection: " + firstIntersection);
                        boolean intersectionIsNextVertex = firstIntersection.equals(nextVertex);
                        int firstIntersectionIndex;

                        if (intersectionIsNextVertex) {
                            newCell.add(nextVertex);
                            newCell.add(cell.get((k + 2) % cell.size()));
                            firstIntersectionIndex = (k + 2) % cell.size();
                        } else {
                            newCell.add(firstIntersection);
                            newCell.add(nextVertex);
                            firstIntersectionIndex = (k + 1) % cell.size();
                        }

                        for (int m = firstIntersectionIndex; m < cell.size(); m++) {
                            currentVertex = cell.get(m);
                            nextVertex = cell.get((m + 1) % cell.size());
                            Point2D secondIntersection = lineAndSegmentIntersection(bisector, currentVertex, nextVertex);

                            if (secondIntersection != null) {
                                System.out.println("Second Intersection: " + secondIntersection);
                                newCell.add(secondIntersection);
                                break;
                            } else {
                                newCell.add(nextVertex);
                            }
                        }

                        if (!isPointInPolygon(currentSite, newCell)) {
                            newCell = new ArrayList<>();
                            for (int m = firstIntersectionIndex; m % cell.size() > firstIntersectionIndex || m % cell.size() < firstIntersectionIndex; m++) {
                                newCell.add(cell.get(m % cell.size()));
                            }
                            newCell.add(firstIntersection);
                        }
                        break;
                    }
                }

                if (newCell.isEmpty()) {
                    newCell = cell;
                }
                cell = newCell;
            }

            if (!cell.isEmpty()) {
                cells.add(cell);
            } else {
                cells.add(null);
            }
        }

        return new VoronoiDiagram(newSites, cells);
    }

    // Preprocess the sites by adding small random perturbations
    private List<Point2D> preprocessSites(List<Point2D> sites) {
        Set<Point2D> uniqueSites = new HashSet<>(sites);

        double[] magnitude = maxXY(new ArrayList<>(uniqueSites));
        double magX = eps * magnitude[0] * 100;
        double magY = eps * magnitude[1] * 100;

        List<Point2D> newSites = new ArrayList<>(uniqueSites);
        Random random = new Random();

        for (int i = 0; i < newSites.size(); i++) {
            Point2D site = newSites.get(i);
            double newX = site.getX() + random.nextDouble() * magX;
            double newY = site.getY() + random.nextDouble() * magY;
            newSites.set(i, new Point2D.Double(newX, newY));
        }

        return newSites;
    }

    // Calculate the maximum x and y values among a list of points
    private double[] maxXY(List<Point2D> points) {
        double x = Math.abs(points.get(0).getX());
        double y = Math.abs(points.get(0).getY());

        for (int i = 1; i < points.size(); i++) {
            double x1 = Math.abs(points.get(i).getX());
            double y1 = Math.abs(points.get(i).getY());

            if (x1 > x) x = x1;
            if (y1 > y) y = y1;
        }

        x = Math.max(x, 1);
        y = Math.max(y, 1);

        return new double[]{x, y};
    }

    // Compute the perpendicular bisector of the line segment defined by two points
    private double[] twoPointsBisector(Point2D A, Point2D B) {
        double midX = (A.getX() + B.getX()) / 2;
        double midY = (A.getY() + B.getY()) / 2;

        double a = B.getX() - A.getX();
        double b = B.getY() - A.getY();
        double c = -midX * a - midY * b;

        return new double[]{a, b, c};
    }

    // Check if a point is inside a polygon
    private boolean isPointInPolygon(Point2D point, List<Point2D> polygon) {
        int n = polygon.size();

        for (int i = 0; i < n; i++) {
            Point2D p1 = polygon.get(i);
            Point2D p2 = polygon.get((i + 1) % n);
            Point2D p3 = polygon.get((i + 2) % n);
            double cross1 = cross2D(p1, p2, point);
            double cross2 = cross2D(p1, p2, p3);

            if (cross1 * cross2 < 0) {
                return false;
            }
        }

        return true;
    }

    // Calculate the cross product of two vectors in 2D space
    private double cross2D(Point2D a, Point2D b, Point2D c) {
        double ax = a.getX() - b.getX();
        double ay = a.getY() - b.getY();
        double bx = c.getX() - b.getX();
        double by = c.getY() - b.getY();

        return ax * by - ay * bx;
    }

    // Find the intersection of a line and a segment
    private Point2D lineAndSegmentIntersection(double[] line, Point2D A, Point2D B) {
        double a1 = A.getY() - B.getY();
        double b1 = B.getX() - A.getX();
        double c1 = A.getX() * B.getY() - B.getX() * A.getY();

        double a2 = line[0];
        double b2 = line[1];
        double c2 = line[2];

        double det = a1 * b2 - a2 * b1;

        if (Math.abs(det) < eps) {
            return null;
        }

        double x = (b1 * c2 - b2 * c1) / det;
        double y = (a2 * c1 - a1 * c2) / det;

        if ((x >= Math.min(A.getX(), B.getX()) && x <= Math.max(A.getX(), B.getX())) &&
                (y >= Math.min(A.getY(), B.getY()) && y <= Math.max(A.getY(), B.getY()))) {
            return new Point2D.Double(x, y);
        }

        return null;
    }

    // Small epsilon value for floating point comparisons
    private double eps = Math.pow(2, -23);

    // Calculate the boundary of a set of points
    private double[] pointsBoundary(List<Point2D> points) {
        double minX = points.get(0).getX();
        double minY = points.get(0).getY();
        double maxX = points.get(0).getX();
        double maxY = points.get(0).getY();
        for (int i = 1; i < points.size(); i++) {
            double x = points.get(i).getX();
            double y = points.get(i).getY();

            if (x < minX) minX = x;
            if (x > maxX) maxX = x;
            if (y < minY) minY = y;
            if (y > maxY) maxY = y;
        }

        return new double[]{minX, minY, maxX, maxY};
    }
}