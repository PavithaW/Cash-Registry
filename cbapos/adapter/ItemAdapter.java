package com.cbasolutions.cbapos.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.ORMTblItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by USER on 07-Nov-17.
 */

public class ItemAdapter extends BaseAdapter {

    ArrayList<ORMTblItem> items;
    ArrayList<Boolean> checked;

    public Activity context;

    public ItemAdapter(Activity context,ArrayList<ORMTblItem> items,ArrayList<Boolean> checked) {
        super();
        this.context = context;
        this.items = items;
        this.checked = checked;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder {

        TextView txtName, txtCategory;
        CheckBox checkbox;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater mInflater = (LayoutInflater) context
                .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.category_item_listdata, null);
            holder = new ViewHolder();

            holder.txtName = (TextView) convertView.findViewById(R.id.txtName);

            holder.txtCategory = (TextView) convertView.findViewById(R.id.txtCategory);

            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox);

            convertView.setTag(holder);

        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        ORMTblItem bean = items.get(position);

        holder.txtName.setText(bean.getItem_name());

        holder.txtCategory.setText(bean.getCategory().getCategory());

        holder.checkbox.setChecked(checked.get(position));

        return convertView;
    }

}
