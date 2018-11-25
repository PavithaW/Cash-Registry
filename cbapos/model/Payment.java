package com.cbasolutions.cbapos.model;

/**
 * Created by Dell on 2018-05-21.
 */

public class Payment {

    private double tTenderAmount;
    private double tBalance;
    private double tAmount;
    private int paymentType;

    public Payment() {
    }

    public void settTenderAmount(double tTenderAmount) {
        this.tTenderAmount = tTenderAmount;
    }

    public double gettTenderAmount() {
        return tTenderAmount;
    }

    public void settBalance(double tBalance) {
        this.tBalance = tBalance;
    }

    public double gettBalance() {
        return tBalance;
    }

    public double gettAmount() {
        return tAmount;
    }

    public void settAmount(double totalAmount) {
        this.tAmount = totalAmount;
    }

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

}
