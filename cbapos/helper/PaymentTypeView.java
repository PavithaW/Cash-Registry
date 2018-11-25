package com.cbasolutions.cbapos.helper;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;

/**
 * Created by Dell on 2018-02-23.
 */

public class PaymentTypeView extends FrameLayout implements View.OnClickListener{

    public Button splitButton;
    public Button cashButton;
    public Button cardButton;
    public static final double MAX_AMOUNT = 999999.99;
    Context mcontext;

    public PaymentTypeView(Context context) {
        super(context);
        mcontext = context;
        init();
    }

    public PaymentTypeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mcontext = context;
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.split_payment, this);
        initViews();
    }
    private void initViews() {
        splitButton = $(R.id.splitButton);
        cashButton = $(R.id.cash_payment);
        cardButton = $(R.id.card_payment);

        cardButton.setOnClickListener(this);
        cashButton.setOnClickListener(this);
        cardButton.setOnClickListener(this);

    }

    protected <T extends View> T $(@IdRes int id) {
        return (T) super.findViewById(id);
    }

    @Override
    public void onClick(View v) {
    if(v.getId() == R.id.cash_payment){

}
    }
}
