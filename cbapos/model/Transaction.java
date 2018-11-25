package com.cbasolutions.cbapos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by USER on 16-Nov-17.
 */

public class Transaction {

    private String tId;
    private String invoiceNo;
    private Date tDateTime;
    private double tValue;
    private double tTenderValue;
    private double tBalance;
    private List<Item> tItems = new ArrayList<Item>();
    private List<Payment> transactionList = new ArrayList<Payment>();
    private List<Discount> tDiscounts = new ArrayList<>();
    private String paymentType;
    //private double splitAmount;
    private double initialAmount;
    private boolean isIncomplete;
    private List<Refund> tRefund = new ArrayList<Refund>();

    public String gettId() {
        return tId;
    }

    public void settId(String tId) {
        this.tId = tId;
    }

    public String getInvoiceNo() {
        return invoiceNo;
    }

    public void setInvoiceNo(String invoiceNo) {
        this.invoiceNo = invoiceNo;
    }

    public Date gettDateTime() {
        return tDateTime;
    }

    public void settDateTime(Date tDateTime) {
        this.tDateTime = tDateTime;
    }

    public double gettValue() {
        return tValue;
    }

    public void settValue(double tValue) {
        this.tValue = tValue;
    }

    public double gettTenderValue() {
        return tTenderValue;
    }

    public void settTenderValue(double tTenderValue) {
        this.tTenderValue = tTenderValue;
    }

    public double gettBalance() {
        return tBalance;
    }

    public void settBalance(double tBalance) {
        this.tBalance = tBalance;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String payType) {
        this.paymentType = payType;
    }

    public List<Item> gettItems() {
        return tItems;
    }

    public void settItems(List<Item> tItems) {
        this.tItems = tItems;
    }

    public List<Discount> gettDiscounts() {
        return tDiscounts;
    }

    public void settDiscounts(List<Discount> tDiscounts) {
        this.tDiscounts = tDiscounts;
    }

    public List<Payment> getTransactionList() {
        return transactionList;
    }

    public void setTransactionList(List<Payment> transactionList) {
        this.transactionList = transactionList;
    }

    public double getInitialAmount() {
        return initialAmount;
    }

    public void setInitialAmount(double initialAmount) {
        this.initialAmount = initialAmount;
    }

    public List<Refund> getTRefund() {
        return tRefund;
    }

    public void setTRefund(List<Refund> tRefund) {
        this.tRefund = tRefund;
    }

    public boolean isIncomplete() {
        return isIncomplete;
    }

    public void setIncomplete(boolean incomplete) {
        isIncomplete = incomplete;
    }
}
