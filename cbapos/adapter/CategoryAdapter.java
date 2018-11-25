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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 08-Nov-17.
 */

public class CategoryAdapter extends BaseAdapter {

    List<Category> data;
    Context context;
    List<Category> searchList = new ArrayList<Category>();
    boolean editItem;

    public CategoryAdapter(List<Category> data, Context c, boolean editItem) {
        Log.d("CATADAPTER" , "inside cons - 1");
        Log.d("----------------","");
        this.data = data;
        this.context = c;
        this.searchList.addAll(this.data);
        this.editItem = editItem;
        ///
    }

    @Override
    public int getCount() {
        Log.d("CATADAPTER size: ",""+ data.size());
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
        final Category item = data.get(position);

        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = vi.inflate(R.layout.category_grid_cell, null);

        TextView name = (TextView) v.findViewById(R.id.cat_name);
        TextView qty = (TextView) v.findViewById(R.id.cat_qty);
        LinearLayout colorLay = (LinearLayout) v.findViewById(R.id.color_lay);
        Button edit = (Button) v.findViewById(R.id.edit_item);
        Button edit1 = (Button) v.findViewById(R.id.edit_item1);
        //LinearLayout layout = v.findViewById(R.id.edit_item_Layout);
        Log.d("CATADAPTER1","Cat Item NAme "+ item.getCategory_name());
        if(editItem){
            edit.setVisibility(View.VISIBLE);
            edit1.setVisibility(View.VISIBLE);
        }else{
            edit.setVisibility(View.GONE);
            edit1.setVisibility(View.GONE);
        }

        name.setText(item.getCategory_name());
        qty.setText(String.valueOf(item.getItemCount()));
        name.setEllipsize(TextUtils.TruncateAt.END);
        name.setHorizontallyScrolling(false);
        name.setSingleLine();
        switch (item.getCatColor()) {
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
                    ((BaseActivity)context).showAddCategoryPopUp(item);
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
            for (Category wp : searchList) {
                if (wp.getCategory_name().toLowerCase(Locale.getDefault()).contains(charText)) {
                    data.add(wp);
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
