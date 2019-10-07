package Client.ClientController;

import javax.swing.*;

/**
 * This class is an abstract class with acts as the super class for all GUI
 * Controllers
 * @author Harsohail Brar
 * @version 4.10.0
 * @since April 5, 2019
 */
public abstract class GUIController extends JFrame {

    /**
     * Client Controller consisting of sockets
     */
    protected ClientController clientController;

    /**
     * Constructor for the class
     * @param c Client Controller
     */
    public GUIController(ClientController c){
        setClientController(c);
    }

    /**
     * Client controller setter which creates a
     * 2-way association between the classes
     * @param c client controller
     */
    public void setClientController(ClientController c){
        clientController = c;   // 2-way association with CC
    }



}