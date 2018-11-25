package com.cbasolutions.cbapos.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.WindowManager;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.SignUpDataBinding;
import com.cbasolutions.cbapos.helper.SignUpClickListner1;
import com.cbasolutions.cbapos.viewmodel.SignUpViewModel1;

/**
 * Created by USER on 23-Oct-17.
 */

public class SignUpProcessOneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process_one);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final SignUpDataBinding binding= DataBindingUtil.setContentView(this, R.layout.activity_sign_up_process_one);

        SignUpViewModel1 viewModel = new SignUpViewModel1(SignUpProcessOneActivity.this, this.getString(R.string.sign_up_email_hint),
                this.getString(R.string.sign_up_confirm_email_hint),this.getString(R.string.sign_up_password_hint), this.getString(R.string.next_button_text));

        binding.setSignUp(viewModel);

        binding.setModelClickListener(new SignUpClickListner1() {
            @Override
            public void onClick() {

                binding.getSignUp().validateInput();
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.passwordEditText.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z0-9]+")){ // here no space character
                            return cs;
                        }
                        return "";
                    }
                }
        });
    }

}
