package com.cbasolutions.cbapos.helper;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IdRes;
import android.support.v4.provider.DocumentFile;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;

import java.util.Locale;

/**
 * Created by "Don" on 10/26/2017.
 * Class Functionality :-
 */

public class KeyboardView extends FrameLayout implements View.OnClickListener,View.OnTouchListener {

    public EditText customAmountEditText;
    public Button handler;
    public TextView addCustomAmount;

    public KeyboardView(Context context) {
        super(context);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public KeyboardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.number_pad_1, this);
        initViews();
    }

    private void initViews() {
        customAmountEditText = $(R.id.password_field);
        handler = $(R.id.handler);
        addCustomAmount = $(R.id.t9_key_add);

        $(R.id.t9_key_0).setOnTouchListener(this);
        $(R.id.t9_key_1).setOnTouchListener(this);
        $(R.id.t9_key_2).setOnTouchListener(this);
        $(R.id.t9_key_3).setOnTouchListener(this);
        $(R.id.t9_key_4).setOnTouchListener(this);
        $(R.id.t9_key_5).setOnTouchListener(this);
        $(R.id.t9_key_6).setOnTouchListener(this);
        $(R.id.t9_key_7).setOnTouchListener(this);
        $(R.id.t9_key_8).setOnTouchListener(this);
        $(R.id.t9_key_9).setOnTouchListener(this);

        $(R.id.t9_key_0).setOnClickListener(this);
        $(R.id.t9_key_1).setOnClickListener(this);
        $(R.id.t9_key_2).setOnClickListener(this);
        $(R.id.t9_key_3).setOnClickListener(this);
        $(R.id.t9_key_4).setOnClickListener(this);
        $(R.id.t9_key_5).setOnClickListener(this);
        $(R.id.t9_key_6).setOnClickListener(this);
        $(R.id.t9_key_7).setOnClickListener(this);
        $(R.id.t9_key_8).setOnClickListener(this);
        $(R.id.t9_key_9).setOnClickListener(this);
        $(R.id.t9_key_add).setOnClickListener(this);
        $(R.id.t9_key_backspace).setOnClickListener(this);

        //addCustomAmount.setEnabled(false);
    }

    /*
    Clears all GradientDrawable of keys
     */
    private void clearBackgroundDrawables(){
        $(R.id.t9_key_0).setBackground(null);
        $(R.id.t9_key_1).setBackground(null);
        $(R.id.t9_key_2).setBackground(null);
        $(R.id.t9_key_3).setBackground(null);
        $(R.id.t9_key_4).setBackground(null);
        $(R.id.t9_key_5).setBackground(null);
        $(R.id.t9_key_6).setBackground(null);
        $(R.id.t9_key_7).setBackground(null);
        $(R.id.t9_key_8).setBackground(null);
        $(R.id.t9_key_9).setBackground(null);
        $(R.id.t9_key_add).setBackground(null);
        $(R.id.t9_key_backspace).setBackground(null);
    }

    private void setGradientDrawable(View v){
        GradientDrawable gd = new GradientDrawable();
        // Changes this drawbale to use a single color instead of a gradient
        gd.setShape(GradientDrawable.OVAL);
        gd.setStroke(5, Color.parseColor("#008DCF"));
        v.setBackground(gd);
    }


//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//        clearBackgroundDrawables();
//        setGradientDrawable(v);
//        return false;
//    }

    @Override
    public void onClick(View v) {
        // handle number button click
        String amount = customAmountEditText.getText().toString(); //0.05

        if (v.getTag() != null && "number_button".equals(v.getTag())) {
            //customAmountEditText.append(((TextView) v).getText());
            if (amount.isEmpty()) {
                customAmountEditText.setText("0.0"+((TextView) v).getText().toString());
            } else {
                String amount2 = String.valueOf(amount) + ((TextView) v).getText().toString();
                double amount3 = Double.parseDouble(amount2) * 10;
                customAmountEditText.setText(String.format("%.2f", amount3));
            }


            return;
        }
        switch (v.getId()) {
            case R.id.t9_key_add: { //
                //mPasswordField.setText(null);
            }
            break;
            case R.id.t9_key_backspace: { // handle backspace button
                // delete one character
//                Editable editable = customAmountEditText.getText();
//                int charCount = editable.length();
//                if (charCount > 0) {
//                    editable.delete(charCount - 1, charCount);
//                }

//                if (!customAmountEditText.getText().toString().isEmpty()) {
//                    String a1 = customAmountEditText.getText().toString();
//                    a1 = a1.substring(0, a1.length() - 1);
//
//                    double a2 = Double.parseDouble(a1) / 10;
//
//                    customAmountEditText.setText(String.format("%.2f", a2));
//                }
                customAmountEditText.setText("");
            }
            break;
        }
    }

    public String getInputText() {
        return customAmountEditText.getText().toString();
    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // PRESSED
                setGradientDrawable(v);
                return false; // if you want to handle the touch event
            case MotionEvent.ACTION_UP:
                // RELEASED
                clearBackgroundDrawables();
                return false; // if you want to handle the touch event
        }
        return false;
    }

}