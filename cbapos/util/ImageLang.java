package com.cbasolutions.cbapos.util;

import android.content.Context;
import android.content.res.Configuration;
import android.widget.Button;
import android.widget.ImageView;

import com.cbasolutions.cbapos.R;

import java.util.Locale;

/**
 * Created by Dell on 2018-06-01.
 */

public class ImageLang  {

    public static void setImage(Context mContext,Button button, String key){

        int selectedLang = LangPrefs.getLanguage(mContext);

        if (selectedLang == LangPrefs.LAN_EN) {

            if(key.equalsIgnoreCase("newReceipt")){
                button.setBackgroundResource(R.drawable.btn_new_receipt_2);
            }

            if(key.equalsIgnoreCase("addItem")){
                button.setBackgroundResource(R.drawable.btn_add_item);
            }

            if(key.equalsIgnoreCase("addvariant")){
                button.setBackgroundResource(R.drawable.btn_add_variant);
            }
            if(key.equalsIgnoreCase("saveItem")){
                button.setBackgroundResource(R.drawable.btn_save_item);
            }
            if(key.equalsIgnoreCase("addCategory")){
                button.setBackgroundResource(R.drawable.save_category);
            }

        } else if (selectedLang == LangPrefs.LAN_SIN) {
            if(key.equalsIgnoreCase("newReceipt")){
                button.setBackgroundResource(R.drawable.btn_new_receipt_2_sinhala);
            }
            if(key.equalsIgnoreCase("addItem")){
                button.setBackgroundResource(R.drawable.btn_add_item_sinhala);
            }
            if(key.equalsIgnoreCase("addvariant")){
                button.setBackgroundResource(R.drawable.btn_add_variant_sinhala);
            }
            if(key.equalsIgnoreCase("saveItem")){
                button.setBackgroundResource(R.drawable.btn_save_item_sinhala);
            }
            if(key.equalsIgnoreCase("addCategory")){
                button.setBackgroundResource(R.drawable.save_category_sinhala);
            }


        } else if (selectedLang == LangPrefs.LAN_TA) {

            if(key.equalsIgnoreCase("newReceipt")){
                button.setBackgroundResource(R.drawable.btn_new_receipt_2_tamil);
            }
            if(key.equalsIgnoreCase("addItem")){
                button.setBackgroundResource(R.drawable.btn_add_item_tamil);
            }
            if(key.equalsIgnoreCase("addvariant")){
                button.setBackgroundResource(R.drawable.btn_add_variant_tamil);
            }

        }


    }

}
