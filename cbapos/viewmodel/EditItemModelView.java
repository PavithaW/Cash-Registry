package com.cbasolutions.cbapos.viewmodel;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.adapter.CategoryAdapter1;
import com.cbasolutions.cbapos.adapter.DiscountAdapter;
import com.cbasolutions.cbapos.adapter.ItemsAdapter1;
import com.cbasolutions.cbapos.databinding.EditItemsFragmentBinding;
import com.cbasolutions.cbapos.fragment.EditItemFragment;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by "Don" on 11/15/2017.
 * Class Functionality :-
 */

public class EditItemModelView extends CRBaseObservable {

    Context context;
    EditItemFragment parentFrag;
    EditItemsFragmentBinding binding;

    //public ItemsAdapter adapter;
    public ItemsAdapter1 adapter1;
    //public CategoryAdapter catAdapter;
    public CategoryAdapter1 catAdapter1;
    public DiscountAdapter discountDapter;

    public ArrayList<Item> allItems = new ArrayList<>();
    public ArrayList<String> allItemsId = new ArrayList<>();

    public ArrayList<Category> allICategories = new ArrayList<>();
    public ArrayList<String> allICategoriesId = new ArrayList<>();
    public ArrayList<Discount> allDiscounts = new ArrayList<>();
    LiveQuery getCatliveQuery;
    LiveQuery getCatCountliveQuery;

    DBManager manager;
    int visibleView = Config.ITEMS_VIEW;


    public EditItemModelView(FragmentActivity activity, EditItemFragment editItemFragment, EditItemsFragmentBinding binding) {
        this.context = activity;
        this.parentFrag = editItemFragment;
        this.binding = binding;
        Log.d("CATADAPTER" , "EditItemModelView "+ editItemFragment);
        setupViewAndQuery();
        //getAllItems();
        getAllItemsId();
        Config.VISIBLE_VIEW = 1;
    }

//    private void getAllItems(){
//
//        final Query query = manager.returnDB(context).createAllDocumentsQuery();
//        LiveQuery getALlItemsiveQuery = query.toLiveQuery();
//        getALlItemsiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
//            public void changed(final LiveQuery.ChangeEvent event) {
//                 parentFrag.getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//
//                        if(visibleView != Config.ITEMS_VIEW){
//                            return;
//                        }
//
//                        allItems =  manager.loadItems(context, query);
//                        Log.d("AAA LIST COUNT", allItems.size()+"");
//
//                        Collections.sort(allItems, new Comparator<Item>() {
//                            @Override
//                            public int compare(Item o1, Item o2) {
//
//                                return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
//                            }
//
//                        });
//
//                        //binding.itemsGrid.setAdapter(null);
//                        adapter = new ItemsAdapter(allItems, context, true);
//                        //adapter.notifyDataSetChanged();
//                        binding.itemsGrid.setAdapter(adapter);
//                    }
//                });
//            }
//        });
//        getALlItemsiveQuery.start();
//    }

