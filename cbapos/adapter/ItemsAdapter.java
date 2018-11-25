package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.util.Util;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by "Don" on 10/18/2017.
 * Class Functionality :-
 * Populates all products / items in a grid view
 */

public class ItemsAdapter extends BaseAdapter {

    List<Item> data;
    Context context;
    List<Item> searchList = new ArrayList<Item>();
    boolean editItem;

    public ItemsAdapter(List<Item> data, Context c, boolean editItem) {
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

        final Item item = data.get(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.product_grid_cell, null);

        ImageView image = (ImageView) v.findViewById(R.id.imageView);
        TextView name = (TextView) v.findViewById(R.id.item_name);
        TextView price = (TextView) v.findViewById(R.id.item_price);
        Button edit = (Button) v.findViewById(R.id.edit_item);
        Button edit1 = (Button) v.findViewById(R.id.edit_item1);
        //LinearLayout layout = v.findViewById(R.id.prod_item);

        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setSingleLine();

        if(editItem){
            edit.setVisibility(View.VISIBLE);
            edit1.setVisibility(View.VISIBLE);
        }else{
            edit.setVisibility(View.GONE);
            edit1.setVisibility(View.GONE);
        }

        LinearLayout noImageLay = (LinearLayout) v.findViewById(R.id.no_img_lay);
        LinearLayout colorLay = (LinearLayout) v.findViewById(R.id.color_lay);
        TextView noImageLayItemName = (TextView) v.findViewById(R.id.no_img_item_name);

        name.setText(item.getItem_name());

        if (item.getPriceVariations().get(0) != null) {
            if (!item.getPriceVariations().get(0).getIsVariable()) {
                price.setText("Rs " + String.format("%.2f", item.getPriceVariations().get(0).getPrice()));
            }else{
                price.setText(context.getString(R.string.variable));
            }

        }

        noImageLayItemName.setEllipsize(TextUtils.TruncateAt.END);
        noImageLayItemName.setHorizontallyScrolling(false);
        noImageLayItemName.setSingleLine();


        //set product image
        if(item.getImage() != null){
            image.setVisibility(View.VISIBLE);
            noImageLay.setVisibility(View.GONE);
            name.setVisibility(View.VISIBLE);
            AQuery aq = new AQuery(context);
            Bitmap myBitmap = null;
            try {

                byte [] encodeByte= Base64.decode(item.getImage(),Base64.DEFAULT);
                myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                Bitmap roundBitmap = Util.getRoundedCornerBitmap(myBitmap, 700);
                if(roundBitmap != null)
                    aq.id(image).image(myBitmap);

            } catch (OutOfMemoryError ooM) {

            }

        }else{
            image.setVisibility(View.GONE);
            noImageLay.setVisibility(View.VISIBLE);
            name.setVisibility(View.INVISIBLE);
            noImageLayItemName.setText(item.getItem_name());

            switch (item.getColor()) {
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

        }

        log.d("AAA inside item adapter: ","Item "+ item.getItem_name());

        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Gson gson = new Gson();
                //reconstruct variation list from linked hash map
                //Convert variations linked list to arraylist using GSON
                for(int i = 0; i < item.getPriceVariations().size(); i++){
                    try {

                        String pVariation = item.getPriceVariations().get(i) + "";
                        JSONObject priceJobj = new JSONObject(pVariation);

                        PriceVariation variation = gson.fromJson(String.valueOf(priceJobj), PriceVariation.class);
                        item.getPriceVariations().set(i, variation);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                if (!Config.dialogIsShowing) {
                    Config.dialogIsShowing = true;
                    ((BaseActivity)context).showAddItemPopUp(item);
                }
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
            for (Item wp : searchList) {
                if (wp.getItem_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
                }
            }
        }

        notifyDataSetChanged();

        if (data.size() == 0 && ItemsViewModel.visibleView == 1 &&  Config.issearching) {
            Toast.makeText(context, "No matches found out for this item", Toast.LENGTH_SHORT).show();
            return;
        }
    }

}

