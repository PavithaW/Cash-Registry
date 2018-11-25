package com.cbasolutions.cbapos.model;

/**
 * Created by Don on 10/12/2017.
 */

public class PriceVariation {

    double price;
    String variation;
    String sku;
    private long stk_count;
    private String barcode = "";
    private boolean isVariable = false;
    private boolean isRegular;

//    public String getVariationId() {
//        return variationId;
//    }
//
//    public void setVariationId(String variationId) {
//        this.variationId = variationId;
//    }

    public double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variationName) {
        this.variation = variationName;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public boolean getIsRegular() {
        return isRegular;
    }

    public void setIsRegular(boolean regular) {
        isRegular = regular;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public boolean getIsVariable() {
        return isVariable;
    }

    public void setIsVariable(boolean variable) {
        isVariable = variable;
    }

    public long getStk_count() {
        return stk_count;
    }

    public void setStk_count(long stk_count) {
        this.stk_count = stk_count;
    }
}
