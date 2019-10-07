package Server.ServerModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

import utils.*;

/**
 * This class is responsible for all data regarding the database.
 * It contains methods that send queries to the database to perform
 * add, remove, search etc. operations
 * @author Ryan Holt
 * @version 4.10.0
 * @since April 12, 2019
 */
public class DatabaseModel implements DatabaseAccessQueries{

    private Connection myConnection;
    private int userId = 2;
    private DefaultTableModel tableModel;

    /**
     * DatabaseModel constructor
     */
    public DatabaseModel(Connection c) {
        myConnection = c;
    }

    /**
     * Checks to see if the user entered is correct
     *
     * @param user the user object to be verified
     * @return returns true if the User exists otherwise false
     */
    public boolean verifyUser(User user) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_USER)) {
            pStmt.setString(1, user.getUsername());
            pStmt.setString(2, user.getPassword());
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    System.out.println("User is logged in");
                    return true;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * adds a user to a database
     * @param user user being added
     */
    public void addUser(User user) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_ADD_USER)) {
            pStmt.setInt(1, userId++);
            pStmt.setString(2, user.getUsername());
            pStmt.setString(3, user.getPassword());
            pStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Unable to add user. You must enter a unique username.");
            e.printStackTrace();
        }
    }

    /**
     * gets all items from the database
     * @return return an array of all items fetched
     */
    synchronized public ArrayList<Item> getItemsFromDB() {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_ALL_ITEMS)) {
            ArrayList<Item> items = new ArrayList<>();
            try (ResultSet rs = pStmt.executeQuery()) {
                while (rs.next()) {
                    items.add(new Item(rs, searchSupplierByID(rs.getInt(5))));
                }
            }
            return items;
        } catch (SQLException e) {
            System.out.println("Getting items from DB error");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * gets all orders from the orders table in the database
     * @return returns an array list of orders
     */
    synchronized public ArrayList<Order> getOrdersFromDB(){
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_ALL_ORDERS)) {
            ArrayList<Order> orders = new ArrayList<>();
            try (ResultSet rs = pStmt.executeQuery()) {
                while (rs.next()) {
                    orders.add(new Order(rs));
                }
            }
            return orders;
        } catch (SQLException e) {
            System.out.println("Getting items from DB error");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * searches for the supplier in the database by ID
     * @param supplierIdNumber id of supplier being requested
     * @return supplier found
     */
    synchronized public Supplier searchSupplierByID(int supplierIdNumber) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_SUPPLIER_BY_ID)) {
            pStmt.setInt(1, supplierIdNumber);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next()) {
                    return new Supplier(rs);
                }
            }
        } catch (SQLException e) {
            System.out.println("Supplier search from DB error");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * searches for an item in the database by name
     * @param name name of item being searched
     * @return item found
     */
    synchronized public Item searchItemByName(String name) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_ITEM_BY_NAME)) {
            pStmt.setString(1, name);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next())
                    return new Item(rs, searchSupplierByID(rs.getInt(5)));
            }
        } catch (SQLException e) {
            System.out.println("Item search by name from DB error");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * searches for an item in the database by ID
     * @param id id of item being searched
     * @return item found
     */
    synchronized public Item searchItemByID(int id) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_ITEM_BY_ID)) {
            pStmt.setInt(1, id);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next())
                    return new Item(rs, searchSupplierByID(rs.getInt(5)));
            }
        } catch (SQLException e) {
            System.out.println("Item search by ID from DB error");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * decreases quantity of item requested in the databse
     * @param id id of item
     * @param newQuantity new quantity of item
     * @return returns true, if item was updated, false otherwise
     */
    synchronized public boolean decreaseItemQuantity(int id, int newQuantity, boolean orderPlaced) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_DECREASE_ITEM_QUANTITY)) {
            pStmt.setInt(1, newQuantity);
            pStmt.setInt(2, id);
            int status = pStmt.executeUpdate();
            if (status == 0) {
                System.out.println("Item already deleted!");
                return false;
            } else {
                System.out.println("Item Quantity in DB Decreased!");
            }
        } catch (SQLException e) {
            System.out.println("Item search by ID from DB error");
            e.printStackTrace();
        }
        if(orderPlaced == true)
            createNewOrder(generateOrderID(), id, getCurrentDate(), "Quantity below 40, order placed");

        createNewOrder(generateOrderID(), id, getCurrentDate(), "Quantity decreased");
        return true;
    }

    /**
     * checks if an item exists
     * @param id id of item being checked
     * @return true if item exists, false otherwise
     */
    synchronized public boolean itemExists(int id) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_ITEM_BY_ID)) {
            pStmt.setInt(1, id);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next())
                    return true;
            }
        } catch (SQLException e) {
            System.out.println("Item search by ID from DB error");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * checks if supplier exists
     * @param supplierIdNumber id of supplier being checked
     * @return true if supplier exists, false otherwise
     */
    synchronized public boolean supplierExists(int supplierIdNumber) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_GET_SUPPLIER_BY_ID)) {
            pStmt.setInt(1, supplierIdNumber);
            try (ResultSet rs = pStmt.executeQuery()) {
                if (rs.next())
                    return true;
            }
        } catch (SQLException e) {
            System.out.println("Supplier search by ID from DB error");
            e.printStackTrace();
        }
        return false;
    }

    /**
     * adds an item to the database
     * @param newItem new item being added
     */
    synchronized public void addItemToDB(Item newItem) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_ADD_ITEM)) {
            pStmt.setInt(1, newItem.getToolId());
            pStmt.setString(2, newItem.getToolName());
            pStmt.setInt(3, newItem.getToolQuantity());
            pStmt.setDouble(4, newItem.getToolPrice());
            pStmt.setInt(5, newItem.getToolSupplier().getId());
            pStmt.executeUpdate();
            System.out.println("Item added to DB");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        createNewOrder(generateOrderID(), newItem.getToolId(), getCurrentDate(), "Added Item");
    }

    /**
     * removes an item from the database
     * @param id id of item being removed
     * @return true if item is removed, false otherwise
     */
    synchronized public boolean removeItemFromDB(int id) {
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_REMOVE_ITEM)) {
            pStmt.setInt(1, id);
            int status = pStmt.executeUpdate();
            if (status == 0) {
                System.out.println("Item already deleted!");
                return false;
            } else {
                System.out.println("Item deleted from DB!");
            }
        } catch (SQLException e) {
            System.out.println("Item remove by ID from DB error");
            e.printStackTrace();
        }
        createNewOrder(generateOrderID(), id, getCurrentDate(), "Removed Item");
        return true;
    }

    /**
     * creates a new order in the database
     * @param orderID random order id
     * @param itemID item order was placed for
     * @param date date the order was placed on
     * @param activity why the order was placed
     */
    synchronized public void createNewOrder(int orderID, int itemID, String date, String activity){
        try (PreparedStatement pStmt = myConnection.prepareStatement(SQL_CREATE_NEW_ORDER)) {
            pStmt.setInt(1, orderID);
            pStmt.setInt(2, itemID);
            pStmt.setString(3, date);
            pStmt.setString(4, activity);
            pStmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Order Creation in DB error");
            e.printStackTrace();
        }
        System.out.println("Order created");
    }

    /**
     * generate a random 5 digit order id
     * @return random order id
     */
    synchronized public int generateOrderID(){
        return (int)Math.round(Math.random() * 89999) + 10000;
    }

    synchronized public String getCurrentDate(){
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-DD");
        return sdf.format(cal.getTime());
    }

    // getters and setters

    /**
     * @return the tableModel
     */
    public DefaultTableModel getTableModel() {
        return tableModel;
    }

    /**
     * @param tableModel the tableModel to set
     */
    public void setTableModel(DefaultTableModel tableModel) {
        this.tableModel = tableModel;
    }
}