package com.cbasolutions.cbapos.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.model.Item;

import java.util.List;

/**
 * Created by USER on 16-Nov-17.
 */

public class TransactionItemsAdapter extends BaseAdapter {

    List<Item> list;
    public Activity context;

    public TransactionItemsAdapter(Activity context, List<Item> list) {
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
        convertView = mInflater.inflate(R.layout.transaction_history_row_3, null);

        TextView pos = (TextView) convertView.findViewById(R.id.position);
        TextView itemName = (TextView) convertView.findViewById(R.id.itemName);
        TextView noOfItems = (TextView) convertView.findViewById(R.id.noOfItems);
        TextView price = (TextView) convertView.findViewById(R.id.price);

        itemName.setEllipsize(TextUtils.TruncateAt.END);
        itemName.setHorizontallyScrolling(false);
        itemName.setSingleLine();

        int itemNo = position + 1;

        pos.setText(""+ itemNo +".");
        itemName.setText(list.get(position).getItem_name());
        noOfItems.setText("x"+list.get(position).getQuantity());
        price.setText("Rs "+String.format("%.2f", list.get(position).getItemTotal()));

        return convertView;
    }
}
