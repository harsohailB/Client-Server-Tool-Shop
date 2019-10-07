package Client.ClientController;

import Client.ClientView.MainView;
import utils.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The MAIN GUIController class essentially holds all code for that
 * action listeners of the various buttons such as browse, it holds
 * objects of the main view and model
 * Overall the purpose of this class is to add the action listeners
 * to the various buttons in the main view
 *
 * @author Harsohail Brar
 * @version 4.10.0
 * @since April 5, 2019
 */
public class MainGUIController extends GUIController {

    //MEMBER VARAIBLES
    private MainView mainView;

    /**
     * Constructor for the MainGUIController class which essentially adds
     * action listeners to the different buttons
     *
     * @param v this is the MainView Object
     */
    public MainGUIController(MainView v, ClientController cc) {
        super(cc);
        mainView = v;

        mainView.addBrowseListener(e -> browseListen());
        mainView.addSearchByIDListener(e -> searchByIDListen());
        mainView.addSearchByNameListener(e -> searchByNameListen());
        mainView.addSaleListener(e -> saleListen());
        mainView.addAddListener(e -> addListen());
        mainView.addRemoveListener(e -> removeListen());
        mainView.addRefreshListener(e -> refreshListen());
        mainView.addShowItemListListener(e -> showItemListListen());
        mainView.addShowOrderListListener(e -> showOrderListListen());
        mainView.addCheckQuantityListener(e -> checkQuantityListen());
    }

    /**
     * When this button is pressed the action performed a
     * list of the many tools will now become visible to the user
     * and it will also add the action listener associated with the list
     */
    public void browseListen(){
        try {
            if (mainView.getItemTable() == null) {
                mainView.createItemTable();
            }
        } catch (Exception f) {
            System.out.println("MainGUIController: BrowseListen error");
            f.printStackTrace();
        }
    }

    /**
     * When the button is pressed the user will be prompted to enter the
     * ID of a tool and it will go through all the tools and try to match
     * the tool ID with one in the database and if it matches
     * then the elements of the tool will appear in a dialog box
     * else tells the user it does not exist
     */
    public void searchByIDListen(){
        int inputID = intInputPrompt("Enter tool ID:");

        try {
            //sending
            clientController.getSocketOut().writeObject("searchByID");

            clientController.getSocketOut().writeObject(String.valueOf(inputID));

            //receiving
            Item readItem = (Item) clientController.getSocketIn().readObject();

            //prompt
            if (readItem != null) {
                JOptionPane.showMessageDialog(null, promptItem(readItem));
            } else {
                JOptionPane.showMessageDialog(null, "Tool not found!");
            }

            //update table
            importItemsFromServer();
            mainView.updateItemTable();
        } catch (Exception f) {
            System.out.println("MainGUIController SearchByID error");
            f.printStackTrace();
        }
    }

    /**
     * When the button is pressed the user will be prompted to enter the
     * name of a tool and it will go through all the tools and try to match
     * the tool name with one in the database and if it matches
     * then the elements of the tool will appear in a dialog box
     * else tells the user it does not exist
     */
    public void searchByNameListen(){
        //inputs
        String input = JOptionPane.showInputDialog("Please enter tool Name:");

        try {
            //sending
            clientController.getSocketOut().writeObject("searchByName");

            clientController.getSocketOut().writeObject(input);

            //receiving
            Item readItem = (Item) clientController.getSocketIn().readObject();

            //prompt
            if (readItem != null) {
                JOptionPane.showMessageDialog(null, promptItem(readItem));
            } else {
                JOptionPane.showMessageDialog(null, "Tool not found!");
            }

            //update table
            importItemsFromServer();
            mainView.updateItemTable();
        } catch (Exception f) {
            System.out.println("MainGUIController SearchByName error");
            f.printStackTrace();
        }
    }

