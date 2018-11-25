package com.cbasolutions.cbapos.mpos;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cba.payablesdk.Payable;
import com.cbasolutions.cbapos.R;

/**
 * Created by Dell on 2018-03-26.
 */

public class SdkRegisterUser extends Activity {
    Payable payable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_register_box);

        TelephonyManager mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //String imsi = mTelephonyMgr.getSubscriberId();
        String imei = mTelephonyMgr.getDeviceId();
        String simno = mTelephonyMgr.getSimSerialNumber();


        //payable = new Payable(this);


        //Bundle bundle = getIntent().getExtras();
        //billamount = bundle.getDouble("billValue");

        final TextView imeiId = findViewById(R.id.imeiText);
        final TextView simId = findViewById(R.id.simText);
        final EditText bankCode = findViewById(R.id.bankCode);
        final EditText tag = findViewById(R.id.tag);
        final Button sendButton = findViewById(R.id.send_button);

        imeiId.setText(imei.toString());
        simId.setText(simno.toString());

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sBankCode = bankCode.getText().toString().trim().toLowerCase();
                String sTag = tag.getText().toString();



                if (sBankCode == null) {
                    //showDialog("Error", "Please enter your bank code.");
                    return;
                } else if (sBankCode.length() == 0) {
                    //showDialog("Error", "Please enter your bank code.");
                    return;
                } else if (!sBankCode.equalsIgnoreCase("commercial")
                        && !sBankCode.equalsIgnoreCase("seylan")
                        && !sBankCode.equalsIgnoreCase("hnb")
                        && !sBankCode.equalsIgnoreCase("boc")) {
                    //showDialog("Error", "Please Enter valid bank code.");
                    return;
                } else {

                }
            }
        });


//        final EditText password = findViewById(R.id.passwordEditText);
//        imageView = findViewById(R.id.logo_image_view);
//        signInButton = findViewById(R.id.sign_in_button);


    }
}
