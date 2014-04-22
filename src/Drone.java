
import javax.swing.JLabel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author aditya
 */
public class Drone 
{

    Point source;
    Point finalDestination;
    int fuel;
    JLabel droneLabel;
    JLabel weatherLabel;
    boolean isFuelLow;
    
    public Drone(JLabel droneLabelFinal,JLabel weatherLabel)
    {
        this.fuel=100;
        this.droneLabel=droneLabelFinal;
        this.isFuelLow=false;
        this.weatherLabel=weatherLabel;
    }
    
}
