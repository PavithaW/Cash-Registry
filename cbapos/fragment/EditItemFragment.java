package com.cbasolutions.cbapos.fragment;

import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.adapter.CategoryAdapter;
import com.cbasolutions.cbapos.adapter.ProductAdapter;
import com.cbasolutions.cbapos.adapter.ProductsItemsAdapter;
import com.cbasolutions.cbapos.databinding.EditItemsFragmentBinding;
import com.cbasolutions.cbapos.helper.ItemsAndCategorySearch;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.ORMTblCategory;
import com.cbasolutions.cbapos.model.ORMTblItem;
import com.cbasolutions.cbapos.util.GridSpacingItemDecoration;
import com.cbasolutions.cbapos.viewmodel.EditItemModelView;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;

import java.util.ArrayList;

/**
 * Created by "Don" on 10/30/2017.
 * Class Functionality :-
 */

public class EditItemFragment extends BaseFragment implements ItemsAndCategorySearch {

    ArrayList<ORMTblItem> products = new ArrayList<>();
    String[] allItems = {"All Items", "Categories", "Discounts"};
    String selectedItem = "All Items";
    ArrayList<String> dropdownItems = new ArrayList<>();

    boolean isVisible = false;

    ImageView arrow;
    TextView selectedTextView;

    ProductsItemsAdapter categoriesAdapter;
    private EditItemModelView viewModel;

    View inflatedView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        EditItemsFragmentBinding binding = DataBindingUtil.inflate(inflater, R.layout.gross_amount_layout, container, false);

        this.inflatedView = inflater.inflate(R.layout.gross_amount_layout, container, false);

        //pass all ui elements wrapper and arraylist to the viewmodel
        viewModel = new EditItemModelView(getActivity(), EditItemFragment.this, binding);
        binding.setEditItem(viewModel);

        View view = binding.getRoot();
        return view;

    }

    private void setupProductsView() {


    }

    private void setupCategoryView() {


    }


    @Override
    public void filter(String string) {
        Log.e("EDIT", string);
        viewModel.doFilter(string);
    }
}
