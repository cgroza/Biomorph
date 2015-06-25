package cgroza;
import java.lang.Math;
// The Vector class is used to represent the vectors necessary for the
// computation of the turning function. "angle" represents the angle between
// two adjacent segments in the polygon. The "length" variable represents the
// scaled length of the side of the polygon.
class Vector
{
    public double angle;
    public double length;

    public Vector(double a, double l)
        {
            angle = a;
            length = l;
        }
    // Multiplies the length by s in order to scale the vector.  For example, to
    // scale a vector of length 2 to length 1, one calls scaleBy(0.5)
    public void scaleBy(double s)
        {
            length *= s;
        }
    // Calculates the dot product with another vector. This function assumes
    // that v is previous to "this" in the polygon. The angle of the vectors is
    // assumed to be the turning angle between "this" and v. Therefore, this is
    // not a general dot product function.
    public double dotProduct(Vector v)
        {
            // A . B = ||A|| . ||B|| cos a
            return length * v.length * Math.cos(angle - v.angle);
        }
    // Calculates the Z component of the cross product with another vector in
    // the same plane. This function assumes that v is previous to "this" in the
    // polygon. The angle of the vectors is assumed to be the turning angle
    // between "this" and v. Therefore, this is not a general cross prduct
    // function.
    public double crossProductZ(Vector v)
        {
            // A x B = ||A|| . ||B|| . sin a
            return length * v.length * Math.sin(angle - v.angle);
        }
}
