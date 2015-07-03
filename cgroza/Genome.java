package cgroza;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Random;
import cgroza.Point;
import cgroza.Polygons;
import cgroza.Vector;
import cgroza.TurnPoint;
import java.util.NoSuchElementException;
import java.util.Collections;
// Polar representation of Point. Only necessary for clockwise sorting of
// lists of points. Represented as a vector.
class PolarPoint extends Vector implements Comparable
{
    // Associates the polar point with the corresponding Cartesian point.
    public Point point;

    public PolarPoint(Point p, double centerX, double centerY )
        {
            super(p, new Point(centerX, centerY));
            point = p;
            // Convert negative angles to equivalent positive ones. Simplifies
            // sorting clockwise.
            if(angle < 0) angle += 2 * Math.PI;
        }
    // Comparison function used in clockwise sorting.
    public int compareTo(Object p)
        {
            // Compare points by angle. If angles are equal, compare by length.
            // If length is larger, then this PolarPoint is smaller and
            // vice-versa.
            if (((PolarPoint) p).angle < angle) return 1;
            else if (((PolarPoint) p).angle > angle) return -1;
            else
            {
                if (((PolarPoint) p).length < length) return -1;
                else if (((PolarPoint) p).length > length) return 1;
                else return 0;

            }
        }
}
// Implements topological sorting (clockwise) of points in a list.
class ClockwiseSort
{
    // After calling sort on a list of points, said points will be ordered as in
    // a polygon. The order in the list will correspong to the drawing order of
    // points so that the polygon does not intersect itself.
    static void sort(LinkedList<Point> ps)
        {
            // Find center point that is equidistant from all points, which is
            // the average of the x,y coordiates of all points.
            double centerX = 0;
            double centerY = 0;
            for(Point p: ps)
            {
                centerX += p.x;
                centerY += p.y;
            }
            centerY /= ps.size();
            centerX /= ps.size();
            // Make a polar points list for sorting.
            LinkedList<PolarPoint> polarPs = new LinkedList<PolarPoint>();
            for(Point p : ps)
            {
                polarPs.add(new PolarPoint(p, centerX, centerY));
            }
            Collections.sort(polarPs); //sort according to angle
            // Extract the points in clockwise order from ordered polar point
            // list.
            ps.clear();
            for(PolarPoint p : polarPs)
            {
                ps.add(p.point);
            }
        }
}

// "Flatland"-like genome. Shapes are guaranteed to be polygons. The polygons
// are represented as a list of vertices implemented by the class Point. The
// class is responsible for mutating and comparing itself with other genomes.
public class Genome
{
    private LinkedList<Point> points;
    private int nPoints;  // Defines the number of points in the polygon.
    // Constant defining the rate of mutation of "genes".
    public static final int MAX__POINTS = 100;
    public static final int MIN__POINTS = 3;
    // Define the mutation rate of "genes" as percentages (%).
    public static final double Y_MUTATION_RATE = 25;
    public static final double X_MUTATION_RATE = 25;
    public static final int POINT_MUTATION_RANGE = 15;
    public static final double SEGMENT_MUTATION_RATE = 10;
    public static final int SEGMENT_VARIATION_RANGE = 5;

    public Genome()
        {
            points = new LinkedList<Point>();
            nPoints = 0;
        }

