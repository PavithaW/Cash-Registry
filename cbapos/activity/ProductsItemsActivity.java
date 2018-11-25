package com.cbasolutions.cbapos.activity;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.adapter.ProductsItemsAdapter;
import com.cbasolutions.cbapos.databinding.ProductItemsDataBinding;
import com.cbasolutions.cbapos.helper.ProductItemsClickListner;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.viewmodel.ProductItemsViewModel;

import java.util.ArrayList;

/**
 * Created by USER on 26-Oct-17.
 */

public class ProductsItemsActivity extends AppCompatActivity {

    ArrayList<Item> products = new ArrayList<>();
    String[] allItems = {"All Items", "Categories", "Discounts"};
    String selectedItem = "All Items";
    ArrayList<String> dropdownItems = new ArrayList<>();

    boolean isVisible = false;

    RecyclerView recyclerView;
    ListView dropdown;
    ImageView arrow;
    TextView selectedTextView;
    RelativeLayout layout_1_1, layout_1_2, layout_1_3, layout_1_4, layout_1_5;
    RelativeLayout layout_2_1, layout_2_2, layout_2_3, layout_2_4, layout_2_5;

    ProductsItemsAdapter categoriesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_items);


        ProductItemsDataBinding binding= DataBindingUtil.setContentView(this,R.layout.activity_products_items);

        ProductItemsViewModel viewModel = new ProductItemsViewModel(ProductsItemsActivity.this, this.getString(R.string.home),
                this.getString(R.string.product_items),this.getString(R.string.transaction_history),this.getString(R.string.statistics),
                this.getString(R.string.settings));

        binding.setProductItems(viewModel);

        binding.setAddItemClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {

            }
        });


        /*binding.setHomeClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {
                layout_1_1.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_1_2.setBackgroundColor(0x00000000);
                layout_1_3.setBackgroundColor(0x00000000);
                layout_1_4.setBackgroundColor(0x00000000);
                layout_1_5.setBackgroundColor(0x00000000);

                layout_2_1.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_2_2.setBackgroundColor(0x00000000);
                layout_2_3.setBackgroundColor(0x00000000);
                layout_2_4.setBackgroundColor(0x00000000);
                layout_2_5.setBackgroundColor(0x00000000);
            }
        });

        binding.setProductItemsClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {
                layout_1_1.setBackgroundColor(0x00000000);
                layout_1_2.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_1_3.setBackgroundColor(0x00000000);
                layout_1_4.setBackgroundColor(0x00000000);
                layout_1_5.setBackgroundColor(0x00000000);

                layout_2_1.setBackgroundColor(0x00000000);
                layout_2_2.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_2_3.setBackgroundColor(0x00000000);
                layout_2_4.setBackgroundColor(0x00000000);
                layout_2_5.setBackgroundColor(0x00000000);
            }
        });

        binding.setTransactionClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {
                layout_1_1.setBackgroundColor(0x00000000);
                layout_1_2.setBackgroundColor(0x00000000);
                layout_1_3.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_1_4.setBackgroundColor(0x00000000);
                layout_1_5.setBackgroundColor(0x00000000);

                layout_2_1.setBackgroundColor(0x00000000);
                layout_2_2.setBackgroundColor(0x00000000);
                layout_2_3.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_2_4.setBackgroundColor(0x00000000);
                layout_2_5.setBackgroundColor(0x00000000);
            }
        });

        binding.setStatisticsClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {
                layout_1_1.setBackgroundColor(0x00000000);
                layout_1_2.setBackgroundColor(0x00000000);
                layout_1_3.setBackgroundColor(0x00000000);
                layout_1_4.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_1_5.setBackgroundColor(0x00000000);

                layout_2_1.setBackgroundColor(0x00000000);
                layout_2_2.setBackgroundColor(0x00000000);
                layout_2_3.setBackgroundColor(0x00000000);
                layout_2_4.setBackgroundColor(Color.parseColor("#008dcf"));
                layout_2_5.setBackgroundColor(0x00000000);
            }
        });

        binding.setSettingsClickListener(new ProductItemsClickListner() {
            @Override
            public void onClick() {
                layout_1_1.setBackgroundColor(0x00000000);
                layout_1_2.setBackgroundColor(0x00000000);
                layout_1_3.setBackgroundColor(0x00000000);
                layout_1_4.setBackgroundColor(0x00000000);
                layout_1_5.setBackgroundColor(Color.parseColor("#008dcf"));

                layout_2_1.setBackgroundColor(0x00000000);
                layout_2_2.setBackgroundColor(0x00000000);
                layout_2_3.setBackgroundColor(0x00000000);
                layout_2_4.setBackgroundColor(0x00000000);
                layout_2_5.setBackgroundColor(Color.parseColor("#008dcf"));
            }
        });*/

        arrow = (ImageView) findViewById(R.id.arrow);
        selectedTextView = (TextView) findViewById(R.id.selectedTextView);
        dropdown = (ListView) findViewById(R.id.listview);
        recyclerView = (RecyclerView) findViewById(R.id.list);
        layout_1_1 = (RelativeLayout) findViewById(R.id.layout_1_1);
        layout_1_2 = (RelativeLayout) findViewById(R.id.layout_1_2);
        layout_1_3 = (RelativeLayout) findViewById(R.id.layout_1_3);
        layout_1_4 = (RelativeLayout) findViewById(R.id.layout_1_4);
        layout_1_5 = (RelativeLayout) findViewById(R.id.layout_1_5);

        layout_2_1 = (RelativeLayout) findViewById(R.id.layout_2_1);
        layout_2_2 = (RelativeLayout) findViewById(R.id.layout_2_2);
        layout_2_3 = (RelativeLayout) findViewById(R.id.layout_2_3);
        layout_2_4 = (RelativeLayout) findViewById(R.id.layout_2_4);
        layout_2_5 = (RelativeLayout) findViewById(R.id.layout_2_5);


        setupProductsView();
        setupDropdownListView();

        RelativeLayout dropdownButton = (RelativeLayout) findViewById(R.id.dropdownButton);
        dropdownButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVisible) {
                    dropdown.setVisibility(View.INVISIBLE);
                    isVisible = false;
                    arrow.setImageResource(R.drawable.arrow_down);
                } else {
                    dropdown.setVisibility(View.VISIBLE);
                    isVisible = true;
                    arrow.setImageResource(R.drawable.arrow_up);
                }
            }
        });
    }

    private void setupProductsView() {
//        products.add(new Item("Cookies", "test desc", 45.0));
//        products.add(new Item("Cookies", "test desc", 45.0));
//        products.add(new Item("Cookies", "test desc", 45.0));
//        products.add(new Item("Cookies", "test desc", 45.0));
//        products.add(new Item("Cookies", "test desc", 45.0));
//        products.add(new Item("Cookies", "test desc", 45.0));

//        recyclerView.setHasFixedSize(true);
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 4, 1, false);
//
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(4, dpToPx(20), false));
//        recyclerView.setItemAnimator(new DefaultItemAnimator());
//
//        ProductAdapter productAdapter = new ProductAdapter(this, products);
//        recyclerView.setAdapter(productAdapter);
//
//        mLayoutManager.setAutoMeasureEnabled(false);
//        recyclerView.setLayoutManager(mLayoutManager);
    }

    private void setupDropdownListView() {
        setDropdownItems();

        dropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedTextView.setText(dropdownItems.get(position));
                selectedItem = dropdownItems.get(position);

                dropdown.setVisibility(View.INVISIBLE);
                isVisible = false;
                arrow.setImageResource(R.drawable.arrow_down);

                setDropdownItems();
                categoriesAdapter.notifyDataSetChanged();
            }
        });
    }

    public void setDropdownItems() {
        dropdownItems = new ArrayList<>();

        for (String s: allItems) {
            if (!s.equals(selectedItem)) {
                dropdownItems.add(s);
            }
        }

        categoriesAdapter = new ProductsItemsAdapter(ProductsItemsActivity.this, dropdownItems);
        dropdown.setAdapter(categoriesAdapter);
    }

    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

}
