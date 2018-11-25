package com.cbasolutions.cbapos.model;

/**
 * Created by Dell on 2018-07-20.
 */

public class Refund {

    private int paymentType;
    private double tRefundAmount;
    private String tReasonForRefund;

    public int getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(int paymentType) {
        this.paymentType = paymentType;
    }

    public double gettRefundAmount() {
        return tRefundAmount;
    }

    public void settRefundAmount(double tRefundAmount) {
        this.tRefundAmount = tRefundAmount;
    }

    public String gettReasonForRefund() {
        return tReasonForRefund;
    }

    public void settReasonForRefund(String tReasonForRefund) {
        this.tReasonForRefund = tReasonForRefund;
    }
}
