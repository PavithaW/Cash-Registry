package com.cbasolutions.cbapos.viewmodel;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.activity.BaseActivity;
import com.cbasolutions.cbapos.activity.CRBaseObservable;
import com.cbasolutions.cbapos.activity.MainActivity;
import com.cbasolutions.cbapos.adapter.CategoryAdapter;
import com.cbasolutions.cbapos.adapter.DiscountAdapter;
import com.cbasolutions.cbapos.adapter.DiscountListAdapter;
import com.cbasolutions.cbapos.adapter.ItemsAdapter;
import com.cbasolutions.cbapos.adapter.ItemsAdapter1;
import com.cbasolutions.cbapos.adapter.VariantAdapter;
import com.cbasolutions.cbapos.databinding.ItemsFragmentBinding;
import com.cbasolutions.cbapos.fragment.ProductsFragment;
import com.cbasolutions.cbapos.helper.Cart;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.couchbase.lite.LiveQuery;
import com.couchbase.lite.Query;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by "Don" on 10/26/2017.
 * Class Functionality :- Populates all items / categories / discounts in a grid
 */

public class ItemsViewModel extends CRBaseObservable {

    Context context;
    public static ProductsFragment parentFrag;
    ItemsFragmentBinding binding;

    public ItemsAdapter adapter;

    public ItemsAdapter1 adapter1;
    public CategoryAdapter catAdapter;
    public DiscountAdapter discountDapter;

    public ArrayList<Item> allItems = new ArrayList<>();
    public ArrayList<String> allItemsId = new ArrayList<>();
    public ArrayList<Category> allICategories = new ArrayList<>();
    public ArrayList<Discount> allDiscounts = new ArrayList<>();
    public ArrayList<Item> allItemsByCategory = new ArrayList<>();
    public ArrayList<String> allItemsByCategoryId = new ArrayList<>();
    public ArrayList<Item> itemsByCategory = new ArrayList<>();
    public ArrayList<String> itemsByCategoryId = new ArrayList<>();


    private Discount selectedDiscount = null;
    TextView txtPrice;
    TextView txtItemName;

    int editedQty = 1;
    PriceVariation selectedVariation;
    public static int visibleView = Config.ITEMS_VIEW;
    private DBManager manager;
    boolean categorySelected = false;
    Category cat;
    boolean isMultipleVariantOpened = false;

    public ItemsViewModel(FragmentActivity activity, ProductsFragment parentFrag, ItemsFragmentBinding binding) {
        this.context = activity;
        this.binding = binding;
        this.parentFrag = parentFrag;
        setupViewAndQuery();
        getAllItems();
    }

