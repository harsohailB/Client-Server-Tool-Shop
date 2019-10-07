package Client.ClientController;

import Client.ClientView.LoginView;
import utils.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class is responsible for controlling the login view
 * as well as creating the main window after login
 *
 * @author  Harsohail Brar
 * @version 4.10.0
 * @since April 5, 2019
 */
public class LoginController extends GUIController{

    //MEMBER VARIABLES

    private LoginView loginView;
    private boolean verified;
    private boolean employee;

    /**
     * Creates a LoginController object and adds the object listener for the
     * log in button
     * @param m main GUI controller object
     * @param l login view object
     * @param o output socket stream
     * @param i input socket stream
     */
    public LoginController(LoginView l, ClientController cc){
        super(cc);
        loginView = l;
        verified = false;
        employee = false;

        loginView.addLoginListener(e -> loginListen());
    }

    /**
     * Action Listener implementation for Login Button
     */
    public void loginListen(){
        try{
            String username = loginView.getUsernameField().getText();
            String password = loginView.getPasswordFeild().getText();

            clientController.getSocketOut().writeObject(new User(username, password));

            String verification = (String)clientController.getSocketIn().readObject();

            if(verification.equals("verified")) {
                loginView.setVisible(false);
                verified = true;
                System.out.println("User Logged In!");
            }else{
                JOptionPane.showMessageDialog(null, "Invalid User!");
            }

            clientController.getSocketOut().flush();
        }catch(Exception f){
            f.printStackTrace();
        }
    }

    //getters and setters
    public LoginView getLoginView() {
        return loginView;
    }

    public boolean isVerified() {
        return verified;
    }

    public boolean isEmployee() {
        return employee;
    }
}