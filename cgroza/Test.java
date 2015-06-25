package cgroza;
import cgroza.Polygons;
import cgroza.Point;
import cgroza.Genome;
import java.util.LinkedList;
class Test
{
    public static void main(String[] args)
        {
            Point p1 = new Point (0, 0);
            Point p2 = new Point(10, 0);
            Point p3 = new Point (10, 10);
            Point p4 = new Point (0, 10);

            LinkedList<Point> points = new LinkedList<Point>();
            points.add(p1);
            points.add(p2);
            points.add(p3);
            points.add(p4);
            TopologicalSort.sort(points);
            Genome g = new Genome(points);
            Genome h = new Genome(g);
            h.getPoints().removeLast();

            TurnFunction f1 = new TurnFunction(Polygons.getTurnPoints(h.toVectorList()));
            TurnFunction f2 = new TurnFunction(Polygons.getTurnPoints(g.toVectorList()));
            TurnFunction difference = f1.substract(f2);
            
            System.out.println(f1.getValueAt(0.25));
            System.out.println(f2.getValueAt(0.25));

            printTurnFunction(f1);
            printTurnFunction(f2);
            printTurnFunction(difference);
        }
    public static void printTurnFunction(TurnFunction f)
        {
            for(TurnPoint p : f.getTurnPoints())
            {
                System.out.println(p.x + " " + p.y);
            }
            System.out.println();
        }
}
