package cgroza;
import java.util.Collections;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
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
// graphical elements and controls.
public class SelectionFrame extends JFrame
{
    // Controls how many offspring are generated in the background. Only the
    // most similar will be displayed in the selection frame.
    public final int AUTO_NUMBER_OF_OFFSPRING = 100;
    // GUI elements.
    private JPanel specimenPanel;
    private JPanel controlPanel;
    private JLabel generationDisplay;
    private JLabel similarityDisplay;
    // User configuration.
    private Config config;
    // Size of specimen grid.
    private static int H_SIZE = 6; // Width of specimen grid.
    private static int V_SIZE = 6; // Height of specimen grid.
    // Stores specimens displayed in the grid.
    private LinkedList<Specimen> specimens;
    // Keeps track of the number of generations.
    private int generationCount;
    // Constructor. Set size of frame and enable visibility.
    public SelectionFrame(String title){
        super(title);
        setSize(500, 500);
        generationCount = 0;
        config = new Config();
        // Add key listener as an anonymous class.
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
        LinkedList<Point> startPs = new LinkedList<Point>();
        // Stock specimen.
        startPs.add(new Point(15,15));
        startPs.add(new Point(15,60));
        startPs.add(new Point(30, 45));
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
        similarityDisplay = new JLabel("-");
        controlPanel.add(configButton);
        controlPanel.add(nextButton);
        controlPanel.add(generationDisplay);
        controlPanel.add(new JSeparator(SwingConstants.VERTICAL));
        controlPanel.add(similarityDisplay);
        add(controlPanel, BorderLayout.NORTH);
        add(specimenPanel);
        setVisible(true);
    }

    // Finds the specimen whose genome resembles the target genome the most. 
    public Specimen autoMostSimilar()
        {
            Genome targetGenome = config.getTargetGenome();
            Specimen bestFit = specimens.peek();
            double distance = targetGenome.getDifference(bestFit.getGenome());
            for(Specimen s : specimens)
            {
                double d = targetGenome.getDifference(s.getGenome());
                if(d < distance) // Check if more similar than previous.
                {
                    bestFit = s;
                    distance = d;
                }
            }
            similarityDisplay.setText(Double.toString(distance));
            return bestFit;
        }

    // Finds selected specimens depending of the current selection mode. 
    public LinkedList<Specimen> getSelectedSpecimens()
        {
            LinkedList<Specimen> selected = new LinkedList<Specimen>();
            SelectionMode mode = config.getSelectionMode();
            if(mode == SelectionMode.MANUAL)
            {
                similarityDisplay.setText("NA");
                // Get selected specimens.
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
                // Select the most similar specimen.
                Specimen mostSimilar = autoMostSimilar();
                mostSimilar.select();
                selected.add(mostSimilar);
            }
            return selected;
        }

    // Provides a list of mutations for the selected speciems. For manual mode,
    // the number of new genomes is H_SIZE * V_SIZE. For automatic mode, the
    // same number is chosen from a sorted list of 1000 mutations.
    private LinkedList<Genome> generateMutations()
        {
            LinkedList<Specimen> selected = getSelectedSpecimens();
            // Find number of offspring per selected specimen.
            int nSelected = selected.size();
            int offspringPerSelectedSpecimen = 0;
            int freeSpecimens = H_SIZE * V_SIZE - nSelected;
            LinkedList<Genome> mutations = new LinkedList<Genome>();

            switch (config.getSelectionMode())
            {
            case AUTO:
                // Only one specimen can be selected.
                mutations.addAll(selected.element().produceOffspring(AUTO_NUMBER_OF_OFFSPRING));
                // Sort in decreasing order of similarity with the target genome.
                Collections.sort(mutations, new GenomeComparator(config.getTargetGenome()));
                break;
            case MANUAL:
                // Yields whole rounded down number.
                offspringPerSelectedSpecimen = freeSpecimens / nSelected;
                // Generate mutations for every selected specime.
                for(Specimen s : selected)
                    mutations.addAll(s.produceOffspring(offspringPerSelectedSpecimen));
                break;
            }
            return mutations;
        }

    // Creates a new array of specimens by mutating the selected ones
    // repeatedly.
    public void nextGeneration()
        {
            LinkedList<Genome> mutations = generateMutations();
            LinkedList<Specimen> selected = getSelectedSpecimens();
            if(mutations.isEmpty()) return;
            // Replace obsolete genomes.
            for(Specimen s : specimens)
            {
                if(!selected.contains(s))
                    s.setGenome(mutations.removeFirst());
            }
            // Increment the generation number.
            generationCount ++;
            getContentPane().repaint();
            generationDisplay.setText(Integer.toString(generationCount));
        }

    public static void main(String[] args)
        {
            SelectionFrame mainFrame = new SelectionFrame("Biomorph");
            mainFrame.setVisible(true);
        }
}
