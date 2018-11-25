package com.cbasolutions.cbapos.helper;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.ContentFrameLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.activity.SignInActivity;
import com.couchbase.lite.Database;

/**
 * Created by Dell on 2018-08-20.
 */

public class NetworkChangeReceiver extends BroadcastReceiver {

    CountDownTimer cTimer = null;
    Context mContext = null;
    private Database mDatabase;
    public static NetworkChangeReceiver receiver;

    @Override
    public void onReceive(final Context context, Intent intent) {

        mContext = context;
        receiver = this;
        if(!isOnline(mContext)) {
            Config.isOnline = false;
            startTimer();

        }else{
            Config.isOnline = true;
        }

        ObservableObject.getInstance().updateValue(intent);

    }

    private boolean isOnline(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getActiveNetworkInfo();
            //should check null because in airplane mode it will be null
            return (netInfo != null && netInfo.isConnected());
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
    }


    void startTimer() {
        cTimer = new CountDownTimer(180000, 5000) {
            public void onTick(long millisUntilFinished) {
                if(!Config.isOnline) {
                    Log.d("PPP", "seconds remaining: " + millisUntilFinished / 1000);
                }else{
                    cancelTimer();
                }

            }
            public void onFinish() {
                Log.d("PPP","Done!! ");
                Config.signOutUser(mContext);
                try {
                    mDatabase = returnDB(mContext);
                    if(mDatabase != null) {
                        //mDatabase.close();
                        mDatabase.delete();
                    }
                    mDatabase = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
                cTimer.cancel();

                Intent i = new Intent(mContext, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mContext.startActivity(i);
                Application.isLoginScreenPassed = false;
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();

        Log.d("PPP","STOPED!! ");

    }

    public Database returnDB(Context context) {
        Application application = (Application) context.getApplicationContext();
        mDatabase = application.getDatabase();

        return mDatabase;
    }


}
