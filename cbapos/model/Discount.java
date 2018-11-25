package com.cbasolutions.cbapos.model;

/**
 * Created by USER on 17-Nov-17.
 */

public class Discount {

    private String discount_id;
    private String discount_name;
    private String discount_type;
    private int discount_color;
    private double dicount_value;

    private boolean checked;

    public String getDiscount_id() {
        return discount_id;
    }

    public void setDiscount_id(String discountId) {
        this.discount_id = discountId;
    }

    public String getDiscount_name() {
        return discount_name;
    }
    public int getDiscount_color() {
        return discount_color;
    }

    public void setDiscount_color(int discountColor) {
        this.discount_color = discountColor;
    }

    public void setDiscount_name(String discountName) {
        this.discount_name = discountName;
    }

    public String getDiscount_type() {
        return discount_type;
    }

    public void setDiscount_type(String discountType) {
        this.discount_type = discountType;
    }

    public double getDiscount_value() {
        return dicount_value;
    }

    public void setDiscount_value(double discountValue) {
        this.dicount_value = discountValue;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
