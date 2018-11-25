package com.cbasolutions.cbapos.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.SignUpDataBinding2;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.helper.SignUpClickListner2;
import com.cbasolutions.cbapos.viewmodel.SignUpViewModel2;

/**
 * Created by USER on 24-Oct-17.
 */

public class SignUpProcessTwoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_process_two);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final SignUpDataBinding2 binding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up_process_two);

        SignUpViewModel2 viewModel = new SignUpViewModel2(SignUpProcessTwoActivity.this, getIntent().getStringExtra("email"), getIntent().getStringExtra("password"));

        binding.setSignUp(viewModel);

        binding.setModelClickListener(new SignUpClickListner2() {
            @Override
            public void onClick() {

                binding.getSignUp().validateInput();
                // On Click It will display all the values
            }
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, Config.CITIES);
        binding.addressCityEditText.setAdapter(adapter);


    }

}
