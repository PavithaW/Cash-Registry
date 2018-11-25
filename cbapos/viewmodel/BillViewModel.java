package com.cbasolutions.cbapos.viewmodel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.Bindable;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.activity.SignInActivity;
import com.cbasolutions.cbapos.adapter.BillAdapter;
import com.cbasolutions.cbapos.adapter.DiscountListAdapter;
import com.cbasolutions.cbapos.adapter.PaymentAdapter;
import com.cbasolutions.cbapos.adapter.TransactionAdapter;
import com.cbasolutions.cbapos.adapter.TransactionDetailAdapter;
import com.cbasolutions.cbapos.adapter.VariantAdapter;
import com.cbasolutions.cbapos.databinding.BillLayoutBinding;
import com.cbasolutions.cbapos.helper.Cart;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.model.Refund;
import com.cbasolutions.cbapos.model.Transaction;
import com.cbasolutions.cbapos.mpos.SdkCardSales;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.cbasolutions.cbapos.service.PayableService;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.UnsavedRevision;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.util.Attributes;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by "Don" on 10/23/2017.
 * Class Functionality :-
 * Performs the entire bill logic.
 * Includes animation, bill calculation, cash acceptance
 */

public class BillViewModel extends CRBaseObservable {

    private Item item;
    private Context mContext;
    BillLayoutBinding binding;
    FragmentActivity fragContext;
    public BillAdapter adapter;
    private Paint p = new Paint();
    private Cart cart;
    long editedQty = 1;
    PriceVariation selectedVariation;
    private DBManager manager;
    private Item clickedItem;
    private TextView txtPrice;
    private TextView txtItemName;
    public static BillViewModel billVMContext;
    public static final double MAX_AMOUNT = 999999.99;
    public static boolean customItemClicked = false;
    public static boolean swipePopUpOpend = false;
    public static boolean splitPopUpOpend = false;
    public static boolean tenderSuccessPopUpOpend = false;
    public static boolean sdkLoginPopUpOpend = false;
    public static boolean sdkChanegePasswordPopUpOpend = false;
    public static double totalSplits = 0.00;
    static TextView balanceTxt = null;
    private ArrayList<Transaction> splitTransactionList = new ArrayList<>();
    private static int count = 0;
    static Dialog dialogSplit = null;
    public static Button dialogButtonBack;
    public static Dialog emailDialog;
    public static Dialog textDialog;
    public static double remaining = 0;
    ArrayList<Payment> splitList;
    private SharedPreferences inCompletedPayments;
    private SharedPreferences.Editor paymentPrefsEditor;
    List<Transaction> list = new ArrayList<>();
    TransactionAdapter transactionAdapter;
    TransactionDetailAdapter transactionDatailAdapter;
    Gson gson = new Gson();

    // public static double totalAmount;

    public BillViewModel(Item item, Context mContext) {
        this.item = item;
        this.mContext = mContext;
    }

