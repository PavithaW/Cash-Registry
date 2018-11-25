package com.cbasolutions.cbapos.model;

/**
 * Created by USER on 08-Nov-17.
 */

public class Category {

    private String category_name;
    private int color;
    private String category_id;
    private int itemCount;

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String catName) {
        this.category_name = catName;
    }

    public int getCatColor() {
        return color;
    }

    public void setCatColor(int catColor) {
        this.color = catColor;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }
}
