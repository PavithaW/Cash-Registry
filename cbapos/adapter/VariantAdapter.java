package com.cbasolutions.cbapos.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.model.PriceVariation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by "Don" on 11/6/2017.
 * Class Functionality :-
 */

public class VariantAdapter extends BaseAdapter {

    List<PriceVariation> data;
    Context context;
    BaseActivity baseContext;
    boolean hideDelete = false;
    boolean hideZeroPos = false;

    public VariantAdapter(List<PriceVariation> data, Context c, BaseActivity baseContext, boolean hideDelete) {
        this.data = data;
        this.context = c;
        this.hideDelete = hideDelete;
        this.baseContext = baseContext;
    }

    @Override
    public int getCount() {
//        if(hideZeroPos) {
//            return data.size()-1;
//        }else{
//            return data.size();
//        }
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

            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = vi.inflate(R.layout.variant_row, null);

                TextView name = (TextView) v.findViewById(R.id.variant_name);
                TextView price = (TextView) v.findViewById(R.id.txt_price);
                Button deleteVariant = (Button) v.findViewById(R.id.variant_delete);


                if (hideZeroPos) {
                    position = position + 1;
                }
                final int pos = position;
//        if (data.size() >= position) {
                final PriceVariation variant = data.get(position);


                name.setText(variant.getVariation());
                if (variant.getPrice() != 0.0) {
                    price.setText(String.format("%.2f", variant.getPrice()));
                } else {
                    price.setText("0.00");
                }


                if (hideDelete) {
                    deleteVariant.setVisibility(View.GONE);
                } else {
                    deleteVariant.setVisibility(View.VISIBLE);
                }

                // }
                deleteVariant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage(context.getString(R.string.sureDeleteVariant))
                                .setCancelable(false)
                                .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })
                                .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        baseContext.removePrice(pos);
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                });

            return v;

    }




}