    public BillViewModel(final FragmentActivity fragContext, final BillLayoutBinding binding, final ArrayList<Item> itemArrayList) {

        //instance of cart
        cart = new Cart();

        this.binding = binding;
        this.fragContext = fragContext;
        billVMContext = this;

        //create an empty adapter, populate later on item add
        adapter = new BillAdapter(cart.itemArrayList, fragContext, this, cart);
        binding.list.setAdapter(adapter);
        adapter.setMode(Attributes.Mode.Single);
        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((SwipeLayout) (binding.list.getChildAt(position - binding.list.getFirstVisiblePosition()))).open(true);
            }
        });

        //handleSwipeToDelete();


        //bring the number pad up when the user want to add a custom item
        binding.addCustom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customItemClicked = true;
                handleNumberPad();
            }
        });

        //send the number pad down after the custom item is added
        binding.keyboard.handler.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleNumberPad();
            }
        });

        //adding a custom item from the keyboard to the user list to be billed
        binding.keyboard.addCustomAmount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (customItemClicked) {
                    String customItem = binding.keyboard.customAmountEditText.getText().toString();
                    if (!customItem.equals("")) {
                        if (Double.parseDouble(customItem) > MAX_AMOUNT) {
                            showHintDialog(fragContext, fragContext.getString(R.string.exceededPrice), fragContext.getString(R.string.alert));
                            return;
                        }

                        if (customItem.length() != 0) {
                            Item item = new Item();
                            item.setItemType(Config.CUSTOM);
                            item.setItem_name("Custom");
                            item.setItemTotal(customItem);
                            item.setItemActualTotal(Double.parseDouble(customItem));
                            item.setQuantity(1);

                            PriceVariation variation = new PriceVariation();
                            variation.setPrice(Double.parseDouble(customItem));
                            variation.setVariation(Config.CUSTOM);

                            item.setPriceSelected(variation);

                            cart.addItemToCart(item, fragContext, billVMContext);
                            int count = cart.getItemCount();
                            adapter.notifyDataSetChanged();
                            //animDown();
                            //paymentTypeDown();
                            handleNumberPad();
                            //handlePaymentPad();
                            //paymentTypeDown();
                            updateItemCountAndBill();
                        }
                    } else {
                        showHintDialog(fragContext, fragContext.getString(R.string.custom_amount_missing), fragContext.getString(R.string.sorry));
                        return;
                    }
                }


            }
        });

        binding.cardOrCash.cashButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                paymentTypeDown();

                double billvalue = cart.getAllItemsBill();

                binding.addCustom.setVisibility(View.GONE);
                //list with user purchased items
                binding.list.setVisibility(View.GONE);
                //layout with user item count and the clear button
                binding.relativeLayout.setVisibility(View.GONE);
                //handler in the keyboard to pull down the keyboard
                binding.keyboard.handler.setVisibility(View.GONE);
                //close bill green button with the total amount
                binding.closeBill.setVisibility(View.GONE);
                //tender button
                binding.tender.setVisibility(View.VISIBLE);
                //clear button for tender, total amount to be tenderd is contained in this view
                binding.tenderSummary.setVisibility(View.VISIBLE);

                binding.tenderValue.setText("Rs " + String.format("%.2f", billvalue) + "");

                customItemClicked = false;

                animUp();

                return false;
            }

        });

        binding.cardOrCash.cardButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                double billvalue = cart.getAllItemsBill();

                customItemClicked = false;

                Intent intent = new Intent(fragContext, SdkCardSales.class);
                Bundle bundle = new Bundle();
                bundle.putDouble("billValue", billvalue);
                intent.putExtras(bundle);
                fragContext.startActivity(intent);

                if (!sdkChanegePasswordPopUpOpend) {
                    //showSwipeCardPopUp(billvalue);
                    //showSignPadPopUp(billvalue);
                    //showChangePasswordPopUp();
                }

                return true;
            }

        });

        binding.cardOrCash.splitButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                double billvalue = cart.getAllItemsBill();
                double initialBill = cart.billValue;

                customItemClicked = false;
                if (!splitPopUpOpend) {
                    //showSwipeCardPopUp(billvalue);
                    billvalue = new BigDecimal(billvalue).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    showSplitPaymentPopUp(null, billvalue, initialBill, 0, 0, null);
                }

                return true;
            }

        });


        binding.closeBill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //handleNumberPad();

                if (cart.getItemCount() == 0) {

                    return;
                } else {
                    //handleNumberPad();

                    double billvalue = cart.getAllItemsBill();
                    if (billvalue > MAX_AMOUNT) {
                        showHintDialog(fragContext, fragContext.getString(R.string.exceededPrice), fragContext.getString(R.string.alert));
                        return;
                    }

                    binding.tenderValue.setText("Rs " + String.format("%.2f", billvalue) + "");

                    binding.addCustom.setVisibility(View.GONE);
                    //binding.relativeLayout.setVisibility(View.GONE);
                    binding.list.setVisibility(View.VISIBLE);
                    binding.tenderSummary.setVisibility(View.GONE);

                    customItemClicked = false;
                    //handleNumberPad();
                    animDown();
                    handlePaymentPad();
                }
            }
        });

        binding.clearTender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animDown();

                //button to add a custom amount
                binding.addCustom.postDelayed(new Runnable() {
                    public void run() {
                        binding.addCustom.setVisibility(View.VISIBLE);
                    }
                }, 400);
                //list with user purchased items
                binding.list.setVisibility(View.VISIBLE);
                //layout with user item count and the clear button
                binding.relativeLayout.setVisibility(View.VISIBLE);
                //handler in the keyboard to pull down the keyboard
                binding.keyboard.handler.setVisibility(View.VISIBLE);
                //close bill green button with the total amount
                binding.closeBill.setVisibility(View.VISIBLE);
                //tender button
                binding.tender.setVisibility(View.GONE);
                //clear button for tender, total amount to be tenderd is contained in this view
                binding.tenderSummary.setVisibility(View.GONE);

                binding.keyboard.customAmountEditText.setText("");

                ViewGroup.LayoutParams params = binding.list.getLayoutParams();

                if (getTab(fragContext) == 7) {
                    params.height = 400;
                } else {
                    params.height = 800;
                }

                // Hide the Panel

                binding.list.setLayoutParams(params);
                binding.list.setSelection(adapter.getCount() - 1);


            }
        });

        binding.tender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (binding.keyboard.customAmountEditText.getText().toString().isEmpty()) {
                    showHintDialog(fragContext, fragContext.getString(R.string.tenderMissing), fragContext.getString(R.string.alert));
                } else {
                    double tenderVal = Double.parseDouble(binding.keyboard.customAmountEditText.getText().toString());
                    double totalBillVal = cart.getAllItemsBill();

                    if (tenderVal < totalBillVal) {
                        showHintDialog(fragContext, fragContext.getString(R.string.invalidTender), fragContext.getString(R.string.alert));
                    } else {
                        double billValue = cart.getAllItemsBill();
                        showTenderSuccessPopUp(tenderVal, billValue, false);

                        final double change = tenderVal - billValue;
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                        String d = df.format(Calendar.getInstance().getTime());
                        Date date = null;

                        try {
                            date = df.parse(d);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        List<Discount> tDiscounts = new ArrayList<>();

                        for (int i = 0; i < cart.itemArrayList.size(); i++) {
                            if (cart.itemArrayList.get(i).getItemType().equals(Config.DISCOUNT)) {
                                Item item = cart.itemArrayList.get(i);
                                tDiscounts.add(item.getItemDiscount());
                            }
                        }

                        //save transaction to DB
                        DBManager manager = new DBManager();

                        final ArrayList<Payment> paymentList = new ArrayList<>();

                        Payment payment = new Payment();
                        payment.setPaymentType(Config.CASH);
                        payment.settAmount(billValue);
                        payment.settTenderAmount(tenderVal);
                        payment.settBalance(change);

                        paymentList.add(0, payment);

                        Refund refund = new Refund();
                        refund.setPaymentType(Config.REFUND);
                        refund.settRefundAmount(0.00);
                        refund.settReasonForRefund("");

                        ArrayList<Refund> refundArrayList = new ArrayList<>();

                        refundArrayList.add(refund);

                        String tId = UUID.randomUUID().toString();


                        byte[] bytes = null;
                        String deviceID = Config.getDeviceID(fragContext);

                        try {
                            bytes = deviceID.getBytes("US-ASCII");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        String invoiceNo = null;
                        int invoice = 0;

                        if (bytes != null) {

                            for (byte ch : bytes) {
                                invoice += ch;
                            }

                            invoiceNo = Config.randomString(6) + invoice;
                        }

                        Transaction transaction = new Transaction();
                        transaction.settId(tId);
                        transaction.setInvoiceNo(invoiceNo);
                        transaction.setIncomplete(false);
                        transaction.settDateTime(date);
                        transaction.settItems(cart.itemArrayList);
                        transaction.settValue(billValue);
                        transaction.setInitialAmount(cart.billValue);
                        transaction.settDiscounts(tDiscounts);
                        transaction.setTransactionList(paymentList);
                        transaction.setTRefund(refundArrayList);

                        try {
                            manager.saveTransaction(fragContext, transaction);

                            ArrayList<Item> updatedQuntityArray = new ArrayList<Item>();
                            long remainQuantity;

                            for(int i = 0; i < cart.itemArrayList.size(); i++) {
                                Item item = cart.itemArrayList.get(i);

                                if(item.getItemType().equals(Config.ITEM)) {

                                    if (updatedQuntityArray.size() != 0) {

                                        for (int x = 0; x < updatedQuntityArray.size(); x++) {
                                            if (updatedQuntityArray.get(x).getPriceVariations().get(0).getVariation().equals(item.getPriceVariations().get(0).getVariation())) {
                                                updatedQuntityArray.get(x).setQuantity(updatedQuntityArray.get(x).getQuantity() + item.getQuantity());

                                            } else {
                                                updatedQuntityArray.add(item);
                                            }
                                        }
                                    } else {
                                        updatedQuntityArray.add(item);
                                        //remainQuantity = item.getPriceVariations().get(0).getStk_count() - item.getQuantity();

                                    }
                                }
                            }


                                for(int i = 0; i < updatedQuntityArray.size(); i++){
                                    Item item  = updatedQuntityArray.get(i);
                                    remainQuantity =  item.getPriceVariations().get(0).getStk_count()- item.getQuantity();
                                //remainQuantity =  item.getPriceVariations().get(0).getStk_count()- item.getQuantity();

                                String itemId = item.getItem_id();
                                final Document document = manager.returnDB(fragContext).getExistingDocument(itemId);

                                final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();

                                ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
                                JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();
                                for (int x = 0; x < jArray.size(); x++) {
                                    PriceVariation variant = gson.fromJson(jArray.get(x), PriceVariation.class);

                                    if(variant.getVariation().equals(item.getPriceSelected().getVariation())){
                                        variant.setStk_count(remainQuantity);
                                    }
                                    priceVariations.add(variant);

                                }


                                document.update(new Document.DocumentUpdater() {
                                    @Override
                                    public boolean update(UnsavedRevision newRevision) {
                                        Map<String, Object> properties = newRevision.getUserProperties();

                                        properties.put("price_variations", priceVariations);

                                        SharedPreferences loginPreferences = fragContext.getSharedPreferences("loginPrefs", 0);
                                        String owner = loginPreferences.getString("owner", null);
                                        String storeId = loginPreferences.getString("storeId", null);

                                        properties.put("owner", owner);
                                        properties.put("store_name", storeId);

                                        newRevision.setUserProperties(properties);
                                        return true;
                                    }
                                });
                            }
                        } catch (CouchbaseLiteException e) {
                            e.printStackTrace();
                        }

                        //Cart.totalBill = 0;
                    }
                }
            }
        });

        binding.billClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearUserItems();
            }
        });

        binding.list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                clickedItem = cart.itemArrayList.get(position);
                if (clickedItem.getItemType().equals(Config.ITEM) || clickedItem.getItemType().equals(Config.CUSTOM)) {
                    showQtyAndVariants(clickedItem, position);

                    if (clickedItem.getPriceSelected() != null) {
                        selectedVariation = clickedItem.getPriceSelected();
                    } else {
                        if (clickedItem.getPriceVariations().size() != 0) {
                            selectedVariation = clickedItem.getPriceVariations().get(0);
                        }
                    }
                }
            }
        });

    }

    //clears items added to the list
    private void clearUserItems() {
        //paymentTypeDown();
        cart.clearItemsInCart();
        adapter.notifyDataSetChanged();
        updateItemCountAndBill();

        //handleNumberPad();
        //handlePaymentPad();
    }

    //updates the text fields with the help of funtions in cart
    public void updateItemCountAndBill() {
        binding.itemCount.setText(cart.getItemCount() + "");
        binding.billValue.setText("Rs " + String.format("%.2f", cart.getAllItemsBill()) + "");
        //totalAmount = cart.getAllItemsBill();
        //if (cart.getItemCount() == 0) {
        //   binding.closeBillText.setText("");
        //} else {
        binding.closeBillText.setText(fragContext.getString(R.string.closeBill));
        //}
    }

    //incoming item from product grid
    public void handleIncomingItem(Item item) {
        cart.addItemToCart(item, fragContext, billVMContext);
        adapter.notifyDataSetChanged();
        updateItemCountAndBill();
        //programatically scroll the recycleview to bottom
        binding.list.setSelection(adapter.getCount() - 1);
    }


    @Bindable
    public String getTitle() {
        return item.getItem_name();
    }

    @Bindable
    public String getPrice() {
        return String.format("%.2f", item.getItemTotal());
    }

    public Item getItem() {
        return item;
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    //determine if the number pad is showing
    private boolean isPanelShown() {
        return binding.numberPad.getVisibility() == View.VISIBLE;

    }

    private boolean isPaymentPadShown() {
        return binding.paymentPad.getVisibility() == View.VISIBLE;
    }

    //depending on the visibility of the number pad, handle the height of the list and the number pad
    private void handleNumberPad() {
        ViewGroup.LayoutParams params = binding.list.getLayoutParams();

        if (!isPanelShown()) {
            binding.relativeLayout.setVisibility(View.GONE);
            binding.addCustom.setVisibility(View.GONE);

            if (getTab(fragContext) == 7) {
                params.height = 150;
            } else {
                params.height = 350;
            }

            // Show the panel
            animUp();
        } else {
            binding.relativeLayout.setVisibility(View.VISIBLE);

            if (getTab(fragContext) == 7) {
                params.height = 400;
            } else {
                params.height = 800;
            }

            // Hide the Panel
            animDown();
            binding.addCustom.setVisibility(View.VISIBLE);
            binding.keyboard.customAmountEditText.setText("");
        }

        binding.list.setLayoutParams(params);
        binding.list.setSelection(adapter.getCount() - 1);
        //binding.list.getLayoutManager().scrollToPosition(cart.getItemCount() - 1);
    }

    private void handlePaymentPad() {
        ViewGroup.LayoutParams params = binding.list.getLayoutParams();

        if (!isPaymentPadShown()) {
            binding.relativeLayout.setVisibility(View.GONE);
            binding.addCustom.setVisibility(View.GONE);

            if (getTab(fragContext) == 7) {
                params.height = 150;
            } else {
                params.height = 350;
            }

            // Show the panel
            paymentTypeUp();
        } else {
            // Hide the Panel
            paymentTypeDown();
            binding.relativeLayout.setVisibility(View.VISIBLE);

            if (getTab(fragContext) == 7) {
                params.height = 400;
            } else {
                params.height = 800;
            }


            // binding.addCustom.setVisibility(View.VISIBLE);
            binding.addCustom.postDelayed(new Runnable() {
                public void run() {
                    binding.addCustom.setVisibility(View.VISIBLE);
                    //binding.relativeLayout.setVisibility(View.VISIBLE);
                }
            }, 200);
            binding.keyboard.customAmountEditText.setText("");
        }

        binding.list.setLayoutParams(params);
        binding.list.setSelection(adapter.getCount() - 1);
        //binding.list.getLayoutManager().scrollToPosition(cart.getItemCount() - 1);
    }


    //animation to push the keyboard up
    private void animUp() {
        Animation bottomUp = AnimationUtils.loadAnimation(fragContext, R.anim.bottom_up);
        binding.numberPad.startAnimation(bottomUp);
        binding.numberPad.setVisibility(View.VISIBLE);
    }

    private void paymentTypeUp() {
        Animation bottomUp = AnimationUtils.loadAnimation(fragContext, R.anim.bottom_up);
        binding.paymentPad.startAnimation(bottomUp);
        binding.paymentPad.setVisibility(View.VISIBLE);
    }

    //animation to push the paymentPad down
    private void paymentTypeDown() {
        Animation bottomDown = AnimationUtils.loadAnimation(fragContext, R.anim.bottom_down);
        binding.paymentPad.startAnimation(bottomDown);
        binding.paymentPad.setVisibility(View.GONE);
    }

    //animation to push the keyboard down
    private void animDown() {
        Animation bottomDown = AnimationUtils.loadAnimation(fragContext, R.anim.bottom_down);
        binding.numberPad.startAnimation(bottomDown);
        binding.numberPad.setVisibility(View.GONE);
    }

    private void showTenderSuccessPopUp(final double tenderValue, final double billValue, final boolean isSplit) {
        //custom dialog
        tenderSuccessPopUpOpend = true;
        final Dialog dialog = new Dialog(fragContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.popup_payment_successful);
        TextView noReceipt = (TextView) dialog.findViewById(R.id.no_receipt);

        Button email = (Button) dialog.findViewById(R.id.emailButton);
        Button text = (Button) dialog.findViewById(R.id.textButton);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                emailDialog = new Dialog(fragContext, R.style.Custom_Dialog);
                emailDialog.setContentView(R.layout.receipt_email);
                emailDialog.setCancelable(true);
                emailDialog.setCanceledOnTouchOutside(false);

                Button sendEmail = (Button) emailDialog.findViewById(R.id.emailSendButton);
                final TextView emailText = (TextView) emailDialog.findViewById(R.id.emailAddress);
                ImageButton backButton = (ImageButton) emailDialog.findViewById(R.id.backButton);

                sendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //emailDialog.dismiss();
                        String email = emailText.getText().toString().trim();
                        if (email.equalsIgnoreCase("")) {
                            Toast.makeText(fragContext, fragContext.getString(R.string.emailMissing), Toast.LENGTH_LONG).show();
                            return;
                        }
                        boolean isValid = isValidEmail(email);
                        if (isValid) {
                            if (checkInternet(fragContext)) {
                                PayableService.sendReceipt(fragContext, null, BillViewModel.this, email, DBManager.invoiceNo);
                            } else {
                                showHintDialog(fragContext, fragContext.getString(R.string.no_internet), fragContext.getString(R.string.alert));
                            }
                        } else {
                            Toast.makeText(fragContext, fragContext.getString(R.string.forgotPasswordInvalid), Toast.LENGTH_LONG).show();
                        }


                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emailDialog.dismiss();
                    }
                });
                emailDialog.show();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textDialog = new Dialog(fragContext, R.style.Custom_Dialog);
                textDialog.setContentView(R.layout.receipt_text);
                textDialog.setCancelable(true);
                textDialog.setCanceledOnTouchOutside(false);

                Button sendSms = (Button) textDialog.findViewById(R.id.textSendButton);
                final TextView phoneNo = (TextView) textDialog.findViewById(R.id.phoneNumber);
                ImageButton backButton = (ImageButton) textDialog.findViewById(R.id.backButton);

                sendSms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //textDialog.dismiss();
                        String phoneNumber = phoneNo.getText().toString().trim();

                        if (phoneNumber == null || phoneNumber.equals("")) {
                            showHintDialog(fragContext, fragContext.getString(R.string.mobileMissing), fragContext.getString(R.string.alert));
                            return;
                        }
                        if (phoneNumber.length() != 10) {
                            showHintDialog(fragContext, fragContext.getString(R.string.invalidMobileNumber), fragContext.getString(R.string.alert));
                            return;
                        }
                        if (!phoneNumber.matches("[0-9]+")) {
                            showHintDialog(fragContext, fragContext.getString(R.string.invalidMobileNumber), fragContext.getString(R.string.alert));
                            return;
                        }

                        if (phoneNumber.equalsIgnoreCase("")) {
                            Toast.makeText(fragContext, fragContext.getString(R.string.mobileMissing), Toast.LENGTH_LONG).show();
                            return;
                        } else {

                            if (!phoneNumber.equalsIgnoreCase("") || phoneNumber != null) {
                                if (checkInternet(fragContext)) {
                                    //showProgressDialog(context, context.getResources().getString(R.string.pleaseWait));
                                    PayableService.sendSms(fragContext, null, BillViewModel.this, phoneNumber, DBManager.invoiceNo);
                                } else {
                                    showHintDialog(fragContext, fragContext.getString(R.string.no_internet), fragContext.getString(R.string.alert));
                                }
                            } else {
                                Toast.makeText(fragContext, "Invalid phone no " + phoneNo, Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textDialog.dismiss();
                    }
                });
                textDialog.show();

            }
        });


        final double remaining;
        if (isSplit) {
            remaining = setBalanceText(0, 0, false);
            dialog.setCanceledOnTouchOutside(false);
            noReceipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.split_pay_dark, 0, 0, 0);
            noReceipt.setText(fragContext.getString(R.string.cont));

            if (remaining == 0) {
                noReceipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pop_success_norecepit, 0, 0, 0);
                noReceipt.setText(fragContext.getString(R.string.noReceipt));
            }

        } else {
            noReceipt.setCompoundDrawablesWithIntrinsicBounds(R.drawable.pop_success_norecepit, 0, 0, 0);
            noReceipt.setText(fragContext.getString(R.string.noReceipt));
            remaining = 0;
        }
        final double change = tenderValue - billValue;

        DisplayMetrics metrics = new DisplayMetrics();
        fragContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;

        dialog.getWindow().setLayout((widthPixels / 8) * 6, LinearLayout.LayoutParams.WRAP_CONTENT);

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(final DialogInterface arg0) {
                dialog.dismiss();
                tenderSuccessPopUpOpend = false;
                if (!isSplit) {
                    Intent intent = new Intent(fragContext, MainActivity.class);
                    fragContext.startActivity(intent);
                    fragContext.finish();
                    Cart.totalBill = 0;
                }

            }
        });

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_back);
        Button noReceiptButton = (Button) dialog.findViewById(R.id.noReceiptButton);
        TextView txtChange = (TextView) dialog.findViewById(R.id.txt_change);
        TextView txtTender = (TextView) dialog.findViewById(R.id.out_of);

        if (isSplit) {
            dialogButton.setVisibility(View.INVISIBLE);
        } else {
            dialogButton.setVisibility(View.VISIBLE);
        }

        txtChange.setText("Rs. " + String.format("%.2f", change));
        txtTender.setText(fragContext.getString(R.string.changeOutOf) + " " + String.format("%.2f", tenderValue));
        // if button is clicked, close the custom dialog
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                tenderSuccessPopUpOpend = false;
                if (!isSplit) {
                    Intent intent = new Intent(fragContext, MainActivity.class);
                    fragContext.startActivity(intent);
                    fragContext.finish();
                }

            }
        });
        noReceiptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isSplit) {
                    if (remaining == 0) {
                        tenderSuccessPopUpOpend = false;
                        dialogSplit.dismiss();
                        Intent intent = new Intent(fragContext, MainActivity.class);
                        fragContext.startActivity(intent);
                        fragContext.finish();
                        Cart.totalBill = 0;
                    }

                }

                dialog.dismiss();
                tenderSuccessPopUpOpend = false;

            }
        });

        dialog.show();
        //if(clickedItem.getItemType()){}

    }

    public void showSplitPaymentPopUp(final Transaction transactionToEdit, final double billValue, final double initialBill, final double previousRemaining, final double previousSplitted, ArrayList<Payment> existingPaymentList) {
        //custom dialogSplit
        splitPopUpOpend = true;
        dialogSplit = new Dialog(fragContext);
        dialogSplit.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSplit.setContentView(R.layout.split_payment_pop_up);
        dialogSplit.setCanceledOnTouchOutside(false);

        DisplayMetrics metrics = new DisplayMetrics();
        fragContext.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;


        dialogButtonBack = (Button) dialogSplit.findViewById(R.id.btn_back);
        TextView billPrice = dialogSplit.findViewById(R.id.totalPrice);
        Button spCardButton = (Button) dialogSplit.findViewById(R.id.card_payment);
        Button spCashButton = (Button) dialogSplit.findViewById(R.id.cash_payment);
        final EditText splitPrice = dialogSplit.findViewById(R.id.splitPrice);
        final EditText tenderPrice = dialogSplit.findViewById(R.id.tenderPrice);
        final ListView paymentList = (ListView) dialogSplit.findViewById(R.id.list);
        balanceTxt = dialogSplit.findViewById(R.id.textView2);

        //billPrice.setText("Rs " + String.format("%.2f", billValue));

        billPrice.setText("Rs " + billValue);

        splitPrice.setFilters(new InputFilter[]{new BillViewModel.DecimalDigitsInputFilter(7, 2)});
        tenderPrice.setFilters(new InputFilter[]{new BillViewModel.DecimalDigitsInputFilter(7, 2)});

        //save initial transaction
        splitList = new ArrayList<>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String d = df.format(Calendar.getInstance().getTime());
        Date date = null;

        try {
            date = df.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Refund refund = new Refund();
        refund.setPaymentType(Config.REFUND);
        refund.settRefundAmount(0.00);
        refund.settReasonForRefund("");

        ArrayList<Refund> refundArrayList = new ArrayList<>();
        refundArrayList.add(refund);

        String tId = UUID.randomUUID().toString();

        byte[] bytes = null;
        String deviceID = Config.getDeviceID(fragContext);

        try {
            bytes = deviceID.getBytes("US-ASCII");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String invoiceNo = null;
        int invoice = 0;

        if (bytes != null) {

            for (byte ch : bytes) {
                invoice += ch;
            }

            invoiceNo = Config.randomString(6) + invoice;
        }


        final Transaction totalTransaction = new Transaction();

        if (manager == null) {
            manager = new DBManager();
        }

        if (transactionToEdit == null) {

            totalTransaction.settId(tId);
            totalTransaction.setInvoiceNo(invoiceNo);
            totalTransaction.settDateTime(date);
            totalTransaction.setIncomplete(true);
            totalTransaction.settItems(cart.itemArrayList);
            totalTransaction.settValue(billValue);
            totalTransaction.setInitialAmount(initialBill);
            totalTransaction.setTransactionList(splitList);
            totalTransaction.setTRefund(refundArrayList);


            try {
                manager.saveTransaction(fragContext, totalTransaction);

                //dialogButtonBack.setVisibility(View.VISIBLE);
            } catch (CouchbaseLiteException e) {
                e.printStackTrace();
            }
            Config.savedTransaction = totalTransaction;
        } else {

            totalTransaction.settId(transactionToEdit.gettId());
            totalTransaction.setPaymentType(transactionToEdit.getPaymentType());
            totalTransaction.settDateTime(transactionToEdit.gettDateTime());
            totalTransaction.setIncomplete(true);
            totalTransaction.setInvoiceNo(transactionToEdit.getInvoiceNo());
            totalTransaction.settItems(transactionToEdit.gettItems());
            totalTransaction.settValue(transactionToEdit.gettValue());
            totalTransaction.setInitialAmount(initialBill);
            totalTransaction.setTransactionList(splitList);
            totalTransaction.setTRefund(refundArrayList);

        }

//        splitList = new ArrayList<>();
//        remaining = 0;
//        totalSplits = 0;

        if (existingPaymentList != null) {

            splitList = existingPaymentList;
            final PaymentAdapter paymentAdapter = new PaymentAdapter(splitList, fragContext);
            paymentList.setAdapter(paymentAdapter);
            remaining = previousRemaining;
            totalSplits = previousSplitted;
            balanceTxt.setText("Rs " + String.format("%.2f", remaining) + " remaining");
            Cart.totalBill = billValue;
            dialogButtonBack.setVisibility(View.INVISIBLE);
        } else {
            totalSplits = 0;
            dialogButtonBack.setVisibility(View.VISIBLE);
        }


        dialogSplit.setOnDismissListener(new DialogInterface.OnDismissListener() {

            @Override
            public void onDismiss(DialogInterface dialog) {

                count = 0;
            }
        });

        spCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                dialogSplit.dismiss();
                splitPopUpOpend = false;
            }

        });

        spCashButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (splitPrice.getText().toString().equals("") || splitPrice.getText().toString().equals(".")) {
                    Toast.makeText(fragContext, fragContext.getString(R.string.splitAmountEmpty), Toast.LENGTH_LONG).show();
                    return;

                }
                if (Double.parseDouble(splitPrice.getText().toString()) == 0) {
                    Toast.makeText(fragContext, fragContext.getString(R.string.splitZero), Toast.LENGTH_LONG).show();
                    return;

                }
                if (tenderPrice.getText().toString().equals("") || tenderPrice.getText().toString().equals(".")) {
                    Toast.makeText(fragContext, fragContext.getString(R.string.tenderEmpty), Toast.LENGTH_LONG).show();
                    return;

                }
                if (Double.parseDouble(tenderPrice.getText().toString()) == 0) {
                    Toast.makeText(fragContext, fragContext.getString(R.string.tenderZero), Toast.LENGTH_LONG).show();
                    return;

                }


                splitPopUpOpend = false;

                double splittedValue = Double.parseDouble(splitPrice.getText().toString());

                double value = Double.parseDouble(splitPrice.getText().toString());
                double tenderAmount = Double.parseDouble(tenderPrice.getText().toString());
                if (tenderAmount < value) {
                    Toast.makeText(fragContext, fragContext.getString(R.string.lessTender), Toast.LENGTH_LONG).show();

                } else {

                    if (billValue < value) {
                        Toast.makeText(fragContext, fragContext.getString(R.string.higherSplit), Toast.LENGTH_LONG).show();
                        return;

                    }

                    totalSplits = totalSplits + value;
                    remaining = billValue - totalSplits;

                    if (remaining < 0) {
                        Toast.makeText(fragContext, fragContext.getString(R.string.exceededSplit), Toast.LENGTH_LONG).show();
                        totalSplits = totalSplits - value;
                        remaining = billValue + totalSplits;
                        return;

                    }

                    double tenderBalance = billValue - splittedValue;
                    Payment payment = new Payment();
                    payment.setPaymentType(Config.CASH);
                    payment.settAmount(Double.parseDouble(splitPrice.getText().toString()));
                    payment.settTenderAmount(tenderAmount);
                    payment.settBalance(tenderBalance);

                    double balance = tenderAmount - splittedValue;

                    splitPrice.setText("");
                    tenderPrice.setText("");

                    splitList.add(count, payment);
                    balanceTxt.setText("Rs " + String.format("%.2f", remaining) + " remaining");

                    count = count + 1;

                    Config.isInCompletePayment = true;
                    Config.totalAmount = billValue;
                    Config.splittedAmount = totalSplits;
                    Config.remainingAmount = remaining;
                    Config.initialAmount = initialBill;
                    Config.savedSplitList = splitList;
                    Config.tId = totalTransaction.gettId();

                    totalTransaction.setTransactionList(splitList);

                    if(remaining == 0){
                        totalTransaction.setIncomplete(false);

                    }else {
                        totalTransaction.setIncomplete(true);
                    }
                    try {
                        manager.editTransaction(totalTransaction, fragContext);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    if (splitList.size() == 0) {
                        dialogButtonBack.setVisibility(View.VISIBLE);
                    } else {
                        dialogButtonBack.setVisibility(View.INVISIBLE);
                    }

                    final PaymentAdapter paymentAdapter = new PaymentAdapter(splitList, fragContext);
                    paymentList.setAdapter(paymentAdapter);

                    if (remaining == 0) {
                        ArrayList<Item> updatedQuntityArray = new ArrayList<Item>();
                        long remainQuantity;

                        for(int i = 0; i < totalTransaction.gettItems().size(); i++) {
                            Item item = totalTransaction.gettItems().get(i);

                            if (updatedQuntityArray.size() != 0) {

                                for (int x = 0; x < updatedQuntityArray.size(); x++) {
                                    if (updatedQuntityArray.get(x).getPriceVariations().get(0).getVariation().equals(item.getPriceVariations().get(0).getVariation())) {
                                        updatedQuntityArray.get(x).setQuantity(updatedQuntityArray.get(x).getQuantity() + item.getQuantity());

                                    } else {
                                        updatedQuntityArray.add(item);
                                    }
                                }
                            } else {
                                updatedQuntityArray.add(item);
                                //remainQuantity = item.getPriceVariations().get(0).getStk_count() - item.getQuantity();

                            }
                        }

                        for(int i = 0; i < updatedQuntityArray.size(); i++) {
                            Item item = updatedQuntityArray.get(i);
                            remainQuantity = item.getPriceVariations().get(0).getStk_count() - item.getQuantity();
                            //remainQuantity =  item.getPriceVariations().get(0).getStk_count()- item.getQuantity();

                            String itemId = item.getItem_id();
                            final Document document = manager.returnDB(fragContext).getExistingDocument(itemId);

                            final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();

                            ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
                            JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();

                            for (int x = 0; x < jArray.size(); x++) {
                                PriceVariation variant = gson.fromJson(jArray.get(x), PriceVariation.class);

                                if (variant.getVariation().equals(item.getPriceSelected().getVariation())) {
                                    variant.setStk_count(remainQuantity);
                                }
                                priceVariations.add(variant);

                            }


                            try {


                                document.update(new Document.DocumentUpdater() {
                                    @Override
                                    public boolean update(UnsavedRevision newRevision) {
                                        Map<String, Object> properties = newRevision.getUserProperties();

                                        properties.put("price_variations", priceVariations);

                                        SharedPreferences loginPreferences = fragContext.getSharedPreferences("loginPrefs", 0);
                                        String owner = loginPreferences.getString("owner", null);
                                        String storeId = loginPreferences.getString("storeId", null);

                                        properties.put("owner", owner);
                                        properties.put("store_name", storeId);

                                        newRevision.setUserProperties(properties);
                                        return true;
                                    }
                                });


                            } catch (CouchbaseLiteException e) {
                                e.printStackTrace();
                            }
                        }





                            list = manager.loadTransactions(fragContext);

                        final ArrayList<Object> listItems = new ArrayList<>();

                        for(int i = 0; i< list.size();i++){
                            listItems.add(list.get(i));
                        }

                        Config.isInCompletePayment = false;

                        inCompletedPayments = fragContext.getSharedPreferences("paymentPrefs", MODE_PRIVATE);
                        paymentPrefsEditor = inCompletedPayments.edit();

                        paymentPrefsEditor.putBoolean("isPaymentComplete", Config.isInCompletePayment);
                        paymentPrefsEditor.commit();
                        count = 0;
                    }


                    if (tenderSuccessPopUpOpend == false) {
                        showTenderSuccessPopUp(tenderAmount, splittedValue, true);
                    }
                }


            }

        });


        dialogButtonBack.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                dialogSplit.dismiss();
                Config.isInCompletePayment = false;
                splitPopUpOpend = false;
                totalSplits = 0.00;
                manager.deleteTransaction(totalTransaction.gettId(), fragContext);
                return false;
            }

        });
        dialogSplit.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode, KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (splitList.size() != 0) {
                        splitPopUpOpend = true;
                        return true;
                    }
                    splitPopUpOpend = false;
                    //dialogSplit.dismiss();
                    //splitPopUpOpend = false;
                }
                return false;
            }
        });

        dialogSplit.show();
        //cardSale(billValue);
    }

    public static double setBalanceText(double trashed, int index, boolean isdelete) {
        double billValue = new BigDecimal(Cart.totalBill).setScale(2, RoundingMode.HALF_UP).doubleValue();

        double updated = (billValue - totalSplits) + trashed;

        if (isdelete) {
            balanceTxt.setText("Rs " + String.format("%.2f", updated) + " remaining");
            totalSplits = totalSplits - trashed;
            count = count - 1;
            //splitTransactionList.remove(index);
        }
        if (updated == billValue) {
            dialogButtonBack.setVisibility(View.VISIBLE);
        }


        return updated;
    }

    private void cardSale(double billValue) {
        Intent intent = new Intent(fragContext, SdkCardSales.class);
        Bundle bundle = new Bundle();
        bundle.putDouble("billValue", billValue);
        intent.putExtras(bundle);
        fragContext.startActivity(intent);
    }


    /*
   On long press of a item in the grid, show this pop up
   1. Handles quantity addition and subtraction
   2. Changes price according to quantity and price variations
    */
    public void showQtyAndVariants(final Item item, final int position) {
        editedQty = 1;
        Gson gson = new Gson();

        manager = new DBManager();
        final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
        ArrayList<Discount> allDiscounts = new ArrayList<>();

        Query query = manager.returnDB(fragContext).createAllDocumentsQuery();
        allDiscounts = manager.loadDiscounts(fragContext, query);

        final Dialog dialog = new Dialog(fragContext, R.style.Custom_Dialog);
        dialog.setContentView(R.layout.add_qty_variants_popup);

        Button btnBack = (Button) dialog.findViewById(R.id.btn_back);
        Button btnDone = (Button) dialog.findViewById(R.id.btn_done);

        ListView variantList = (ListView) dialog.findViewById(R.id.variant_list);
        ListView discountList = (ListView) dialog.findViewById(R.id.list);

        txtItemName = (TextView) dialog.findViewById(R.id.prod_name);
        final TextView txtQty = (TextView) dialog.findViewById(R.id.qty);
        txtPrice = (TextView) dialog.findViewById(R.id.price);
        TextView txtMinus = (TextView) dialog.findViewById(R.id.minus);
        TextView txtPlus = (TextView) dialog.findViewById(R.id.plus);
        final EditText enteredQty = (EditText) dialog.findViewById(R.id.entered_qty);

        txtItemName.setText(item.getItem_name());

        txtItemName.setEllipsize(TextUtils.TruncateAt.END);
        txtItemName.setHorizontallyScrolling(false);
        txtItemName.setSingleLine();

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        enteredQty.setText(item.getQuantity() + "");
        txtQty.setText("(x" + item.getQuantity() + ")");
        editedQty = item.getQuantity();
        txtPrice.setText(String.format("%.2f", item.getItemTotal()));
        DiscountListAdapter.itemTotal = item.getItemTotal();


        //Populate variant listview
        VariantAdapter varAdapter = new VariantAdapter(item.getPriceVariations(), fragContext, null, true);
        variantList.setAdapter(varAdapter);
        //selectedVariation = item.getPriceVariations().get(0);
        enteredQty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (selectedVariation == null) {
//                    Toast.makeText(fragContext, "Please select a variation", Toast.LENGTH_SHORT).show();
//                    return;
                    if (item.getPriceVariations().size() != 0) {
                        selectedVariation = item.getPriceVariations().get(0);
                    }
                }

                if (s.length() != 0) {
                    editedQty = Long.parseLong(enteredQty.getText().toString());
                    txtQty.setText("(x" + editedQty + ")");
                    String total;
                    if (selectedVariation != null) {
                        total = getTotal(fragContext, editedQty, selectedVariation.getPrice(), item.getItemDiscount());
                        txtPrice.setText(total);
                        DiscountListAdapter.itemTotal = Double.parseDouble(total);
                    } else {
                        total = getTotal(fragContext, editedQty, item.getItemTotal(), item.getItemDiscount());
                        txtPrice.setText(total);
                        DiscountListAdapter.itemTotal = Double.parseDouble(total);
                    }

                } else {
                    editedQty = 0;
                    txtQty.setText("(x" + editedQty + ")");
                    txtPrice.setText("0.00");
                }
            }
        });

        //compare discount list with selected item's discount
        if (item.getItemDiscount() != null) {
            for (int i = 0; i < allDiscounts.size(); i++) {
                if (allDiscounts.get(i).getDiscount_id().equals(item.getItemDiscount().getDiscount_id())) {
                    allDiscounts.get(i).setChecked(true);
                }
            }
        }

        ArrayList<Discount> nonZeroDiscounts = new ArrayList<>();
        for (int i = 0; i < allDiscounts.size(); i++) {
            if (allDiscounts.get(i).getDiscount_value() > 0) {
                nonZeroDiscounts.add(allDiscounts.get(i));
            }
        }

        DiscountListAdapter discountAdapter = new DiscountListAdapter(nonZeroDiscounts, fragContext, null, BillViewModel.this, item);
        discountList.setAdapter(discountAdapter);

        variantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                txtItemName.setText("");
                selectedVariation = item.getPriceVariations().get(i);
                //txtPrice.setText((Double.parseDouble(selectedVariation.getPrice()) * editedQty) + "");
                String total = getTotal(fragContext, editedQty, selectedVariation.getPrice(), item.getItemDiscount());
                txtPrice.setText(total);
                DiscountListAdapter.itemTotal = Double.parseDouble(total);

                String CurrentString = item.getItem_name();
                String[] baseName = CurrentString.split("-");
                String baseName1 = baseName[0];
                txtItemName.setText(baseName1 + "-" + selectedVariation.getVariation());
            }
        });


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editedQty != 0) {
                    //String price = getTotal(fragContext, editedQty, selectedVariation.getPrice(), item.getItemDiscount());
                    if (selectedVariation != null) {
                        item.setItemTotal(getTotal(fragContext, editedQty, selectedVariation.getPrice(), item.getItemDiscount()));
                        item.setItemActualTotal(getActualTotal());
                    } else {
                        item.setItemTotal(getTotal(fragContext, editedQty, item.getItemTotal(), item.getItemDiscount()));
                        item.setItemActualTotal(getActualTotal());
                    }
                    item.setQuantity(editedQty);

                    String CurrentString = item.getItem_name();
                    String[] baseName = CurrentString.split("-");
                    String baseName1 = baseName[0];

                    if (selectedVariation != null) {
                        item.setItem_name(baseName1 + "-" + selectedVariation.getVariation());
                    } else {
                        item.setItem_name(baseName1);
                    }

                    cart.itemArrayList.set(position, item);
                    adapter.notifyDataSetChanged();
                    updateItemCountAndBill();
                    dialog.dismiss();
                } else {
                    showHintDialog(fragContext, fragContext.getString(R.string.invalidQty), fragContext.getString(R.string.alert));
                }
            }
        });

        txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (selectedVariation == null) {
//                    Toast.makeText(fragContext, "Please select a variation", Toast.LENGTH_SHORT).show();
//                    return;
                    if (item.getPriceVariations().size() != 0) {
                        selectedVariation = item.getPriceVariations().get(0);
                    }
                }

                if (enteredQty.getText().toString().trim().length() == 0) {
                    editedQty = 0;
                } else {
                    editedQty = Long.parseLong(enteredQty.getText().toString().trim());
                }
                editedQty++;


                enteredQty.setText(editedQty + "");
                txtQty.setText("(x" + editedQty + ")");

            }
        });

        txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedVariation == null) {
//                    Toast.makeText(fragContext, "Please select a variation66", Toast.LENGTH_SHORT).show();
//                    return;
                    if (item.getPriceVariations().size() != 0) {
                        selectedVariation = item.getPriceVariations().get(0);
                    }
                }

                if (enteredQty.getText().toString().trim().length() == 0) {
                    editedQty = 0;
                } else {
                    editedQty = Integer.parseInt(enteredQty.getText().toString().trim());
                }

                if (editedQty != 0) {
                    editedQty--;
                    enteredQty.setText(editedQty + "");
                    txtQty.setText("(x" + editedQty + ")");
                }
            }
        });

        if (clickedItem.getItemType().equalsIgnoreCase(Config.ITEM) || clickedItem.getItemType().equalsIgnoreCase(Config.CUSTOM)) {
            dialog.show();
        }

    }

    public void handleIncomingDiscount(Discount discount) {

        if (clickedItem.getPriceVariations().size() == 1) {
            selectedVariation = clickedItem.getPriceVariations().get(0);
        }


        clickedItem.setItemDiscount(discount);
        //String total = getTotal(fragContext, editedQty, selectedVariation.getPrice(), clickedItem.getItemDiscount());

        if (selectedVariation != null) {
            String CurrentString = clickedItem.getItem_name();
            String[] baseName = CurrentString.split("-");
            String baseName1 = baseName[0];
            txtPrice.setText(getTotal(fragContext, editedQty, selectedVariation.getPrice(), clickedItem.getItemDiscount()));
            txtItemName.setText(baseName1 + "-" + selectedVariation.getVariation().toString());
        } else {
            txtPrice.setText(getTotal(fragContext, editedQty, clickedItem.getItemTotal(), clickedItem.getItemDiscount()));
            txtItemName.setText(clickedItem.getItem_name().toString());
        }


    }

    public static void didReceiveSendReceiptResults(Context context, int responseCode, JSONObject response) {
        if (responseCode == Config.OK) {
            String error = null;
            try {
                if (response.get("error") != null) {
                    error = response.get("error").toString();
                    if (error.equalsIgnoreCase("no user")) {
                        Toast.makeText(context, context.getString(R.string.emailWrong), Toast.LENGTH_LONG).show();

                    }
                } else {
                    emailDialog.dismiss();
                }
            } catch (Exception e) {
                Toast.makeText(context, "cc", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, SignInActivity.class);
                context.startActivity(intent);
            }

        } else {
            Toast.makeText(context, context.getString(R.string.serverError), Toast.LENGTH_LONG).show();
            emailDialog.dismiss();
        }

    }

    public static void didReceiveSendSmsResults(Context context, int responseCode, JSONObject response) {
        if (responseCode == Config.OK) {
            String error = null;
            try {
                if (response.get("error") != null) {
                    error = response.get("error").toString();
                    if (error.equalsIgnoreCase("no user")) {
                        Toast.makeText(context, context.getString(R.string.emailWrong), Toast.LENGTH_LONG).show();

                    }
                }
            } catch (Exception e) {
                //Toast.makeText(context, "Exception in msg sending- billviewmodel", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(context, SignInActivity.class);
//                context.startActivity(intent);
            }

        } else {
            Toast.makeText(context, context.getString(R.string.serverError), Toast.LENGTH_LONG).show();
        }

    }

    // limit discount amount to two decimal places
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }


}