package com.cbasolutions.cbapos.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.model.DateTime;
import com.cbasolutions.cbapos.model.Transaction;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by USER on 16-Nov-17.
 */

public class TransactionAdapter extends BaseAdapter {

    ArrayList<Object> list;
    private Activity context;
    private int pos;

    public TransactionAdapter(Activity context, ArrayList<Object> list, int pos) {
        super();
        this.context = context;
        this.list = list;
        this.pos = pos;
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

        if (list.get(position) instanceof DateTime) {
            convertView = mInflater.inflate(R.layout.transaction_history_row_2, null);

            TextView date = (TextView) convertView.findViewById(R.id.date);

            Date d1 = ((DateTime) list.get(position)).getDate();

            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
            String d2 = newDateFormat.format(d1);

            newDateFormat.applyPattern("EEEE");

            String myDate1 = newDateFormat.format(d1) + " " + d2.split("\\s+")[0].split("-")[2]+"/" + d2.split("\\s+")[0].split("-")[1] + "/" +
                    d2.split("\\s+")[0].split("-")[0].substring(2,4);

            date.setText(myDate1);

        } else if (list.get(position) instanceof Transaction) {
            convertView = mInflater.inflate(R.layout.transaction_history_row, null);

            Log.d("mash_logs", ((Transaction) list.get(position)).getInvoiceNo());

            final TextView id = (TextView) convertView.findViewById(R.id.id);
            final TextView total_amount = (TextView) convertView.findViewById(R.id.total_amount);
            final ImageView iv = (ImageView) convertView.findViewById(R.id.imageView11);

            final RelativeLayout layout = (RelativeLayout) convertView.findViewById(R.id.layout);

            id.setText(((Transaction) list.get(position)).getInvoiceNo());
            total_amount.setText("Rs "+String.format("%.2f", ((Transaction) list.get(position)).gettValue()));

            if(((Transaction) list.get(position)).isIncomplete() == true){
                iv.setImageResource(R.drawable.incompleate_transaction);


            }else {

                if (((Transaction) list.get(position)).getTRefund().size() != 0) {

                    if (((Transaction) list.get(position)).getTRefund().get(0).gettRefundAmount() > 0.0) {
                        iv.setImageResource(R.drawable.card_dark_refund);
                    } else {
                        iv.setImageResource(R.drawable.card_dark);
                    }
                }
            }

            if (pos == position) {
                layout.setBackgroundColor(Color.parseColor("#131629"));
                id.setTextColor(Color.parseColor("#ffffff"));
                total_amount.setTextColor(Color.parseColor("#ffffff"));

                if(((Transaction) list.get(position)).isIncomplete() == true){
                    iv.setImageResource(R.drawable.incompleate_transactionselected);


                }else {

                    iv.setImageResource(R.drawable.card_light);


                    if (((Transaction) list.get(position)).getTRefund().size() != 0) {
                        if (((Transaction) list.get(position)).getTRefund().get(0).gettRefundAmount() > 0.0) {
                            iv.setImageResource(R.drawable.card_light_refund);
                        } else {
                            iv.setImageResource(R.drawable.card_light);
                        }
                    }
                }
            }
        }

        return convertView;
    }

}
