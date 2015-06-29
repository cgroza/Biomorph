package cgroza;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.util.LinkedList;
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import cgroza.Specimen;
import cgroza.Genome;
import cgroza.Config;

// SelectionFrame implements the main frame. It's sole purpose is to display
// graphical elements.
public class SelectionFrame extends JFrame
{
    private JPanel specimenPanel;
    private JPanel controlPanel;
    private JLabel generationDisplay;
    private Config config;
    private static int H_SIZE = 6; // width of specimen grid
    private static int V_SIZE = 6; // height of specimen grid 
    private LinkedList<Specimen> specimens;
    // keeps track of the number of generations
    private int generationCount;
    // Constructor. Set size of frame and enable visibility.
    public SelectionFrame(String title){
        super(title);
        setSize(500, 500);
        generationCount = 0;
        config = new Config();
        // add key listener as an anonymous class
        addKeyListener(new KeyListener ()
            {
                public void keyReleased(KeyEvent e)
                    {
                        switch(e.getKeyCode())
                        {
                        case KeyEvent.VK_ENTER:
                            nextGeneration();
                            break;
                        case KeyEvent.VK_SPACE:
                            config.setVisible(true);
                            break;
                        }
                    }
                public void keyPressed(KeyEvent e) {}
                public void keyTyped(KeyEvent e) {}
            });

        // Panel with grid layout to organize the specimens.
        specimenPanel = new JPanel(new GridLayout(H_SIZE, V_SIZE));
        // Create the specimens.
        LinkedList<Point> startPs = new LinkedList();
        // Stock specimen.
        startPs.add(new Point(15,15));
        startPs.add(new Point(15,60));
        startPs.add(new Point(30, 45));
        startPs.add(new Point(30, 70));
        Genome startGenome = new Genome(startPs);
        specimens = new LinkedList<Specimen>();
        // Populate the specimen grid.
        for(int i = 0; i < H_SIZE * V_SIZE; i++)
        {
            Specimen s = new Specimen(new Genome(startGenome));
            specimens.add(s);
            specimenPanel.add(s);
        }
        controlPanel = new JPanel(new FlowLayout());
        JButton configButton = new JButton("Settings");
        configButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                    {
                        config.setVisible(true);
                    }
            });
        JButton nextButton = new JButton("Next");
        nextButton.addActionListener(new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                    {
                        nextGeneration();
                    }
            });
        generationDisplay = new JLabel(Integer.toString(generationCount));
        controlPanel.add(configButton);
        controlPanel.add(nextButton);
        controlPanel.add(generationDisplay);
        add(controlPanel, BorderLayout.NORTH);
        add(specimenPanel);
        setVisible(true);
    }

    // Finds the specimen whose genome resembles the target genome the most. 
    public Specimen autoMostSimilar()
        {
            Genome targetGenome = config.getTargetGenome();
            Specimen bestFit = specimens.peek();
            double distance = targetGenome.getSimilarity(bestFit.getGenome());
            for(Specimen s : specimens)
            {
                double d = targetGenome.getSimilarity(s.getGenome());
                if(d < distance) // check if more similar than previous
                {
                    bestFit = s;
                    distance = d;
                }
            }
            System.out.println(distance);
            return bestFit;
        }
    // Finds selected specimens depending of the current selection mode. 
    public LinkedList<Specimen> getSelectedSpecimens()
        {
            LinkedList<Specimen> selected = new LinkedList<Specimen>();
            SelectionMode mode = config.getSelectionMode();
            if(mode == SelectionMode.MANUAL)
            { 
                // get selected specimens
                for(Specimen s : specimens)
                {
                    if(s.isSelected())
                        selected.add(s);
                }
            }
            else if (mode == SelectionMode.AUTO)
            {
                for(Specimen s: specimens)
                    s.unselect();
                // select the most similar specimen
                Specimen mostSimilar = autoMostSimilar();
                mostSimilar.select();
                selected.add(mostSimilar);
            }
            return selected;
        }
    // Creates a new array of specimens by mutating the selected ones
    // repeatedly.
    public void nextGeneration()
        {
            LinkedList<Specimen> selected = getSelectedSpecimens();
            // Find number of offspring per selected specimen.
            int nSelected = selected.size();
            if(nSelected == 0) return; // Nothing to do if no selection.

            int freeSpecimens = H_SIZE * V_SIZE - nSelected;
            // Yields whole rounded down number.
            int offspringPerSelectedSpecimen = freeSpecimens / nSelected;
            // Generate mutations.
            LinkedList<Genome> mutations = new LinkedList();
            for(Specimen s : selected)
            {
                mutations.addAll(s.produceOffspring(offspringPerSelectedSpecimen));
            }
            // Replace obsolete genomes.
            for(Specimen s : specimens)
            {
                if(!selected.contains(s))
                {
                    s.setGenome(mutations.removeFirst());
                }
            }
            getContentPane().repaint();
            // Increment the generation number.
            generationCount ++;
            generationDisplay.setText(Integer.toString(generationCount));
        }
}