    private void getAllItemsByCategory(final Category category) {

        final Query query = manager.returnDB(context).createAllDocumentsQuery();

        LiveQuery liveQuery = query.toLiveQuery();
        liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {

                parentFrag.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(visibleView != Config.ITEMS_VIEW){
                            return;
                        }

                        allItems = manager.loadItems(context, query);


                        allItemsByCategory = new ArrayList<>();

                        for (int i = 0; i < allItems.size(); i++) {
                            if((allItems.get(i).getCategory() != null)) {
                                if (allItems.get(i).getCategory().getCategory_name().equals(category.getCategory_name())) {
                                    allItemsByCategory.add(allItems.get(i));
                                    }
                                }
                            }
                        Collections.sort(allItemsByCategory, new Comparator<Item>() {
                            @Override
                            public int compare(Item o1, Item o2) {

                                return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
                        }
//                        Collections.sort(allItemsByCategory, new Comparator<Item>() {
//                            @Override
//                            public int compare(Item o1, Item o2) {
//
//                                return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
//                            }
//
//                        });

                        });

                        adapter = new ItemsAdapter(allItemsByCategory, context, false);
                        binding.itemsGrid.setAdapter(adapter);
                    }
                });
            }
        });
        liveQuery.start();
    }

    private void getAllItems() {
        final Query query = manager.returnDB(context).createAllDocumentsQuery();

        LiveQuery liveQuery = query.toLiveQuery();
        liveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {

                if(parentFrag != null) {

                    parentFrag.getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            if (visibleView != Config.ITEMS_VIEW) {
                                return;
                            }

                            //allItems = manager.loadItems(context, query);

                            allItemsId = manager.loadItemsId(context, query);
                            Collections.sort(allItems, new Comparator<Item>() {
                                @Override
                                public int compare(Item o1, Item o2) {

                                    return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
                                }

                            });
                            adapter1 = new ItemsAdapter1(allItemsId, context, false);
                            binding.itemsGrid.setAdapter(adapter1);
//
//                            adapter = new ItemsAdapter(allItems, context, false);
//                            binding.itemsGrid.setAdapter(adapter);
                        }
                    });
                }
            }
        });
        liveQuery.start();
    }

    private void getAllCategories() {

        final Query query = manager.returnDB(context).createAllDocumentsQuery();

        //live query to get all categories
        final LiveQuery getCatliveQuery = query.toLiveQuery();
        getCatliveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                if(visibleView != Config.CATEGORY_VIEW){
                    return;
                }

                //allItems = manager.loadItems(context, query);

                allICategories = manager.loadCategories(context,query);

                Collections.sort(allICategories, new Comparator<Category>() {
                    @Override
                    public int compare(Category c1, Category c2) {

                        return c1.getCategory_name().toLowerCase().compareTo(c2.getCategory_name().toLowerCase());
                    }

                });

                //live query to get count for categories
                final Query queryCount = manager.returnDB(context).createAllDocumentsQuery();
                LiveQuery getCatCountliveQuery = query.toLiveQuery();
                getCatCountliveQuery.addChangeListener(new LiveQuery.ChangeListener() {
                    public void changed(final LiveQuery.ChangeEvent event) {

                        //get the count and set to the category object
                        for (int i = 0; i < allICategories.size(); i++) {
                            allICategories.get(i).setItemCount(manager.getItemCountByCategoryName(context, queryCount, allICategories.get(i).getCategory_name()));
                        }

                        parentFrag.getActivity().runOnUiThread(new Runnable() {
                            public void run() {
                                catAdapter = new CategoryAdapter(allICategories, context, false);
                                binding.itemsGrid.setAdapter(catAdapter);
                            }
                        });
                    }
                });
                getCatCountliveQuery.start();
            }
        });
        getCatliveQuery.start();
    }

    private void getAllDiscounts() {

        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        LiveQuery getALlDiscLiveQuery = query.toLiveQuery();
        getALlDiscLiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                parentFrag.getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        if(visibleView != Config.DISCOUNT_VIEW){
                            return;
                        }

                        allDiscounts = manager.loadDiscounts(context, query);

                        Collections.sort(allDiscounts, new Comparator<Discount>() {
                            @Override
                            public int compare(Discount d1, Discount d2) {

                                return d1.getDiscount_name().toLowerCase().compareTo(d2.getDiscount_name().toLowerCase());
                            }

                        });
                        discountDapter = new DiscountAdapter(allDiscounts, context, false);
                        binding.itemsGrid.setAdapter(discountDapter);
                    }
                });
            }
        });
        getALlDiscLiveQuery.start();

    }



    //look for the "item" document and select all
    //live query written to instantly update after adding an item
    private void setupViewAndQuery() {
            manager = new DBManager();
        String[] dropDownArray = context.getResources().getStringArray(R.array.drop_down_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, R.layout.option_drop_down_home, dropDownArray);
        adapter.setDropDownViewResource(R.layout.option_drop_down_home);
        binding.spinner1.setAdapter(adapter);

        binding.spinner1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                MainActivity.editsearch.setQuery("", false);
                MainActivity.editsearch.clearFocus();

                binding.itemsGrid.setAdapter(null);
                switch (position) {
                    case 0:
                        visibleView = Config.ITEMS_VIEW;
                        binding.bcButtonLayout.setVisibility(View.VISIBLE);
                        if (!categorySelected) {
                            getAllItems();
                        } else{
                            getAllItemsByCategory(cat);
                        }
                        categorySelected = false;
                        break;
                    case 1:
                        visibleView = Config.CATEGORY_VIEW;
                        binding.bcButtonLayout.setVisibility(View.INVISIBLE);
                        getAllCategories();
                        categorySelected = false;
                        break;
                    case 2:
                        visibleView = Config.DISCOUNT_VIEW;
                        binding.bcButtonLayout.setVisibility(View.INVISIBLE);
                        getAllDiscounts();
                        categorySelected = false;
                        break;
                    default:
                        visibleView = Config.ITEMS_VIEW;
                        binding.bcButtonLayout.setVisibility(View.VISIBLE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        /*
           Check if there are many variations
           1. If no variations simply set the new price to the OBJ setItemTotal()
           2. If there are multiple variants, show pop up with prices, on selection of the price set the new price to the OBJ setItemTotal()
         */
        binding.itemsGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //proceed only if items are displayed
                if (visibleView == Config.ITEMS_VIEW) {
                    Item item = (Item) parent.getAdapter().getItem(position);

                    if (item.getPriceVariations().get(0).getIsVariable()) {
                        ((BaseActivity)context).showAddVariableItemPrice(item,parentFrag);
                        return;
                    }

                    item.setItemDiscount(selectedDiscount);
                    item.setPriceSelected(selectedVariation);
                    if (item.getPriceVariations().size() > 1) {
                        if(!isMultipleVariantOpened) {
                            showMultipleVariants(item);
                        }
                    } else {

                        if (item.getPriceVariations().get(0).getStk_count() != 0) {

                            Item itemToSend = new Item();
                            itemToSend.setItem_id(item.getItem_id());
                            itemToSend.setItemTotal(String.format("%.2f", item.getPriceVariations().get(0).getPrice()));
                            itemToSend.setItem_name(item.getItem_name() + "-" + item.getPriceVariations().get(0).getVariation());
                            itemToSend.setQuantity(1);
                            itemToSend.setCategory(item.getCategory());
                            itemToSend.setPriceVariations(item.getPriceVariations());
                            selectedVariation = item.getPriceVariations().get(0);
                            //item.setPriceSelected(selectedVariation);
                            itemToSend.setItemType(Config.ITEM);

                            PriceVariation variation = new PriceVariation();
                            variation.setPrice(item.getPriceVariations().get(0).getPrice());
                            variation.setVariation("Regular");
                            //variation.setStk_count(selectedVariation.getStk_count()-1);

                            itemToSend.setPriceSelected(variation);

                            parentFrag.itemToAdd.sendData(itemToSend);

                            ///////////
                        }else{
                            showHintDialog(context , context.getString(R.string.zeroStock), context.getString(R.string.alert));
                            return;
                        }
                    }
                } else if (visibleView == Config.CATEGORY_VIEW) {
                    cat = (Category) parent.getAdapter().getItem(position);
                    categorySelected = true;
                    visibleView = Config.ITEMS_VIEW;
                    getItemsForCat(cat);
                    binding.spinner1.setSelection(0);
//                    ItemsAdapter adapter = new ItemsAdapter(allItems, context, false);
//                    binding.itemsGrid.setAdapter(adapter);
                } else if (visibleView == Config.DISCOUNT_VIEW) {
                    //convert discount to item OBJ and set as the first first OBJ (0th) in  the array
                    Discount discount = allDiscounts.get(position);


                    if (discount.getDiscount_value() == 0.00) {
                        if(Cart.totalBill == 0.00){
                            showHintDialog(context , context.getString(R.string.discountHigherTotal), context.getString(R.string.alert));
                            return;
                        }else {
                            ((BaseActivity) context).showAddVariableDiscount(discount, parentFrag);
                            return;
                        }
                    }

                    if(Cart.totalBill == 0.00){
                        showHintDialog(context , context.getString(R.string.emptyCart), context.getString(R.string.alert));
                        return;
                    }else {

                        if (discount.getDiscount_type().equals("Rs") && discount.getDiscount_value() > Cart.totalBill) {
                            showHintDialog(context, context.getString(R.string.discountHigherTotal), context.getString(R.string.alert));
                            return;
                        } else {

                            Item discItem = new Item();
                            discItem.setQuantity(1);
                            discItem.setItem_name(discount.getDiscount_name() + "-Discount");
                            discItem.setItemTotal("-20");
                            discItem.setItemType(Config.DISCOUNT);
                            //discItem.setBillDiscountType(discount.getDiscount_type());
                            //discItem.setBillDiscountValue(discount.getDiscount_value());
                            discItem.setItemDiscount(discount);
                            parentFrag.itemToAdd.sendData(discItem);
                        }
                    }
                }

            }
        });


        binding.scanBCOnSale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //IntentIntegrator.forSupportFragment(activity).initiateScan();
                IntentIntegrator scanIntegrator = new IntentIntegrator(parentFrag.getActivity());
                scanIntegrator.initiateScan();
                BaseActivity.scanBConSale = true;

