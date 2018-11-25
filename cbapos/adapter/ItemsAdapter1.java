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
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.cbasolutions.cbapos.util.Util;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;
import com.couchbase.lite.Document;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by "Pavitha" on 2018-06-04.
 * Class Functionality :-
 * Populates all products / items in a grid view
 */

public class ItemsAdapter1 extends BaseAdapter {

    List<String> data;
    Context context;
    List<String> searchList = new ArrayList<String>();
    boolean editItem;
    DBManager manager;
    Gson gson;

    public ItemsAdapter1(List<String> data, Context c, boolean editItem) {
        this.data = data;
        this.context = c;
        this.searchList.addAll(this.data);
        this.editItem = editItem;
        manager = new DBManager();
        gson = new Gson();

    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        Gson gson = new Gson();
        final Document document = manager.returnDB(context).getExistingDocument(data.get(position));
        final Item item = new Item();
        try {
            item.setItem_name(document.getProperty("item_name").toString());
            item.setItem_id(document.getProperty("item_id").toString());

            if (document.getProperty("description") != null) {
                item.setDescription(document.getProperty("description").toString());
            }

            if (document.getProperty("category") != null) {
                try {
                    if (document.getProperty("category") instanceof Category) {
                        Category category = (Category) document.getProperty("category");
                        item.setCategory(category);
                    } else {
                        JSONObject catObj = new JSONObject(document.getProperty("category").toString());
                        item.setCategory(gson.fromJson(catObj.toString(), Category.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
            ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
            JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();
            for (int i = 0; i < jArray.size(); i++) {
                PriceVariation variant = gson.fromJson(jArray.get(i), PriceVariation.class);
                priceVariations.add(variant);
//                if(variant.getIsRegular()){
//                    item.setSku(variant.getSku());
//                }
            }


            item.setPriceVariations(priceVariations);
            item.setColor((Integer) document.getProperty("color"));

            if(document.getProperty("image") != null) {
                item.setImage(document.getProperty("image").toString());
            }

            //reconstruct variation list from linked hash map
            //Convert variations linked list to arraylist using GSON
            for (int i = 0; i < item.getPriceVariations().size(); i++) {
                try {

                    String pVariation = item.getPriceVariations().get(i) + "";
                    JSONObject priceJobj = new JSONObject(pVariation);

                    PriceVariation variation = gson.fromJson(String.valueOf(priceJobj), PriceVariation.class);
                    item.getPriceVariations().set(i, variation);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return item;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {

        final String itemId = data.get(position);
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.product_grid_cell, null);

        final Document document = manager.returnDB(context).getExistingDocument(itemId);

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

        try {

            String name1 = document.getProperty("item_name").toString();
            name.setText(name1);
            //price.setText(context.getString(R.string.variable));
            ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
            JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();


            final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
            for (int i = 0; i < jArray.size(); i++) {
                PriceVariation variant = gson.fromJson(jArray.get(i), PriceVariation.class);
                priceVariations.add(variant);
            }
            if (priceVariations.get(0) != null) {
                if (!priceVariations.get(0).getIsVariable()) {

                    price.setText("Rs " + String.format("%.2f", priceVariations.get(0).getPrice()));
            }else{
                    price.setText(context.getString(R.string.variable));
                }

            }

            noImageLayItemName.setEllipsize(TextUtils.TruncateAt.END);
            noImageLayItemName.setHorizontallyScrolling(false);
            noImageLayItemName.setSingleLine();


            //set product image
            if (document.getProperty("image") != null ) {
                image.setVisibility(View.VISIBLE);
                noImageLay.setVisibility(View.GONE);
                name.setVisibility(View.VISIBLE);
                AQuery aq = new AQuery(context);
                Bitmap myBitmap = null;
                try {

                    byte[] encodeByte = Base64.decode(document.getProperty("image").toString(), Base64.DEFAULT);
                    myBitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    Bitmap roundBitmap = Util.getRoundedCornerBitmap(myBitmap, 700);
                    if (roundBitmap != null)
                        aq.id(image).image(myBitmap);

                } catch (OutOfMemoryError ooM) {

                }

            } else {
                image.setVisibility(View.GONE);
                noImageLay.setVisibility(View.VISIBLE);
                name.setVisibility(View.INVISIBLE);
                noImageLayItemName.setText(document.getProperty("item_name").toString());

                switch ((Integer) document.getProperty("color")) {
                    case 0:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_1));
                        break;
                    case 1:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_2));
                        break;
                    case 2:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_3));
                        break;
                    case 3:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_4));
                        break;
                    case 4:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_5));
                        break;
                    case 5:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_6));
                        break;
                    default:
                        colorLay.setBackgroundColor(context.getResources().getColor(R.color.item_color_1));
                        break;
                }

            }


            edit1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Gson gson = new Gson();
                    final Item item = new Item();
                    item.setItem_name(document.getProperty("item_name").toString());
                    item.setItem_id(document.getProperty("item_id").toString());

                    if (document.getProperty("description") != null) {
                        item.setDescription(document.getProperty("description").toString());
                    }

//                    if (document.getProperty("sku") != null) {
//                        item.setSku(document.getProperty("sku").toString());
//                    }

                    if (document.getProperty("category") != null) {
                        try {
                            if (document.getProperty("category") instanceof Category) {
                                Category category = (Category) document.getProperty("category");
                                item.setCategory(category);
                            } else {
                                JSONObject catObj = new JSONObject(document.getProperty("category").toString());
                                item.setCategory(gson.fromJson(catObj.toString(), Category.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    item.setPriceVariations(priceVariations);
                    item.setColor((Integer) document.getProperty("color"));
                    if(document.getProperty("image") != null) {
                        item.setImage(document.getProperty("image").toString());
                    }

                    //reconstruct variation list from linked hash map
                    //Convert variations linked list to arraylist using GSON
                    for (int i = 0; i < item.getPriceVariations().size(); i++) {
                        try {

                            String pVariation = item.getPriceVariations().get(i) + "";
                            JSONObject priceJobj = new JSONObject(pVariation);

                            PriceVariation variation = gson.fromJson(String.valueOf(priceJobj), PriceVariation.class);
                            item.getPriceVariations().set(i, variation);

//                            if(variation.getIsRegular()){
//                                item.setSku(variation.getSku());
//                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (!Config.dialogIsShowing) {
                        Config.dialogIsShowing = true;
                        ((BaseActivity) context).showAddItemPopUp(item);
                    }
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }

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
            for (String itemId : searchList) {
                final Document document = manager.returnDB(context).getExistingDocument(itemId);
                if (document.getProperty("item_name").toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(itemId);
                }
            }
        }

        notifyDataSetChanged();

        if (data.size() == 0 && ItemsViewModel.visibleView == 1 &&  Config.issearching) {
            Toast.makeText(context, context.getString(R.string.noMatchesFoundItem), Toast.LENGTH_SHORT).show();
            return;
        }
    }

}