    /**
     * When the button is pressed, this function takes the selected
     * row item and decreases its quantity by the specified amount.
     * Then it sends the item and the new quantity to the server
     * which updates the inventory of the shop
     */
    public void saleListen() {
        int selectedRow = -1;
        try {
            clientController.getSocketOut().writeObject("sale");

            selectedRow = mainView.getItemTable().getSelectedRow();

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(null, "Please select an item!");
                clientController.getSocketOut().writeObject("reset");
                importItemsFromServer();
                return;
            }


            String s = JOptionPane.showInputDialog("Enter number of items sold:");
            if (s == null)
                clientController.getSocketOut().writeObject("reset");

            int sold = Integer.parseInt(s);

            // allows server to proceed in the method called
            clientController.getSocketOut().writeObject("continue");

            String itemID = (String) mainView.getItemTableModel().getValueAt(selectedRow, 0);

            //send itemID to server to get item from DB
            clientController.getSocketOut().writeObject(itemID);

            Item readItem = (Item) clientController.getSocketIn().readObject();
            int currQuantity = readItem.getToolQuantity();

            while (sold > currQuantity) {
                JOptionPane.showMessageDialog(null, "Sale exceeded quantity! Please refresh!");
                sold = Integer.parseInt(JOptionPane.showInputDialog("Enter number of items sold:"));
            }

            int newQuantity = currQuantity - sold;

            clientController.getSocketOut().writeObject(String.valueOf(newQuantity));

            //gets confirmation from server
            String verif = (String) clientController.getSocketIn().readObject();
            if (verif.equals("not updated")) {
                JOptionPane.showMessageDialog(null, "Tool not updated! Please refresh!");
            }

            //update table
            importItemsFromServer();
            mainView.updateItemTable();
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

        /**
         * When the button is pressed, this function takes inputs from the user
         * for Item id, name, quantity, price, and supplier. Then it creates a new item
         * and adds it to the GUI table as well as sends it to the server to add it
         * to the shop inventory
         *
         */
        public void addListen () {
            try {
                clientController.getSocketOut().writeObject("add");


                int id = intInputPrompt("Enter new tool ID: (integer)");

                //server id check
                clientController.getSocketOut().writeObject(String.valueOf(id));
                String idExists = (String) clientController.getSocketIn().readObject();

                while (idExists.equals("true")) {
                    JOptionPane.showMessageDialog(null, "ID already exists, try again!");
                    id = intInputPrompt("Enter new tool ID: (integer)");
                    clientController.getSocketOut().writeObject(String.valueOf(id));
                    idExists = (String) clientController.getSocketIn().readObject();
                }

                String name = JOptionPane.showInputDialog("Enter new tool name:");
                int quantity = intInputPrompt("Enter new tool quantity: (integer)");
                double price = doubleInputPrompt("Enter new tool price: (double)");

                String verif = " ";
                int suppID = 0;

                while (!verif.equals("verified")) {
                    suppID = intInputPrompt("Enter new tool supplier ID: (Integer)");
                    verif = sendSuppID(suppID);
                    if (!verif.equals("verified"))
                        JOptionPane.showMessageDialog(null, "Supplier doesn't exist, try again!");
                }

                //reads new supplier
                Supplier newSupp = (Supplier) clientController.getSocketIn().readObject();
                ;
                Item newItem = new Item(id, name, quantity, price, newSupp);

                //send item to server
                clientController.getSocketOut().writeObject(newItem);

                //update table
                importItemsFromServer();
                mainView.updateItemTable();
            } catch (Exception f) {
                f.printStackTrace();
            }
        }

    /**
     * When the button is pressed, this function removes the row that is selected,
     * then sends the item of that row to the server to remove it from the
     * inventory
     */
    public void removeListen(){
        int selectedRow = -1;

        try {
            clientController.getSocketOut().writeObject("remove");

            selectedRow = mainView.getItemTable().getSelectedRow();

            if (selectedRow < 0) {
                JOptionPane.showMessageDialog(null, "Please select an item!");
                clientController.getSocketOut().writeObject("reset");
                return;
            }

            clientController.getSocketOut().writeObject("continue");


            String itemID = (String) mainView.getItemTableModel().getValueAt(selectedRow, 0);

            //send item ID to server
            clientController.getSocketOut().writeObject(itemID);

            //gets confirmation from server
            String verif = (String) clientController.getSocketIn().readObject();
            if (verif.equals("not updated")) {
                JOptionPane.showMessageDialog(null, "Tool not deleted! Please refresh!");
            }

            //update table
            importItemsFromServer();
            mainView.updateItemTable();
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    /**
     * When the refresh button is pressed, this action listener
     * refreshes the centre panel which includes the table
     * by importing the latest items from the database
     */
    public void refreshListen(){
        try {
            //send
            clientController.getSocketOut().writeObject("refresh");
            //receiving
            importItemsFromServer();
            //update table
            mainView.updateItemTable();
        } catch (Exception f) {
            f.printStackTrace();
        }
    }

    /**
     * checks and prompts quantity of an item
     */
    public void checkQuantityListen(){
        //inputs
        int id = intInputPrompt("Enter new tool ID: (integer)");

        try{
            //send
            clientController.getSocketOut().writeObject("checkQuantity");
            clientController.getSocketOut().writeObject(String.valueOf(id));
            //recieve
            Item readItem = (Item)clientController.getSocketIn().readObject();

            JOptionPane.showMessageDialog(null, "Quantity of item: " + readItem.getToolId() + " is " + readItem.getToolQuantity());

            importItemsFromServer();
            //update table
            mainView.updateItemTable();
        }catch (Exception f){
            f.printStackTrace();
        }
    }

    /**
     * When the Show Item List button is pressed, this action listener
     * creates and displays the item table on the centre panel
     */
    public void showItemListListen(){
        mainView.createItemTable();
    }

    /**
     * When the Show Order List button is pressed, this action lsitener
     * creates and displays the order table on the centre panel
     */
    public void showOrderListListen(){
        try {
            clientController.getSocketOut().writeObject("orders");
            importOrdersFromServer();
            mainView.createOrderTable();
        }catch (IOException f){
            f.printStackTrace();
        }
    }


    /**
     * Gets an integer input from the user with error checking
     *
     * @param n message being displayed for input
     * @return integer entered by user
     */
    public int intInputPrompt(String n) {
        String input = null;
        int num = 0;
        while (input == null || num < 0) {

            try {
                input = JOptionPane.showInputDialog(n);
                num = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Add Item NFE");
                JOptionPane.showMessageDialog(null, "Try again!");
                input = null;
            }

        }

        return num;
    }

    /**
     * Gets a double input from the user with error checking
     *
     * @param n message being displayed for input
     * @return integer entered by user
     */
    public double doubleInputPrompt(String n) {
        String input = null;
        double num = 0;
        while (input == null || num < 0) {

            try {
                input = JOptionPane.showInputDialog(n);
                num = Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("Add Item NFE");
                JOptionPane.showMessageDialog(null, "Try again!");
                input = null;
            }

        }

        return num;
    }

    public String promptItem(Item i) {
        return "ID: " + i.getToolId() +
                "  Name: " + i.getToolName() +
                "  Quantity: " + i.getToolQuantity() +
                "  Price: " + i.getToolPrice() +
                "  Supplier: " + i.getToolSupplier().getName();
    }

    /**
     * sends supplier id from user to the server to check if the supplier
     * entered exists
     *
     * @param suppID supplier id
     * @return verified or not
     */
    public String sendSuppID(int suppID) {
        String verif = null;

        try {
            clientController.getSocketOut().writeObject(String.valueOf(suppID));
            verif = (String) clientController.getSocketIn().readObject();
        } catch (Exception f) {
            System.out.println("Supplier ID writing error from client");
            f.printStackTrace();
        }

        return verif;
    }

    /**
     * imports items from server
     */
    public void importItemsFromServer() {
        try {
            int numOfItems = Integer.parseInt((String) clientController.getSocketIn().readObject());
            String[][] data = new String[numOfItems][5];
            String[] header = {"ID", "Name", "Quantity", "Price", "Supplier"};

            for (int i = 0; i < numOfItems; i++) {
                Item readItem = (Item) clientController.getSocketIn().readObject();

                data[i][0] = String.valueOf(readItem.getToolId());
                data[i][1] = readItem.getToolName();
                data[i][2] = String.valueOf(readItem.getToolQuantity());
                data[i][3] = String.valueOf(readItem.getToolPrice());
                data[i][4] = readItem.getToolSupplier().getId() + " - " + readItem.getToolSupplier().getName();
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, header){
                public boolean isCellEditable(int rowIndex, int mColIndex){
                    return false;
                }
            };
            mainView.setItemTableModel(tableModel);
        } catch (Exception e) {
            System.out.println("Importing item from server error");
            e.printStackTrace();
        }
    }

    /**
     * imports order from server
     */
    public void importOrdersFromServer(){
        try{
            int numOfOrders = Integer.parseInt((String) clientController.getSocketIn().readObject());
            String[][] data = new String[numOfOrders][4];
            String[] header = {"Order ID", "Order Date", "Item ID", "Activity"};

            for (int i = 0; i < numOfOrders; i++) {
                Order readOrder = (Order) clientController.getSocketIn().readObject();

                data[i][0] = String.valueOf(readOrder.getOrderId());
                data[i][1] = readOrder.getOrderDate();
                data[i][2] = String.valueOf(readOrder.getItemOrdered());
                data[i][3] = readOrder.getActivity();
            }

            DefaultTableModel tableModel = new DefaultTableModel(data, header){
                public boolean isCellEditable(int rowIndex, int mColIndex){
                    return false;
                }
            };
            mainView.setOrderTableModel(tableModel);
        }catch(Exception e){
            System.out.println("Importing orders from server error");
            e.printStackTrace();
        }
    }

    //GETTERS AND SETTERS
    public MainView getMainView() {
        return mainView;
    }

}