    private void getAllItemsId(){

        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        LiveQuery getALlItemsiveQuery = query.toLiveQuery();
        getALlItemsiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                if(parentFrag != null) {
                    parentFrag.getActivity().runOnUiThread(new Runnable() {
                        public void run() {

                            if (visibleView != Config.ITEMS_VIEW) {
                                return;
                            }

                            allItemsId = manager.loadItemsId(context, query);
                            //allItems = manager.loadItems(context,query);
                            Log.d("AAA LIST COUNT", allItemsId.size() + "");

//                        Collections.sort(allItems, new Comparator<Item>() {
//                            @Override
//                            public int compare(Item o1, Item o2) {
//
//                                return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
//                            }
//
//                        });

                            //binding.itemsGrid.setAdapter(null);
                            adapter1 = new ItemsAdapter1(allItemsId, context, true);
                            binding.itemsGrid.setAdapter(adapter1);

                            //adapter = new ItemsAdapter(allItems, context, true);
                            adapter1.notifyDataSetChanged();
                            //binding.itemsGrid.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        getALlItemsiveQuery.start();
    }

    private void getAllCategories(){

        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        //live query to get all categories
        LiveQuery getCatliveQuery = query.toLiveQuery();
        getCatliveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {

                try {

                    if(parentFrag.getActivity() == null){
                        Thread.sleep(100);
                    }

                    if(parentFrag != null) {

                        parentFrag.getActivity().runOnUiThread(new Runnable() {
                            public void run() {

                                if (visibleView != Config.CATEGORY_VIEW) {
                                    return;
                                }

                                allICategoriesId = manager.loadCategoriesId(context,query);

//                                Collections.sort(allICategories, new Comparator<Category>() {
//                                    @Override
//                                    public int compare(Category c1, Category c2) {
//
//                                        return c1.getCategory_name().toLowerCase().compareTo(c2.getCategory_name().toLowerCase());
//                                    }
//
//                                });

                                //get the count and set to the category object

//                                for (int i = 0; i < allICategories.size(); i++) {
//                                    allICategories.get(i).setItemCount(manager.getItemCountByCategoryName(context, query, allICategories.get(i).getCategory_name()));
//                                }

                                if (!BaseActivity.closeCatListner) {
//                                    catAdapter = new CategoryAdapter(allICategories, context, true);
//                                    binding.itemsGrid.setAdapter(catAdapter);

                                    catAdapter1 = new CategoryAdapter1(allICategoriesId, context, true);
                                    binding.itemsGrid.setAdapter(catAdapter1);
                                }
                            }
                        });
                    }
                    //Thread.sleep(300);
                }catch (Exception e){
                    e.printStackTrace();
                }
                    }
               // });
                //getCatCountliveQuery.start();
            //}
        });
        getCatliveQuery.start();


    }

    private void getAllDiscounts(){

        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        LiveQuery getALlDiscLiveQuery = query.toLiveQuery();
        getALlDiscLiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                parentFrag.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(visibleView != Config.DISCOUNT_VIEW){
                            return;
                        }

                        allDiscounts =  manager.loadDiscounts(context, query);
                        Log.d("AAA DIS LIST COUNT", allDiscounts.size() + "");
                        Collections.sort(allDiscounts, new Comparator<Discount>() {
                            @Override
                            public int compare(Discount d1, Discount d2) {

                                return d1.getDiscount_name().toLowerCase().compareTo(d2.getDiscount_name().toLowerCase());
                            }

                        });
                        //binding.itemsGrid.setAdapter(null);
                       // binding.itemsGrid.removeAllViewsInLayout();
                        discountDapter = new DiscountAdapter(allDiscounts, context, true);
                        binding.itemsGrid.setAdapter(discountDapter);
                    }
                });
            }
        });
        getALlDiscLiveQuery.start();
    }

    //look for the "item" document and select all
    private void setupViewAndQuery() {
        manager = new DBManager();
        String[] dropDownArray = context.getResources().getStringArray(R.array.drop_down_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.options_drop_down, dropDownArray);
        adapter.setDropDownViewResource(R.layout.options_drop_down);
        binding.spinner1.setAdapter(adapter); // this will set list of values to spinner

        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MainActivity.editsearch.setQuery("", false);
                MainActivity.editsearch.clearFocus();
                BaseActivity.closeCatListner = false;
                hideAllActionButtons();
                binding.itemsGrid.setAdapter(null);
                switch (position) {
                    case 0:
                        Config.VISIBLE_VIEW = 1;
                        visibleView = Config.ITEMS_VIEW;
                        ItemsViewModel.visibleView = visibleView;
                        binding.addItem.setVisibility(View.VISIBLE);
                        //getAllItems();
                        getAllItemsId();
                        break;
                    case 1:
                        Config.VISIBLE_VIEW = 2;
                        visibleView = Config.CATEGORY_VIEW;
                        ItemsViewModel.visibleView = visibleView;
                        binding.addCategory.setVisibility(View.VISIBLE);
                        getAllCategories();
                        break;
                    case 2:
                        Config.VISIBLE_VIEW = 3;
                        visibleView = Config.DISCOUNT_VIEW;
                        ItemsViewModel.visibleView = visibleView;
                        binding.addDiscount.setVisibility(View.VISIBLE);
                        getAllDiscounts();
                        break;
                    default:
                        Log.d("mash_logs","default");
                        binding.addItem.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        binding.addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Config.dialogIsShowing) {
                    Config.dialogIsShowing = true;
                    ((BaseActivity)context).showAddItemPopUp(null);
                }

            }
        });

        binding.addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Config.catDialogIsShowing) {
                    Config.catDialogIsShowing = true;
                    ((BaseActivity) context).showAddCategoryPopUp(null);
                }
            }
        });

        binding.addDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity)context).showDiscountPopUp(null);
            }
        });
    }

    public void doFilter(String searchString){

        switch(visibleView){
            case Config.ITEMS_VIEW:
                //adapter.filter(searchString);
                adapter1.filter(searchString);
                break;
            case Config.CATEGORY_VIEW:
                //catAdapter.filter(searchString);
                catAdapter1.filter(searchString);
                break;
            case Config.DISCOUNT_VIEW:
                discountDapter.filter(searchString);
                break;
        }

    }

    private void hideAllActionButtons(){
        binding.addItem.setVisibility(View.GONE);
        binding.addCategory.setVisibility(View.GONE);
        binding.addDiscount.setVisibility(View.GONE);
    }

}
