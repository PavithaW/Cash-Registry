package com.cbasolutions.cbapos.fragment;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.databinding.ItemsFragmentBinding;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.helper.ProductSearch;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;

/**
 * Created by "Don" on 10/18/2017.
 * Class Functionality :-
 * Populates all items in the grid
 * Passes the clicked item to bill
 * Handles search in the adapter using filter()
 */

public class ProductsFragment extends Fragment implements ProductSearch {

    private ItemsViewModel viewModel;
    public SendItem itemToAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ItemsFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.items_fragment, container, false);

        //pass all ui elements wrapper and arraylist to the viewmodel
        viewModel = new ItemsViewModel(getActivity(), ProductsFragment.this, binding);
        binding.setItem(viewModel);

        View view = binding.getRoot();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try {
            itemToAdd = (SendItem) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("Error in retrieving data. Please try again");
        }
    }


    @Override
    public void filter(String string) {
        //viewModel.doFilter(string);
        Config.issearching = true;
        switch(viewModel.visibleView){
            case Config.ITEMS_VIEW:
                Config.VISIBLE_VIEW = 1;
                viewModel.adapter1.filter(string);
                //viewModel.adapter.filter(string);
                break;
            case Config.CATEGORY_VIEW:
                Config.VISIBLE_VIEW = 2;
                if(viewModel.catAdapter != null)
                    viewModel.catAdapter.filter(string);
                else{
                    viewModel.adapter1.filter(string);
                    //viewModel.adapter.filter(string);
                }
                break;
            case Config.DISCOUNT_VIEW:
                Config.VISIBLE_VIEW = 3;
                if(viewModel.discountDapter != null) {
                    viewModel.discountDapter.filter(string);
                }else{
                    viewModel.adapter1.filter(string);
                    //viewModel.adapter.filter(string);
                }
                break;
        }
    }

    /*
    Interface used to send items to BillFragment
     */
    public interface SendItem {
        void sendData(Item item);
    }
}
