package com.cbasolutions.cbapos.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by "Don" on 10/20/2017.
 * Class Functionality :-
 */

public class Item {

    private Category category;
    private String description;
    private String item_id;
    private double itemTotal; //String selectedPrice renamed to double itemTotal
    private double itemActualTotal;
    private String itemType;// - type can be ITEM / DISCOUNT : Discount is set as the type when adding a discount to the total bill, to identify the discoount
    private String item_name;
    private long quantity;
    private List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
    private Discount itemDiscount;
    private int color;
    private String sku;
    private String image;
    private boolean isChecked;
    private PriceVariation priceSelected;

    //related to bill


    public Item(String id, String name, Category category, String description, int color, List<PriceVariation> priceVariations) {
        this.item_id = id;
        this.item_name = name;
        this.category = category;
        this.description = description;
        this.color = color;
        this.priceVariations = priceVariations;
    }

    public Item() {
        this.item_id = item_id;
        this.item_name = item_name;
        this.category = category;
        this.description = description;
        this.color = color;
        this.priceVariations = priceVariations;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Item other = (Item) obj;
        if (item_name != other.getItem_name())
            return false;
        return true;
    }


    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String name) {
        this.item_name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<PriceVariation> getPriceVariations() {
        return priceVariations;
    }

    public void setPriceVariations(List<PriceVariation> priceVariations) {
        this.priceVariations = priceVariations;
    }

//    public String getSku() {
//        return sku;
//    }
//
//    public void setSku(String sku) {
//        this.sku = sku;
//    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public double getItemTotal() {
        return itemTotal;
    }

    public void setItemTotal(String selectedPrice) {

        this.itemTotal = Double.parseDouble(selectedPrice);
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public Discount getItemDiscount() {
        return itemDiscount;
    }

    public void setItemDiscount(Discount selectedDiscount) {
        this.itemDiscount = selectedDiscount;
    }

    public PriceVariation getPriceSelected() {
        return priceSelected;
    }

    public void setPriceSelected(PriceVariation priceSelected) {
        this.priceSelected = priceSelected;
    }

    public String getItemType() {
        return itemType;
    }

    public void setItemType(String type) {
        this.itemType = type;
    }

    public double getItemActualTotal() {
        return itemActualTotal;
    }

    public void setItemActualTotal(double itemActualTotal) {
        this.itemActualTotal = itemActualTotal;
    }
//
//    public long getStock() {
//        return stock;
//    }
//
//    public void setStock(long stock) {
//        this.stock = stock;
//    }

//    public double getStock() {
//        return stock;
//    }
//
//    public void setStock(double stock) {
//        this.stock = stock;
//    }
}
