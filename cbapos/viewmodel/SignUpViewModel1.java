package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.databinding.Bindable;
import android.text.InputFilter;
import android.text.Spanned;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.SignUpProcessTwoActivity;
import com.cbasolutions.cbapos.helper.Config;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by USER on 23-Oct-17.
 */

public class SignUpViewModel1 extends CRBaseObservable {

    //All the hints of edittexts .
    private String emailHint;
    private String confirmEmailHint;
    public String passwordHint;

    //Data of edittexts you require for data saving.
    private String userEmail;
    private String confirmEmail;
    private String userPassword;

    // Setting texts next button
    public String nextButtonText;

    // Variables holding Errors for Runtime Validation
    private String errorEmail;
    private String errorConfirmEmail;
    private String errorPassword;
    //Their Getters are also Necessary To get RunTime Validation Errors

    Context mContext;

    public SignUpViewModel1(Context mContext, String emailHint, String confirmEmailHint, String passwordHint, String nextButtonText) {
        this.emailHint = emailHint;
        this.confirmEmailHint = confirmEmailHint;
        this.passwordHint = passwordHint;
        this.nextButtonText = nextButtonText;
        this.mContext =  mContext;
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
        this.userEmail = userEmail.trim();
        /** To get value of edittext enterd by user, This Updates the value of userEmail on Every LEtter Entered by User*/
        notifyPropertyChanged(R.id.emailEditText);
    }

    public String getConfirmEmailHint() {
        return confirmEmailHint;
    }

    public void setConfirmEmailHint(String confirmEmailHint) {
        this.confirmEmailHint = confirmEmailHint;
    }

    public String getConfirmEmail() {
        return confirmEmail;
    }

    public void setConfirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail.trim();
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
    }

    public String getNextButtonText() {
        return nextButtonText;
    }

    public void setNextButtonText(String loginButtonText) {
        this.nextButtonText = loginButtonText;
    }



    @Bindable
    public String getErrorPassword() {
        return null;
    }



    // If you Dont Bind Here You Wont get BR values
    @Bindable
    public String getErrorEmail() {
        return null;
    }

    @Bindable
    public String getErrorConfirmEmail() {
        return null;
    }

    public void validateInput(){
        if(getUserEmail() == null){
            showHintDialog(mContext, mContext.getString(R.string.emailMissing), mContext.getString(R.string.alert));
            return;
        }

        if(!isValidEmail(getUserEmail().trim())){
            showHintDialog(mContext, mContext.getString(R.string.emailWrong), mContext.getString(R.string.alert));
            return;
        }

        if (getConfirmEmail() == null) {
            showHintDialog(mContext, mContext.getString(R.string.confirmEmail), mContext.getString(R.string.alert));
            return;
        }

        if (!isValidEmail(getConfirmEmail())) {
            showHintDialog(mContext, mContext.getString(R.string.confirmEmailWrong),mContext.getString(R.string.alert));
            return;
        }

        if (!getUserEmail().equals(getConfirmEmail())) {
            showHintDialog(mContext, mContext.getString(R.string.emailNotMatching), mContext.getString(R.string.alert));
            return;
        }

        if(getUserPassword() == null){
            showHintDialog(mContext, mContext.getString(R.string.passwordMissing), mContext.getString(R.string.alert));
            return;
        }
        if(getUserPassword().length() < 8){
            showHintDialog(mContext, mContext.getString(R.string.invalidPasswordLength), mContext.getString(R.string.alert));
            return;
        }
        if (getUserPassword().matches("[0-9]+")) {
            showHintDialog(mContext,mContext.getString(R.string.passwordValidation), mContext.getString(R.string.alert));
            return;
        }
        if (getUserPassword().matches("[a-zA-Z]+")) {
            showHintDialog(mContext,mContext.getString(R.string.passwordValidation), mContext.getString(R.string.alert));
            return;
        }

        Intent intent = new Intent(mContext, SignUpProcessTwoActivity.class);
        intent.putExtra("email", getUserEmail());
        intent.putExtra("password", getUserPassword());
        mContext.startActivity(intent);

    }

}
