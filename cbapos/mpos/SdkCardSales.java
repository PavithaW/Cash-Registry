package com.cbasolutions.cbapos.mpos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.cba.payablesdk.Payable;
import com.cba.payablesdk.PayableCardReaderCallBack;
import com.cba.payablesdk.PayableException;
import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Cart;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.Refund;
import com.cbasolutions.cbapos.model.Transaction;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.couchbase.lite.CouchbaseLiteException;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import common.PayableCallBack;
import common.PayableCallBack_RegisterCardReader;
import models.BatchlistRes;
import models.PayableTX;
import models.TxSaleRes;
import models.TxSettlementResponse;
import models.TxSettlementSummaryEle;
import models.TxVoidRes;

/**
 * Created by Dell on 2018-02-26.
 */

public class SdkCardSales extends Activity implements PayableCallBack,PayableCardReaderCallBack, PayableCallBack_RegisterCardReader {
    Payable payable;
    public static double amount;
    Context mContext = null;
    public static ArrayList<Refund> refundArrayList = new ArrayList<>();
    //private Cart cart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.swipe_card_pop_up);
        Bundle bundle = getIntent().getExtras();
        double billamount = bundle.getDouble("billValue");
        amount = billamount;
        //cart = new Cart();
        Button dialogButton = findViewById(R.id.backClose);
        TextView billPrice = findViewById(R.id.totalPrice);

        billPrice.setText("Rs " + String.format("%.2f", amount));
        //cardSale(billValue);

        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        payable = new Payable(this,this);
        mContext = getApplicationContext();
        performSale(amount);
    }


    @Override
    public void onFailureAuthenticate(PayableException e) {

    }


    private void performSale(Double amount){

        try {
            unpairBluetoothDevices();
            payable.sale(this,this,amount ,"aaa10");
        } catch (PayableException e) {
            e.printStackTrace();
            if(e.toString().equalsIgnoreCase("Not logged in")) {
                Intent intent = new Intent(getApplicationContext(), SdkSignInActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putDouble("billValue", amount);
                intent.putExtras(bundle);
                mContext.startActivity(intent);
                finish();
            }
        }
    }

    public void unpairBluetoothDevices(){

       BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    if(device.getName().startsWith("MPOS")){
                        Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onSuccessRegister(String res) {
        //performSale();
    }

    @Override
    public void onFailureRegister(String res) {


    }

    @Override
    public void onSuccessSales(TxSaleRes txSaleRes) {

        saveTransaction();

        Toast.makeText(getApplicationContext(), "Success Transaction "+ txSaleRes.getApprovalCode(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent(getApplicationContext(), SignPad.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putDouble("billValue", amount);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
        finish();
    }

    @Override
    public void onFailureSales(PayableException e) {
        Toast.makeText(getApplicationContext(), "Fail Transaction "+ e.toString(), Toast.LENGTH_LONG).show();


        if(e.toString().equalsIgnoreCase("No cardreader detected.")) {

          finish();

        }

            if(e.toString().equalsIgnoreCase("No registred card reader")) {
//                Intent intent = new Intent(getApplicationContext(), SignPad.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                Bundle bundle = new Bundle();
//                intent = new Intent(getApplicationContext(), SdkRegisterReader.class);
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                bundle.putDouble("billValue", amount);
//                intent.putExtras(bundle);
//                mContext.startActivity(intent);
                finish();
            }



    }

    public void saveTransaction(){

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String d = df.format(Calendar.getInstance().getTime());
        Date date = null;

        try {
            date = df.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<Discount> tDiscounts = new ArrayList<>();

        for (int i = 0; i < Cart.arrayList.size(); i++) {
            if (Cart.arrayList.get(i).getItemType().equals(Config.DISCOUNT)) {
                Item item = Cart.arrayList.get(i);
                tDiscounts.add(item.getItemDiscount());
            }
        }

        Refund refund = new Refund();
        refund.setPaymentType(Config.CASH);
        refund.settRefundAmount(0.00);
        refund.settReasonForRefund("");

        refundArrayList.add(refund);

        //save transaction to DB
        DBManager manager = new DBManager();
        Transaction transaction = new Transaction();
        transaction.settDateTime(date);
        transaction.settItems(Cart.arrayList);
        transaction.settValue(amount);
        transaction.setPaymentType("cash");
        transaction.settDiscounts(tDiscounts);
        transaction.setTRefund(refundArrayList);

        try {
            manager.saveTransaction(mContext, transaction);
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onBluetoothTurnedOn(String res) {

    }

    @Override
    public void onBluetoothTurnedOff(String res) {

    }

    @Override
    public void cardReaderStatus(int status, String message) {

    }

    @Override
    public void onSuccessEcho(int res) {

    }

    @Override
    public void onFailureEcho(PayableException e) {

    }

    @Override
    public void onSuccessOpenTx(ArrayList<PayableTX> res) {

    }

    @Override
    public void onFailureOpenTx(PayableException e) {

    }

    @Override
    public void onSuccessCloseTx(ArrayList<PayableTX> res) {

    }

    @Override
    public void onFailureCloseTx(PayableException e) {

    }

    @Override
    public void onSuccessVoid(TxVoidRes txVoidRes) {

    }

    @Override
    public void onFailureVoid(PayableException e) {

    }

    @Override
    public void onSuccessSignature() {

    }

    @Override
    public void onFailureSignature(PayableException e) {

    }

    @Override
    public void onSuccessSettlementSummary(ArrayList<TxSettlementSummaryEle> res) {

    }

    @Override
    public void onFailureSettlementSummary(PayableException e) {

    }

    @Override
    public void onSuccessSettle(TxSettlementResponse res) {

    }

    @Override
    public void onFailureSettle(PayableException e) {

    }

    @Override
    public void onSuccessBatchList(ArrayList<BatchlistRes> res) {

    }

    @Override
    public void onFailureBatchList(PayableException e) {

    }

    @Override
    public void onSucessAuthenticate() {

    }


}
