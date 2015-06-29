package cgroza;
import java.util.LinkedList;
import java.awt.Container;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Color;
import javax.swing.BorderFactory;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import cgroza.Specimen;
import cgroza.Genome;

// Selection mode denotes whether specimens are selected by the user of by the
// built-in turning function algorithm.
// MANUAL - specimens selected by the user.
// AUTO - specimens selected by the turning function algorithm.
enum SelectionMode {MANUAL, AUTO}

// Used to input a polygon graphically.
class GenomeDrawBoard extends JPanel
{
    private LinkedList<Point> points;
    public GenomeDrawBoard()
        {
            super();
            points = new LinkedList<Point>();
            addMouseListener(new MouseListener()
                {
                    public void mouseClicked(MouseEvent e)
                        {
                            // Read point relative to GenoneDrawBoard.
                            points.add(new Point(e.getX(), e.getY()));
                            ClockwiseSort.sort(points);
                            repaint();
                        }
                    public void mouseExited(MouseEvent e) {}
                    public void mouseEntered(MouseEvent e) {}
                    public void mousePressed(MouseEvent e) {}
                    public void mouseReleased(MouseEvent e) {}
                });
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    public LinkedList<Point> getPoints()
        {
            return points;
        }
    // Clears the drawing surface and deletes all accumulated points.
    public void clearBoard()
        {
            points.clear();
            repaint();
        }
    public void paintComponent(Graphics g)
        {
            super.paintComponent(g); // repaint and clear JPanel
            // construct swing polygon from genome
            Polygon polygon = new Polygon();
            for(Point p : points)
            {
                polygon.addPoint((int) p.x, (int) p.y);
            }
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawPolygon(polygon);
        }
}


public class Config extends JFrame
{
    private JRadioButton autoRButton;
    private JRadioButton manualRButton;
    private JButton clearJButton;
    private JButton okJButton;
    GenomeDrawBoard board;
    public Config()
        {
            // Create GUI components.
            // Radio buttons.
            JPanel rButtonPanel = new JPanel();
            autoRButton = new JRadioButton("Auto");
            manualRButton = new JRadioButton("Manual");
            manualRButton.setSelected(true);
            rButtonPanel.add(autoRButton, BorderLayout.WEST);
            rButtonPanel.add(manualRButton, BorderLayout.EAST);
            ButtonGroup rButtonGroup = new ButtonGroup();
            rButtonGroup.add(autoRButton);
            rButtonGroup.add(manualRButton);

            board = new GenomeDrawBoard();
            // Buttons.
            JPanel buttonPanel = new JPanel();
            clearJButton = new JButton("Clear");
            clearJButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                        {
                            board.clearBoard();
                        }
                });
            okJButton = new JButton("OK");
            okJButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent e)
                        {
                            setVisible(false);
                        }
                });
            buttonPanel.add(clearJButton, BorderLayout.EAST);
            buttonPanel.add(okJButton, BorderLayout.WEST);

            // Add GUI elements to layout.
            Container pane = getContentPane();
            pane.add(rButtonPanel, BorderLayout.NORTH);
            pane.add(board, BorderLayout.CENTER);
            pane.add(buttonPanel, BorderLayout.SOUTH);
        }
                     
    // Returns the mode of selection.
    public SelectionMode getSelectionMode()
        {
            if(autoRButton.isSelected())
                return SelectionMode.AUTO;
            else return SelectionMode.MANUAL;
        }
    // Creates a genome using the points extracted from GenomeDrawBoard.
    public Genome getTargetGenome()
        {
            return new Genome(board.getPoints());
        }
}
