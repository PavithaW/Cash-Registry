package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.Transaction;

import java.util.List;

/**
 * Created by Dell on 2018-05-24.
 */

public class TransactionDetailAdapter extends BaseAdapter {

    List<Payment> data;
    Context context;
    Transaction trans;

    public TransactionDetailAdapter(List<Payment> data, Context c,Transaction transaction){
        this.data = data;
        this.context = c;
        this.trans = transaction;
    }
    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Payment payment = data.get(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.transaction_item, null);


        ImageView imageIcon = v.findViewById(R.id.imageView10);
        TextView paymentType = v.findViewById(R.id.textView21);
        TextView amountText = v.findViewById(R.id.cashAmount);
        TextView transactionId = v.findViewById(R.id.textView23);



        int typePay = payment.getPaymentType();
        //paymentType.setText("");

        if(typePay == Config.CASH){
            imageIcon.setImageResource(R.drawable.cash_blue);
            amountText.setText("Rs " + String.format( "%.2f", payment.gettAmount()));
            transactionId.setText(trans.gettId());
            paymentType.setText(context.getString(R.string.cashPayment));
        }
        else if(typePay == Config.CARD){
            imageIcon.setImageResource(R.drawable.card_blue);
        }
        else{
            imageIcon.setImageResource(R.drawable.refund);
            amountText.setText("Rs -" + String.format( "%.2f",trans.getTRefund().get(0).gettRefundAmount()));
            transactionId.setText(trans.getTRefund().get(0).gettReasonForRefund());
            paymentType.setText(context.getString(R.string.refund));

        }


        return v;
    }
}
