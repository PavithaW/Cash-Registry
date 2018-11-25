package com.cbasolutions.cbapos.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.google.gson.Gson;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Dell on 2018-08-23.
 */

public class AppKillDetector extends Service {

    private SharedPreferences inCompletedPayments;
    private SharedPreferences.Editor paymentPrefsEditor;
    private SharedPreferences loginPreferences;

    private String mStoreId;
    Gson gson = new Gson();
    private DBManager manager;

    Set<Payment> set = new HashSet<Payment>();
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ClearFromRecentService", "Service Started");
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.e("ClearFromRecentService", "END");

        if(Config.savedSplitList == null){
            if (manager == null) {
                manager = new DBManager();
                if(Config.savedTransaction != null)
                    manager.deleteTransaction(Config.savedTransaction.gettId(), this);
            }
        }else {

            //inCompletedPayments = PreferenceManager.getDefaultSharedPreferences(this);
            loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
            mStoreId = loginPreferences.getString("storeId", null);

            inCompletedPayments = this.getSharedPreferences("paymentPrefs", MODE_PRIVATE);
            paymentPrefsEditor = inCompletedPayments.edit();

            paymentPrefsEditor.putBoolean("isPaymentComplete", Config.isInCompletePayment);
            paymentPrefsEditor.putString("storeId", mStoreId);
            paymentPrefsEditor.putString("totalAmount", String.valueOf(Config.totalAmount));
            paymentPrefsEditor.putString("splittedAmount", String.valueOf(Config.splittedAmount));
            paymentPrefsEditor.putString("remainingAmount", String.valueOf(Config.remainingAmount));
            paymentPrefsEditor.putString("initialAmount", String.valueOf(Config.initialAmount));
            paymentPrefsEditor.putString("transactionId", String.valueOf(Config.tId));

            String jsonsplittedList = gson.toJson(Config.savedSplitList);
            paymentPrefsEditor.putString("paymentList", jsonsplittedList);

            paymentPrefsEditor.commit();
        }

        //Code here
        stopSelf();
    }
}
