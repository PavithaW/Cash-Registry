package com.cbasolutions.cbapos.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;

import java.util.ArrayList;

/**
 * Created by USER on 04-Aug-17.
 */

public class ProductsItemsAdapter extends BaseAdapter {

    ArrayList<String> list;
    public Activity context;

    public ProductsItemsAdapter(Activity context, ArrayList<String> list) {
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

    private class ViewHolder {

        TextView txtCategory;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ProductsItemsAdapter.ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.product_items_dropdown, null);
            holder = new ProductsItemsAdapter.ViewHolder();


            holder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);

            convertView.setTag(holder);

        } else {

            holder = (ProductsItemsAdapter.ViewHolder) convertView.getTag();
        }

        holder.txtCategory.setText(list.get(position));

        return convertView;
    }
}
