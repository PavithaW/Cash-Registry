package com.cbasolutions.cbapos.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.ForgetPasswordDataBinding;
import com.cbasolutions.cbapos.helper.ForgetPasswordClickListner;
import com.cbasolutions.cbapos.viewmodel.ForgetPasswordViewModel;

/**
 * Created by USER on 25-Oct-17.
 */

public class ForgetPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final ForgetPasswordDataBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_forget_password);

        ForgetPasswordViewModel viewModel = new ForgetPasswordViewModel(ForgetPasswordActivity.this, this.getString(R.string.forget_password_email_hint),
                this.getString(R.string.send_button_txt),this.getString(R.string.back_button_txt), binding);

        binding.setForgetPassword(viewModel);

        binding.setModelClickListener(new ForgetPasswordClickListner() {
            @Override
            public void onClick() {
                binding.getForgetPassword().validateInput();
            }
        });

        binding.setBackClickListener(new ForgetPasswordClickListner() {
            @Override
            public void onClick() {
                binding.getForgetPassword().back();
            }
        });
    }
}