    public Genome(LinkedList<Point> segs)
        {
            ClockwiseSort.sort(segs);
            points = segs;
            nPoints = segs.size();
        }
    // Copy constructor, necessary to create independent copies of genome
    // mutations.
    public Genome(Genome g)
        {
            // Create a deep copy of the g Genome's list of points. This
            // creating a new list and then adding a new reference to a copy of
            // every in the original list.
            points = new LinkedList<Point> ();
            // create a deep copy of the list
            for(Point p : g.getPoints())
            {
                if(p == null)
                    System.out.println("NULL");
                points.add(new Point(p));
            }
            nPoints = points.size();
        }
    // Produces a random mutation of itself. This is done by deciding which
    // points are changed in a random manner. The amount of change is also
    // random, although it is limited in a certain boundary. Note that this does
    // not modify the object in place, but creates a copy and returns it the
    // mutated version instead.
    public Genome mutate()
        {
            Random rand = new Random();
            // Create a new genome and mutate it.
            Genome g = new Genome(this);
            // Mutate number of points. restricted to +/- 3.
            boolean willMutate = rand.nextInt(101) <= SEGMENT_MUTATION_RATE;
            // Find random variation [-2, 2]. The variation must respect the minimum
            // and maximum point number.
            double variation = rand.nextInt(SEGMENT_VARIATION_RANGE) -
                SEGMENT_VARIATION_RANGE / 2;
            // Needed to generate negative random numbers with Random.nextInt().
            int halfRate = POINT_MUTATION_RANGE / 2;
            if(variation > 0)
            {
                // Add points to genome between two random points.
                while(variation > 0 && g.getPoints().size() < MAX__POINTS)
                {
                    int index = rand.nextInt(points.size() - 2);
                    // Calculate midpoint coordinates.
                    Point p1 = g.getPoints().get(index);
                    Point p2 = g.getPoints().get(index + 1);
                    Point midPoint = new Point((p1.x + p2.x)/2, (p1.y + p2.y)/2);
                    // Introduce random variation at this point.
                    midPoint.moveY(rand.nextInt(POINT_MUTATION_RANGE) - halfRate);
                    midPoint.moveX(rand.nextInt(POINT_MUTATION_RANGE) - halfRate);
                    g.getPoints().add(index, midPoint);
                    variation--;
                }
            }
            else if(variation < 0 && g.getPoints().size() > MIN__POINTS)
            {
                // Remove random points from genome.
                while(variation < 0 && g.getPoints().size() > MIN__POINTS)
                {
                    g.getPoints().remove(rand.nextInt(g.getPoints().size()));
                    variation ++;
                }
            }
            // Mutate X and Y coordinates. deviation is confined within values so
            // that polygons are not too unstable.
            Iterator<Point> it = g.getPoints().listIterator(0);
            while (it.hasNext()){
                Point p = it.next();
                // Y
                willMutate = rand.nextInt(101) <= Y_MUTATION_RATE;
                if(willMutate)
                    p.moveY(rand.nextInt(POINT_MUTATION_RANGE) - halfRate);
                // X
                willMutate = rand.nextInt(101) <= X_MUTATION_RATE;
                if(willMutate)
                    p.moveX(rand.nextInt(POINT_MUTATION_RANGE) - halfRate);
            }
            // Mutations might have changed the order of points in a polygon.
            // Therefore, it is necessary to reorder the list of points to
            // mimic their order within the polygon (clockwise). This will avoid
            // drawing figures that intersect themselves.
            ClockwiseSort.sort(g.getPoints());
            return g;
        }
    // Returns the number of vertices that define the polygon.
    public int getNPoints()
        {
            return nPoints;
        }
    public void setPoints(LinkedList<Point> ps)
        {
            points = ps;
        }
    // Returns the list storing the vertices that define the polygon.
    public LinkedList<Point> getPoints()
        {
            return points;
        }
    // This function uses turning functions to compare the polygons of each
    // genome. The metric is called turning distance, a metric of similarity.
    // The smaller the value returned by this function, the more similar the
    // polygons. A value of 0 indicates exact equality.
    public double getSimilarity(Genome g)
        {
            LinkedList<Vector> sides = g.toVectorList();
            LinkedList<TurnPoint> turnPointsF1 = Polygons.getTurnPoints(toVectorList());
            LinkedList<TurnPoint> turnPointsF2 = Polygons.getTurnPoints(sides);

            TurnFunction turnF1 = new TurnFunction(turnPointsF1);
            TurnFunction turnF2 = new TurnFunction(turnPointsF2);

            Vector ringOrigin = sides.getFirst(); // First order.
            double distance = turnF1.distanceWith(turnF2);

            sides.addLast(sides.removeFirst()); // Rotate polygon.
            while(sides.getFirst() != ringOrigin)
            {
                turnPointsF2 = Polygons.getTurnPoints(sides);
                turnF2 = new TurnFunction(turnPointsF2);
                double d = turnF1.distanceWith(turnF2);
                if(d < distance)
                    distance = d;
                sides.addLast(sides.removeFirst()); // Rotate polygon.
            }

            return distance;
        }
    // Converts a list of points to a list of vectors. The angle of the vector
    // is equal to the angle formed by its length with the horizontal.
    public LinkedList<Vector> toVectorList()
        {
            LinkedList<Vector> vectors = new LinkedList<Vector>();
            ListIterator<Point> it = points.listIterator(0);

            // Loop through the rest of the list to find the turning vector at
            // every angle.
            try
            {
                while(it.hasNext())
                {
                    vectors.add(Point.toVector(it.next(), it.next()));
                    it.previous();
                }
            }
            catch(NoSuchElementException e)
            {
                vectors.add(Point.toVector(points.getLast(), points.getFirst()));
            }
            return vectors;
        }
}
