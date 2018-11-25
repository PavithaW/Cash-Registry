package com.cbasolutions.cbapos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cbasolutions.cbapos.model.Category;

import java.util.List;

/**
 * Created by "Don" on 11/17/2017.
 * Class Functionality :-
 */

public class CategorySpinnerAdapter extends ArrayAdapter<Category> {

    List<Category> values;
    private Context context;


    public CategorySpinnerAdapter(Context context, int textViewResourceId,  List<Category> values) {
        super(context, textViewResourceId, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public int getCount(){
        return values.size();
    }

    @Override
    public Category getItem(int position){
        return values.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }


    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        if (position == 0) {
            label.setTextColor(Color.parseColor("#808080"));
        } else {
            label.setTextColor(Color.parseColor("#262948"));
        }

        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        label.setText(values.get(position).getCategory_name());

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(100,20,20,20);
        label.setLayoutParams(params);

        if(position == 0){
            params = new LinearLayout.LayoutParams(0,0);
            params.setMargins(0,0,0,0);
            label.setLayoutParams(params);
        }else{
            label.setPadding(5, 20, 5, 20);
        }

        label.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        label.setBackgroundColor(Color.parseColor("#FFFFFF"));
        label.setTextColor(Color.BLACK);
        label.setTextSize(14);
        label.setGravity(Gravity.LEFT | Gravity.BOTTOM);

        if (position == 0) {

        } else {
            label.setText(values.get(position).getCategory_name());
        }

        return label;
    }
}