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
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 08-Nov-17.
 */

public class CategoryAdapter1 extends BaseAdapter {

    List<String> data;
    Context context;
    List<String> searchList = new ArrayList<String>();
    boolean editItem;
    DBManager manager;

    public CategoryAdapter1(List<String> data, Context c, boolean editItem) {

        this.data = data;
        this.context = c;
        this.searchList.addAll(this.data);
        this.editItem = editItem;
        manager = new DBManager();
        ///
    }

    @Override
    public int getCount() {

        return data.size();
    }

    @Override
    public Object getItem(int position) {
        Gson gson = new Gson();
        final Document document = manager.returnDB(context).getExistingDocument(data.get(position));
        final Category category = new Category();
        try{
            category.setCategory_name(document.getProperty("category_name").toString());

            if (document.getProperty("color") != null) {
                category.setCatColor((Integer) document.getProperty("color"));
            }

            if (document.getProperty("category_id") != null) {
                category.setCategory_id(document.getProperty("category_id").toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return category;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        final String catId = data.get(position);
        final Document document = manager.returnDB(context).getExistingDocument(catId);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.category_grid_cell, null);

        TextView name = (TextView) v.findViewById(R.id.cat_name);
        TextView qty = (TextView) v.findViewById(R.id.cat_qty);
        LinearLayout colorLay = (LinearLayout) v.findViewById(R.id.color_lay);
        Button edit = (Button) v.findViewById(R.id.edit_item);
        Button edit1 = (Button) v.findViewById(R.id.edit_item1);

        if(editItem){
            edit.setVisibility(View.VISIBLE);
            edit1.setVisibility(View.VISIBLE);
        }else{
            edit.setVisibility(View.GONE);
            edit1.setVisibility(View.GONE);
        }

        name.setText(document.getProperty("category_name").toString());
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setSingleLine();

        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        int itemCount = manager.getItemCountByCategoryName(context, query, document.getProperty("category_name").toString());
        qty.setText(String.valueOf(itemCount));

        switch ((Integer) document.getProperty("color")) {

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


        edit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.catDialogIsShowing) {
                    Config.catDialogIsShowing = true;

                    Gson gson = new Gson();
                    final Category category = new Category();
                    try{
                        category.setCategory_name(document.getProperty("category_name").toString());

                        if (document.getProperty("color") != null) {
                            category.setCatColor((Integer) document.getProperty("color"));
                        }

                        if (document.getProperty("category_id") != null) {
                            category.setCategory_id(document.getProperty("category_id").toString());
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    ((BaseActivity)context).showAddCategoryPopUp(category);
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
            for (String catId  : searchList) {
                final Document document = manager.returnDB(context).getExistingDocument(catId);
                if (document.getProperty("category_name").toString().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(catId);
                }
            }
        }
        notifyDataSetChanged();

        Config.VISIBLE_VIEW = 2;

        if (data.size() == 0 && Config.VISIBLE_VIEW == 2) {
            Toast.makeText(context, context.getString(R.string.noMatchesFoundCat), Toast.LENGTH_SHORT).show();
            return;
        }
    }

}
