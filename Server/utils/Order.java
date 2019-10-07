package utils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * This is the Order class which keep track of each order placed
 * for items under quantity of 40
 */
public class Order implements Serializable {

    private static final long serialVersionUID = 4L;
    /**
     * ID of an order
     */
    int orderId;

    /**
     * Date order was placed on
     */
    String orderDate;

    /**
     * Items ordered in the order
     */
    int itemOrdered;

    /**
     * Reason for order
     */
    String activity;

    /**
     * Constructs an object Order with specified values of  id, date,
     * and a list of items
     * @param id ID of order
     * @param date Date order is being placed on
     * @param items list of items being ordered
     */
    public Order(int id, int item, String date, String a){
        orderId = id;
        orderDate = date;
        itemOrdered = item;
        activity = a;
    }

    /**
     * Constructs an object Order with a result set
     * @param rs result set
     */
    public Order(ResultSet rs){
        try {
            orderId = rs.getInt(1);
            itemOrdered = rs.getInt(3);
            orderDate = rs.getString(2);
            activity = rs.getString(4);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    //getters and setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getItemOrdered() {
        return itemOrdered;
    }

    public void setItemOrdered(int itemOrdered) {
        this.itemOrdered = itemOrdered;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
