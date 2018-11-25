package com.cbasolutions.cbapos.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.databinding.BaseObservable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Discount;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by "Don" on 10/20/2017.
 * Class Functionality :-
 */

public class CRBaseObservable extends BaseObservable {

    AlertDialog dialogBuilder;
    ProgressDialog dialog;


    /**
     * initializes the progress bar
     *
     * @param context
     */
    protected void progressDialog(Context context) {

        if (dialog == null) {
            dialog = new ProgressDialog(context);
            dialog.setCancelable(false);
            dialog.setMessage("Please Wait...");
            dialog.setCancelable(false);
        }
    }

    /**
     * hides the progress bar
     */
    protected void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    /**
     * shows the progress bar
     *
     * @param context
     * @param message
     */
    protected void showProgressDialog(Context context, String message) {

        if (dialog == null) {
            progressDialog(context);
        }
        if (!dialog.isShowing()) {
            hideProgressDialog();
            dialog.setMessage(message);
        }
        dialog.show();

    }

    /**
     * check whether the internet is available or not
     *
     * @param context
     * @return
     */
    public static boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Config.isOnline = true;
            // Log.v("INTERNET", "Available");
            return true;
        }
        return false;
    }

    /**
     * shows messages in a dialog
     *
     * @param context
     * @param message
     * @param title
     */
    protected final void showHintDialog(Context context, String message, String title) {
        dialogBuilder = new AlertDialog.Builder(context).setMessage(message).setTitle(title)
                .setPositiveButton("Done", null).create();
        if(!dialogBuilder.isShowing()) {
            dialogBuilder.show();
        }

    }
    public static double actualTotal = 0;

    public String getTotal(Context context, long qty, double price, Discount discount){

        double totalPrice = 0;
        double selectedPrice = price;

        if(qty == 0){
            return totalPrice+"";
        }
        if(selectedPrice == 0.0){
            return totalPrice+"";
        }

        totalPrice = qty * selectedPrice;

        if(discount != null){
            double discounted = 0.0;
            if(discount.getDiscount_type().equals(Config.DISCOUNT_TYPE_1)){
                 discounted = (totalPrice * (discount.getDiscount_value()/100));
            }else {
                 discounted = discount.getDiscount_value();
            }

            if(discounted > totalPrice){
                showHintDialog(context, context.getString(R.string.discountHigherTotal), context.getString(R.string.alert));
                return  totalPrice+"";
            }

            actualTotal = totalPrice;

            totalPrice = totalPrice - discounted;

        }

        return String.format("%.2f", totalPrice);

    }



    public double getActualTotal(){

       return actualTotal;
    }

    //function to get the tab type
    public int  getTab(FragmentActivity context){
        int tab = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth > 720) {
            //Device is a 10" tablet
            tab = 10;
        }else if (smallestWidth > 600) {
            //Device is a 7" tablet
            tab = 7;
        }else{
            tab = 7;
        }

        return  tab;
    }

    public static boolean isValidEmail(final String userEmail) {
        Pattern pattern;
        Matcher matcher;
        // final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = android.util.Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }


}
