package com.example.grocerycodeu.grocerycodeu;

/**
 * Created by saryal on 7/19/15.
 */
public class walmartItemObject {

    int itemID;
    String itemTitle;
    double itemCost;
    String itemImage;
    int totalOrder;

    /**
     * Constructor for the walmartItemObject
     *
     * @param itemID     unique key
     * @param itemTitle  name of the item
     * @param itemCost   cost of the item
     * @param itemImage  image url
     * @param totalOrder total number of order for a respective item
     */
    public walmartItemObject(int itemID, String itemTitle, double itemCost, String itemImage, int totalOrder) {
        this.itemID = itemID;
        this.itemTitle = itemTitle;
        this.itemCost = itemCost;
        this.itemImage = itemImage;
        this.totalOrder = totalOrder;
    }

    /**
     * Get method for ID
     * @return unique item ID for walmartItemObject
     */
    public int getItemID() { return this.itemID;}

    /**
     * Set method for ID
     */
    public void setItemID(int itemID) { this.itemID = itemID; }

    /**
     * Get method for Title
     * @return item name for walmartItemObject
     */
    public String getitemTitle() { return this.itemTitle; }

    /**
     * Set method for Title
     */
    public void setitemTitle(String itemTitle) { this.itemTitle = itemTitle; }

    /**
     * Get method for Cost
     * @return item cost for walmartItemObject
     */
    public double getItemCost() { return this.itemCost; }

    /**
     * Set method for cost
     */
    public void setItemCost(double itemCost) {
        this.itemCost = itemCost;
    }

    /**
     * Get method for image url
     * @return item image for walmartItemObject
     */
    public String getItemImage() {
        return itemImage;
    }

    /**
     * Set method for image url
     */
    public void setItemImage(String itemImage) {
        this.itemImage = itemImage;
    }

    /**
     * Get method for total number of item ordred
     * @return total number of item ordred
     */
    public int getTotalOrder() {
        return totalOrder;
    }

    /**
     * Set method for total order
     */
    public void setTotalOrder(int totalOrder) {
        this.totalOrder = totalOrder;
    }
}
