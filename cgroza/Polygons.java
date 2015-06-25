package cgroza;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.lang.Math;
import cgroza.Point;
import cgroza.Genome;
import cgroza.TurnFunction;
import java.util.NoSuchElementException;

// Non instantiable utilitiy class that contains common operations on polygons.
class Polygons
{
    private Polygons()
        {
        }
    // This function calculates the turn function of a polygon as a list of
    // TurnPoint containing the x, y values of the graph.
    public static LinkedList<TurnPoint> getTurnPoints(LinkedList<Vector> vs)
        {
            Polygons.scalePolygonToUnit(vs);
            ListIterator<Vector> it = vs.listIterator(0);
            // contains the points of the turn function
            LinkedList<TurnPoint> turnPoints =
                new LinkedList<TurnPoint>();
            double x_var = 0;  // keeps track of the independent variable
            double y_var = 0;
            // Loop and find the dot and cross product between every adjacent
            // vectors. Then use the results to compute the x,y of the turn
            // function.
            try
            {
                while(it.hasNext())
                {
                    Vector v1 = it.next();
                    Vector v2 = it.next();
                    it.previous();
                    TurnPoint p = TurnPoint.findTurnPoint(x_var, v1, v2);
                    turnPoints.add(p);
                    y_var += p.y;
                    p.y = y_var;
                    x_var += v2.length;
                }
            }
            catch(NoSuchElementException e)
            {
                TurnPoint p = TurnPoint.findTurnPoint(x_var, vs.getLast(), vs.getFirst());
                y_var += p.y;
                p.y = y_var;
                turnPoints.add(p);
            }
            return turnPoints;
        }

    // This function scales evey side of the polygon so that the polygon's
    // perimeter becomes equal to 1.
    public static LinkedList<Vector> scalePolygonToUnit(LinkedList<Vector> vectors)
        {
            double perimeter = Polygons.getPerimeter(vectors);
            double scaleRatio = 1/perimeter;
            // iteratre through vectors and scale them by the scaleRatio
            for(Vector v : vectors)
            {
                v.scaleBy(scaleRatio);
            }
            return vectors;
        }
    // Calculates the polygon's perimeter.
    public static double getPerimeter(LinkedList<Vector> vectors)
        {
            // loop through list and add their lengths
            // ignore first vector as it repeats at the end of the list
            double perimeter = 0;
            for(Vector v : vectors)
            {
                perimeter += v.length;
            }
            return perimeter;
        }
}
