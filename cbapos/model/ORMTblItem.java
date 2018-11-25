package com.cbasolutions.cbapos.model;

public class ORMTblItem {

    private long id;
    private String item_name;
    private double unit_price;
    private String sku;
    private ORMTblCategory category;
    private String color_code;
    private String item_img;
    private int no_of_variant;
    private long added_ts;
    private String description;
    private int qty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public double getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(double unit_price) {
        this.unit_price = unit_price;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public ORMTblCategory getCategory() {
        return category;
    }

    public void setCategory(ORMTblCategory category) {
        this.category = category;
    }

    public String getColor_code() {
        return color_code;
    }

    public void setColor_code(String color_code) {
        this.color_code = color_code;
    }

    public String getItem_img() {
        return item_img;
    }

    public void setItem_img(String item_img) {
        this.item_img = item_img;
    }

    public int getNo_of_variant() {
        return no_of_variant;
    }

    public void setNo_of_variant(int no_of_variant) {
        this.no_of_variant = no_of_variant;
    }

    public long getAdded_ts() {
        return added_ts;
    }

    public void setAdded_ts(long added_ts) {
        this.added_ts = added_ts;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }
}
