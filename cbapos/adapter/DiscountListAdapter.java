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
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;

import java.util.List;

/**
 * Created by "Don" on 11/22/2017.
 * Class Functionality :-
 *
 * Could not use checkbox here, unidentified bug coz the checkbox can not be clicked.
 */

public class DiscountListAdapter extends BaseAdapter {

    List<Discount> data;
    Context context;
    ItemsViewModel itemsViewModel;
    BillViewModel billViewModel;
    static int previousClickPosition;
    Item item;
    public static double itemTotal;

    public DiscountListAdapter(List<Discount> data, Context c, ItemsViewModel itemsViewModel, BillViewModel billViewModel, Item item) {
        this.data = data;
        this.context = c;
        this.itemsViewModel = itemsViewModel;
        this.billViewModel = billViewModel;
        this.item = item;
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
    public View getView(final int position, View convertView, ViewGroup arg2) {
        final Discount discount = data.get(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.discount_row, null);

        TextView name = (TextView) v.findViewById(R.id.txtName);
        TextView price = (TextView) v.findViewById(R.id.txtPrice);
        final Button selected = (Button) v.findViewById(R.id.disc_checkbox);

        if (!data.get(position).isChecked()) {
            selected.setBackgroundResource(R.drawable.btn_unchecked);
            data.get(position).setChecked(false);
        } else {
            selected.setBackgroundResource(R.drawable.btn_checked);
            data.get(position).setChecked(true);
        }

        name.setText(discount.getDiscount_name());

        if (discount.getDiscount_type().equals(Config.DISCOUNT_TYPE_1)) {
            price.setText(String.format("%.2f",discount.getDiscount_value()) + " " + discount.getDiscount_type());
        } else {
            price.setText(discount.getDiscount_type() + " " + String.format("%.2f",discount.getDiscount_value()));
        }

        selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                if(previousClickPosition == position) {
//                    resetButtons();
//                    selected.setBackgroundResource(R.drawable.btn_unchecked);
//                    data.get(position).setChecked(false);
//                    //previousClickPosition = 0;
//
//
//                    if (itemsViewModel != null) {
//                        itemsViewModel.handleIncomingDiscount(null);
//                    } else {
//                        billViewModel.handleIncomingDiscount(null);
//                    }
//                }


                //resetButtons();
                if (data.get(position).isChecked()) {

                    selected.setBackgroundResource(R.drawable.btn_unchecked);
                    data.get(position).setChecked(false);

                    if(itemsViewModel != null){
                        itemsViewModel.handleIncomingDiscount(null);
                    }else{
                        billViewModel.handleIncomingDiscount(null);
                    }

                } else {
                    //if(data.get(position).getDiscount_type().equals(Config.DISCOUNT_TYPE_2)){
                        if (data.get(position).getDiscount_type().equals(Config.DISCOUNT_TYPE_2) && data.get(position).getDiscount_value() > itemTotal) {

                        //resetButtons();
                            selected.setBackgroundResource(R.drawable.btn_unchecked);
                            data.get(position).setChecked(false);

                            if (itemsViewModel != null) {
                                itemsViewModel.handleIncomingDiscount(null);
                            } else {
                                billViewModel.handleIncomingDiscount(null);
                            }
                            Toast.makeText(context, context.getString(R.string.discountHigherTotal), Toast.LENGTH_LONG).show();
                            return;

                        //}
                    }else {


                            if (item.getItemDiscount() != null) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setMessage(context.getString(R.string.existingDiscount))
                                        .setCancelable(false)
                                        .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        })
                                        .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        resetButtons();
                                                        selected.setBackgroundResource(R.drawable.btn_checked);
                                                        data.get(position).setChecked(true);

                                                        if (itemsViewModel != null) {
                                                            itemsViewModel.handleIncomingDiscount(data.get(position));
                                                        } else {
                                                            billViewModel.handleIncomingDiscount(data.get(position));
                                                        }
                                                    }
                                                });
                                AlertDialog alert = builder.create();
                                alert.show();
                            } else {
                                resetButtons();
                                Discount x = data.get(position);
                                selected.setBackgroundResource(R.drawable.btn_checked);
                                data.get(position).setChecked(true);

                                if (itemsViewModel != null) {
                                    itemsViewModel.handleIncomingDiscount(data.get(position));
                                } else {
                                    if (billViewModel != null) {
                                        billViewModel.handleIncomingDiscount(data.get(position));
                                    }
                                }
                            }
                        }


                }

                previousClickPosition = position;
            }
        });

        return v;
    }

    private void transferDiscount(int position){
        if(itemsViewModel != null){
            itemsViewModel.handleIncomingDiscount(data.get(position));
        }else{
            billViewModel.handleIncomingDiscount(data.get(position));
        }

    }

    private void resetButtons(){
        for(int i = 0; i < data.size(); i++){
            data.get(i).setChecked(false);
        }
        notifyDataSetChanged();
    }

}

