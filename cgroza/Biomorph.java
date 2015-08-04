package cgroza;
import java.awt.Graphics;
import java.applet.Applet;
import javax.swing.SwingUtilities;
import cgroza.SelectionFrame;

// Implements the java applet. Launches the SelectionFrame in a web browser.
public class Biomorph extends Applet
{
    public void init()
        {
            try
            {
            // Start new thread that will initialize the application.
            SwingUtilities.invokeAndWait(new Runnable ()
                {
                    public void run()
                        {
                            startBiomorph();
                        }
                });
            }
            catch(Exception e)
            {
            }
        }

    // Starts program execution from Applet.
    public void startBiomorph()
        {
            SelectionFrame mainFrame = new SelectionFrame("Simulation");
            mainFrame.setVisible(true);
        }
}
