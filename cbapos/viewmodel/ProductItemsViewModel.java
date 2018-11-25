package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.widget.RelativeLayout;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.CRBaseObservable;

/**
 * Created by USER on 01-Nov-17.
 */

public class ProductItemsViewModel extends CRBaseObservable {

    public String homeText;
    public String productItemsText;
    public String transactionText;
    public String statisticsText;
    public String settingsText;

    Context mContext;

    public ProductItemsViewModel(Context mContext, String homeText, String productItemsText, String transactionText,
                                 String statisticsText, String settingsText) {
        this.homeText = homeText;
        this.productItemsText = productItemsText;
        this.transactionText = transactionText;
        this.statisticsText = statisticsText;
        this.settingsText = settingsText;
        this.mContext =  mContext;
    }

    public String getHomeText() {
        return homeText;
    }

    public void setHomeText(String homeText) {
        this.homeText = homeText;
        notifyPropertyChanged(R.id.homeText);
    }

    public String getProductItemsText() {
        return productItemsText;
    }

    public void setProductItemsText(String productItemsText) {
        this.productItemsText = productItemsText;
        notifyPropertyChanged(R.id.productItemsText);
    }

    public String getTransactionText() {
        return transactionText;
    }

    public void setTransactionText(String transactionText) {
        this.transactionText = transactionText;
        notifyPropertyChanged(R.id.transactionText);
    }

    public String getStatisticsText() {
        return statisticsText;
    }

    public void setStatisticsText(String statisticsText) {
        this.statisticsText = statisticsText;
        notifyPropertyChanged(R.id.statisticsText);
    }

    public String getSettingsText() {
        return settingsText;
    }

    public void setSettingsText(String settingsText) {
        this.settingsText = settingsText;
        notifyPropertyChanged(R.id.settingsText);
    }

}
