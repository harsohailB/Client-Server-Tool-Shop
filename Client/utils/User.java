package utils;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * This class is responsible for holding a user's
 * data  such as username, password etc.
 */
public class User implements Serializable {

    //MEMBER VARIABLES
    private static final long serialVersionUID = 3L;
    private String username;
    private String password;
    private ArrayList<Item> itemsOrdered;

    /**
     * Constructs a user object
     * @param u username
     * @param p password
     */
    public User(String u, String p){
        username = u;
        password = p;
        itemsOrdered = new ArrayList<>();
    }

    public boolean compareUser(User u){
        if(u.username.equals(this.username) && u.password.equals(this.password))
            return true;

        return false;
    }

    //GETTERS AND SETTERS
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ArrayList<Item> getItemsOrdered() {
        return itemsOrdered;
    }

    public void setItemsOrdered(ArrayList<Item> itemsOrdered) {
        this.itemsOrdered = itemsOrdered;
    }
}
