package com.cbasolutions.cbapos.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.model.DateTime;
import com.cbasolutions.cbapos.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by USER on 07-Dec-17.
 */

public class PrintersAdapter extends BaseAdapter {

    ArrayList<String> list;
    public Activity context;

    public PrintersAdapter(Activity context, ArrayList<String> list) {
        super();
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        convertView = mInflater.inflate(R.layout.printers_list_row, null);

        TextView name = (TextView) convertView.findViewById(R.id.name);

        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setMaxLines(1);

        name.setText(list.get(position));

        return convertView;
    }
}
