package com.cbasolutions.cbapos.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.TransactionLayoutBinding;
import com.cbasolutions.cbapos.helper.TransactionSearch;
import com.cbasolutions.cbapos.viewmodel.TransactionViewModel;

/**
 * Created by "Don" on 10/30/2017.
 * Class Functionality :-
 */

public class TransactionHistoryFragment extends BaseFragment implements TransactionSearch {

    TransactionLayoutBinding  binding;
    TransactionViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate( inflater, R.layout.transaction_layout, container, false);

        viewModel = new TransactionViewModel(getActivity(), binding);
        binding.setTransaction(viewModel);

        View view = binding.getRoot();

        return view;
    }

    @Override
    public void filter(String searchString) {
        viewModel.doFilter(searchString);
    }
}
