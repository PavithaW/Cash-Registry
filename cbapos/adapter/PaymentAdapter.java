package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;

import java.util.List;

/**
 * Created by Dell on 2018-05-21.
 */

public class PaymentAdapter extends BaseAdapter {

    List<Payment> data;
    Context context;

    public PaymentAdapter(List<Payment> data, Context c){

        this.data = data;
        this.context = c;

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

        final Payment payment = data.get(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.payment_item_listdata, null);

        TextView name = (TextView) v.findViewById(R.id.txtName);
        TextView amount = (TextView) v.findViewById(R.id.amount);
        Button remove = (Button) v.findViewById(R.id.removeButton);
        remove.setTag(position);

        if(payment.getPaymentType() == Config.CASH) {
            name.setText(context.getString(R.string.cash));
        }else{
            name.setText(context.getString(R.string.card));
        }
        amount.setText("Rs " + String.format( "%.2f", payment.gettAmount()));

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Integer index = (Integer) view.getTag();
                double trashedAmount =  data.get(index.intValue()).gettAmount();
                BillViewModel.setBalanceText(trashedAmount,index.intValue(),true);
                data.remove(index.intValue());
                notifyDataSetChanged();


            }});
                return v;
    }
}
