package com.cbasolutions.cbapos.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.Bindable;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.ForgetPasswordActivity;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.activity.SignUpProcessOneActivity;
import com.cbasolutions.cbapos.databinding.LoginDataBinding;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.service.PayableService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by "Don" on 10/20/2017.
 * Class Functionality :-
 */

public class SignInViewModel extends CRBaseObservable {

    //All the hints of edittexts .

    public String passwordHint;
    private String emailHint;

    //Data of edittexts you require for data saving.
    private String userEmail;
    private String userPassword;


    // Setting texts of forgot password link and

    public String forgotPasswordText;
    public String loginButtonText;


    // Variables holding Errors for Runtime Validation

    private String errorPassword;
    private String errorEmail;
    //Their Getters are also Necessary To get RunTime Validation Errors
    Activity mActivity;
    Context mContext;
    LoginDataBinding binding;

    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;


    //  Constructor

    public SignInViewModel(Context mContext, String emailHint, String passwordHint, String forgotPasswordText, String loginButtonText, LoginDataBinding binding,Activity act) {
        this.emailHint = emailHint;
        this.passwordHint = passwordHint;
        this.forgotPasswordText = forgotPasswordText;
        this.loginButtonText = loginButtonText;
        this.mContext =  mContext;
        this.binding = binding;
        this.mActivity = act;

        binding.forgotPasswordButton.setPaintFlags(binding.forgotPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        binding.signUpButton.setPaintFlags(binding.forgotPasswordButton.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        loginPreferences = mContext.getSharedPreferences("loginPrefs", mContext.MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

    }

    //getters and setters are necessary

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
        this.userEmail = userEmail.trim();
        /** To get value of edittext enterd by user, This Updates the value of userEmail on Every LEtter Entered by User*/
        notifyPropertyChanged(R.id.EmaileditText);
        /**to check Email for validation on every character inserted by user*/
        //Similarly This
        //notifyPropertyChanged(BR.errorEmail);
    }

    public String getPasswordHint() {
        return passwordHint;
    }

    public void setPasswordHint(String passwordHint) {
        this.passwordHint = passwordHint;
    }

    @Bindable
    public String getUserPassword() {
        return userPassword;

    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
        notifyPropertyChanged(R.id.PasswordeditText);

    }

    public String getForgotPasswordText() {
        return forgotPasswordText;
    }

    public void setForgotPasswordText(String forgotPasswordText) {
        this.forgotPasswordText = forgotPasswordText;
    }

    public String getLoginButtonText() {
        return loginButtonText;
    }

    public void setLoginButtonText(String loginButtonText) {
        this.loginButtonText = loginButtonText;
    }
// Now we have data , But is not validated, Lets do that

    /**
     * VALIDATIONS All Four Methods Required
     */


    @Bindable
    public String getErrorPassword() {
        return null;
    }

    //These Methods Check For Validation Every Time user enters a character


    // If you Dont Bind Here You Wont get BR values
    @Bindable
    public String getErrorEmail() {
        return null;
    }

    public void validateInput(){
        if(getUserEmail() == null || getUserEmail().equals("")){
            showHintDialog(mContext, mContext.getString(R.string.emailMissing), mContext.getString(R.string.alert));
            return;
        }

        if(!isValidEmail(getUserEmail().trim())){
            showHintDialog(mContext, mContext.getString(R.string.emailWrong), mContext.getString(R.string.alert));
            return;
        }

        if(getUserPassword() == null|| getUserPassword().equals("")){
            showHintDialog(mContext,  mContext.getString(R.string.passwordMissing), mContext.getString(R.string.alert));
            return;
        }

//        if(getUserPassword().length() < 8){
//            showHintDialog(mContext, mContext.getString(R.string.invalidPasswordLength), mContext.getString(R.string.alert));
//            return;
//        }
//        if (getUserPassword().matches("[0-9]+")) {
//            showHintDialog(mContext,mContext.getString(R.string.passwordValidation), mContext.getString(R.string.alert));
//            return;
//        }
//        if (getUserPassword().matches("[a-zA-Z]+")) {
//            showHintDialog(mContext,mContext.getString(R.string.passwordValidation), mContext.getString(R.string.alert));
//            return;
//        }

        if (checkInternet(mContext)) {
            showProgressDialog(mContext, mContext.getResources().getString(R.string.pleaseWait));
            PayableService.logUserIn(mContext, SignInViewModel.this, getUserEmail(), getUserPassword());
        } else {
            showHintDialog(mContext, mContext.getString(R.string.no_internet), mContext.getString(R.string.alert));
        }

    }

    public void forgetPassword() {
        Intent intent = new Intent(mContext, ForgetPasswordActivity.class);
        mContext.startActivity(intent);
    }

    public void signUp() {
        Intent intent = new Intent(mContext, SignUpProcessOneActivity.class);
        mContext.startActivity(intent);
    }

    public void didReceiveLoginResults(int code, JSONObject response) {

        if (code == Config.OK) {
            try {
                if (response.getString("error").equalsIgnoreCase("Unauthorized")) {
                    hideProgressDialog();
                    Toast.makeText(mContext, mContext.getString(R.string.inValidLogin), Toast.LENGTH_SHORT).show();
                    return;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            JSONObject data = null;
            try {
                //obj = response.getJSONArray("rows").getJSONObject(0).getJSONObject("value");
                String value = response.getString("storeData");
                Object sync = response.getJSONArray("sync_session").get(0);
                Object expireDate = response.getJSONArray("sync_session").get(2);
                String exDate = expireDate.toString();
                String[] separated = exDate.split("=");
                exDate = separated[1];

                String[] syncIdString = sync.toString().split("=");
                String syncId = syncIdString[1];


                String loginId = null;
                String appSecret = null;
                String channel = null;
                String storeAddress = null;
                String storeMobile = null;
                String storeName  = null;
                String storeId = null;
                String type = null;
                String userName = null;
                String owner = null;
                String key = null;


                try {
                    JSONObject jo = new JSONObject(value);
                    JSONObject test = jo.getJSONArray("rows").getJSONObject(0);
                    loginId = jo.getJSONArray("rows").getJSONObject(0).getString("id");
                    key = jo.getJSONArray("rows").getJSONObject(0).getString("key");
                    JSONArray value2 = jo.getJSONArray("rows").getJSONObject(0).getJSONArray("value");
                    data = value2.getJSONObject(1);

                    appSecret = data.getString("appsecret");
                    channel = data.getString("channels");
                    storeAddress = data.getString("storeAddress");
                    storeMobile = data.getString("storeMobile");
                    storeName = data.getString("storename");
                    storeId = data.getString("store_id");
                    type = data.getString("type");

                    if(data.getString("email") != null) {
                        userName = data.getString("email");
                    }
                    if(data.getString("username") != null) {
                        owner = data.getString("username");
                    }

                }catch (Exception e){
                    e.printStackTrace();

                }

                try {
                    loginPrefsEditor.putString("key", key);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    loginPrefsEditor.putString("loginId", loginId);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("syncId", syncId.toString());
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("expiryDate", exDate);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("storeMobile", storeMobile);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("storeAddress", storeAddress);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("storename", storeName);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("storeId", storeId);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("username", userName);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("owner", owner);
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    loginPrefsEditor.putString("appSecret", appSecret);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    loginPrefsEditor.putString("channel", channel);
                }catch (Exception e){
                    e.printStackTrace();
                }

                try {
                    loginPrefsEditor.putString("userType", type);
                }catch (Exception e){
                    e.printStackTrace();
                }

                loginPrefsEditor.commit();

                Toast.makeText(mContext, getUserEmail()+" " + mContext.getString(R.string.loginSuccess), Toast.LENGTH_SHORT).show();
                if(data == null){
                    showHintDialog(mContext, mContext.getString(R.string.storeDataEmpty), mContext.getString(R.string.alert));

                }else {

                    Application application = (Application) mActivity.getApplication();
                    application.login(syncId.toString(), mContext);
                    Application.isLoginScreenPassed = true;
                    mActivity.finish();
                }

//                Intent intent = new Intent(mContext, MainActivity.class);
//                mContext.startActivity(intent);

            } catch (JSONException e) {
                e.printStackTrace();

            }


        } else {
            Toast.makeText(mContext, Config.getErrorResponseMessage(code), Toast.LENGTH_SHORT).show();
        }

        hideProgressDialog();
    }


    public void unAuthorizeLogin(int code,String response){
        if(code == Config.OK){
            hideProgressDialog();
            Toast.makeText(mContext, getUserEmail() + mContext.getString(R.string.loginFail), Toast.LENGTH_SHORT).show();
        }
    }

    public void loginFail(int code,String response, Throwable throwable){
        if(code == 500){
            hideProgressDialog();
            Toast.makeText(mContext, getUserEmail() + throwable.toString(), Toast.LENGTH_SHORT).show();
        }
    }

/**
 * Now Your XML is bound with ViewModel
 * Lets Check now
 *
 * Before , we need click listener
 */


}
