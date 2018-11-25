package com.cbasolutions.cbapos.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.model.ORMTblItem;

import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.MyViewHolder> {

    private Context mContext;
    private ArrayList<ORMTblItem> products = new ArrayList<>();

    public ProductAdapter(Context context, ArrayList<ORMTblItem> products) {
        this.mContext = context;
        this.products = products;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, price;

        MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.itemNameTextView);
            price = (TextView) view.findViewById(R.id.itemPriceTextView);

            name.setEllipsize(TextUtils.TruncateAt.END);
            name.setHorizontallyScrolling(false);
            name.setSingleLine();
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_items_grid_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final MyViewHolder holder, @SuppressLint("RecyclerView") final int position) {

        holder.name.setText(products.get(position).getItem_name());
        holder.price.setText(""+products.get(position).getUnit_price());
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

}
