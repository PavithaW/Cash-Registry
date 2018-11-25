package com.cbasolutions.cbapos.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.adapter.ItemAdapter;
import com.cbasolutions.cbapos.adapter.LowStockItemAdapter;
import com.cbasolutions.cbapos.adapter.TransactionDetailAdapter;
import com.cbasolutions.cbapos.fragment.BillFragment;
import com.cbasolutions.cbapos.fragment.EditItemFragment;
import com.cbasolutions.cbapos.fragment.HomeFragment;
import com.cbasolutions.cbapos.fragment.ProductsFragment;
import com.cbasolutions.cbapos.fragment.SettingsFragment;
import com.cbasolutions.cbapos.fragment.StatisticsFragment;
import com.cbasolutions.cbapos.fragment.TransactionHistoryFragment;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.helper.ItemsAndCategorySearch;
import com.cbasolutions.cbapos.helper.NetworkChangeReceiver;
import com.cbasolutions.cbapos.helper.ObservableObject;
import com.cbasolutions.cbapos.helper.ProductSearch;
import com.cbasolutions.cbapos.helper.TransactionSearch;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.ORMTblCategory;
import com.cbasolutions.cbapos.model.ORMTblItem;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.Transaction;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.cbasolutions.cbapos.service.PayableService;
import com.cbasolutions.cbapos.util.ImageLang;
import com.cbasolutions.cbapos.util.LangPrefs;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Observable;
import java.util.Observer;

/*
 * This class initiates the navigation drawer, adds fragments on navigation click change
 * Initiates the product search from the searchview and passes search key from this to homefragment and then to product fragment
 * Also passes the product that was selected from Products fragment to BilFragment using SendItem interface
 */
public class MainActivity extends BaseActivity implements SearchView.OnQueryTextListener, ProductsFragment.SendItem, View.OnClickListener, Observer {

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private Toolbar toolbar;
    private Button addItem;
    private Spinner spinner;
    private LinearLayout footer;
    private SharedPreferences inCompletedPayments;

    public static SearchView editsearch;
    public static TextView userNameText;
    private ProductSearch listener;
    private ItemsAndCategorySearch allItemslistener;
    private TransactionSearch transactionListener;
    CountDownTimer cTimer = null;
    private Database mDatabase;
    List<Transaction> list = new ArrayList<>();
    LowStockItemAdapter lowStockAdapter;

    // index to identify current nav menu item
    public static int navItemIndex = 0;

    // tags used to attach the fragments
    private static final String TAG_HOME = "home";
    private static final String TAG_PRODUCTS = "products";
    private static final String TAG_TRANSACTION = "transactions";
    private static final String TAG_STATS = "statistics";
    private static final String TAG_GROSS = "gross";
    public static String CURRENT_TAG = TAG_HOME;

    private SharedPreferences loginPreferences;
    public  static String userName;
    public  static String storeID;

    // toolbar titles respected to selected nav menu item
    private String[] activityTitles;

    // flag to load home fragment when user presses back key
    private boolean shouldLoadHomeFragOnBackPress = false;
    private Handler mHandler;
    private boolean transactionActivated = false;
    Fragment fragment;
    View rootview;
    Gson gson = new Gson();
    double totalAmount = 0;
    double remainAmount = 0;
    double splittedAmount = 0;
    double initialAmount = 0;
    String paymentList = null;
    Snackbar snackbar;
    ArrayList<Item> lowStockItems = new ArrayList<>();

    private SharedPreferences.Editor paymentPrefsEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        rootview = findViewById(android.R.id.content);
        ObservableObject.getInstance().addObserver(this);
        int selectedLang = LangPrefs.getLanguage(this);
        Locale locale = null;

        if (selectedLang == LangPrefs.LAN_EN) {
            locale = new Locale("en");
        } else if (selectedLang == LangPrefs.LAN_SIN) {
            locale = new Locale("si");
        } else if (selectedLang == LangPrefs.LAN_TA) {
            locale = new Locale("ta");
        }

        Configuration config = getBaseContext().getResources().getConfiguration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        //rootview = (CoordinatorLayout)findViewById(R.id.coordinatorLayout);

        setSupportActionBar(toolbar);

        boolean isOnline = checkInternet(getApplicationContext());
        if(!isOnline){
            startTimer();
            showSnackBar();
        }

