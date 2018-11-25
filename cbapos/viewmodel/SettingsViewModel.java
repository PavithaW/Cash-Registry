package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.activity.SignInActivity;
import com.cbasolutions.cbapos.activity.SplashScreen;
import com.cbasolutions.cbapos.adapter.PrintersAdapter;
import com.cbasolutions.cbapos.adapter.TransactionAdapter;
import com.cbasolutions.cbapos.databinding.SettingsLayoutBinding;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.util.LangPrefs;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.logging.Handler;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by USER on 28-Nov-17.
 */

public class SettingsViewModel extends CRBaseObservable {

    FragmentActivity activity;
    private Database mDatabase;
    private SharedPreferences loginPreferences;

    public SettingsViewModel(final FragmentActivity activity, final SettingsLayoutBinding binding) {
        this.activity = activity;
        loginPreferences = activity.getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String userName = loginPreferences.getString("username", null);
        if(userName != null) {
            binding.editText.setText(userName.toString());
        }

        int selectedLang = LangPrefs.getLanguage(activity);

        if (selectedLang == LangPrefs.LAN_EN) {
            binding.checkBox1.setChecked(true);
        } else if (selectedLang == LangPrefs.LAN_SIN) {
            binding.checkBox2.setChecked(true);
        } else if (selectedLang == LangPrefs.LAN_TA) {
            binding.checkBox3.setChecked(true);
        }

        binding.deviceNameButton.setText(activity.getString(R.string.deviceName)+" [" + Config.getDeviceName(activity) + "]");

        binding.sinhalaTxt.setText("isxy,");
        binding.sinhalaTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"sinhala_font/araliya.ttf"));

        binding.tamilTxt.setText("jkpo;");
        binding.tamilTxt.setTypeface(Typeface.createFromAsset(activity.getAssets(),"tamil_font/Baminiii.TTF"));

        if(Config.ENVIRONMENT.equalsIgnoreCase("dev") || Config.ENVIRONMENT.equalsIgnoreCase("qa")){
            binding.viewDbButton.setVisibility(View.VISIBLE);
        }else{
            binding.viewDbButton.setVisibility(View.INVISIBLE);
        }

        binding.printersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.printersButton.setBackgroundColor(Color.parseColor("#262948"));
                binding.printersButton.setTextColor(Color.WHITE);
                binding.deviceNameButton.setBackgroundColor(Color.TRANSPARENT);
                binding.deviceNameButton.setTextColor(Color.parseColor("#555555"));
                binding.viewDbButton.setBackgroundColor(Color.TRANSPARENT);
                binding.viewDbButton.setTextColor(Color.parseColor("#555555"));
                binding.languageButton.setBackgroundColor(Color.TRANSPARENT);
                binding.languageButton.setTextColor(Color.parseColor("#555555"));

                binding.printersLayout1.setVisibility(View.VISIBLE);
                binding.deviceNameLayout1.setVisibility(View.GONE);
                binding.languageLayout1.setVisibility(View.GONE);
                //binding.dbLayout1.setVisibility(View.GONE);
            }
        });

        binding.deviceNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //
                binding.printersButton.setBackgroundColor(Color.TRANSPARENT);
                binding.printersButton.setTextColor(Color.parseColor("#555555"));
                binding.deviceNameButton.setBackgroundColor(Color.parseColor("#262948"));
                binding.deviceNameButton.setTextColor(Color.WHITE);
                binding.viewDbButton.setBackgroundColor(Color.TRANSPARENT);
                binding.viewDbButton.setTextColor(Color.parseColor("#555555"));
                binding.languageButton.setBackgroundColor(Color.TRANSPARENT);
                binding.languageButton.setTextColor(Color.parseColor("#555555"));

                binding.printersLayout1.setVisibility(View.GONE);
                ///binding.dbLayout1.setVisibility(View.GONE);
                binding.deviceNameLayout1.setVisibility(View.VISIBLE);
                binding.languageLayout1.setVisibility(View.GONE);
            }
        });

        binding.languageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.printersButton.setBackgroundColor(Color.TRANSPARENT);
                binding.printersButton.setTextColor(Color.parseColor("#555555"));
                binding.deviceNameButton.setBackgroundColor(Color.TRANSPARENT);
                binding.deviceNameButton.setTextColor(Color.parseColor("#555555"));
                binding.viewDbButton.setBackgroundColor(Color.TRANSPARENT);
                binding.viewDbButton.setTextColor(Color.parseColor("#555555"));
                binding.languageButton.setBackgroundColor(Color.parseColor("#262948"));
                binding.languageButton.setTextColor(Color.WHITE);

                binding.printersLayout1.setVisibility(View.GONE);
                binding.deviceNameLayout1.setVisibility(View.GONE);
                //binding.dbLayout1.setVisibility(View.GONE);
                binding.languageLayout1.setVisibility(View.VISIBLE);
            }
        });

        binding.viewDbButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.printersButton.setBackgroundColor(Color.TRANSPARENT);
                binding.printersButton.setTextColor(Color.parseColor("#555555"));
                binding.deviceNameButton.setBackgroundColor(Color.TRANSPARENT);
                binding.deviceNameButton.setTextColor(Color.parseColor("#555555"));
                binding.languageButton.setBackgroundColor(Color.TRANSPARENT);
                binding.languageButton.setTextColor(Color.parseColor("#555555"));
                binding.viewDbButton.setBackgroundColor(Color.parseColor("#262948"));
                binding.viewDbButton.setTextColor(Color.WHITE);

                binding.printersLayout1.setVisibility(View.GONE);
                binding.deviceNameLayout1.setVisibility(View.GONE);
                binding.languageLayout1.setVisibility(View.GONE);
                //binding.dbLayout1.setVisibility(View.VISIBLE);

                returnDB(activity);

                try {
                    LiveQuery query = mDatabase.createAllDocumentsQuery().toLiveQuery();
                    query.setAllDocsMode(Query.AllDocsMode.ALL_DOCS);
                    QueryEnumerator result = query.run();
                    StringBuilder sb = new StringBuilder();
                    String property = null;
                    for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                        QueryRow row = it.next();
                        int rowId = result.getCount();
                        Document document = row.getDocument();
                        property = document.getCurrentRevision().getProperties().toString();
                        property = sb.append(property).toString();

                    }
                    Log.d("*****DBLOG_PRINT*****" , property );


                    }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.checkBox1.setChecked(true);
                binding.checkBox2.setChecked(false);
                binding.checkBox3.setChecked(false);

                LangPrefs.setLanguage(activity, LangPrefs.LAN_EN);
            }
        });

        binding.layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.checkBox1.setChecked(false);
                binding.checkBox2.setChecked(true);
                binding.checkBox3.setChecked(false);

                LangPrefs.setLanguage(activity, LangPrefs.LAN_SIN);
            }
        });

        binding.layout3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.checkBox1.setChecked(false);
                binding.checkBox2.setChecked(false);
                binding.checkBox3.setChecked(true);

                LangPrefs.setLanguage(activity, LangPrefs.LAN_TA);
            }
        });

        binding.deviceNameSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.deviceNameEditText.getText().toString().isEmpty()) {
                    Toast.makeText(activity,activity.getString(R.string.deviceName), Toast.LENGTH_LONG).show();
                } else {
                    Config.setDeviceName(activity,binding.deviceNameEditText.getText().toString());
                    binding.deviceNameButton.setText(activity.getString(R.string.deviceName)+" [" + Config.getDeviceName(activity) + "]");
                    Toast.makeText(activity,activity.getString(R.string.deviceNameSavedSuccess), Toast.LENGTH_LONG).show();
                }
            }
        });

        binding.languageSaveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeLanguage();
                Intent i = new Intent(activity, MainActivity.class);
                activity.startActivity(i);
                activity.finish();
            }
        });


        binding.signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showProgressDialog(activity, activity.getResources().getString(R.string.pleaseWait));

                Config.signOutUser(activity);
                try {
                    mDatabase = returnDB(activity);
                    if(mDatabase != null) {
                        mDatabase.close();
                        mDatabase.delete();
                    }
                    mDatabase = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
                hideProgressDialog();
                Intent i = new Intent(activity, SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                activity.startActivity(i);
                activity.finish();
                Application.isLoginScreenPassed = false;

            }
        });




        ArrayList<String> printers = new ArrayList<>();
        printers.add("Printer One");

        PrintersAdapter printersAdapter = new PrintersAdapter(activity, printers);
        binding.list.setAdapter(printersAdapter);
    }

    private void changeLanguage() {
        int selectedLang = LangPrefs.getLanguage(activity);
        Locale locale = null;

        if (selectedLang == LangPrefs.LAN_EN) {
            locale = new Locale("en");
        } else if (selectedLang == LangPrefs.LAN_SIN) {
            locale = new Locale("si");
        } else if (selectedLang == LangPrefs.LAN_TA) {
            locale = new Locale("ta");
        }

        Configuration config = activity.getResources().getConfiguration();
        config.locale = locale;
        activity.getResources().updateConfiguration(config, activity.getResources().getDisplayMetrics());
    }

    public Database returnDB(Context context) {
        Application application = (Application) context.getApplicationContext();
        mDatabase = application.getDatabase();

        return mDatabase;
    }

}
