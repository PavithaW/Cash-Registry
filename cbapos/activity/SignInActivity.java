package com.cbasolutions.cbapos.activity;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.WindowManager;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.LoginDataBinding;
import com.cbasolutions.cbapos.helper.NetworkChangeReceiver;
import com.cbasolutions.cbapos.helper.SignInClickListner;
import com.cbasolutions.cbapos.viewmodel.SignInViewModel;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        final LoginDataBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_sign_in);

        // Set Value Of XML properties PROGRAMMATICALY
        SignInViewModel viewModel = new SignInViewModel(SignInActivity.this, this.getString(R.string.email_address_hint),
                this.getString(R.string.password_hint),this.getString(R.string.sign_in_forgot_password),this.getString(R.string.sign_in_txt), binding,this);

        binding.setLogin(viewModel);


        //View Is Bound with ViewModel here Lets proceed with getting Data and Validations

        binding.setModelClickListener(new SignInClickListner() {
            @Override
            public void onClick() {

                binding.getLogin().validateInput();

                // On Click It will display all the values
            }
        });

        binding.setForgetPasswordClickListener(new SignInClickListner() {
            @Override
            public void onClick() {
                binding.getLogin().forgetPassword();
            }
        });

        binding.setSignUpClickListener(new SignInClickListner() {
            @Override
            public void onClick() {

                binding.getLogin().signUp();
                binding.PasswordeditText.setText("");
                binding.EmaileditText.setText("");
            }
        });

        binding.PasswordeditText.setFilters(new InputFilter[] {
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

//    @Override
//    protected void onStop()
//    {
//        if(NetworkChangeReceiver.receiver != null)
//            unregisterReceiver(NetworkChangeReceiver.receiver);
//        super.onStop();
//    }


}
