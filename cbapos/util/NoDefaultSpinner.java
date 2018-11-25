package com.cbasolutions.cbapos.util;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Spinner;
import android.support.v7.widget.AppCompatSpinner;

/**
 * Created by USER on 22-Dec-17.
 */

public class NoDefaultSpinner extends AppCompatSpinner {

    OnItemSelectedListener listener;
    int prevPos = -1;
    public NoDefaultSpinner(Context context) {
        super(context);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoDefaultSpinner(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        if (position == getSelectedItemPosition() && prevPos == position) {
            getOnItemSelectedListener().onItemSelected(null, null, position, 0);
        }
        prevPos = position;
    }
}