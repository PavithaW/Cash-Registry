package com.cbasolutions.cbapos.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.adapter.BillAdapter;
import com.cbasolutions.cbapos.databinding.BillLayoutBinding;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.Transaction;
import com.cbasolutions.cbapos.viewmodel.BillViewModel;

import java.util.ArrayList;

/**
 * Created by "Don" on 10/18/2017.
 * Class Functionality :-
 * Handles bill of the customer.
 * Populates items in a listview with the use of a recycleview
 */

public class BillFragment extends BaseFragment {

    ArrayList<Item> itemArrayList = new ArrayList<>();
    BillLayoutBinding binding;
    BillViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        binding = DataBindingUtil.inflate( inflater, R.layout.bill_layout, container, false);

        //pass all ui elements wrapper and arraylist to the viewmodel
        viewModel = new BillViewModel(getActivity(), binding, itemArrayList);
        binding.setBill(viewModel);

        View view = binding.getRoot();
        return view;
    }

    /*
    Item added to user bill - pass to the view model to hide the logic
     */
    public void handleIncomingItem(Item item){
        viewModel.handleIncomingItem(item);
    }
    public void handleSplitPopup(Transaction trans, double totalAmount, double initialAmount, double remainAmount, double splittedAmount, ArrayList<Payment> existingPaymentList){
        viewModel.showSplitPaymentPopUp(trans,totalAmount,initialAmount,remainAmount,splittedAmount,existingPaymentList);
    }


}
