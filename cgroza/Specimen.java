package cgroza;
import cgroza.Genome;
import cgroza.Point;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.LinkedList;
import java.awt.Polygon;
import javax.swing.SwingUtilities;
import java.awt.Color;
import javax.swing.BorderFactory;
import javax.swing.border.Border;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

// Specimen is responsabile for drawing a single individual. It offers methods
// in order to abstract the interaction with the rest of the program.
public class Specimen extends JComponent
{
    private boolean selected = false; // specifies wether this specimen was chosen by user.
    private Genome genome; // keeps the specimen's genome.
    private final int BORDER_WIDTH = 5;
    public Specimen()
        {
            this(new Genome());
        }
    public Specimen(Genome g)
        {
            super();
            genome = g;
            Border b = BorderFactory.createLineBorder(Color.BLACK);
            setBorder(b);
            addMouseListener(new MouseListener ()
                {
                    public void mouseClicked(MouseEvent e)
                        {
                            clicked();
                        }
                    public void mouseExited(MouseEvent e)
                        {
                        }
                    public void mouseEntered(MouseEvent e)
                        {
                        }
                    public void mouseReleased(MouseEvent e)
                        {
                        }
                    public void mousePressed(MouseEvent e)
                        {
                        }
                });
            setVisible(true);

        }
    public boolean isSelected()   // returns selected
        {
            return selected;
        }
    public Genome getGenome()    // returns genome
        {
            return genome;
        }
    public void setGenome(Genome g)
        {
            genome = g;
        }
    // API function. It draws the polygons represented by the genomes in its
    // respective component.
    public void paintComponent(Graphics g)
        {
            super.paintComponent(g);

            confineGenome();
            // construct swing polygon from genome
            Polygon polygon = new Polygon();
            for(Point p : genome.getPoints())
            {
                polygon.addPoint(p.x, p.y);
            }
            g.setColor(Color.RED);
            g.drawPolygon(polygon);
        }
    // produceOffspring returns N number of mutated copies of itself.
    public LinkedList<Genome> produceOffspring(int n)
        {
            LinkedList<Genome> offspring = new LinkedList<Genome>();
            for(int i = 0; i < n; i++)
                offspring.add(genome.mutate());
            return offspring;
        }
    public void clicked()
        {
            selected = !selected;
            // change border color
            if(selected)
                setBorder(BorderFactory.createLineBorder(Color.RED, BORDER_WIDTH));
            else
                setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    public void select()
        {
            selected = true;
            setBorder(BorderFactory.createLineBorder(Color.RED, BORDER_WIDTH));
        }
    public void unselect()
        {
            selected = false;
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    public void confineGenome()
        {
            // used for displaying polygons inside the component in order to avoid shapes
            // being drawn off the frame
            int yDimension = (int) getSize().getHeight();
            int xDimension = (int) getSize().getWidth();
            
            // take left most point, find distance to top edge
            // take top most point, find distance to top edge
            LinkedList<Point> points = genome.getPoints();
            Point smallestPX = points.peek();
            Point smallestPY = points.peek();
            for(Point p : points)
            {
                if(p.x < smallestPX.x) smallestPX = p;
                if(p.y < smallestPY.y) smallestPY = p;
            }
            int xOffset = smallestPX.x - BORDER_WIDTH;
            int yOffset = smallestPY.y - BORDER_WIDTH;
            // offset all points by X, Y
            for(Point p : points)
            {
                p.x -= xOffset;
                p.y -= yOffset;
            }
            // cut all points that are still beyond the borders of JComponent
            for(Point p : points)
            {
                if(p.x > xDimension) p.x -= p.x - xDimension;
                if(p.y > yDimension) p.y -= p.y - yDimension;
            }
        }
}
