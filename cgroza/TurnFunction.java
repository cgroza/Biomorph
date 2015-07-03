package cgroza;
import java.lang.Math;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Collections;
import java.util.NoSuchElementException;
// Rudimentary class to represent the points of the turning function.
class TurnPoint
{
    public double x;            // Position along the perimeter.
    public double y;            // Turn in angles. 
    TurnPoint(TurnPoint p)
        {
            x = p.x;
            y = p.y;
        }
    TurnPoint(double x, double y)
        {
            this.x = x;
            this.y = y;
        }
    // Calculates the turn point formed by vectors v1, v2 at the x_ points of
    // the turn function.
    public static TurnPoint findTurnPoint(double x_, Vector v1, Vector v2)
        {
            // The dot product is used to determine the value of the turn point
            // (the angle between the two vectors). The cross-product is used to
            // determine whether the turn is clockwise or anti-clockwise.
            double dotP = v2.dotProduct(v1);
            double crossP = v2.crossProductZ(v1);

            // Clockwise turn.
            if(crossP > 0)
                return new TurnPoint(x_, Math.acos(dotP/(v2.length * v1.length)));
            // Flip the sign for negative cross products because the turn is
            // anti-clockwise. Only clockwise turns may have positive signs since
            // the maximum value of the turn function must be 2Pi.
            else if(crossP < 0)
                return new TurnPoint(x_, -Math.acos(dotP/(v2.length * v1.length)));
            // The turn function remains constant for 0 cross products.
            // Therefore, the turn point is 0.
            else
                return new TurnPoint(x_, 0);
        }
}

// Represents the turn function for polygons. Provides methods for subtracting,
// integrating, squaring, accessing the values and calculating the distance with
// another turn function.
public class TurnFunction
{
    private LinkedList<TurnPoint> turnPoints;
    public TurnFunction(LinkedList<TurnPoint> ts)
        {
            turnPoints = ts;

        }
    // Calculates the area under the curve formed by the list of turn points.
    // Note that the curve has a shape similar to a step function.
    public double integrate()
        {
            // The integral of the turn function is the area under the curve.
            ListIterator<TurnPoint> it = turnPoints.listIterator(0);
            double area = 0;
            while(it.hasNext())
            {
                TurnPoint p1 = it.next();
                // Return the area if p1 is the last point in list.
                if(!it.hasNext())
                {
                    area += (1 - p1.x) * p1.y;
                    return area;
                }
                // Else, calculate the area formed by the p1,p2 rectangle and add
                // to the sum of areas
                TurnPoint p2 = it.next();
                area += (p2.x - p1.x) * p1.y;
                it.previous();
            }
            return area;
        }
    // Returns a new turn function that represents the difference between this
    // turn function and f.
    public TurnFunction substract(TurnFunction f)
        {
            // Find which function has more points (more detail).
            LinkedList<Double> xValues = new LinkedList<Double>();
            LinkedList<TurnPoint> allPoints = new LinkedList<TurnPoint>();
            allPoints.addAll(f.getTurnPoints());
            allPoints.addAll(getTurnPoints());
            for(TurnPoint p: allPoints)
                xValues.add(p.x);
            Collections.sort(xValues);
            LinkedList<TurnPoint> subPoints = new LinkedList<TurnPoint>();
            for(double x : xValues)
                subPoints.add(new TurnPoint(x, Math.abs(getValueAt(x)
                                                          - f.getValueAt(x))));
            return new TurnFunction(subPoints);
        } 
    // Squares the turn function.
    public void square()
        {
            // Square all y values.
            for(TurnPoint p : turnPoints)
                p.y = Math.pow(p.y, 2.0);
        }
    // Returns f(x) for this turning function.
    public double getValueAt(double x)
        {
            ListIterator<TurnPoint> it = turnPoints.listIterator(0);
            // Find two turn points that sandwich the x independent value.
            while(it.hasNext())
            {
                try
                {
                    TurnPoint left = it.next();
                    TurnPoint right = it.next();
                    if(left.x <= x && right.x > x)
                        return left.y;
                    it.previous();
                }
                // Return last point.
                catch(NoSuchElementException e)
                {
                    return turnPoints.getLast().y;
                }
            }
            return 0;
        }
    // Returns the points that define the turn function.
    public LinkedList<TurnPoint> getTurnPoints()
        {
            return turnPoints;
        }
    // Returns the distance with another turn function.
    public double distanceWith(TurnFunction f)
        {
            // The distance is defined as sqrt(integral((f1(x) - f2(x))^2)).
            TurnFunction diff = substract(f);
            diff.square();
            return Math.sqrt(diff.integrate());
        }
}
