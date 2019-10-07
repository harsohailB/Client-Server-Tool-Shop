package Client.ClientView;

import utils.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The mainView class holds the entire GUI after you log in
 * The main purpose of this class is to create the GUI as well as the
 * different buttons
 * @author  Gary Wu
 * @version 4.10.0
 * @since April 5, 2019
 */
public class MainView extends JFrame {

    //MEMBER VARIABLES
    private JPanel titlePanel, centrePanel, buttonPanel;

    private JButton browseButton, searchByIDButton, searchByNameButton, checkQuantityButton,
                    saleButton, addButton, removeButton, refreshButton,
                    showToolListButton, showOrderListButton;

    private DefaultTableModel itemTableModel, orderTableModel;
    private JScrollPane itemScrollPane, orderScrollPane;
    private JTable itemTable, orderTable;

    private int width;
    private int height;

    /**
     * This is the constructor for the MainView Class
     * which starts by making the GUI by adding JPanel and then adding values
     * to different aspects of the GUI such as buttons
     * @param width width of GUI
     * @param height height of GUI
     */
    public MainView(int width, int height){
        titlePanel = new JPanel();
        centrePanel = new JPanel();
        buttonPanel = new JPanel();

        browseButton = new JButton("Browse");
        searchByIDButton = new JButton("Search by ID");
        searchByNameButton = new JButton("Search by Name");
        checkQuantityButton = new JButton("Check Item Quantity");
        saleButton = new JButton("Sale");
        addButton = new JButton("Add Item");
        removeButton = new JButton("Remove Item");
        refreshButton = new JButton("Refresh");
        showToolListButton = new JButton("Show Tool List");
        showOrderListButton = new JButton("Show Order List");

        setTitle("Main Window");
        setSize(width, height);
        setLayout(new BorderLayout());
        add("North", titlePanel);
        add("Center", centrePanel);
        add("South", buttonPanel);

        titlePanel.add(showToolListButton);
        titlePanel.add(new Label("A Tool Shop Application for Employee"));
        titlePanel.add(showOrderListButton);

        buttonPanel.add(browseButton);
        buttonPanel.add(searchByIDButton);
        buttonPanel.add(searchByNameButton);
        buttonPanel.add(checkQuantityButton);
        buttonPanel.add(saleButton);
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(refreshButton);

        pack();
        setSize(width, height);
        setLocationRelativeTo(null);

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        this.width = width;
        this.height = height;
    }

    /**
     * Creates an item table of the objects and displays it on the GUI
     */
    public void createItemTable(){
        if(centrePanel != null)
            remove(centrePanel);

        centrePanel = new JPanel();
        add("Center", centrePanel);

        itemTable = new JTable(itemTableModel);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemScrollPane = new JScrollPane(itemTable);

        centrePanel.add(itemScrollPane, BorderLayout.LINE_END);
        revalidate();
    }

    /**
     * Creates an order table on the centre panel
     */
    public void createOrderTable(){
        if(centrePanel != null)
            remove(centrePanel);

        centrePanel = new JPanel();
        add("Center", centrePanel);

        orderTable = new JTable(orderTableModel);
        orderTable.getTableHeader().setReorderingAllowed(false);
        orderScrollPane = new JScrollPane(orderTable);

        centrePanel.add(orderScrollPane, BorderLayout.LINE_END);
        revalidate();
    }


    /**
     * Updates table
     */
    public void updateItemTable(){
        if(centrePanel != null)
            remove(centrePanel);

        centrePanel = new JPanel();
        add("Center", centrePanel);

        itemTable = new JTable(itemTableModel);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemScrollPane = new JScrollPane(itemTable);

        centrePanel.add(itemScrollPane, BorderLayout.LINE_END);
        revalidate();
    }

    /**
     * Adds an action listener to the browse button
     * @param listenForBrowseButton
     */
    public void addBrowseListener(ActionListener listenForBrowseButton){
        browseButton.addActionListener(listenForBrowseButton);
    }

    /**
     * Adds an action listener to the searchByID button
     * @param listenForSearchByIDButton
     */
    public void addSearchByIDListener(ActionListener listenForSearchByIDButton){
        searchByIDButton.addActionListener(listenForSearchByIDButton);
    }

    /**
     * Adds an action listener to the searchByName button
     * @param listenForSearchByNameButton
     */
    public void addSearchByNameListener(ActionListener listenForSearchByNameButton){
        searchByNameButton.addActionListener(listenForSearchByNameButton);
    }

    /**
     * Adds an action listener to the sale button
     * @param listenForSaleButton
     */
    public void addSaleListener(ActionListener listenForSaleButton){
        saleButton.addActionListener(listenForSaleButton);
    }

    /**
     * Adds an action listener to the add button
     * @param listenForAddButton
     */
    public void addAddListener(ActionListener listenForAddButton){
        addButton.addActionListener(listenForAddButton);
    }

    /**
     * Adds an action listener to the remove button
     * @param listenerForRemoveButton
     */
    public void addRemoveListener(ActionListener listenerForRemoveButton){
        removeButton.addActionListener(listenerForRemoveButton);
    }

    /**
     * Adds an action listener to the refresh button
     * @param listenerForRefreshButton
     */
    public void addRefreshListener(ActionListener listenerForRefreshButton){
        refreshButton.addActionListener(listenerForRefreshButton);
    }


    /**
     * Adds an action listener to the show item list button
     * @param listenerForShowItemListButton
     */
    public void addShowItemListListener(ActionListener listenerForShowItemListButton){
        showToolListButton.addActionListener(listenerForShowItemListButton);
    }

    /**
     * Adds an action listener to the show order list button
     * @param listenerForShowOrderListButton
     */
    public void addShowOrderListListener(ActionListener listenerForShowOrderListButton){
        showOrderListButton.addActionListener(listenerForShowOrderListButton);
    }

    /**
     * Adds an action listener to the check quanitity button
     * @param listenerForCheckQuantityButton
     */
    public void addCheckQuantityListener(ActionListener listenerForCheckQuantityButton){
        checkQuantityButton.addActionListener(listenerForCheckQuantityButton);
    }



    //getters and setters

    public JButton getBrowseButton() {
        return browseButton;
    }

    public JButton getSearchByIDButton() {
        return searchByIDButton;
    }

    public JButton getSearchByNameButton() {
        return searchByNameButton;
    }

    public JButton getSaleButton() {
        return saleButton;
    }

    public JButton getAddButton() {
        return addButton;
    }

    public JButton getRemoveButton() {
        return removeButton;
    }

    public JButton getRefreshButton() {
        return refreshButton;
    }

    public JButton getShowToolListButton() {
        return showToolListButton;
    }

    public JButton getShowOrderListButton() {
        return showOrderListButton;
    }

    public JPanel getCentrePanel() {
        return centrePanel;
    }

    public JButton getCheckQuantityButton() {
        return checkQuantityButton;
    }

    public JTable getItemTable() {
        return itemTable;
    }

    public void setItemTable(JTable table) {
        this.itemTable = table;
    }

    public DefaultTableModel getItemTableModel() {
        return itemTableModel;
    }

    public void setItemTableModel(DefaultTableModel tableModel) {
        this.itemTableModel = tableModel;
    }

    public void setOrderTableModel(DefaultTableModel orderTableModel) {
        this.orderTableModel = orderTableModel;
    }
}