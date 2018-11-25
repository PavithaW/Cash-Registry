package com.cbasolutions.cbapos.mpos;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cba.payablesdk.Payable;
import com.cba.payablesdk.PayableEnvironment;
import com.cba.payablesdk.PayableException;
import com.cbasolutions.cbapos.R;

import java.util.ArrayList;

import common.PayableCallBack;
import models.BatchlistRes;
import models.Credentials;
import models.PayableTX;
import models.TxSaleRes;
import models.TxSettlementResponse;
import models.TxSettlementSummaryEle;
import models.TxVoidRes;

/**
 * Created by Dell on 2018-03-21.
 */

public class SdkSignInActivity extends Activity implements PayableCallBack {

    Payable payable;
    Button signInButton;
    String sBankCode;
    String sUserName;
    String sPassword;
    public static double billamount;
    GestureDetector gestureDetector;
    boolean tapped;
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.sdk_login_box);
        payable = new Payable(this);

        Bundle bundle = getIntent().getExtras();
        billamount = bundle.getDouble("billValue");

        final EditText bankCode = findViewById(R.id.bankCodeEditText);
        final EditText userName = findViewById(R.id.userNameEditText);
        final EditText password = findViewById(R.id.passwordEditText);
        imageView = findViewById(R.id.logo_image_view);
        signInButton = findViewById(R.id.sign_in_button);

        // inside onCreate of Activity or Fragment
        gestureDetector = new GestureDetector(this,new GestureListener());

        imageView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                return gestureDetector.onTouchEvent(event);
            }

        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sBankCode = bankCode.getText().toString();
                sUserName = userName.getText().toString();
                sPassword = password.getText().toString();

                authenticate(sBankCode,sUserName,sPassword);
            }
        });

    }

    public void authenticate(String bankCode, String userName, String password) {

        Credentials c = new Credentials() ;
        c.setUserName(userName);
        c.setPwd(password);
        c.setDeveloperKey("895CE8EA3075F0ED7BAE164C3CC23DF6C4F7DE704161E7643E2CB123FF06CFD5D8A7C67CFE7E38AACE1A22CE83F1F55E738D25F464D79F6546C09C40CEFDF3A2");
        c.setDeveloperToken("BDAA59153102007076AC201A6C8A1A4D1E1863D2");
        c.setEnvironment(PayableEnvironment.SAND_BOX);

        try {
            payable.authenticate(SdkSignInActivity.this,c);
        } catch (PayableException e) {
            //Log.i(TAG , "exception e:" + e.toString());
        }
    }

    public class GestureListener extends
            GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }
        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            tapped = !tapped;
            if (tapped) {
                Intent intent = new Intent(getApplicationContext(), SdkRegisterUser.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
                Bundle bundle = new Bundle();
                bundle.putDouble("billValue", billamount);
                intent.putExtras(bundle);
                getApplicationContext().startActivity(intent);
                finish();

            } else {

            }
            return true;
        }
    }


    @Override
    public void onSucessAuthenticate() {

        Log.d("onSucess: ","Authenticate");
        Intent intent = new Intent(getApplicationContext(), SdkRegisterReader.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putDouble("billValue", billamount);
        intent.putExtras(bundle);
        getApplicationContext().startActivity(intent);
        finish();
    }

    @Override
    public void onFailureAuthenticate(PayableException e) {
        Log.d("onFailure: ","Authenticate");
        e.printStackTrace();
        finish();
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

}
