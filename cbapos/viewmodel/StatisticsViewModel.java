package com.cbasolutions.cbapos.viewmodel;

import android.support.v4.app.FragmentActivity;

import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.databinding.StatsLayoutBinding;
import com.cbasolutions.cbapos.databinding.TransactionLayoutBinding;

/**
 * Created by "Don" on 11/14/2017.
 * Class Functionality :-
 */

public class StatisticsViewModel extends CRBaseObservable {

    public StatisticsViewModel(FragmentActivity activity, StatsLayoutBinding binding) {
        binding.webview.getSettings().setJavaScriptEnabled(true);
        binding.webview.loadUrl("http://192.168.2.241/cashRegistry/summary");
    }
}
