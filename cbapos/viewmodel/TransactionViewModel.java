package com.cbasolutions.cbapos.viewmodel;

import android.app.Dialog;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.adapter.ItemsAdapter1;
import com.cbasolutions.cbapos.adapter.TransactionAdapter;
import com.cbasolutions.cbapos.adapter.TransactionDetailAdapter;
import com.cbasolutions.cbapos.adapter.TransactionItemsAdapter;
import com.cbasolutions.cbapos.databinding.TransactionLayoutBinding;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.DateTime;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.Refund;
import com.cbasolutions.cbapos.model.Transaction;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by "Don" on 11/14/2017.
 * Class Functionality :-
 */

public class TransactionViewModel extends CRBaseObservable {

    private double total = 0.0;
    private boolean isSelected = false;
    private Transaction trans;
    String refundReason = null;
    int pos = 1;
    TransactionAdapter transactionAdapter;
    TransactionDetailAdapter transactionDatailAdapter;
    FragmentActivity activity;
    public static double transactionVal = 0;
    public static String transactionId = "";
    private List<Payment> paymentList = new ArrayList<Payment>();
    DBManager manager;
    TransactionLayoutBinding binding;
    public static TransactionViewModel transactionVM = null;

    List<Transaction> list = new ArrayList<>();
    ArrayList<Object> listItems = new ArrayList<>();
    int visibleView = Config.TRANSACTION_VIEW;
    public static boolean isInitial = false;
    double splittedAmount = 0;

    public TransactionViewModel(final FragmentActivity activity, final TransactionLayoutBinding binding) {
        this.activity = activity;
        this.binding = binding;
        transactionVM = this;
        isInitial = true;

        manager = new DBManager();
        list = manager.loadTransactions(activity);
//        Log.d("mash_logs",""+list.size());
        divideItems(list);
        getTransactionList();

        if(list.size() == 0){
            binding.refundButton.setVisibility(View.INVISIBLE);
        }else{
            binding.refundButton.setVisibility(View.VISIBLE);
        }

        binding.refundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isSelected) {
                    Toast.makeText(activity, activity.getString(R.string.transactionNotSelected), Toast.LENGTH_LONG).show();
                    return;
                }

                if(trans.getTRefund().size() != 0) {

                    if (trans.getTRefund().get(0).gettRefundAmount() > 0.0) {
                        Toast.makeText(activity, activity.getString(R.string.refundAgain), Toast.LENGTH_LONG).show();
                        return;
                    }
                }

