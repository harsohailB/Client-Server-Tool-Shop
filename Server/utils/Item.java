package utils;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This is the Item class which holds information of each tool
 */
public class Item implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * ID of the tool
     */
    private int toolId;

    /**
     * Name of the tool
     */
    private String toolName;

    /**
     *  Quantity of the tool
     */
    private int toolQuantity;

    /**
     * Price of the tool
     */
    private double toolPrice;

    /**
     * Supplier of the tool
     */
    private Supplier toolSupplier;

    /**
     * Constructs an Item object with specified values of id, name, quantity, price,
     * and supplier
     * @param id ID of tool
     * @param name Name of tool
     * @param quantity Quantity of tool
     * @param price Price of tool
     * @param supplier Supplier of tool
     */
    public Item(int id, String name, int quantity, double price, Supplier supplier){
        toolId = id;
        toolName = name;
        toolQuantity = quantity;
        toolPrice = price;
        toolSupplier = supplier;
    }

    /**
     * Constructs a supplier object with database result set
     * @param rs result set
     */
    public Item(ResultSet rs, Supplier supp) {
        try {
            this.toolId = rs.getInt(1);
            this.toolName = rs.getString(2);
            this.toolQuantity = rs.getInt(3);
            this.toolPrice = rs.getDouble(4);
            this.toolSupplier = supp;
        } catch (SQLException e){
            System.out.println("Supplier creation error (ResultSet)");
            e.printStackTrace();
        }
    }

    /**
     * formats Item object information into a string
     * @return string consisting of Item object information
     */
    public String toString() {
        return String.format("%-5s %-20s %-10s %-7s %-15s", toolId, toolName, toolQuantity, toolPrice, toolSupplier.getName());
    }

    /**
     * checks if the item requested has quantity under 40
     * @return true, if quantity is under 40, false otherwise
     */
    public boolean lowQuantity(){
        if(getToolQuantity() < 40)
            return true;
        return false;
    }
    //getters and setters
    public int getToolId() {
        return toolId;
    }

    public void setToolId(int toolId) {
        this.toolId = toolId;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public int getToolQuantity() {
        return toolQuantity;
    }

    public void setToolQuantity(int toolQuantity) {
        this.toolQuantity = toolQuantity;
    }

    public double getToolPrice() {
        return toolPrice;
    }

    public void setToolPrice(double toolPrice) {
        this.toolPrice = toolPrice;
    }

    public Supplier getToolSupplier() {
        return toolSupplier;
    }

    public void setToolSupplier(Supplier toolSupplier) {
        this.toolSupplier = toolSupplier;
    }
}
