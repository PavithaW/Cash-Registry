package com.cbasolutions.cbapos.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.helper.ProductSearch;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;

/**
 * Opens when the first tab of the drawer is pressed
 * Contains 2 frame layouts where ProductsFragment and BillFragment are populated in
 */
public class HomeFragment extends BaseFragment implements ProductSearch {


    private ProductSearch listener ;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // Create an instance of ExampleFragment
        ProductsFragment firstFragment = new ProductsFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getActivity().getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.products_fragment, firstFragment).commit();

        //Registering ProductsFragment inorder to pass data from searchview sent from MainActivity
        setListener(firstFragment);

        // Create an instance of ExampleFragment
        BillFragment secondFragment = new BillFragment();

        // In case this activity was started with special instructions from an Intent,
        // pass the Intent's extras to the fragment as arguments
        secondFragment.setArguments(getActivity().getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.bill_fragment, secondFragment, "BILL").commit();


        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    public void setListener(ProductSearch listener)
    {
        this.listener = listener ;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void filter(String text) {
        //pass data to ProuctsFragment that has the exact products grid and the adapter
        listener.filter(text);


    }

}
