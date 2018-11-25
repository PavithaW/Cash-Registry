package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.Bindable;
import android.graphics.Paint;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.SignInActivity;
import com.cbasolutions.cbapos.databinding.ForgetPasswordDataBinding;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.service.PayableService;

import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by USER on 25-Oct-17.
 */

public class ForgetPasswordViewModel extends CRBaseObservable {

    //hints of edittexts .
    private String emailHint;

    //Data of edittexts you require for data saving.
    private String userEmail;

    // Setting texts of back link and send button
    public String backButtonText;
    public String sendButtonText;

    private String errorEmail;
    private SharedPreferences forgotPasswordPreferences;
    private SharedPreferences.Editor forgotPrefsEditor;

    Context mContext;

    //  Constructor
    public ForgetPasswordViewModel(Context mContext, String emailHint, String sendButtonText, String backButtonText, ForgetPasswordDataBinding binding) {
        this.emailHint = emailHint;
        this.sendButtonText = sendButtonText;
        this.backButtonText = backButtonText;
        this.mContext =  mContext;

        forgotPasswordPreferences = mContext.getSharedPreferences("forgotPasswordPrefs", mContext.MODE_PRIVATE);
        forgotPrefsEditor = forgotPasswordPreferences.edit();

        binding.backButton.setPaintFlags(binding.backButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
    }

    public String getEmailHint() {
        return emailHint;
    }

    public void setEmailHint(String emailHint) {
        this.emailHint = emailHint;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
        notifyPropertyChanged(R.id.emailAddressEditText);
    }

    public String getSendButtonText() {
        return sendButtonText;
    }

    public void setSendButtonText(String sendButtonText) {
        this.sendButtonText = sendButtonText;
    }

    public String getBackButtonText() {
        return backButtonText;
    }

    public void setBackButtonText(String backButtonText) {
        this.backButtonText = backButtonText;
    }

    public static boolean isValidEmail(final String userEmail) {
        Pattern pattern;
        Matcher matcher;
        // final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = android.util.Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }

    @Bindable
    public String getErrorEmail() {
        return null;
    }

    public void validateInput(){
        if(getUserEmail() == null){
            showHintDialog(mContext, mContext.getString(R.string.emailMissing), mContext.getString(R.string.alert));
            return;
        }

        if(!isValidEmail(getUserEmail())){
            showHintDialog(mContext, mContext.getString(R.string.emailWrong), mContext.getString(R.string.alert));
            return;
        }

//        Toast.makeText(mContext, Config.FORGOT_PW_EMAIL_SENT, Toast.LENGTH_SHORT).show();
//        Intent intent = new Intent(mContext, SignInActivity.class);
//        mContext.startActivity(intent);

        if (checkInternet(mContext)) {
            showProgressDialog(mContext, mContext.getResources().getString(R.string.pleaseWait));
            PayableService.forgetPassword(mContext, ForgetPasswordViewModel.this, getUserEmail());
        } else {
            showHintDialog(mContext, mContext.getString(R.string.no_internet), mContext.getString(R.string.alert));
        }
    }

    public void didReceiveForgotPasswordResult(int responseCode, JSONObject response,String message) {


        if(responseCode == Config.OK) {
            String error = null;
            try {
                if(response.get("error") != null) {
                    error = response.get("error").toString();
                    if (error.equalsIgnoreCase("no user")) {
                        Toast.makeText(mContext,mContext.getString(R.string.emailWrong), Toast.LENGTH_LONG).show();

                    }
                }
            } catch (Exception e) {
                //e.printStackTrace();
                //saveAuth(response);
                Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
                Intent intent = new Intent(mContext, SignInActivity.class);
                mContext.startActivity(intent);
            }

            hideProgressDialog();
        }
    }

    public void back() {
        Intent intent = new Intent(mContext, SignInActivity.class);
        mContext.startActivity(intent);
    }

//    public void saveAuth(JSONObject response){
//        try {
//            String emailAddress = response.getString("email");
//            String auth = response.getString("authcode");
//
//            try {
//                forgotPrefsEditor.putString("forgotPswdEmail", emailAddress);
//                forgotPrefsEditor.putString("auth",auth);
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//
//    }

}
