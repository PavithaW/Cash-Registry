package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Discount;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by USER on 17-Nov-17.
 */

public class DiscountAdapter extends BaseAdapter {

    List<Discount> data;
    Context context;
    List<Discount> searchList = new ArrayList<Discount>();
    boolean editItem;

    public DiscountAdapter(List<Discount> data, Context c,  boolean editItem) {
        this.data = data;
        this.context = c;
        this.searchList.addAll(this.data);
        this.editItem = editItem;
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
    public View getView(int position, View convertView, ViewGroup arg2) {
        final Discount discount = data.get(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.discount_grid_cell, null);

        TextView name = (TextView) v.findViewById(R.id.disc_name);
        TextView value = (TextView) v.findViewById(R.id.disc_val);
        Button edit = (Button) v.findViewById(R.id.edit_item);
        Button edit1 = (Button) v.findViewById(R.id.edit_item1);
        LinearLayout colorLay = (LinearLayout) v.findViewById(R.id.color_lay);
        //LinearLayout layout = v.findViewById(R.id.edit_item_Layout);


        if(editItem){
            edit.setVisibility(View.VISIBLE);
            edit1.setVisibility(View.VISIBLE);
        }else{
            edit.setVisibility(View.GONE);
            edit1.setVisibility(View.GONE);
        }

        name.setText(discount.getDiscount_name());
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setSingleLine();

        switch (discount.getDiscount_color()) {
            case 0: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_1));
                break;
            case 1: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_2));
                break;
            case 2: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_3));
                break;
            case 3: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_4));
                break;
            case 4: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_5));
                break;
            case 5: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_6));
                break;
            default: colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_1));
                break;
        }

        double disVal = discount.getDiscount_value();
        if(disVal == 0.00) {
            value.setText(context.getString(R.string.variable));
        }else{

            if (discount.getDiscount_type().equals(Config.DISCOUNT_TYPE_1)) {
                value.setText(String.format("%.2f", discount.getDiscount_value()) + " " + discount.getDiscount_type());
            } else {
                value.setText(discount.getDiscount_type() + " " + String.format("%.2f", discount.getDiscount_value()));
            }
        }

        log.d("AAA inside discount adapter: ","Dis Item "+ discount.getDiscount_name());

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ((BaseActivity)context).showDiscountPopUp(discount);
            }
        });

        return v;
    }

    //Filter dataset with searchview text
    public void filter(String charText) {
        Log.e("adapter", charText);
        charText = charText.toLowerCase(Locale.getDefault());
        if (charText.length() == 0) {
            data.clear();
            data.addAll(searchList);
        } else {
            data.clear();
            for (Discount wp : searchList) {
                if (wp.getDiscount_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                }
            }
        }
        notifyDataSetChanged();

        Config.VISIBLE_VIEW = 3;

        if (data.size() == 0 && Config.VISIBLE_VIEW == 3) {
            Toast.makeText(context, context.getString(R.string.noMatchesFoundDis), Toast.LENGTH_SHORT).show();
            return;
        }
    }


}
