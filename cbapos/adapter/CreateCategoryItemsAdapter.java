package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by "Don" on 11/20/2017.
 * Class Functionality :-
 */

public class CreateCategoryItemsAdapter extends BaseAdapter {

    List<Item> data;
    Context context;
    List<Item> searchList = new ArrayList<Item>();
    public List<Item> catSelectedItems = new ArrayList<Item>();
    public static boolean isSearched = false;

    public CreateCategoryItemsAdapter(List<Item> data, Context c) {
        this.data = data;
        this.context = c;

        //check if there are already selected items
        for(int i = 0; i < data.size(); i++){
            if (data.get(i).isChecked()){
                catSelectedItems.add(data.get(i));
            }
        }
        this.searchList.addAll(this.data);
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
        View v = vi.inflate(R.layout.category_item_listdata, null);

        TextView name = (TextView) v.findViewById(R.id.txtName);
        TextView catName = (TextView) v.findViewById(R.id.txtCategory);
        CheckBox selected = (CheckBox) v.findViewById(R.id.checkbox);

//        for(int i=0; i< catSelectedItems.size(); i++){
//            if(item.getItem_name().toString().equalsIgnoreCase(catSelectedItems.get(i).getItem_name().toString())){
//                selected.setChecked(true);
//            }
//
//        }

        if(item.isChecked()){
            selected.setChecked(true);
            catSelectedItems.add(item);

        }else{
            for(int i = 0; i < catSelectedItems.size(); i++){
                if(item.equals(catSelectedItems.get(i))){
                    catSelectedItems.remove(item);
                }
            }
            selected.setChecked(false);
        }

        selected.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                item.setChecked(isChecked);
                notifyDataSetChanged();

                //pavitha
//                if(selected.isChecked()){
//                    item.setChecked(true);
//                    catSelectedItems.add(item);
//
//                }else{
//                    item.setChecked(false);
//                    for(int i = 0; i < catSelectedItems.size(); i++){
//                        if(item.equals(catSelectedItems.get(i))){
//                            catSelectedItems.remove(item);
//                        }
//                    }
//                }

            }
        }
        );

        name.setText(item.getItem_name());

        if (item.getCategory() != null) {
            catName.setText(item.getCategory().getCategory_name());
        }


        return v;
    }

    //Filter dataset with searchview text
    public void filter(String charText) {
        isSearched = true;
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

        if (data.size() == 0) {
            Toast.makeText(context, context.getString(R.string.noMatchesFoundItem), Toast.LENGTH_SHORT).show();
            return;
        }
    }

}

