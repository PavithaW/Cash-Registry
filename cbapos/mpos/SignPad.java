package com.cbasolutions.cbapos.mpos;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cba.payablesdk.Payable;
import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.helper.DrawingArea;

import java.io.ByteArrayOutputStream;

import models.ModelSignature;

/**
 * Created by Dell on 2018-03-27.
 */

public class SignPad extends Activity {

    public static double amount;
    private DrawingArea drawingArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signature_pad);

        Bundle bundle = getIntent().getExtras();
        double billamount = bundle.getDouble("billValue");
        amount = billamount;

        //LinearLayout layout = findViewById(R.id.signLayout);
        Button dialogButton = findViewById(R.id.backClose);
        TextView billPrice = findViewById(R.id.totalPrice);
        Button signClearButton  = findViewById(R.id.clear_sig_button);
        final TextView txtSignHere = findViewById(R.id.txtSignHere);
        drawingArea = findViewById(R.id.signaturePad);
        Button saveSignButton  = findViewById(R.id.btn_save_item);

        txtSignHere.setTypeface(Typeface.createFromAsset(getApplicationContext().getAssets(),"sinhala_font/kinescope.otf"));

        DisplayMetrics metrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;

        this.getWindow().setLayout((widthPixels / 8) * 6, LinearLayout.LayoutParams.WRAP_CONTENT);


        billPrice.setText("Rs " + String.format("%.2f", amount));
        //cardSale(billValue);
        drawingArea.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                txtSignHere.setVisibility(View.INVISIBLE);
                return false;
            }
        });


        dialogButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }

        });

        signClearButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                drawingArea.clearPad();
                txtSignHere.setVisibility(View.VISIBLE);
                return false;
            }
        });

        dialogButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                finish();
                return false;
            }

        });


        saveSignButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!drawingArea.isValidSig()) {
                    Toast.makeText(getApplicationContext(), "Please put your signature. ", Toast.LENGTH_LONG).show();
                    return;
                }
                Bitmap img = drawingArea.fetchSignature();
                String strimg = encodeTobase64(img);
                // Log.i(TAG, strimg) ;

                ModelSignature sig = new ModelSignature();
                sig.setSaleId(878);
                sig.setSignature(strimg);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                getApplicationContext().startActivity(intent);

                finish();
            }
        });



    }

    public static Bitmap decodeBase64(String input) {

        if (input == null) {
            return null;
        }

        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory
                .decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    public static String encodeTobase64(Bitmap image) {

        if (image == null) {
            return null;
        }

        Bitmap immagex = image;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        immagex.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);

        return imageEncoded;
    }
}
