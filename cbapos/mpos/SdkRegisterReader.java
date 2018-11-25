package com.cbasolutions.cbapos.mpos;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.util.Log;
import android.widget.Toast;

import com.cba.payablesdk.Payable;
import com.cba.payablesdk.PayableCardReaderCallBack;
import com.cba.payablesdk.PayableException;
import com.cbasolutions.cbapos.R;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
 * Created by Dell on 2018-03-21.
 */

public class SdkRegisterReader extends Activity implements PayableCallBack,PayableCardReaderCallBack, PayableCallBack_RegisterCardReader {

    private BluetoothAdapter mBluetoothAdapter;
    Payable payable;
    public static double billamount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_reader);
        payable = new Payable(this,this);

        Bundle bundle = getIntent().getExtras();
        billamount = bundle.getDouble("billValue");

        //unpairBluetoothDevices();
        registerReader();
    }

    public void unpairBluetoothDevices(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                try {
                    if(device.getName().startsWith("MPOS")){
                        Method m = device.getClass().getMethod("removeBond", (Class[]) null);
                        m.invoke(device, (Object[]) null);

                        registerReader();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }else{
            registerReader();
        }
    }


    public void registerReader(){

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            showUnsupported();
        } else {
            if (mBluetoothAdapter.isEnabled()) {

                try {
                    // payable.registerDevice(RegisterActivity.this);
                    payable.registerReader(this);
                } catch (PayableException e) {
                    e.printStackTrace();
                }

            } else {
                showDisabled();
            }
        }

    }

    private void showDisabled() {
        Toast.makeText(getApplicationContext(), "Bluetooth is turned off.Please turn it on.", Toast.LENGTH_LONG).show();


    }

    private void showUnsupported() {
        Toast.makeText(getApplicationContext(),"Bluetooth is unsupported by this device", Toast.LENGTH_LONG).show();

    }
    @Override
    public void onSuccessRegister(String res) {
        Log.d("onSucess: ","Register");
        //finish();
        Intent intent = new Intent(getApplicationContext(), SdkCardSales.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putDouble("billValue", billamount);
        intent.putExtras(bundle);
        getApplicationContext().startActivity(intent);
        finish();
    }

    @Override
    public void onFailureRegister(String res) {
        Log.d("onFailure: ","Register");
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
    public void onSuccessSales(TxSaleRes txSaleRes) {

    }

    @Override
    public void onFailureSales(PayableException e) {

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

    @Override
    public void onFailureAuthenticate(PayableException e) {

    }
}
