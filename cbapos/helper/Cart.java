package com.cbasolutions.cbapos.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.math.RoundingMode;

/**
 * Created by "Don" on 11/8/2017.
 * Class Functionality :-
 * This class handles the shopping cart of Items
 * It uses a array list of Item Objects
 */

public class Cart extends BaseActivity {

    private double discount = 0.0;
    public static double totalBill;
    public static double billValue;
    public static int itemCount;
    public static ArrayList<Item> arrayList = new ArrayList<>();
    public ArrayList<Item> itemArrayList = new ArrayList<>();

    //add item and handle adding and replacing discounts
    public void addItemToCart(final Item item, Context context, final BillViewModel billVMContext) {
        boolean discountFound = false;
        //there is already a discount, ask user if he wants to replace, send the discounted item to 0th location
        if(item.getItemType().equals(Config.DISCOUNT)){//if the item to be added is a discount
            for(int i = 0; i < itemArrayList.size(); i++){//loop the list until an existing discount is found
                if(itemArrayList.get(i).getItemType().equals(Config.DISCOUNT)){
                    discountFound = true;
                    final int finalI = i;
                    //if there is already a discount, show an alert asking if user wants to replace.
                    new AlertDialog.Builder(context)
                            .setTitle(context.getString(R.string.alert))
                            .setMessage(context.getString(R.string.existingDiscount))
                            .setPositiveButton(context.getString(R.string.no), null)
                            .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    //on yes click, update the position with the new obj
                                    updateDiscountValue(item);
                                    itemArrayList.set(finalI, item);
                                    billVMContext.adapter.notifyDataSetChanged();
                                    billVMContext.updateItemCountAndBill();
                                }
                            }).show();
                }
            }
            //add  the initial discount to the list
            if(!discountFound){
                updateDiscountValue(item);
                itemArrayList.add(item);
            }
        }else{
            //add all items except discounts
            itemArrayList.add(item);
            //after adding an item, update the discount value if exists
            for(int i = 0; i < itemArrayList.size(); i++) {//loop the list until an existing discount is found
                if (itemArrayList.get(i).getItemType().equals(Config.DISCOUNT)) {
                    itemArrayList.set(i, updateDiscountValue(itemArrayList.get(i)));
                }
            }
        }

        arrayList = itemArrayList;
    }

    public Item updateDiscountValue(Item item){

        discount = 0.0;

        if (item.getItemDiscount().getDiscount_type().equals(Config.DISCOUNT_TYPE_1)) { //%
            discount = (getAllItemsBill() * (item.getItemDiscount().getDiscount_value() / 100));
        } else { //Rs.

            discount = item.getItemDiscount().getDiscount_value();
        }

        item.setItemTotal(discount+"");
        return item;
    }


    public void deleteItemFromCart(int position, Item item) {
        itemArrayList.remove(position);

        if (item.getItemType().equals(Config.DISCOUNT)) {
            discount = 0.0;
        } else {
            for (int i = 0; i < itemArrayList.size(); i++) {
                if (itemArrayList.get(i).getItemType().equals(Config.DISCOUNT)) {
                    updateDiscountValue(itemArrayList.get(i));
                }
            }
        }
        arrayList = itemArrayList;
    }

    public int getItemCount() {
        int count = 0;

        for (int i = 0; i < itemArrayList.size(); i++) {
            if (itemArrayList.get(i).getItemType().equals(Config.ITEM) ||itemArrayList.get(i).getItemType().equals(Config.CUSTOM)) {
                count++;
            }
        }
        itemCount = count;

        return count;
    }

    public void clearItemsInCart() {
        itemArrayList.clear();
        discount = 0;
    }

    public double getAllItemsBill() {
        billValue = 0.00;

        for (int i = 0; i < itemArrayList.size(); i++) {

            if(!itemArrayList.get(i).getItemType().equals(Config.DISCOUNT)){

                billValue = billValue + Double.parseDouble(String.format("%.2f",itemArrayList.get(i).getItemTotal()));
                Math.round(billValue);
            }
        }
        if(billValue == 0){
            totalBill = 0.00;
        }else {
            totalBill = billValue - discount;
        }
        Math.round(totalBill);

        //return billValue - discount;
        return totalBill;
    }

}
