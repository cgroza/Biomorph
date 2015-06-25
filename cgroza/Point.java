package cgroza;
import java.lang.Math;
import java.lang.Comparable;
// Represents a vertex in a polygon and provides functions for the computation
// of the turning function. Also contains vector related operations on points.
public class Point 
{
    public int x, y;

    // Copy constructor for deep copy. This is necessary for creating independent
    // copies of points that are not simply references to existing points.
    public Point(Point p)
        {
            x = p.x;
            y = p.y;
        }
    public Point(int _x, int _y)
        {
            x = _x;
            y = _y;
        }
    // General setter function.
    public void setPosition(int _x, int _y)
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
    // Finds the angle between the segments formed by the three points. Specify
    // the desired angle by calling the function with its corresponding point as
    // the second argument. The angle is returned as radians.
    public static double findAngle(Point p1, Point p2, Point p3)
        {
            double dP1P2 = distanceBetweenPoints(p1, p2);
            double dP2P3 = distanceBetweenPoints(p2, p3);
            double dP1P3 = distanceBetweenPoints(p1, p3);
            // use the cosine law to find the angle formed by the three points.
            // a^2 = b^2 + c^2 - 2bc cos A
            // cos A = (a^2 - b^2 - c^2)/(-2bc) =>
            // A = acos((a^2 - b^2 - c^2)/(-2bc))
            double angle = Math.acos((Math.pow(dP1P3, 2) - Math.pow(dP1P2, 2) - Math.pow(dP2P3, 2))
                                     /(-2 * dP1P2 * dP2P3));
            return angle;
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

    public static double distanceBetweenPoints(Point p1, Point p2)
        {
           return Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2));
        }
}
