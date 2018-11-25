package com.cbasolutions.cbapos.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.StatsLayoutBinding;
import com.cbasolutions.cbapos.databinding.TransactionLayoutBinding;
import com.cbasolutions.cbapos.viewmodel.StatisticsViewModel;
import com.cbasolutions.cbapos.viewmodel.TransactionViewModel;

/**
 * Created by "Don" on 10/30/2017.
 * Class Functionality :-
 */

public class StatisticsFragment extends BaseFragment {

        StatsLayoutBinding binding;
        StatisticsViewModel viewModel;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {

            binding = DataBindingUtil.inflate( inflater, R.layout.stats_layout, container, false);

            viewModel = new StatisticsViewModel(getActivity(), binding);
            binding.setStats(viewModel);

            View view = binding.getRoot();
            return view;
        }

}
