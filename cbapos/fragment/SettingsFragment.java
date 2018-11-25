package com.cbasolutions.cbapos.fragment;

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.SettingsLayoutBinding;
import com.cbasolutions.cbapos.viewmodel.SettingsViewModel;

/**
 * Created by USER on 28-Nov-17.
 */

public class SettingsFragment extends BaseFragment {

    SettingsLayoutBinding binding;
    SettingsViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        binding = DataBindingUtil.inflate(inflater, R.layout.settings_layout, container, false);

        viewModel = new SettingsViewModel(getActivity(), binding);
        binding.setSettings(viewModel);

        View view = binding.getRoot();

        return view;
    }


}
