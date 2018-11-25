package com.cbasolutions.cbapos.activity;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.Bindable;

import com.cbasolutions.cbapos.model.Item;

/**
 * Created by "Don" on 10/20/2017.
 * Class Functionality :-
 */

public class ProductViewModel extends BaseObservable {

    private Item item;
    private Context mContext;

    public ProductViewModel(Item item, Context mContext) {
        this.item = item;
        this.mContext = mContext;
    }

    @Bindable
    public String getTitle() {
        return item.getItem_name();
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }
}