//                IntentIntegrator scanIntegrator = new IntentIntegrator(activity);
//                scanIntegrator.initiateScan();
            }
        });

    }


    /*
    On long press of a item in the grid, show this pop up
    1. Handles quantity addition and subtraction
    2. Changes price according to quantity and price variations
     */
    public void showQtyAndVariants(final Item item, final Context context) {
        editedQty = 1;
        Gson gson = new Gson();
        final List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
        Query query = manager.returnDB(context).createAllDocumentsQuery();
        allDiscounts = manager.loadDiscounts(context, query);

        final Dialog dialog = new Dialog(context, R.style.Custom_Dialog);
        dialog.setContentView(R.layout.add_qty_variants_popup);

        Button btnBack = (Button) dialog.findViewById(R.id.btn_back);
        Button btnDone = (Button) dialog.findViewById(R.id.btn_done);

        final ListView variantList = (ListView) dialog.findViewById(R.id.variant_list);
        ListView discountList = (ListView) dialog.findViewById(R.id.list);

        txtItemName = (TextView) dialog.findViewById(R.id.prod_name);
        final TextView txtQty = (TextView) dialog.findViewById(R.id.qty);
        txtPrice = (TextView) dialog.findViewById(R.id.price);
        TextView txtMinus = (TextView) dialog.findViewById(R.id.minus);
        final TextView txtPlus = (TextView) dialog.findViewById(R.id.plus);
        final EditText enteredQty = (EditText) dialog.findViewById(R.id.entered_qty);

        txtItemName.setEllipsize(TextUtils.TruncateAt.END);
        txtItemName.setHorizontallyScrolling(false);
        txtItemName.setSingleLine();

        editedQty = 1;
        enteredQty.setText(editedQty+"");
        txtQty.setText("(x" + editedQty + ")");

        txtItemName.setText(item.getItem_name());

        selectedVariation = item.getPriceVariations().get(0);
        //Populate variant listview
        VariantAdapter varAdapter = new VariantAdapter(item.getPriceVariations(), context, null, true);
        variantList.setAdapter(varAdapter);


        enteredQty.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (selectedVariation == null) {
//                    Toast.makeText(context, "Please select a variation", Toast.LENGTH_SHORT).show();
//                    return;
                    selectedVariation = item.getPriceVariations().get(0);
                }

                if(s.length() != 0) {
                    editedQty = Integer.parseInt(enteredQty.getText().toString());
                    txtQty.setText("(x" + editedQty + ")");
                    txtPrice.setText(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));
                } else {
                    editedQty = 0;
                    txtQty.setText("(x" + editedQty + ")");
                    txtPrice.setText("0.00");
                }
            }
        });

        //set default price to the base price
        txtPrice.setText(String.format("%.2f", selectedVariation.getPrice()));
        ArrayList<Discount> nonZeroDiscounts = new ArrayList<>();
        for(int i=0; i< allDiscounts.size(); i++){
            if(allDiscounts.get(i).getDiscount_value()>0){
                nonZeroDiscounts.add(allDiscounts.get(i));
            }
        }

        DiscountListAdapter discountAdapter = new DiscountListAdapter(nonZeroDiscounts, context, ItemsViewModel.this, null, item);
        discountList.setAdapter(discountAdapter);

        variantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedVariation = item.getPriceVariations().get(i);
                item.setPriceSelected(selectedVariation);
                //txtPrice.setText((Double.parseDouble(selectedVariation.getPrice()) * editedQty) + "");
                txtPrice.setText(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));
                txtItemName.setText(selectedVariation.getVariation());
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedDiscount = null;
                dialog.dismiss();
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editedQty != 0) {
                    item.setItemType(Config.ITEM);
                    //item.setItem_name(selectedVariation.getVariation());
                    item.setItemTotal(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));
                    item.setItemActualTotal(getActualTotal());
                    item.setItemDiscount(selectedDiscount);
                    item.setQuantity(editedQty);
                    //item.setPriceSelected(selectedVariation);

                    PriceVariation variation = new PriceVariation();
                    variation.setPrice(Double.parseDouble(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount)));
                    variation.setVariation(item.getPriceSelected().getVariation());
                    item.setPriceSelected(variation);

                    parentFrag.itemToAdd.sendData(item);
                    selectedDiscount = null;
                    dialog.dismiss();
                } else {
                    showHintDialog(context, context.getString(R.string.invalidQty), context.getString(R.string.alert));
                }
            }
        });

        txtPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedVariation == null) {
//                    Toast.makeText(context, "Please select a variation", Toast.LENGTH_SHORT).show();
//                    return;
                    selectedVariation = item.getPriceVariations().get(0);
                }

                if (enteredQty.getText().toString().trim().length() == 0) {
                    editedQty = 0;
                } else {
                    editedQty = Integer.parseInt(enteredQty.getText().toString().trim());
                }

                editedQty++;
                enteredQty.setText(editedQty + "");
                txtQty.setText("(x" + editedQty + ")");
                //txtPrice.setText((Double.parseDouble(selectedVariation.getPrice()) * editedQty) + "");
                txtPrice.setText(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));

            }
        });

        txtMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedVariation == null) {
//                    Toast.makeText(context, "Please select a variation", Toast.LENGTH_SHORT).show();
//                    return;
                    selectedVariation = item.getPriceVariations().get(0);
                }

                if (enteredQty.getText().toString().trim().length() == 0) {
                    editedQty = 0;
                } else {
                    editedQty = Integer.parseInt(enteredQty.getText().toString().trim());
                }

                if (editedQty != 0) {
                    editedQty--;
                    enteredQty.setText(editedQty + "");
                    txtQty.setText("(x" + editedQty + ")");
                    //txtPrice.setText((Double.parseDouble(selectedVariation.getPrice()) * editedQty) + "");
                    txtPrice.setText(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));
                }
            }
        });


        dialog.show();

    }


    //dialog that tells user to select a price variant - single tap
    private void showMultipleVariants(final Item item) {
        isMultipleVariantOpened = true;
        List<String> listItems = new ArrayList<String>();

        for (int i = 0; i < item.getPriceVariations().size(); i++) {
            //if(item.getPriceVariations().get(i).getStk_count() != 0) {

                listItems.add(item.getPriceVariations().get(i).getVariation() + " - " + "Rs " + String.format("%.2f", item.getPriceVariations().get(i).getPrice()));

           // }
            }

        final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select the Price Variation for - " + item.getItem_name());
        builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                //create a new object, bind the variation name to the item name

                if(item.getPriceVariations().get(position).getStk_count() == 0) {
                    showHintDialog(context , context.getString(R.string.zeroStock), context.getString(R.string.alert));
                    isMultipleVariantOpened = false;

                }else {
                    selectedVariation = item.getPriceVariations().get(position);

                    Item itemToSend = new Item();
                    itemToSend.setItem_id(item.getItem_id());
                    itemToSend.setItemTotal(String.format("%.2f", item.getPriceVariations().get(position).getPrice()));
                    itemToSend.setItem_name(item.getItem_name() + "-" + item.getPriceVariations().get(position).getVariation());
                    itemToSend.setQuantity(1);
                    itemToSend.setCategory(item.getCategory());
                    itemToSend.setPriceVariations(item.getPriceVariations());
                    itemToSend.setItemType(Config.ITEM);

                    PriceVariation variation = new PriceVariation();
                    variation.setPrice(item.getPriceVariations().get(position).getPrice());
                    variation.setVariation(item.getPriceVariations().get(position).getVariation());
                    itemToSend.setPriceSelected(variation);
                    //itemToSend.setPriceSelected(selectedVariation);
                    parentFrag.itemToAdd.sendData(itemToSend);
                    isMultipleVariantOpened = false;
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void handleIncomingDiscount(Discount discount) {
        if (selectedVariation == null) {
            Toast.makeText(context, "Please select a variation", Toast.LENGTH_SHORT).show();
            return;
           //selectedVariation = item.getPriceVariations().get(0);
        }

        selectedDiscount = discount;
        txtPrice.setText(getTotal(context, editedQty, selectedVariation.getPrice(), selectedDiscount));
    }

    private void getItemsForCat(final Category category){
        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        LiveQuery getALlItemsiveQuery = query.toLiveQuery();
        getALlItemsiveQuery.addChangeListener(new LiveQuery.ChangeListener() {
            public void changed(final LiveQuery.ChangeEvent event) {
                parentFrag.getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        if(visibleView != Config.ITEMS_VIEW){
                            return;
                        }

                        allItems =  manager.loadItems(context, query);
                        itemsByCategory = new ArrayList<>();

                        for (int i = 0; i < allItems.size(); i++) {
                            if((allItems.get(i).getCategory() != null)) {
                                if (allItems.get(i).getCategory().getCategory_name().equals(category.getCategory_name())) {
                                    itemsByCategory.add(allItems.get(i));
                                }
                            }
                        }
                        Collections.sort(itemsByCategory, new Comparator<Item>() {
                            @Override
                            public int compare(Item o1, Item o2) {

                                return o1.getItem_name().toLowerCase().compareTo(o2.getItem_name().toLowerCase());
                            }

                        });

                        adapter = new ItemsAdapter(itemsByCategory, context, false);
                        binding.itemsGrid.setAdapter(adapter);
                        // binding.spinner1.setSelection(0);
                    }
                });
            }
        });
        getALlItemsiveQuery.start();
    }



}
