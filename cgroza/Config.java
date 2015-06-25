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
import cgroza.SelectionFrame;

// Selection mode denotes whether specimens are selected by the user of by the
// built-in turning function algorithm.
enum SelectionMode {MANUAL, AUTO}

class GenomeDrawBoard extends JPanel implements MouseListener
{
    private LinkedList<Point> points;
    public GenomeDrawBoard()
        {
            super();
            points = new LinkedList<Point>();
            addMouseListener(this);
            setBorder(BorderFactory.createLineBorder(Color.BLACK));
        }
    public LinkedList<Point> getPoints()
        {
            return points;
        }
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
                polygon.addPoint(p.x, p.y);
            }
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            g.drawPolygon(polygon);
        }
    public void mouseClicked(MouseEvent e)
        {
            points.add(new Point(e.getX(), e.getY()));
            TopologicalSort.sort(points);
            repaint();
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
            // create GUI components
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
            // setup layout
            Container pane = getContentPane();
            pane.add(rButtonPanel, BorderLayout.NORTH);
            pane.add(board, BorderLayout.CENTER);
            pane.add(buttonPanel, BorderLayout.SOUTH);
        }
                     
    public SelectionMode getSelectionMode()
        {
            if(autoRButton.isSelected())
                return SelectionMode.AUTO;
            else return SelectionMode.MANUAL;
        }
    public Genome getTargetGenome()
        {
            return new Genome(board.getPoints());
        }
}
