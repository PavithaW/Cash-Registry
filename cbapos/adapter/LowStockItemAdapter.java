package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.cbasolutions.cbapos.model.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 2018-09-13.
 */

public class LowStockItemAdapter extends BaseAdapter {

    ArrayList<Item> data;
    Context context;

    public LowStockItemAdapter(ArrayList<Item> data, Context c){
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
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
