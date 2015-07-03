package cgroza;
import java.lang.Math;
import java.lang.Comparable;
// Represents a vertex in a polygon and provides methods for easily moving the 
// coordinates of points. Also contains vector related operations on points.
public class Point 
{
    public double x, y;

    // Copy constructor for deep copy. This is necessary for creating independent
    // copies of points that are not simply references to existing points.
    public Point(Point p)
        {
            x = p.x;
            y = p.y;
        }
    public Point(double _x, double _y)
        {
            x = _x;
            y = _y;
        }
    // General setter function.
    public void setPosition(double _x, double _y)
        {
            x = _x;
            y = _y;
        }

    // Changes the x coordinate by a constant.
    public void moveX(double x_change)
        {
            x += x_change;
        }
    // Changes the y coordinate by a constant.
    public void moveY(double y_change)
        {
            y += y_change;
        }
    // Returns the vector formed by the two points. The vector starts at p1 and
    // points to p2.
    public static Vector toVector(Point p1, Point p2)
        {
            double length = distanceBetweenPoints(p1, p2);
            double angle = Math.atan2(p2.y - p1.y, p2.x - p1.x);
            if(angle < 0) angle += 2 * Math.PI;
            return new Vector(angle, length);
        }

    // Calculates the shortest distance between points using Pythagoras'
    // theorem.
    public static double distanceBetweenPoints(Point p1, Point p2)
        {
           return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }
}