                final Dialog dialog = new Dialog(activity);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.issue_refund_pop_up);

                DisplayMetrics displayMetrics = new DisplayMetrics();
                activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int height = displayMetrics.heightPixels;
                int width = displayMetrics.widthPixels;

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = width/2;
                lp.height = height-100;
                lp.gravity = Gravity.CENTER;

                dialog.getWindow().setAttributes(lp);

                ImageButton backButton = dialog.findViewById(R.id.backButton);

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                RelativeLayout layout_1 = dialog.findViewById(R.id.layout_1);
                RelativeLayout layout_2 = dialog.findViewById(R.id.layout_2);
                RelativeLayout layout_3 = dialog.findViewById(R.id.layout_3);

                final CheckBox checkBox1 = dialog.findViewById(R.id.checkBox1);
                final CheckBox checkBox2 = dialog.findViewById(R.id.checkBox2);
                final CheckBox checkBox3 = dialog.findViewById(R.id.checkBox3);

                layout_1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (checkBox1.isChecked()) {
                            checkBox1.setChecked(false);
                            refundReason = null;
                        } else {
                            checkBox1.setChecked(true);
                            refundReason = Config.REFUND_REASON_1;
                        }

                        checkBox2.setChecked(false);
                        checkBox3.setChecked(false);
                    }
                });

                layout_2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBox1.setChecked(false);

                        if (checkBox2.isChecked()) {
                            checkBox2.setChecked(false);
                            refundReason = null;
                        } else {
                            checkBox2.setChecked(true);
                            refundReason = Config.REFUND_REASON_2;
                        }

                        checkBox3.setChecked(false);
                    }
                });

                layout_3.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        checkBox1.setChecked(false);
                        checkBox2.setChecked(false);

                        if (checkBox3.isChecked()) {
                            checkBox3.setChecked(false);
                            refundReason = null;
                        } else {
                            checkBox3.setChecked(true);
                            refundReason = Config.REFUND_REASON_3;
                        }
                    }
                });

                TextView refundAmount = dialog.findViewById(R.id.refund_amount);
                refundAmount.setText("Rs. " + String.format( "%.2f", total));

                final EditText amount = dialog.findViewById(R.id.amountEditText);
                final EditText otherReason = dialog.findViewById(R.id.otherEditText);

                Button refundButton = dialog.findViewById(R.id.refund_button);

                refundButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (amount.getText().toString().isEmpty()) {
                            Toast.makeText(activity, activity.getString(R.string.refundAmountMissing), Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            double refundAmount = 0.0;

                            try {
                                refundAmount = Double.parseDouble(amount.getText().toString());
                            } catch (NumberFormatException e) {
                                Toast.makeText(activity, activity.getString(R.string.invalidRefundAmount), Toast.LENGTH_LONG).show();
                                return;
                            }

                            if (refundAmount > total) {
                                Toast.makeText(activity, activity.getString(R.string.refundAmountGreater), Toast.LENGTH_LONG).show();
                                return;
                            } else if (refundAmount == 0) {
                                Toast.makeText(activity, activity.getString(R.string.lessRefundAmount), Toast.LENGTH_LONG).show();
                                return;
                            } else {
                                if (refundReason == null && otherReason.getText().toString().isEmpty()) {
                                    Toast.makeText(activity, activity.getString(R.string.refundReason), Toast.LENGTH_LONG).show();
                                } else {
                                    DBManager manager = new DBManager();

//                                    Payment refundPayment = new Payment();
//                                    refundPayment.setPaymentType(Config.REFUND);
                                    //refundPayment.setRefundAmount(refundAmount);
                                    //trans.settRefundAmount(refundAmount);

                                    Refund refund = new Refund();
                                    refund.setPaymentType(Config.REFUND);
                                    refund.settRefundAmount(refundAmount);

                                    if (refundReason == null) {
                                        //trans.settReasonForRefund(otherReason.getText().toString());
                                        refund.settReasonForRefund(otherReason.getText().toString());

                                    } else {
                                        //trans.settReasonForRefund(refundReason);
                                        refund.settReasonForRefund(refundReason);
                                    }

                                    ArrayList<Refund> refundArrayList = new ArrayList<>();
                                    refundArrayList.add(refund);

                                    trans.setTRefund(refundArrayList);

                                    int size = trans.getTransactionList().size();
                                    //paymentList = trans.getTransactionList();
                                    //paymentList.add(refund);

                                    //trans.getTransactionList().add(refundPayment);
                                    transactionDatailAdapter.notifyDataSetChanged();


                                    try {
                                        manager.editTransaction(trans, activity);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        Toast.makeText(activity, activity.getString(R.string.refundFailed), Toast.LENGTH_LONG).show();
                                        dialog.dismiss();
                                        return;
                                    }

                                    transactionAdapter.notifyDataSetChanged();
                                    setSelected(trans, binding, activity);
                                    Toast.makeText(activity, activity.getString(R.string.refundSuccess), Toast.LENGTH_LONG).show();
                                    //setSelected(trans, binding, activity);
                                    refundReason = null;
                                    dialog.dismiss();
                                }
                            }
                        }
                    }
                });

                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

                dialog.show();
            }
        });

        binding.chargeNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final double totalAmount = trans.gettValue();
                final double initialAmount = trans.getInitialAmount();

                splittedAmount = 0;
                for(int i=0; i< trans.getTransactionList().size(); i++){
                    splittedAmount = splittedAmount + trans.getTransactionList().get(i).gettAmount();

                }
                final double remainAmount = trans.gettValue() - splittedAmount;
                final ArrayList<Payment> existingPaymentList = (ArrayList)trans.getTransactionList();

                Intent intent = new Intent(activity, MainActivity.class);
                activity.startActivity(intent);
                activity.finish();

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        BillViewModel.billVMContext.showSplitPaymentPopUp(trans,totalAmount,initialAmount,remainAmount,splittedAmount,existingPaymentList);

                    }
                }, 1500);

                //BillViewModel.billVMContext.showSplitPaymentPopUp(trans,totalAmount,initialAmount,remainAmount,splittedAmount,existingPaymentList);
            }});

    }

    private void divideItems(List<Transaction> list) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);

        Collections.sort(list, new Comparator<Transaction>() {
            @Override
            public int compare(Transaction o1, Transaction o2) {
                if(o1.gettDateTime().before(o2.gettDateTime())){
                    return 1;
                }
                return -1;
            }
        });

        final ArrayList<Object> listItems = new ArrayList<>();

        Date d2 = null;

        for (int i = 0; i < list.size(); i++) {
            Date d1 = list.get(i).gettDateTime();
            String reportDate = df.format(d1);

            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            if (i==0) {
                DateTime dateTime = new DateTime();
                dateTime.setDate(d1);

                listItems.add(dateTime);
                listItems.add(list.get(i));
            } else {
                try {
                    if (d2.equals(newDateFormat.parse(reportDate.split("\\s+")[0]))) {
                        listItems.add(list.get(i));
                    } else {
                        DateTime dateTime = new DateTime();
                        dateTime.setDate(d1);

                        listItems.add(dateTime);
                        listItems.add(list.get(i));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            try {
                d2 = newDateFormat.parse(reportDate.split("\\s+")[0]);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }



        if (listItems.size() > 1 && isInitial) {
            if (listItems.get(1) instanceof Transaction) {
                pos = 1;
                isSelected = true;
                trans = (Transaction) listItems.get(1);
                transactionVal = trans.gettValue();
                transactionId = trans.getInvoiceNo();
                setSelected((Transaction) listItems.get(1), binding, activity);
            }
        }else{
            if(!isInitial){
                setSelected(trans, binding, activity);
            }
        }


        transactionAdapter = new TransactionAdapter(activity, listItems, pos);
        binding.list1.setAdapter(transactionAdapter);

        binding.list1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int j, long l) {
                isInitial = false;
                if (listItems.get(j) instanceof Transaction) {
                    isSelected = true;
                    trans = (Transaction) listItems.get(j);
                    transactionVal = trans.gettValue();
                    transactionId = trans.getInvoiceNo();

                    pos = j;
                    transactionAdapter = new TransactionAdapter(activity, listItems, pos);
                    binding.list1.setAdapter(transactionAdapter);

                    setSelected(trans, binding, activity);
                }
            }
        });
    }

    private void setSelected(Transaction trans, TransactionLayoutBinding binding, FragmentActivity activity) {
        Date d1 = trans.gettDateTime();

        SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
        String d2 = newDateFormat.format(d1);

        newDateFormat.applyPattern("EEEE");
        //String myDate1 = newDateFormat.format(d1) + " " + d2.split("\\s+")[0].split("-")[1] + "/" +d2.split("\\s+")[0].split("-")[0].substring(2,4);

//        String myDate1 = newDateFormat.format(d1) + " " + d2.split("\\s+")[0].split("-")[0].substring(2,4)+"/"+
//                d2.split("\\s+")[0].split("-")[1] + "/" +d2.split("\\s+")[0].split("-")[0];
        String myDate1 = newDateFormat.format(d1) + " " + d2.split("\\s+")[0].split("-")[2] + "/" + d2.split("\\s+")[0].split("-")[1] + "/" +
                d2.split("\\s+")[0].split("-")[0].substring(2, 4);

        binding.dateTxt.setText(myDate1);

        int time = Integer.parseInt(d2.split("\\s")[1].split(":")[0]);
        String t = null;

        if (time == 0) {
            t = "12" + "." + String.valueOf(d2.split("\\s")[1].split(":")[1]) + "am";
        } else if (time == 12) {
            t = "12" + "." + String.valueOf(d2.split("\\s")[1].split(":")[1]) + "pm";
        } else if (time > 12) {
            t = String.valueOf(time - 12) + "." + String.valueOf(d2.split("\\s")[1].split(":")[1]) + "pm";
        } else {
            t = String.valueOf(time) + "." + String.valueOf(d2.split("\\s")[1].split(":")[1]) + "am";
        }

        paymentList = trans.getTransactionList();

        if(trans.isIncomplete()){

            binding.incompleteLay.setVisibility(View.VISIBLE);

            double paymentTotal = 0;
            for(int i=0; i< trans.getTransactionList().size(); i++){
                paymentTotal = paymentTotal + trans.getTransactionList().get(i).gettAmount();

            }
            double remainingAmount = trans.gettValue() - paymentTotal;
            binding.incompleteAmount.setText("Rs "+String.format("%.2f", remainingAmount));
        }else{
            binding.incompleteLay.setVisibility(View.INVISIBLE);
        }


        transactionDatailAdapter = new TransactionDetailAdapter(paymentList, activity, trans);
        binding.list3.setAdapter(transactionDatailAdapter);

        if(trans.getTRefund().size() != 0) {
        if (trans.getTRefund().get(0).gettRefundAmount() != 0) {
            boolean isAlreadyRefunded = false;

            for (int i = 0; i < paymentList.size(); i++) {
                if (paymentList.get(i).getPaymentType() == Config.REFUND) {
                    isAlreadyRefunded = true;
                    return;

                }
            }

            if (!isAlreadyRefunded) {
                Payment payment = new Payment();
                payment.setPaymentType(Config.REFUND);
//                    payment.setRefundAmount(trans.gettRefundAmount());
//                    payment.setRefundReason(trans.gettReasonForRefund());

                paymentList.add(payment);

                transactionDatailAdapter.notifyDataSetChanged();
            }


        }
    }


        binding.timeTxt.setText(t);

        TransactionItemsAdapter transactionItemsAdapter = new TransactionItemsAdapter(activity, trans.gettItems());
        binding.list2.setAdapter(transactionItemsAdapter);

        String text = "<font color=#8ab2cc>"+activity.getString(R.string.viewAll)+" "+ "</font><font color=#0091d1>("+getItemCount()+") </font><font color=#8ab2cc>" +" "+activity.getString(R.string.viewAll1)+"</font>";
        binding.allItemsTxt.setText(Html.fromHtml(text));

        total = trans.gettValue();
    }
    public int getItemCount() {
        int count = 0;

        for (int i = 0; i < trans.gettItems().size(); i++) {
            if (trans.gettItems().get(i).getItemType().equals(Config.ITEM) || trans.gettItems().get(i).getItemType().equals(Config.CUSTOM)) {
                count++;
            }
        }

        return count;
    }

    public void doFilter(String searchString){

        List<Transaction> list1 = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            if (list.get(i).getInvoiceNo().toLowerCase().contains(searchString.toLowerCase())) {
                list1.add(list.get(i));
            }
        }

        divideItems(list1);

        Config.VISIBLE_VIEW = 4;

        if (list1.size() == 0 && Config.VISIBLE_VIEW == 4) {
            Toast.makeText(activity, activity.getString(R.string.noMatchesFoundReceipt), Toast.LENGTH_SHORT).show();
            return;
        }

    }

    private void getTransactionList(){

        final Query query = manager.returnDB(activity).createAllDocumentsQuery();
        LiveQuery getALlItemsiveQuery = query.toLiveQuery();
        getALlItemsiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                if(activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        public void run() {

                            if (visibleView != Config.TRANSACTION_VIEW) {
                                return;
                            }

                            list = manager.loadTransactions(activity);
                            divideItems(list);
                        }
                    });
                }
            }
        });
        getALlItemsiveQuery.start();
    }



}