        mHandler = new Handler();

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        editsearch = (SearchView) findViewById(R.id.search);
        addItem = (Button) findViewById(R.id.add_item);
        footer = (LinearLayout) findViewById(R.id.footer);
        //editsearch.setOnKeyListener(onSoftKeyboardDonePress);
        editsearch.setOnQueryTextListener(this);
        editsearch.setQueryHint(getApplicationContext().getString(R.string.search));
        navigationView.setItemIconTintList(null);
        userNameText = (TextView) findViewById(R.id.textView_username);

        spinner = (Spinner) findViewById(R.id.spinner1);

        ArrayList<String> options = new ArrayList<String>();
        options.add("Sinhala");
        options.add("Tamil");
        options.add("English");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.options_drop_down, options);
        adapter.setDropDownViewResource(R.layout.options_drop_down);
        spinner.setAdapter(adapter); // this will set list of values to spinner
        spinner.setSelection(options.indexOf(0));

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        if(loginPreferences != null) {
            userName = loginPreferences.getString("username", null);
            storeID = loginPreferences.getString("storeId", null);

            if (userName != null) {
                userNameText.setText(userName);
            } else {
                userNameText.setText("");
            }
        }else{
            userNameText.setText("");
        }

        // load toolbar titles from string resources
        activityTitles = getResources().getStringArray(R.array.nav_item_activity_titles);

        // initializing navigation menu
        setUpNavigationView();

        inCompletedPayments = getSharedPreferences("paymentPrefs", MODE_PRIVATE);
        boolean isIncompletedPayment = inCompletedPayments.getBoolean("isPaymentComplete", false);
        String paymentStoreId = inCompletedPayments.getString("storeId", null);
            if (isIncompletedPayment && paymentStoreId.equals(storeID)) {
                openIncompletedPaymentDialog();
            }


        if (savedInstanceState == null) {
            navItemIndex = 0;
            CURRENT_TAG = TAG_HOME;
            loadHomeFragment();
        }

        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        String owner = loginPreferences.getString("owner", null);

        Calendar currTime = Calendar.getInstance();
        int hour = currTime.get(Calendar.HOUR_OF_DAY);

        if (hour >= 8 && hour < 12) {

            //PayableService.getLowStockItems(this, owner);
        }

        PayableService.getLowStockItems(this, owner);

        Config.issearching = false;

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setListners();

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Runnable mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        // update the main content by replacing fragments
                        fragment = new SettingsFragment();
                        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                        fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);

                        fragmentTransaction.commit();

                        transactionActivated = false;
                        shouldLoadHomeFragOnBackPress = false;
                        changeButton(transactionActivated);

                    }
                };

                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                }

                drawer.closeDrawers();

                invalidateOptionsMenu();
            }
        });



    }

    private void setListners(){
        addItem.setOnClickListener(this);
    }

    /***
     * Returns respected fragment that user
     * selected from navigation menu
     */
    private void loadHomeFragment() {
        // selecting appropriate nav menu item
        selectNavMenu();

        // set toolbar title
        setToolbarTitle();

        // if user select the current navigation menu again, don't do anything
        // just close the navigation drawer
//        if (getSupportFragmentManager().findFragmentByTag(CURRENT_TAG) != null) {
//            drawer.closeDrawers();
//
//            return;
//        }

        // Sometimes, when fragment has huge data, screen seems hanging
        // when switching between navigation menus
        // So using runnable, the fragment is loaded with cross fade effect
        // This effect can be seen in GMail app
        Runnable mPendingRunnable = new Runnable() {
            @Override
            public void run() {
                // update the main content by replacing fragments
                Fragment fragment = getHomeFragment();
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
                fragmentTransaction.replace(R.id.frame, fragment, CURRENT_TAG);
                fragmentTransaction.commit();
                shouldLoadHomeFragOnBackPress = false;

            }
        };

        // If mPendingRunnable is not null, then add to the message queue
        if (mPendingRunnable != null) {
            mHandler.post(mPendingRunnable);
        }

        //Closing drawer on item click
        drawer.closeDrawers();

        // refresh toolbar menu
        invalidateOptionsMenu();
        //setListener(myFragment);
    }

    private Fragment getHomeFragment() {

        switch (navItemIndex) {

            case 0:
                // home
                HomeFragment homeFragment = new HomeFragment();
                //Registering homefragment inorder to pass data from searchview
                setListener(homeFragment);
                transactionActivated = false;
                shouldLoadHomeFragOnBackPress = false;
                changeButton(transactionActivated);
                return homeFragment;
            case 1:
                EditItemFragment grossFrag1 = new EditItemFragment();
                setEditListener(grossFrag1);
                transactionActivated = false;
                shouldLoadHomeFragOnBackPress = true;
                changeButton(transactionActivated);
                return grossFrag1;
            case 2:
                TransactionHistoryFragment transFragment = new TransactionHistoryFragment();
                setTransactionListener(transFragment);
                Config.VISIBLE_VIEW = 4;
                transactionActivated = true;
                shouldLoadHomeFragOnBackPress = true;
                changeButton(transactionActivated);
                return transFragment;
            case 3:
                StatisticsFragment statsFragment = new StatisticsFragment();
                transactionActivated = false;
                shouldLoadHomeFragOnBackPress = true;
                changeButton(transactionActivated);
                return statsFragment;
            case 4:
                EditItemFragment grossFrag = new EditItemFragment();
                transactionActivated = false;
                shouldLoadHomeFragOnBackPress = true;
                changeButton(transactionActivated);
                return grossFrag;
            default:
                transactionActivated = false;
                shouldLoadHomeFragOnBackPress = false;
                changeButton(transactionActivated);

                return new HomeFragment();
        }
    }


    private void setToolbarTitle() {
        getSupportActionBar().setTitle(activityTitles[navItemIndex]);
    }

    private void selectNavMenu() {
        navigationView.getMenu().getItem(navItemIndex).setChecked(true);
    }

    private void setUpNavigationView() {
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                editsearch.setQuery("", false);
                editsearch.clearFocus();
                //Check to see which item was being clicked and perform appropriate action
                switch (menuItem.getItemId()) {
                    //Replacing the main content with ContentFragment Which is our Inbox View;
                    case R.id.home:
                        navItemIndex = 0;
                        CURRENT_TAG = TAG_HOME;
                        shouldLoadHomeFragOnBackPress = false;
                        break;
                    case R.id.nav_products:
                        navItemIndex = 1;
                        CURRENT_TAG = TAG_PRODUCTS;
                        shouldLoadHomeFragOnBackPress = true;
                        break;
                    case R.id.nav_trans:
                        navItemIndex = 2;
                        CURRENT_TAG = TAG_TRANSACTION;
                        shouldLoadHomeFragOnBackPress = true;
                        break;
                    case R.id.nav_stats:
                        navItemIndex = 3;
                        CURRENT_TAG = TAG_STATS;
                        shouldLoadHomeFragOnBackPress = true;
                        break;
                    default:
                        navItemIndex = 0;
                        shouldLoadHomeFragOnBackPress = false;
                }

                //Checking if the item is in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }
                menuItem.setChecked(true);

                loadHomeFragment();

                return true;
            }
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                // Code here will be triggered once the drawer closes as we dont want anything to happen so we leave this blank
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                // Code here will be triggered once the drawer open as we dont want anything to happen so we leave this blank
                super.onDrawerOpened(drawerView);
            }
        };

        //Setting the actionbarToggle to drawer layout
        drawer.setDrawerListener(actionBarDrawerToggle);

        //calling sync state is necessary or else your hamburger icon wont show up
        actionBarDrawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        // This code loads home fragment when back key is pressed
        // when user is in other fragment than home
        if (shouldLoadHomeFragOnBackPress) {
            // checking if user is on other navigation menu
            // rather than home
            if (navItemIndex != 0) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
                loadHomeFragment();
                return;
            }
        }
        if(Application.isLoginScreenPassed){
            finish();

        }else{
            super.onBackPressed();

        }

       //finish();
        //super.onBackPressed();
    }


    public void setListener(ProductSearch listener)
    {
        this.listener = listener ;
    }

    public void setEditListener(ItemsAndCategorySearch listener)
    {
        this.allItemslistener = listener ;
    }

    public void setTransactionListener(TransactionSearch listener) {
        this.transactionListener = listener;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        editsearch.clearFocus();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        //pass data to HomeFragment
       // if(listener != null) {

            //if(!text.equalsIgnoreCase("")) {
            //listener.filter(text);
            //}
        //}

        if(listener != null && Config.issearching && navItemIndex == 0){
            listener.filter(text);
            return false;
            //Config.issearching = false;
        }

        if(allItemslistener != null && navItemIndex == 1){
            allItemslistener.filter(text);
            return false;
        }

        if (transactionListener != null && navItemIndex == 2) {
            transactionListener.filter(text);
            return false;
        }
        Config.issearching = true;

        return false;
    }

    @Override
    public void sendData(Item item) {
        BillFragment f = (BillFragment) getSupportFragmentManager().findFragmentByTag("BILL");
        f.handleIncomingItem(item);
    }

    // handling click events
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_item:
                if (transactionActivated) {
                    showIssueReceiptPopUp();

                } else {
                    if (!Config.dialogIsShowing) {
                        Config.dialogIsShowing = true;
                        showAddItemPopUp(null);
                    }
                }
                break;
            default:
                break;
        }
    }

    public void openIncompletedPaymentDialog(){
        showIncompletePaymentPopUp();
    }

    private void changeButton(boolean transactionActivated) {

        if (transactionActivated) {
            //addItem.setBackgroundResource(R.drawable.btn_new_receipt_2);
            ImageLang.setImage(getApplicationContext(),addItem,"newReceipt");
        } else {
            ImageLang.setImage(getApplicationContext(),addItem,"addItem");
        }
    }

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

    void startTimer() {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(!Config.isOnline) {
                    Log.d("PPP", "seconds remaining: " + millisUntilFinished / 1000);
                }else{
                    cancelTimer();
                }

            }
            public void onFinish() {
                Config.signOutUser(getApplicationContext());
                try {
                    mDatabase = returnDB(getApplicationContext());
                    if(mDatabase != null) {
                        //mDatabase.close();
                        mDatabase.delete();
                    }
                    mDatabase = null;
                }catch (Exception e){
                    e.printStackTrace();
                }
                cTimer.cancel();
                Intent i = new Intent(getApplicationContext(), SignInActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getApplicationContext().startActivity(i);
                finish();
                Application.isLoginScreenPassed = false;
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    public Database returnDB(Context context) {
        Application application = (Application) context.getApplicationContext();
        mDatabase = application.getDatabase();

        return mDatabase;
    }

    public void showIncompletePaymentPopUp() {

        final Dialog incompletePaymentDialog = new Dialog(this, R.style.Custom_Dialog);
        incompletePaymentDialog.setContentView(R.layout.incomplete_payment_alert);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(incompletePaymentDialog.getWindow().getAttributes());

        if (getTab() == 10) {
            lp.width = width * 4 / 10;
            lp.height = height * 2 / 3;
        } else {
            lp.width = width * 5 / 10;
            lp.height = (int) (height * (2.5 / 3));
        }

        lp.gravity = Gravity.CENTER;

        incompletePaymentDialog.getWindow().setAttributes(lp);
        incompletePaymentDialog.setCancelable(false);
        Button completeNowButton = incompletePaymentDialog.findViewById(R.id.completeButton);
        Button laterButton = incompletePaymentDialog.findViewById(R.id.later_button);
        final TextView paymentType = incompletePaymentDialog.findViewById(R.id.paymentType);
        final TextView paymentId = incompletePaymentDialog.findViewById(R.id.paymentId);
        final TextView totalAmountTV = incompletePaymentDialog.findViewById(R.id.totalAmount);
        final TextView remainingAmountTV = incompletePaymentDialog.findViewById(R.id.remaining);
        final TextView splittedAmountTV = incompletePaymentDialog.findViewById(R.id.cashAmount);
        final TextView transactionCountTV = incompletePaymentDialog.findViewById(R.id.transactionNumber);

        if(inCompletedPayments.getString("totalAmount", null) != null){
            totalAmount = Double.parseDouble(inCompletedPayments.getString("totalAmount", null));
        }
        if(inCompletedPayments.getString("remainingAmount", null) != null){
            remainAmount = Double.parseDouble(inCompletedPayments.getString("remainingAmount", null));
        }
        if(inCompletedPayments.getString("splittedAmount", null) != null){
            splittedAmount = Double.parseDouble(inCompletedPayments.getString("splittedAmount", null));
        }
        if(inCompletedPayments.getString("initialAmount", null) != null){
            initialAmount = Double.parseDouble(inCompletedPayments.getString("initialAmount", null));
        }
        if(inCompletedPayments.getString("paymentList", null) != null) {
            paymentList = inCompletedPayments.getString("paymentList", null);
        }

        if(inCompletedPayments.getString("transactionId", null) != null) {
            String transId = inCompletedPayments.getString("transactionId", null);
            paymentId.setText(transId.toString());
        }

        manager = new DBManager();
        list = manager.loadTransactions(getApplicationContext());

        int count = 0;
        for(int x = 0; x< list.size(); x++){
            if(list.get(x).isIncomplete()){
                count = count +1;
            }
        }

        transactionCountTV.setText(String.format(getApplicationContext().getString(R.string.incompleteTransactionCount), count));

        Type type = new TypeToken<List<Payment>>(){}.getType();
        final ArrayList<Payment> paymentsDoneList = gson.fromJson(paymentList, type);

        totalAmountTV.setText("Rs " + String.format("%.2f",totalAmount));
        splittedAmountTV.setText("Rs " + String.format("%.2f",splittedAmount));
        remainingAmountTV.setText("Rs " + String.format("%.2f",remainAmount)+" "+getApplicationContext().getString(R.string.remain));

        laterButton.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Config.isInCompletePayment = false;
                                                inCompletedPayments = getApplicationContext().getSharedPreferences("paymentPrefs", MODE_PRIVATE);
                                                paymentPrefsEditor = inCompletedPayments.edit();

                                                paymentPrefsEditor.putBoolean("isPaymentComplete", Config.isInCompletePayment);
                                                paymentPrefsEditor.commit();
                                                incompletePaymentDialog.dismiss();
                                            }
                                        });
        completeNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Config.isInCompletePayment = false;
                String t_id = inCompletedPayments.getString("transactionId", null);

                final ArrayList<Object> listItems = new ArrayList<>();

                Transaction tObj = null;

                for(int i=0; i < list.size(); i++){
                    if(list.get(i).gettId().equals(t_id)) {
                        tObj = list.get(i);
                    }

                }

                BillFragment f = (BillFragment) getSupportFragmentManager().findFragmentByTag("BILL");
                f.handleSplitPopup(tObj,totalAmount,initialAmount,remainAmount,splittedAmount,paymentsDoneList);
                incompletePaymentDialog.dismiss();
            }
        });

        incompletePaymentDialog.show();
    }

    public void showLowStockAlert(){

        final Dialog lowStockAlert = new Dialog(this, R.style.Custom_Dialog);
        lowStockAlert.setContentView(R.layout.low_stock_alert);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(lowStockAlert.getWindow().getAttributes());

        if (getTab() == 10) {
            lp.width = width * 4 / 10;
            lp.height = height * 2 / 3;
        } else {
            lp.width = width * 5 / 10;
            lp.height = (int) (height * (2.5 / 3));
        }

        lp.gravity = Gravity.CENTER;

        lowStockAlert.getWindow().setAttributes(lp);
        lowStockAlert.setCancelable(false);
        lowStockAlert.show();
    }

    @Override
    public void update(Observable o, Object arg) {

        showSnackBar();
    }

    public void showSnackBar(){
        int navbarHeight = 65;
        View view;
        if(!Config.isOnline) {

            snackbar = Snackbar.make(findViewById(R.id.root_view), getApplicationContext().getString(R.string.offlineBanner), Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            view.setBackgroundColor(Color.RED);
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.FILL_PARENT, navbarHeight));

        }else{
            snackbar = Snackbar.make(findViewById(R.id.root_view), getApplicationContext().getString(R.string.onlineBanner), Snackbar.LENGTH_LONG);
            view = snackbar.getView();
            view.setBackgroundColor(Color.parseColor("#ff21ab29"));
            view.setLayoutParams(new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.FILL_PARENT, navbarHeight));

        }

        TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
        tv.setTextColor(ContextCompat.getColor(this, R.color.white));


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        } else {
            tv.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.FILL_HORIZONTAL | Gravity.TOP;

        snackbar.show();
    }

    public void didReceiveLowStockItems(Context context,int responseCode, JSONArray response,Throwable throwable) {
        if (responseCode == Config.OK) {
            String error = null;

            if(response.length() != 0){

                try {
                    for (int i = 0; i < response.length(); i++) {
                        Item responseObj = (Item)response.get(i);
                        lowStockItems.add(responseObj);
                    }
                }catch(Exception e){}
                //showLowStockAlert();
            }

            lowStockAdapter = new LowStockItemAdapter(lowStockItems, this);
            //binding.list3.setAdapter(transactionDatailAdapter);


        } else {
            Toast.makeText(context, context.getString(R.string.serverError), Toast.LENGTH_LONG).show();
        }

    }

}
