package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.activity.SignInActivity;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.service.PayableService;
import com.google.gson.Gson;
import com.google.gson.JsonParser;

import org.json.JSONObject;

/**
 * Created by USER on 24-Oct-17.
 */

public class SignUpViewModel2 extends CRBaseObservable {

    //All the hints of edittexts .
    private String addressHint;
    private String addressHint1;
    private String addressCityHint;
    private String mobileNoHint;
    public String landNoHint;

    //Data of edittexts you require for data saving.
    private String address;
    private String address1;
    private String city;
    private String mobileNo;
    private String landNo;

    // Setting texts next button
    public String signUpButtonText;

    // Variables holding Errors for Runtime Validation
    private String errorAddress;
    private String errorMobileNo;
    private String errorLandNo;
    private String email;
    private String password;
    //Their Getters are also Necessary To get RunTime Validation Errors

    Context mContext;

    public SignUpViewModel2(Context mContext, String email, String password) {
        this.addressHint = mContext.getString(R.string.businessAddress);
        this.mobileNoHint = mContext.getString(R.string.mobileNumber);
        this.landNoHint = mContext.getString(R.string.businessname);
        this.signUpButtonText = mContext.getString(R.string.sign_up);
        this.addressHint1 = mContext.getString(R.string.businessAddress1);
        this.addressCityHint = mContext.getString(R.string.businessAddressCity);
        this.mContext =  mContext;
        this.email = email;
        this.password = password;
    }

    public String getAddressHint() {
        return addressHint;
    }

    public void setAddressHint(String addressHint) {
        this.addressHint = addressHint;
    }

    public String getAddressCityHint() {
        return addressCityHint;
    }

    public void setAddressCityHint(String addressCityHint) {
        this.addressCityHint = addressCityHint;
    }

    public String getAddressHint1() {
        return addressHint1;
    }

    public void setAddressHint1(String addressHint1) {
        this.addressHint1 = addressHint1;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        /** To get value of edittext enterd by user, This Updates the value of userEmail on Every LEtter Entered by User*/
        notifyPropertyChanged(R.id.addressEditText);
        /**to check Email for validation on every character inserted by user*/
        //Similarly This
        //notifyPropertyChanged(BR.errorEmail);
    }

    public String getMobileNoHint() {
        return mobileNoHint;
    }

    public void setMobileNoHint(String mobileNoHint) {
        this.mobileNoHint = mobileNoHint;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
        notifyPropertyChanged(R.id.mobileNoEditText);
}

    public String getLandNoHint() {
        return landNoHint;
    }

    public void setLandNoHint(String landNoHint) {
        this.landNoHint = landNoHint;
    }

    public String getLandNo() {
        return landNo;
    }

    public void setLandNo(String landNo) {
        this.landNo = landNo;
        notifyPropertyChanged(R.id.landNoEditText);
    }

    public String getSignUpButtonText() {
        return signUpButtonText;
    }

    public void setSignUpButtonText(String signUpButtonText) {
        this.signUpButtonText = signUpButtonText;
    }

    public void validateInput(){

        if(getLandNo() == null){//IS BUSINESS NAME
            showHintDialog(mContext, mContext.getString(R.string.businessNameMissing), mContext.getString(R.string.alert));
            return;
        }

        if(getAddress() == null){
            showHintDialog(mContext, mContext.getString(R.string.addressMissing), mContext.getString(R.string.alert));
            return;
        }
        if(getCity() == null){
            showHintDialog(mContext, mContext.getString(R.string.cityMissing), mContext.getString(R.string.alert));
            return;
        }

        if (getMobileNo() == null) {
            showHintDialog(mContext, mContext.getString(R.string.mobileMissing), mContext.getString(R.string.alert));
            return;
        }
        if (getMobileNo().length() != 10) {
            showHintDialog(mContext, mContext.getString(R.string.invalidMobileNumber), mContext.getString(R.string.alert));
            return;
        }
        if (!getMobileNo().matches("[0-9]+")) {
            showHintDialog(mContext,mContext.getString(R.string.invalidMobileNumber), mContext.getString(R.string.alert));
            return;
        }

        if (checkInternet(mContext)) {
            showProgressDialog(mContext, mContext.getResources().getString(R.string.pleaseWait));
            PayableService.signUpUserIn(mContext, SignUpViewModel2.this, email, password, getLandNo(),getAddress(), getMobileNo() );
            //PayableService.signUpUserIn(mContext, SignUpViewModel2.this, "a@mail.com", "abc123", "0222209876","dsds", "999999999" );
        } else {
            showHintDialog(mContext, mContext.getString(R.string.no_internet), mContext.getString(R.string.alert));
        }

    }

    public void didReceiveSIgnUpResults(int responseCode, JSONObject response) {

        if(responseCode == Config.OK) {
            try {
                if (response.getString("reason").equalsIgnoreCase("Already exists")) {
                    showHintDialog(mContext, mContext.getString(R.string.alreadyExistUser), mContext.getString(R.string.alert));
                }
                if(response.getString("reason").equalsIgnoreCase("Invalid role name")){
                    showHintDialog(mContext, mContext.getString(R.string.alreadyExistUser), mContext.getString(R.string.alert));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (response.getString("message").equalsIgnoreCase("Created")) {
                    Toast.makeText(mContext, mContext.getString(R.string.signUpSuccess), Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(mContext, SignInActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    mContext.startActivity(intent);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        hideProgressDialog();
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
