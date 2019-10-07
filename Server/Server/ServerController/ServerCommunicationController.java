package Server.ServerController;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.ArrayList;

import utils.*;

/**
 * This class is responsible for communicating with the client.
 * A new instance of this class is appointed to each client in an
 * independent thread.
 * @author Harsohail Brar
 * @since April 12, 2019
 */
public class ServerCommunicationController implements Runnable {

    private Socket aSocket;
    private ObjectInputStream socketIn;
    private ObjectOutputStream socketOut;
    private ServerController serverController;

    public ServerCommunicationController(Socket s, ServerController serverController) {
        try {
            aSocket = s;
            setServerController(serverController);

            socketOut = new ObjectOutputStream(aSocket.getOutputStream());

            printIPInfo();
        } catch (IOException e) {
            System.out.println("ServerCommController: Create ServerCommController Error");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        createInputStream();
        exportItemsToClient();
        verifyLogin();
        communicate();
    }

    public void communicate() {
        while (true) {
            try {
                String input = (String) socketIn.readObject();

                switch (input) {
                    case "searchByID":
                        searchItemByIDFromDB();
                        exportItemsToClient();
                        break;

                    case "searchByName":
                        searchItemByNameFromDB();
                        exportItemsToClient();
                        break;

                    case "sale":
                        saleItemFromDB();
                        exportItemsToClient();
                        break;

                    case "add":
                        addItemToDB();
                        exportItemsToClient();
                        break;

                    case "remove":
                        removeItemFromDB();
                        exportItemsToClient();
                        break;

                    case "refresh":
                        exportItemsToClient();
                        break;

                    case "orders":
                        exportOrdersToClient();
                        break;

                    case "checkQuantity":
                        checkItemQuantity();
                        exportItemsToClient();
                        break;
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * checks quantity for the item requested
     */
    public void checkItemQuantity(){
        try{
            int id = Integer.parseInt((String)socketIn.readObject());

            Item searchedItem = serverController.getDatabaseController().getDatabaseModel().searchItemByID(id);

            socketOut.writeObject(searchedItem);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * sends all items to client
     */
    public void exportItemsToClient() {
        ArrayList<Item> items = serverController.getDatabaseController().getDatabaseModel().getItemsFromDB();

        try {
            socketOut.writeObject(String.valueOf(items.size()));

            for (Item i : items) {
                socketOut.writeObject(i);
            }
        } catch (IOException e) {
            System.out.println("Exporting items from server error");
            e.printStackTrace();
        }
    }

    /**
     * send all orders to client
     */
    public void exportOrdersToClient(){
        ArrayList<Order> orders = serverController.getDatabaseController().getDatabaseModel().getOrdersFromDB();

        try {
            socketOut.writeObject(String.valueOf(orders.size()));

            for (Order o: orders) {
                socketOut.writeObject(o);
            }
        } catch (IOException e) {
            System.out.println("Exporting items from server error");
            e.printStackTrace();
        }
    }

    /**
     * searches item requested from database by item id
     */
    public void searchItemByIDFromDB() {
        try {
            int id = Integer.parseInt((String) socketIn.readObject());
            Item searchedItem = serverController.getDatabaseController().getDatabaseModel().searchItemByID(id);

            socketOut.writeObject(searchedItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * searches item requested from database by item name
     */
    public void searchItemByNameFromDB() {
        try {
            String name = (String) socketIn.readObject();
            Item searchedItem = serverController.getDatabaseController().getDatabaseModel().searchItemByName(name);

            socketOut.writeObject(searchedItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * sells item from database (decreases quantity) as requested
     */
    public void saleItemFromDB() {
        try {
            String status = (String) socketIn.readObject();

            if (status.equals("reset")) {
                return;
            }

            int itemID = Integer.parseInt((String) socketIn.readObject());
            //returns item to client for quantity check
            Item searchedItem = serverController.getDatabaseController().getDatabaseModel().searchItemByID(itemID);
            socketOut.writeObject(searchedItem);

            int newQuantity = Integer.parseInt((String) socketIn.readObject());

            boolean updated  = false;
            if(newQuantity < 40){
                updated = serverController.getDatabaseController().getDatabaseModel().decreaseItemQuantity(itemID, 50, true);
            }else{
                updated = serverController.getDatabaseController().getDatabaseModel().decreaseItemQuantity(itemID, newQuantity, false);
            }

            if(updated){
                socketOut.writeObject("updated");
            }else{
                socketOut.writeObject("not updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * adds items to database
     */
    public void addItemToDB() {
        try {
            //reads new item id
            int itemID = Integer.parseInt((String) socketIn.readObject());

            //checks if item already exists
            while (serverController.getDatabaseController().getDatabaseModel().itemExists(itemID)) {
                socketOut.writeObject("true");
                itemID = Integer.parseInt((String) socketIn.readObject());
            }

            socketOut.writeObject("false"); // id OK, doesn't exists

            //reads supplier ID of new item
            int suppID = Integer.parseInt((String) socketIn.readObject());

            //checks if supplier exists
            while (!serverController.getDatabaseController().getDatabaseModel().supplierExists(suppID)) {
                socketOut.writeObject("not verified");
                suppID = Integer.parseInt((String) socketIn.readObject());
            }

            socketOut.writeObject("verified");

            //sends supplier to client
            Supplier searchedSupp = serverController.getDatabaseController().getDatabaseModel().searchSupplierByID(suppID);
            socketOut.writeObject(searchedSupp);

            //recieves new item
            Item newItem = (Item) socketIn.readObject();

            //add item to database
            serverController.getDatabaseController().getDatabaseModel().addItemToDB(newItem);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void removeItemFromDB() {
        try {
            String status = (String) socketIn.readObject();

            if (status.equals("reset"))
                return;

            int itemID = Integer.parseInt((String) socketIn.readObject());

            //remove item from database
            boolean updated = serverController.getDatabaseController().getDatabaseModel().removeItemFromDB(itemID);
            if(updated){
                socketOut.writeObject("updated");
            }else{
                socketOut.writeObject("not updated");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an input socket stream from server
     */
    public void createInputStream() {
        try {
            socketIn = new ObjectInputStream(aSocket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error creating server output stream");
            e.printStackTrace();
        }
    }

    /**
     * Verifies the log in by running an infinite loop that only stops if the user
     * has entered a valid username and password
     */
    public void verifyLogin() {
        try {
            boolean verified = false;

            while (!verified) {
                User readUser = (User) socketIn.readObject();

                if (serverController.getDatabaseController().getDatabaseModel().verifyUser(readUser)) {
                    socketOut.writeObject("verified");
                    System.out.println("Login Success!");
                    verified = true;
                    return;
                } else {
                    socketOut.writeObject("Invalid Username and Password");
                }

                socketOut.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void printIPInfo() {
        InetAddress ip;
        try {
            ip = InetAddress.getLocalHost();
            System.out.println("You current IP address: " + ip);
        } catch (UnknownHostException e) {
            System.out.println("IP Print error");
            e.printStackTrace();
        }
    }

    //GETTERS AND SETTERS
    public void setServerController(ServerController sc) {
        serverController = sc; // 2-way association
    }
}
