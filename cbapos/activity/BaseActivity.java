package com.cbasolutions.cbapos.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.cbasolutions.cbapos.R;
import com.cbasolutions.cbapos.adapter.CategorySpinnerAdapter;
import com.cbasolutions.cbapos.adapter.CreateCategoryItemsAdapter;
import com.cbasolutions.cbapos.adapter.VariantAdapter;
import com.cbasolutions.cbapos.fragment.ProductsFragment;
import com.cbasolutions.cbapos.helper.Cart;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.persistance.DBManager;
import com.cbasolutions.cbapos.service.PayableService;
import com.cbasolutions.cbapos.util.Util;
import com.cbasolutions.cbapos.viewmodel.ItemsViewModel;
import com.cbasolutions.cbapos.viewmodel.TransactionViewModel;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Query;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by "Don" on 11/6/2017.
 * Class Functionality :-
 * BaseActivity will be extended by all activities to take use of common functions
 */

public class BaseActivity extends AppCompatActivity {

    ListView variantList;
    VariantAdapter adapter;
    private List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
    private String itemName;
    private String itemBarcode;
    private Category itemCat;
    private Discount discount;
    private String itemDesc;
    private String itemStock;
    private String itemPrice;
    private String itemSku;
    private int itemColor = 0;
    private int catColor = 0;
    private Context context = this;
    private Button color1, color2, color3, color4, color5, color6;
    //public static Boolean hideZeroPosition = false;

    private static final int PERMISSION_REQUESTS = 1010;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_CHOOSE_PHOTO = 2;
    private static final int THUMBNAIL_SIZE = 150;
    public static final double MAX_AMOUNT = 999999.99;
    private String mImagePathToBeAttached;
    private static boolean isBasePrice = false;
    private static boolean isImageDeleted = false;
    ImageView photo;
    Bitmap itemImage;
    Dialog addItemDialog;
    Dialog addVariablePriceDialog;
    Dialog addVariableDiscountDialog;
    public static boolean closeCatListner = false;
    public static boolean isCategoryAddedToItemPopup = false;
    private static boolean isAddItemLoaded = false;
    TextView barcodeTextView;
    TextView variantBarcode;
    boolean isMultipleBarcodeOpened = false;

    List<Category> allICategories;
    CategorySpinnerAdapter catAdapter1;
    DBManager manager;
    Spinner sp1;
    EditText price;
    EditText sku;

    Item itemToUpdate;
    Item item;
    public static Dialog emailDialog;
    private static boolean baseBCScanClick = false;
    private static boolean variantBCScanClick = false;
    public static boolean scanBConSale = false;

    /*
    Alert that is used to add / edit items
    Can be accessed from any activity
     */
    public void showAddItemPopUp(final Item itemToUpdate) {
        // custom dialog
        this.itemToUpdate = itemToUpdate;
        addItemDialog = new Dialog(this, R.style.Custom_Dialog);
        addItemDialog.setContentView(R.layout.add_item_pop_up);
        addItemDialog.setCancelable(true);
        addItemDialog.setCanceledOnTouchOutside(false);
        baseBCScanClick = false;
        scanBConSale = false;
        itemImage = null;

        allICategories = new ArrayList<>();
        manager = new DBManager();
        allICategories = manager.loadCategories(context);

        isImageDeleted = false;
        isAddItemLoaded = true;

        Category dummyCat = new Category();
        //if(!isCategoryAddedToItemPopup) {
        if (itemToUpdate != null) {
            //hideZeroPosition = true;
            if (itemToUpdate.getCategory() != null) {
                dummyCat.setCategory_name(context.getString(R.string.category));
                allICategories.add(0, dummyCat);
                itemCat = itemToUpdate.getCategory();
            } else {
                dummyCat.setCategory_name(context.getString(R.string.category));
                allICategories.add(0, dummyCat);
            }
        } else {
            //hideZeroPosition = false;
            dummyCat.setCategory_name(context.getString(R.string.category));
            allICategories.add(0, dummyCat);
            itemCat = null;
        }
        //}

        sp1 = (Spinner) addItemDialog.findViewById(R.id.spinner1);

        // Initialize the adapter sending the current context
        // Send the simple_spinner_item layout
        // And finally send the Users array (Your data)
        catAdapter1 = new CategorySpinnerAdapter(BaseActivity.this, R.layout.simple_spinner_item, allICategories);
        sp1.setAdapter(catAdapter1); // Set the custom adapter to the spinner
        for (int x = 0; x < allICategories.size(); x++) {
            if (itemCat != null) {
                if (!itemCat.getCategory_name().equals("")) {
                    if (itemCat.getCategory_name().equals(allICategories.get(x).getCategory_name())) {
                        sp1.setSelection(x);
                        //return;
                    }
                }
            }
        }

        color1 = (Button) addItemDialog.findViewById(R.id.col_1);
        color2 = (Button) addItemDialog.findViewById(R.id.col_2);
        color3 = (Button) addItemDialog.findViewById(R.id.col_3);
        color4 = (Button) addItemDialog.findViewById(R.id.col_4);
        color5 = (Button) addItemDialog.findViewById(R.id.col_5);
        color6 = (Button) addItemDialog.findViewById(R.id.col_6);

        photo = (ImageView) addItemDialog.findViewById(R.id.item_pic);

        Button addCategory = (Button) addItemDialog.findViewById(R.id.add_category);
        Button addVariant = (Button) addItemDialog.findViewById(R.id.add_variant);
        Button saveItem = (Button) addItemDialog.findViewById(R.id.btn_save_item);
        Button back = (Button) addItemDialog.findViewById(R.id.btn_back);
        Button delete = (Button) addItemDialog.findViewById(R.id.btn_delete);
        Button scanBarcode = addItemDialog.findViewById(R.id.bcScanButton);
        barcodeTextView = addItemDialog.findViewById(R.id.barcodeText);

        variantList = (ListView) addItemDialog.findViewById(R.id.variant_list);

        final EditText name = (EditText) addItemDialog.findViewById(R.id.name);
        final EditText desc = (EditText) addItemDialog.findViewById(R.id.description);
        final EditText quantity = (EditText) addItemDialog.findViewById(R.id.quantity);
        price = (EditText) addItemDialog.findViewById(R.id.price);
        sku = (EditText) addItemDialog.findViewById(R.id.sku);

        TextView dialogTitle = (TextView) addItemDialog.findViewById(R.id.dialog_title);
        color1.setBackgroundResource(R.drawable.ic_col_one_selected);
        itemColor = Config.COLOR_1;
        priceVariations = new ArrayList<>();
        price.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});

        desc.setSingleLine(false);
        desc.setImeOptions(EditorInfo.TYPE_TEXT_FLAG_IME_MULTI_LINE);
        //desc.setSelection(desc.getText().length());

        //ImageLang.setImage(context,saveItem,"saveItem");

        adapter = new VariantAdapter(priceVariations, this, BaseActivity.this, false);
        variantList.setAdapter(adapter);


        variantList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                PriceVariation priceVariation = (PriceVariation) adapterView.getAdapter().getItem(position);
                showVariantPopUp(priceVariation);
            }
        });

        //editing the item, preload all values
        if (itemToUpdate != null) {
            dialogTitle.setText(context.getString(R.string.editItem));
            name.setText(itemToUpdate.getItem_name());
            //hideZeroPosition = true;
            if (itemToUpdate.getDescription() != null) {
                desc.setText(itemToUpdate.getDescription());
            }


            if (itemToUpdate.getItemTotal() != 0) {
                price.setText(String.format("%.2f", itemToUpdate.getItemTotal()));
            }
            if (itemToUpdate.getPriceVariations().size() != 0) {
                if (itemToUpdate.getPriceVariations().get(0).getIsRegular()) {
                    if (itemToUpdate.getPriceVariations().get(0).getSku() != null) {
                        sku.setText(itemToUpdate.getPriceVariations().get(0).getSku());
                    }

                }
            }

            if (itemToUpdate.getPriceVariations().get(0).getBarcode() != null) {
                barcodeTextView.setText(itemToUpdate.getPriceVariations().get(0).getBarcode());
            }

            if (itemToUpdate.getPriceVariations().get(0).getStk_count() != 0) {
                quantity.setText(String.valueOf(itemToUpdate.getPriceVariations().get(0).getStk_count()));
            }else{
                quantity.setText("");
            }

            if (itemToUpdate.getImage() != null) {
//                itemImage = BitmapFactory.decodeFile(itemToUpdate.getImage());
//                Bitmap roundBitmap = Util.getRoundedCornerBitmap(itemImage, 700);
//                if(roundBitmap != null)
//                    photo.setImageBitmap(roundBitmap);
                try {
                    byte[] encodeByte = Base64.decode(itemToUpdate.getImage(), Base64.DEFAULT);
                    itemImage = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
                    Bitmap roundBitmap = Util.getRoundedCornerBitmap(itemImage, 700);
                    if (roundBitmap != null)
                        photo.setImageBitmap(roundBitmap);
                } catch (Exception e) {
                    e.getMessage();

                }

            }

            priceVariations = itemToUpdate.getPriceVariations();

            if (priceVariations.size() > 1) {
                price.setVisibility(View.GONE);
                sku.setVisibility(View.GONE);
                quantity.setVisibility(View.GONE);
                barcodeTextView.setVisibility(View.GONE);
                scanBarcode.setVisibility(View.GONE);

            } else {
                if (priceVariations.size() != 0) {
                    if (!priceVariations.get(0).getIsVariable()) {
                        price.setText("");
                        sku.setText("");
                        price.setVisibility(View.GONE);
                        sku.setVisibility(View.GONE);
                        quantity.setVisibility(View.GONE);
                        barcodeTextView.setVisibility(View.GONE);
                        scanBarcode.setVisibility(View.GONE);
                    } else {
                        price.setText("");
                        sku.setText(priceVariations.get(0).getSku());
                    }
                }
            }


            if (priceVariations.size() > 1 || !priceVariations.get(0).getIsVariable()) {
                adapter = new VariantAdapter(priceVariations, this, BaseActivity.this, false);
                variantList.setAdapter(adapter);
            }

            clearBackgroundDrawables();
            switch (itemToUpdate.getColor()) {
                case 0:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
                case 1:
                    color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                    break;
                case 2:
                    color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                    break;
                case 3:
                    color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                    break;
                case 4:
                    color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                    break;
                case 5:
                    color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                    break;
                default:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
            }

            delete.setVisibility(View.VISIBLE);
            itemColor = itemToUpdate.getColor();
//            itemPrice = null;
//            itemSku = null;

        }

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayAttachImageDialog();
            }
        });


        scanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                baseBCScanClick = true;
                variantBCScanClick = false;
                displayScanBarcodeDialog();

            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItemDialog.dismiss();
                Config.dialogIsShowing = false;
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.sureDeleteItem))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                manager = new DBManager();
                                manager.deleteItem(itemToUpdate.getItem_id(), context);
                                //Util.deleteImageFile(itemToUpdate.getImage(),context);
                                addItemDialog.dismiss();
                                Config.dialogIsShowing = false;
                                clearElements();
                                Toast.makeText(context, context.getString(R.string.itemDeletedSuccess), Toast.LENGTH_LONG).show();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //handling button clicks and setting the item color
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                itemColor = Config.COLOR_1;
            }
        });
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                itemColor = Config.COLOR_2;
            }
        });
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                itemColor = Config.COLOR_3;
            }
        });
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                itemColor = Config.COLOR_4;
            }
        });
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                itemColor = Config.COLOR_5;
            }
        });
        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                itemColor = Config.COLOR_6;
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!Config.catDialogIsShowing) {
                    Config.catDialogIsShowing = true;
                    showAddCategoryPopUp(null);
                }

            }
        });

        //show the variant popup
        addVariant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showVariantPopUp(null);

            }
        });

        addItemDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                // dialog dismiss without button press
                Config.dialogIsShowing = false;

                itemCat = null;
            }
        });


        //on save click, validate all fields and send to DB
        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }

                itemName = name.getText().toString().trim();
                itemDesc = desc.getText().toString().trim();
                itemStock = quantity.getText().toString().trim();
                itemPrice = price.getText().toString().trim();
                itemSku = sku.getText().toString().trim();
                itemBarcode = barcodeTextView.getText().toString();

                if (itemName.length() == 0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.itemNameMissing), context.getString(R.string.alert));
                    return;
                }


                if (isImageDeleted) {
                    //Util.deleteImageFile(itemToUpdate.getImage(), context);
                    itemImage = null;
                    itemToUpdate.setImage(Config.NO_IMG);
                }

                boolean isItemExist = false;
                boolean isSkuExist = false;

                if (manager == null) {
                    manager = new DBManager();
                }

                ArrayList<Item> all_items = manager.loadItems(context);
//                if (!itemSku.equals("")) {
//                    for (int i = 0; i < priceVariations.size(); i++) {
//                        //if (priceVariations.get(i).getVariation().equals(enteredName.replace(" " , ""))) {
//                        if (priceVariations.get(i).getSku() != null) {
//                            if (priceVariations.get(i).getSku().toLowerCase().equals(itemSku.toLowerCase())) {
//                                Toast.makeText(context, context.getString(R.string.duplicateSku), Toast.LENGTH_LONG).show();
//                                isSkuExist = true;
//                                return;
//                            } else {
//                                isSkuExist = false;
//                            }
//                        }
//
//                    }
//                }

                if (itemToUpdate == null) {

                    for (int i = 0; i < all_items.size(); i++) {
                        if (all_items.get(i).getItem_name().toLowerCase().equals(itemName.toLowerCase())) {

                            isItemExist = true;
                            Toast.makeText(context, context.getString(R.string.itemExists), Toast.LENGTH_LONG).show();
                            return;

                        } else {
                            isItemExist = false;
                        }

                        if (!itemSku.equals("")) {

                            for (int x = 0; x < all_items.get(i).getPriceVariations().size(); x++) {
                                if (all_items.get(i).getPriceVariations().get(x).getSku() != null) {

                                    if (all_items.get(i).getPriceVariations().get(x).getSku().toLowerCase().equals(itemSku.toLowerCase())) {
                                        Toast.makeText(context, context.getString(R.string.duplicateSku1, all_items.get(i).getItem_name()), Toast.LENGTH_LONG).show();
                                        isSkuExist = true;
                                        return;
                                    } else {
                                        isSkuExist = false;
                                    }
                                }
                            }
                        }
                    }

                    if (!itemBarcode.equals("")) {
                        if (itemPrice.equals("")) {
                            showHintDialog(BaseActivity.this, context.getString(R.string.barcodeWarning), context.getString(R.string.alert));
                            return;
                        }
                    }
                } else {

                    for (int i = 0; i < all_items.size(); i++) {

                        //if (itemToUpdate.getPriceVariations().size() == 0) {

                        if (all_items.get(i).getItem_name().toLowerCase().equals(itemName.toLowerCase()) && !itemToUpdate.getItem_name().toLowerCase().equals(itemName.toLowerCase())) {

                            isItemExist = true;
                            Toast.makeText(context, context.getString(R.string.itemExists), Toast.LENGTH_LONG).show();
                            return;

                        } else {
                            isItemExist = false;
                        }

                    }
                    //psg update for variable issue

                    if (itemToUpdate.getPriceVariations().size() != 0) {

                        if (itemToUpdate.getPriceVariations().get(0).getIsVariable()) {
                            if (!itemSku.equals("")) {
                                itemToUpdate.getPriceVariations().get(0).setSku(itemSku);
                            }
                            if (!itemPrice.equalsIgnoreCase("") || itemToUpdate.getPriceVariations().size() > 1) {
                                itemToUpdate.getPriceVariations().remove(0);

                            }

                        }
                    }

                }

                if (!isItemExist && !isSkuExist) {

                    //make sure the base price is not saved again when editing the item
                    if (!itemPrice.equals("")) {

                        double priceinDouble = Double.parseDouble(itemPrice);
                        if (priceinDouble > MAX_AMOUNT) {
                            showHintDialog(BaseActivity.this, context.getString(R.string.exceededPrice), context.getString(R.string.alert));
                            return;
                        }

                        PriceVariation variation = new PriceVariation();
                        variation.setPrice(Double.parseDouble(itemPrice));
                        variation.setVariation("Regular");
                        variation.setIsRegular(true);
                        variation.setIsVariable(false);

                        if (!itemStock.equals("")) {
                            variation.setStk_count(Long.parseLong(itemStock));
                        }
                        if (!itemBarcode.equals("")) {
                            variation.setBarcode(itemBarcode);
                        }
                        if (itemSku != null) {
                            variation.setSku(itemSku);
                        }
                        if (itemSku.length() != 0) {
                            variation.setSku(itemSku);
                        }

                        priceVariations.add(0, variation);
                        isBasePrice = true;


                    } else {

                        if (priceVariations.size() == 0) {
                            isBasePrice = false;
                            PriceVariation variation = new PriceVariation();
                            variation.setVariation("Variable");
                            variation.setIsRegular(false);
                            variation.setIsVariable(true);

                            if (!itemStock.equals("")) {
                                variation.setStk_count(Long.parseLong(itemStock));
                            }
                            if (!itemBarcode.equals("")) {
                                variation.setBarcode(itemBarcode);
                            }
                            if (itemSku != null) {
                                variation.setSku(itemSku);
                            }

                            priceVariations.add(0, variation);
                        }
                    }

                    if (priceVariations.size() == 0 && !itemPrice.equals("")) {
                        PriceVariation variation = new PriceVariation();
                        variation.setPrice(Double.parseDouble(itemPrice));
                        variation.setVariation("Regular");
                        variation.setIsRegular(true);
                        variation.setIsVariable(false);

                        if (!itemStock.equals("")) {
                            variation.setStk_count(Long.parseLong(itemStock));
                        }
                        if (!itemBarcode.equals("")) {
                            variation.setBarcode(itemBarcode);
                        }
                        if (itemSku != null) {
                            variation.setSku(itemSku);
                        }

                        priceVariations.add(0, variation);
                        isBasePrice = true;

                    } else {
                        isBasePrice = false;
                    }


                    if (priceVariations.size() != 0) {


                        for (int i = 0; i < priceVariations.size(); i++) {
                            if (isBasePrice) {
                                //if (priceVariations.get(i).getVariation().equals("Regular")) {
                                priceVariations.get(i).setPrice(Double.parseDouble(itemPrice));

                            }

                        }

                    }
                    if (itemColor == 0) {
                        color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                        itemColor = Config.COLOR_1;
                    }

                    //create Item OBJ, and pass to DB manager.
                    item = new Item();
                    item.setColor(itemColor);
                    item.setItem_name(itemName);

                    if (itemCat != null) {
                        item.setCategory(itemCat);
                    }

                    if (itemDesc.length() != 0) {
                        item.setDescription(itemDesc);
                    } else {
                        item.setDescription("");
                    }


                    item.setPriceVariations(priceVariations);
                    if (priceVariations.size() != 0) {
                        item.setPriceSelected(priceVariations.get(0));
                    }

                    //set item ID that is needed for updating
                    if (itemToUpdate != null) {
                        item.setItem_id(itemToUpdate.getItem_id());
                    }

                    if (itemImage != null) {


                        //Bitmap roundBitmap = Util.getRoundedCornerBitmap(itemImage, 700);
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        itemImage.compress(Bitmap.CompressFormat.PNG, 100, baos);
                        byte[] byteArrayImage = baos.toByteArray();

                        String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
                        //String imgPath = Util.saveImage(itemImage,itemName).toString();
                        item.setImage(encodedImage);
                    } else {
                        item.setImage(Config.NO_IMG);
                    }

                    if (manager == null) {

                        manager = new DBManager();
                    }
                    boolean doesExist = false;
                    try {
                        //updating the item
                        if (itemToUpdate != null) {
                            manager.editItem(item, context);
                            Toast.makeText(context, context.getString(R.string.itemEdited), Toast.LENGTH_LONG).show();

                        } else {
                            //adding an item
                            manager.saveItem(context, item);
                            Toast.makeText(context, context.getString(R.string.itemAddSuccess), Toast.LENGTH_LONG).show();

                        }
                        BaseActivity.closeCatListner = true;
                    } catch (CouchbaseLiteException e) {
                        e.printStackTrace();
                    }


                    if (!isItemExist && !isSkuExist) {
                        addItemDialog.dismiss();
                        isAddItemLoaded = false;
                        Config.dialogIsShowing = false;
                        itemCat = null;
                        clearElements();

                    }
                }

            }
        });

//        final List<Category> finalAllICategories = allICategories;
        sp1.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

                if (!isAddItemLoaded) {

                    allICategories.clear();
                    if (manager == null) {
                        manager = new DBManager();
                    }
                    allICategories = manager.loadCategories(context);

                    Category dummyCat = new Category();
                    dummyCat.setCategory_name(context.getString(R.string.category));
                    allICategories.add(0, dummyCat);

                }
                if (position != 0) {
                    sp1.setSelection(position);
                    itemCat = allICategories.get(position);

                } else {

                    sp1.setSelection(0);
                    itemCat = null;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

        addItemDialog.show();

    }


    /*
    On imageview click, show these options
     */
    private void showImagePickerDialog() {
        CharSequence[] items;
        items = new CharSequence[]{context.getString(R.string.takePhoto), context.getString(R.string.choosePhoto), context.getString(R.string.deletePhoto)};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(context.getString(R.string.addDeletePhoto));
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item1) {
                if (item1 == 0) {
                    dispatchTakePhotoIntent();
                } else if (item1 == 1) {
                    dispatchChoosePhotoIntent();
                } else if (item1 == 2) {
                    if (itemToUpdate != null) {
                        if (itemToUpdate.getImage() != null) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.sureDeleteItemImage))
                                    .setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            isImageDeleted = false;
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //Util.deleteImageFile(itemToUpdate.getImage(), context);
                                            itemImage = null;
                                            isImageDeleted = true;
                                            itemToUpdate.setImage(Config.NO_IMG);
                                            photo.setImageResource(R.drawable.add_picture);

                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else if (photo != null) {
                            itemToUpdate.setImage(Config.NO_IMG);
                            photo.setImageResource(R.drawable.add_picture);
                            isImageDeleted = true;

                        } else {
                            Toast.makeText(context, context.getString(R.string.noImageToDelete), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (itemImage != null) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setMessage(context.getString(R.string.sureDeleteItemImage))
                                    .setCancelable(false)
                                    .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    })
                                    .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            itemImage = null;
                                            photo.setImageResource(R.drawable.add_picture);
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            Toast.makeText(context, context.getString(R.string.noImageToDelete), Toast.LENGTH_LONG).show();
                        }
                    }
                }
            }
        });
        builder.show();
    }


    //open cam to take photo
    private void dispatchTakePhotoIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String fileName = Config.APP_NAME + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(fileName, ".jpg", storageDir);
        mImagePathToBeAttached = image.getAbsolutePath();
        return image;
    }


    private void dispatchChoosePhotoIntent() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select File"), REQUEST_CHOOSE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUESTS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showImagePickerDialog();
                }
                break;

            }
            case 1111: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                    scanIntegrator.initiateScan();
                }
            }
        }
    }

    private void displayAttachImageDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA}, PERMISSION_REQUESTS);
        } else {
            showImagePickerDialog();
        }
    }

    private void displayScanBarcodeDialog() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1111);

        } else {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK) {
            return;
        }

        final int size = THUMBNAIL_SIZE;
        if (requestCode == REQUEST_TAKE_PHOTO) {
            File file = new File(mImagePathToBeAttached);
            if (file.exists()) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                options.inJustDecodeBounds = false;
                //bitmap to be saved to sdcard
                itemImage = BitmapFactory.decodeFile(mImagePathToBeAttached, options);
                //bitmap to be loaded to the imageview
                Bitmap roundBitmap = Util.getRoundedCornerBitmap(itemImage, 700);
                if (roundBitmap != null)
                    photo.setImageBitmap(roundBitmap);
                file.delete();
            }
        } else if (requestCode == REQUEST_CHOOSE_PHOTO) {
            Uri uri = data.getData();
            ContentResolver resolver = this.getContentResolver();
            try {
                //bitmap to be saved to sdcard
                itemImage = MediaStore.Images.Media.getBitmap(resolver, uri);
                //bitmap to be loaded to the imageview
                Bitmap roundBitmap = Util.getRoundedCornerBitmap(itemImage, 700);
//                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//                roundBitmap.compress(Bitmap.CompressFormat.JPEG, 40,bytes);
                if (roundBitmap != null)
                    photo.setImageBitmap(roundBitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (scanningResult != null) {
                String scanContent = scanningResult.getContents();
                //String scanFormat = scanningResult.getFormatName();

                if (baseBCScanClick) {
                    barcodeTextView.setText(scanContent);
                }
                if (variantBCScanClick) {
                    variantBarcode.setText(scanContent);
                }
                if (scanBConSale) {
                    sendItemToCartByBarcode(scanContent);
                }


            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "No scan data received!", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void clearBackgroundDrawables() {
        color1.setBackgroundResource(R.drawable.ic_col_1);
        color2.setBackgroundResource(R.drawable.ic_col_2);
        color3.setBackgroundResource(R.drawable.ic_col_3);
        color4.setBackgroundResource(R.drawable.ic_col_4);
        color5.setBackgroundResource(R.drawable.ic_col_5);
        color6.setBackgroundResource(R.drawable.ic_col_6);
    }

    public void sendItemToCartByBarcode(String barcode) {

        if (manager == null) {
            manager = new DBManager();
        }

        ArrayList<Item> all_items = manager.loadItems(context);
        ArrayList<JSONObject> matchingItems = new ArrayList<>();
        JSONObject matchedBarcode = null;

        for (int i = 0; i < all_items.size(); i++) {
            for (int x = 0; x < all_items.get(i).getPriceVariations().size(); x++) {
                if (all_items.get(i).getPriceVariations().get(x).getBarcode() != null) {
                    if (all_items.get(i).getPriceVariations().get(x).getBarcode().equals(barcode)) {
                        //Toast.makeText(context, "Item found " + all_items.get(i).getItem_name(), Toast.LENGTH_LONG).show();
                        try {
                            matchedBarcode = new JSONObject();
                            matchedBarcode.putOpt("item", all_items.get(i));
                            matchedBarcode.putOpt("variation", all_items.get(i).getPriceVariations().get(x));
                        } catch (Exception e) {

                        }

                        //matchingItems.add(all_items.get(i));
                        matchingItems.add(matchedBarcode);

                    }
                }
            }

        }

        if (matchingItems.size() == 0) {

            Toast.makeText(context, "No item found ", Toast.LENGTH_LONG).show();

        } else if (matchingItems.size() == 1) {
            Item item = null;
            PriceVariation matchedVariation = null;

            try {
                JSONObject obj = matchingItems.get(0);
                item = (Item) obj.get("item");
                matchedVariation = (PriceVariation) obj.get("variation");
            } catch (Exception e) {

            }

            if (item.getPriceVariations().get(0).getIsVariable()) {
                showAddVariableItemPrice(item, ItemsViewModel.parentFrag);
                return;
            }

            item.setItemDiscount(null);
            item.setPriceSelected(item.getPriceSelected());


            Item itemToSend = new Item();
            itemToSend.setItemTotal(String.format("%.2f", matchedVariation.getPrice()));
            itemToSend.setItem_name(item.getItem_name() + "-" + matchedVariation.getVariation());
            itemToSend.setQuantity(1);
            itemToSend.setCategory(item.getCategory());
            itemToSend.setPriceVariations(item.getPriceVariations());
            //item.setPriceSelected(selectedVariation);
            itemToSend.setItemType(Config.ITEM);

            PriceVariation variation = new PriceVariation();
            variation.setPrice(matchedVariation.getPrice());
            variation.setVariation(matchedVariation.getVariation());
            //variation.setStk_count(matchedVariation.getStk_count()-1);

            itemToSend.setPriceSelected(variation);

            ItemsViewModel.parentFrag.itemToAdd.sendData(itemToSend);


        } else {
            multipleBarcodePopup(matchingItems);

        }
    }

    public void multipleBarcodePopup(final ArrayList<JSONObject> matchingItems) {

        isMultipleBarcodeOpened = true;
        List<String> listItems = new ArrayList<String>();

        PriceVariation matchedVariation = null;
        Item item = null;

        for (int i = 0; i < matchingItems.size(); i++) {
            try {
                JSONObject obj = matchingItems.get(i);
                item = (Item) obj.get("item");
                matchedVariation = (PriceVariation) obj.get("variation");

                listItems.add(item.getItem_name()+ " - "+matchedVariation.getVariation()+"  -   Rs "+matchedVariation.getPrice());

            } catch (Exception e) {

            }
        }

        final CharSequence[] charSequenceItems = listItems.toArray(new CharSequence[listItems.size()]);


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select an item");
        builder.setItems(charSequenceItems, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                //create a new object, bind the variation name to the item name
                Item item = null;
                PriceVariation matchedVariation = null;
                try {
                    JSONObject object = matchingItems.get(position);
                    item = (Item) object.get("item");
                    matchedVariation = (PriceVariation) object.get("variation");
                } catch (Exception e) {
                }

                item.setItemDiscount(null);
                item.setPriceSelected(item.getPriceSelected());

                Item itemToSend = new Item();
                itemToSend.setItemTotal(String.format("%.2f", matchedVariation.getPrice()));
                itemToSend.setItem_name(item.getItem_name() + "-" + matchedVariation.getVariation());
                itemToSend.setQuantity(1);
                itemToSend.setCategory(item.getCategory());
                itemToSend.setPriceVariations(item.getPriceVariations());
                //item.setPriceSelected(selectedVariation);
                itemToSend.setItemType(Config.ITEM);

                PriceVariation variation = new PriceVariation();
                variation.setPrice(matchedVariation.getPrice());
                variation.setVariation(matchedVariation.getVariation());
                variation.setStk_count(matchedVariation.getStk_count());

                itemToSend.setPriceSelected(variation);

                ItemsViewModel.parentFrag.itemToAdd.sendData(itemToSend);
                isMultipleBarcodeOpened = false;
            }
        });
        AlertDialog alert = builder.create();
        alert.show();


    }

    /*
    Pop up that adds price variants
     */
    public void showVariantPopUp(final PriceVariation priceVariationToEdit) {

        final Dialog dialog = new Dialog(BaseActivity.this, R.style.Custom_Dialog);
        dialog.setContentView(R.layout.variant_pop_up);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = dialog.getWindow().getAttributes();

        //align to right top of the below pop up and make background dim
        wlp.gravity = Gravity.RIGHT | Gravity.TOP;
        wlp.dimAmount = 0.7f;

        if (getTab() == 10) {
            wlp.x = 280;
            wlp.y = 120;
        }

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);

        final EditText variantName = (EditText) dialog.findViewById(R.id.name);
        final EditText variantPrice = (EditText) dialog.findViewById(R.id.priceV);
        final EditText variantStock = (EditText) dialog.findViewById(R.id.quantytyV);
        final EditText variantSku = (EditText) dialog.findViewById(R.id.skuV);
        Button save = (Button) dialog.findViewById(R.id.btn_save_item);
        Button back = (Button) dialog.findViewById(R.id.btn_back);
        TextView dialogTitle = (TextView) dialog.findViewById(R.id.dialog_title);
        variantBarcode = dialog.findViewById(R.id.barcodeText);
        Button variantScanBarcode = dialog.findViewById(R.id.bcScanButton);

        //ImageLang.setImage(context,save,"addvariant");
        variantBCScanClick = false;
        variantPrice.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});

        if (priceVariationToEdit != null) {
            dialogTitle.setText(context.getString(R.string.editPricePoint));

            if (priceVariationToEdit.getVariation() != null) {
                variantName.setText(priceVariationToEdit.getVariation());
            }

            //if (priceVariationToEdit.getPrice() != 0) {
            variantPrice.setText(String.format("%.2f", priceVariationToEdit.getPrice()));
            //}

            if (priceVariationToEdit.getSku() != null) {
                variantSku.setText(priceVariationToEdit.getSku());
            }
            if (priceVariationToEdit.getBarcode() != null) {
                variantBarcode.setText(priceVariationToEdit.getBarcode());
            }
            if (priceVariationToEdit.getStk_count() != 0) {
                variantStock.setText(String.valueOf(priceVariationToEdit.getStk_count()));
            }
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        variantScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                variantBCScanClick = true;
                baseBCScanClick = false;
                displayScanBarcodeDialog();

            }
        });

        /*
        On save click, validate the fields, create a variation OBJ,
        Add to the list, notify the dataset to populate in the list,
        Finally close the dialog.
         */
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = variantName.getText().toString().trim();
                String enteredPrice = variantPrice.getText().toString().trim();
                String enteredSku = variantSku.getText().toString().trim();
                String scannedBarcode = variantBarcode.getText().toString();
                String enteredStock = variantStock.getText().toString().trim();

                if (enteredName.length() == 0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.variantNameMissing), context.getString(R.string.alert));
                    return;
                }

                if (enteredPrice.length() == 0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.variantPriceMissing), context.getString(R.string.alert));
                    return;
                }

                if (Double.parseDouble(enteredPrice) > MAX_AMOUNT) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.exceededVariantPrice), context.getString(R.string.alert));
                    return;
                }
                if (manager == null) {

                    manager = new DBManager();
                }


                ArrayList<Item> all_items = manager.loadItems(context);
                boolean isSkuExist = false;

                String baseSku = sku.getText().toString().trim();

                if (!enteredSku.equals("") && !baseSku.equals("")) {
                    if (enteredSku.toLowerCase().equals(baseSku.toLowerCase())) {
                        Toast.makeText(context, context.getString(R.string.duplicateSku), Toast.LENGTH_LONG).show();
                        isSkuExist = true;
                        return;
                    }
                }

                if (priceVariationToEdit != null) {
                    if (priceVariationToEdit.getSku() != null && !enteredSku.equals("")) {

                        if (!priceVariationToEdit.getSku().equalsIgnoreCase(enteredSku)) {

                            for (int i = 0; i < priceVariations.size(); i++) {
                                if (priceVariations.get(i).getSku() != null) {
                                    if (priceVariations.get(i).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                        Toast.makeText(context, context.getString(R.string.duplicateSku), Toast.LENGTH_LONG).show();
                                        isSkuExist = true;
                                        return;
                                    } else {
                                        isSkuExist = false;
                                    }
                                }

                            }

                            for (int i = 0; i < all_items.size(); i++) {

                                for (int x = 0; x < all_items.get(i).getPriceVariations().size(); x++) {
                                    if (all_items.get(i).getPriceVariations().get(x).getSku() != null) {
                                        if (all_items.get(i).getPriceVariations().get(x).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                            Toast.makeText(context, context.getString(R.string.duplicateSku1, all_items.get(i).getItem_name()), Toast.LENGTH_LONG).show();
                                            isSkuExist = true;
                                            return;
                                        } else {
                                            isSkuExist = false;
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        if (!enteredSku.equals("")) {

                            for (int y = 0; y < priceVariations.size(); y++) {
                                //if (priceVariations.get(i).getVariation().equals(enteredName.replace(" " , ""))) {
                                if (priceVariations.get(y).getSku() != null && !enteredSku.equals("")) {
                                    if (priceVariations.get(y).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                        Toast.makeText(context, context.getString(R.string.duplicateSku), Toast.LENGTH_LONG).show();
                                        isSkuExist = true;
                                        return;
                                    }
                                } else {
                                    isSkuExist = false;
                                }
                            }

                            for (int i = 0; i < all_items.size(); i++) {

                                for (int x = 0; x < all_items.get(i).getPriceVariations().size(); x++) {
                                    if (all_items.get(i).getPriceVariations().get(x).getSku() != null) {
                                        if (all_items.get(i).getPriceVariations().get(x).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                            Toast.makeText(context, context.getString(R.string.duplicateSku1, all_items.get(i).getItem_name()), Toast.LENGTH_LONG).show();
                                            isSkuExist = true;
                                            return;
                                        } else {
                                            isSkuExist = false;
                                        }
                                    }
                                }

                            }
                        }
                    }


                    if (!isSkuExist) {

                        priceVariationToEdit.setVariation(enteredName);
                        priceVariationToEdit.setPrice(Double.parseDouble(enteredPrice));
                        priceVariationToEdit.setIsRegular(false);
                        priceVariationToEdit.setBarcode(scannedBarcode);
                        if(!enteredStock.equals("")) {
                            priceVariationToEdit.setStk_count(Long.parseLong(enteredStock));
                        }

                        if (enteredSku.length() == 0) {
                            priceVariationToEdit.setSku(null);
                        } else {
                            priceVariationToEdit.setSku(enteredSku);
                        }

                        adapter.notifyDataSetChanged();


                        Toast.makeText(context, context.getString(R.string.pricepointUpdatedSuccess), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                } else {
                    for (int i = 0; i < priceVariations.size(); i++) {
                        //if (priceVariations.get(i).getVariation().equals(enteredName.replace(" " , ""))) {
                        if (priceVariations.get(i).getSku() != null && !enteredSku.equals("")) {
                            if (priceVariations.get(i).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                Toast.makeText(context, context.getString(R.string.duplicateSku), Toast.LENGTH_LONG).show();
                                isSkuExist = true;
                                return;
                            }
                        } else {
                            isSkuExist = false;
                        }
                        // }
                    }
                    for (int i = 0; i < all_items.size(); i++) {

                        for (int x = 0; x < all_items.get(i).getPriceVariations().size(); x++) {
                            if (all_items.get(i).getPriceVariations().get(x).getSku() != null && !enteredSku.equals("")) {
                                if (all_items.get(i).getPriceVariations().get(x).getSku().toLowerCase().equals(enteredSku.toLowerCase())) {
                                    Toast.makeText(context, context.getString(R.string.duplicateSku1, all_items.get(i).getItem_name()), Toast.LENGTH_LONG).show();
                                    isSkuExist = true;
                                    return;
                                } else {
                                    isSkuExist = false;
                                }
                            }
                        }
                    }

                    if (!isSkuExist) {


                        PriceVariation variation = new PriceVariation();
                        variation.setVariation(enteredName);
                        variation.setPrice(Double.parseDouble(enteredPrice));
                        variation.setBarcode(scannedBarcode);
                        variation.setIsRegular(false);
                        variation.setIsVariable(false);
                        if(!enteredStock.equals("")) {
                            variation.setStk_count(Long.parseLong(enteredStock));
                        }

                        if (enteredSku.length() != 0) {
                            variation.setSku(enteredSku);
                        }

                        for (int i = 0; priceVariations.size() > i; i++) {
                            if (priceVariations.get(i).getIsVariable()) {
                                priceVariations.remove(i);
                            }
                        }

                        priceVariations.add(variation);
                        adapter = new VariantAdapter(priceVariations, context, BaseActivity.this, false);
                        variantList.setAdapter(adapter);

                        Toast.makeText(context, context.getString(R.string.pricepointSavedSuccess), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                }
            }
        });


        dialog.show();
    }


    public void showAddCategoryPopUp(final Category categoryToEdit) {

        final Dialog addCatDialog = new Dialog(this, R.style.Custom_Dialog);
        addCatDialog.setContentView(R.layout.add_category_pop_up);
        //get all items
        ArrayList<Item> allItems = new ArrayList<>();
        if (manager == null) {
            manager = new DBManager();
        }
        final Query query = manager.returnDB(context).createAllDocumentsQuery();
        allItems = manager.loadItems(context, query);

        color1 = (Button) addCatDialog.findViewById(R.id.col_1);
        color2 = (Button) addCatDialog.findViewById(R.id.col_2);
        color3 = (Button) addCatDialog.findViewById(R.id.col_3);
        color4 = (Button) addCatDialog.findViewById(R.id.col_4);
        color5 = (Button) addCatDialog.findViewById(R.id.col_5);
        color6 = (Button) addCatDialog.findViewById(R.id.col_6);

        final Button addCategory = (Button) addCatDialog.findViewById(R.id.btn_done);
        Button btnBack = (Button) addCatDialog.findViewById(R.id.btn_back);
        EditText searchView = (EditText) addCatDialog.findViewById(R.id.search);
        Button deleteCategory = (Button) addCatDialog.findViewById(R.id.btn_delete);
        final EditText catName = (EditText) addCatDialog.findViewById(R.id.name);

        ListView itemList = (ListView) addCatDialog.findViewById(R.id.list);
        TextView dialogTitle = (TextView) addCatDialog.findViewById(R.id.dialog_title);

        clearBackgroundDrawables();
        color1.setBackgroundResource(R.drawable.ic_col_one_selected);
        catColor = Config.COLOR_1;

        //ImageLang.setImage(context,addCategory,"addCategory");

        //prepopulate the selected category of the item to edit

        if (categoryToEdit != null) {
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getCategory() != null && allItems.get(i).getCategory().getCategory_name().equals(categoryToEdit.getCategory_name())) {
                    allItems.get(i).setChecked(true);
                }

            }
        }

        final ArrayList<Item> undefineCategory = new ArrayList<>();
        if (categoryToEdit == null) {
            for (int i = 0; i < allItems.size(); i++) {
                Item item = allItems.get(i);
                if (item.getCategory() == null || item.getCategory().equals(null)) {
                    undefineCategory.add(item);
                }
            }
        } else {
            for (int i = 0; i < allItems.size(); i++) {
                Item item = allItems.get(i);
                if (item.getCategory() == null || item.getCategory().getCategory_name().toString().equalsIgnoreCase(categoryToEdit.getCategory_name().toString())) {
                    undefineCategory.add(item);
                }
            }
        }

        final CreateCategoryItemsAdapter catAdapter = new CreateCategoryItemsAdapter(undefineCategory, context);
        itemList.setAdapter(catAdapter);

        searchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                catAdapter.filter(s.toString());
            }
        });

        if (categoryToEdit != null) {
            dialogTitle.setText(context.getString(R.string.editCat));

            catName.setText(categoryToEdit.getCategory_name());
            clearBackgroundDrawables();
            switch (categoryToEdit.getCatColor()) {
                case 0:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
                case 1:
                    color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                    break;
                case 2:
                    color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                    break;
                case 3:
                    color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                    break;
                case 4:
                    color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                    break;
                case 5:
                    color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                    break;
                default:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
            }

            deleteCategory.setVisibility(View.VISIBLE);
            catColor = categoryToEdit.getCatColor();

        }

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCatDialog.dismiss();
                Config.catDialogIsShowing = false;
                if (addItemDialog != null) {
                    if (addItemDialog.isShowing()) {
                        allICategories.clear();
                        allICategories = manager.loadCategories(context);

                        Category dummyCat = new Category();
                        dummyCat.setCategory_name("");
                        allICategories.add(0, dummyCat);

                        catAdapter1 = new CategorySpinnerAdapter(BaseActivity.this, R.layout.simple_spinner_item, allICategories);
                        sp1.setAdapter(catAdapter1);

                    }
                }
            }
        });

        deleteCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }

                manager = new DBManager();

                ArrayList<Item> all_items = manager.loadItems(context);
                boolean hasItem = false;

                for (int i = 0; i < all_items.size(); i++) {
                    if (all_items.get(i).getCategory() != null) {

                        if (all_items.get(i).getCategory().getCategory_name().equals(categoryToEdit.getCategory_name())) {
                            hasItem = true;
                        }
                    }
                }

                if (hasItem) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.wishToCont))
                            .setCancelable(false)
                            .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    manager = new DBManager();

                                    ArrayList<Item> all_items = manager.loadItems(context);

                                    for (int i = 0; i < all_items.size(); i++) {
                                        if (all_items.get(i).getCategory() != null) {
                                            if (all_items.get(i).getCategory().getCategory_name().equals(categoryToEdit.getCategory_name())) {
                                                all_items.get(i).setCategory(null);
                                                manager.editItem(all_items.get(i), context);
                                            }
                                        }
                                    }

                                    manager.deleteCategory(categoryToEdit.getCategory_id(), context);
                                    addCatDialog.dismiss();
                                    Config.catDialogIsShowing = false;
                                    //clearElements();
                                    Toast.makeText(context, context.getString(R.string.catDeleteSuccess), Toast.LENGTH_LONG).show();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setMessage(context.getString(R.string.sureDeleteCat))
                            .setCancelable(false)
                            .setPositiveButton(context.getText(R.string.no), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton(context.getText(R.string.yes), new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    manager.deleteCategory(categoryToEdit.getCategory_id(), context);
                                    addCatDialog.dismiss();
                                    Config.catDialogIsShowing = false;
                                    clearElements();
                                    Toast.makeText(context, context.getString(R.string.catDeleteSuccess), Toast.LENGTH_LONG).show();
                                }
                            });


                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        });


        //handling button clicks and setting the item color
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                catColor = Config.COLOR_1;
            }
        });
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                catColor = Config.COLOR_2;
            }
        });
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                catColor = Config.COLOR_3;
            }
        });
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                catColor = Config.COLOR_4;
            }
        });
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                catColor = Config.COLOR_5;
            }
        });
        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                catColor = Config.COLOR_6;
            }
        });

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }

                String categoryName = catName.getText().toString().trim();

                if (categoryName.length() == 0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.catNameMissing), context.getString(R.string.alert));
                    return;
                }
                if (manager == null) {
                    manager = new DBManager();
                }
                final Query query = manager.returnDB(context).createAllDocumentsQuery();

                ArrayList<Category> allICategories = manager.loadCategories(context, query);
                boolean doesExist = false;

                if (categoryToEdit == null) {

                    for (int i = 0; i < allICategories.size(); i++) {
                        if (allICategories.get(i).getCategory_name().toLowerCase().equals(categoryName.toLowerCase())) {

                            Toast.makeText(context, context.getString(R.string.catExists), Toast.LENGTH_LONG).show();
                            doesExist = true;
                            return;

                        } else {
                            doesExist = false;
                        }
                    }
                } else {
                    for (int i = 0; i < allICategories.size(); i++) {
                        if (allICategories.get(i).getCategory_name().toLowerCase().equals(categoryName.toLowerCase()) && !categoryToEdit.getCategory_name().equalsIgnoreCase(categoryName)) {
                            doesExist = true;
                            Toast.makeText(context, context.getString(R.string.catExists), Toast.LENGTH_LONG).show();
                            return;

                        } else {
                            doesExist = false;
                        }
                    }
                }


                Category category = new Category();
                category.setCatColor(catColor);
                category.setCategory_name(categoryName);


                try {

                    //editing an item, check if item exists
                    if (categoryToEdit != null) {
                        category.setCategory_id(categoryToEdit.getCategory_id());
                        //edit all items of the edited category as well
                        //manager.editItemCategory(category, context);
                        doesExist = manager.editCategory(category, context);
                        if (!doesExist) {
                            //Toast.makeText(context, Config.CAT_ALREADY_EXISTS, Toast.LENGTH_LONG).show();
                            Toast.makeText(context, context.getString(R.string.catEditSuccess), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
                        String userName = loginPreferences.getString("username", null);
                        String storeId = loginPreferences.getString("storeId", null);
                        //String catId = userName + "." + UUID.randomUUID();
                        String catId = Config.CATEGORY + "." + category.getCategory_name()+ "." +storeId;

                        category.setCategory_id(catId);
                        //inserting an item, check if item exists
                        doesExist = manager.saveCategory(context, category);
                        Log.d("TAG", "manager.saveCategory doesExist = " + doesExist);
                        if (!doesExist) {
                            //Toast.makeText(context, Config.CAT_ALREADY_EXISTS, Toast.LENGTH_LONG).show();

                            Toast.makeText(context, context.getString(R.string.catSaveSuccess), Toast.LENGTH_LONG).show();
                            if (addItemDialog != null) {
                                if (addItemDialog.isShowing()) {

                                    isCategoryAddedToItemPopup = true;

                                    allICategories.clear();
                                    allICategories = manager.loadCategories(context);

                                    Category dummyCat = new Category();
                                    dummyCat.setCategory_name(context.getString(R.string.category));
                                    allICategories.add(0, dummyCat);
                                    isAddItemLoaded = false;

                                    catAdapter1 = new CategorySpinnerAdapter(BaseActivity.this, R.layout.simple_spinner_item, allICategories);
                                    sp1.setAdapter(catAdapter1);
                                    sp1.setPrompt("Select Category");

                                    color1 = (Button) addItemDialog.findViewById(R.id.col_1);
                                    color2 = (Button) addItemDialog.findViewById(R.id.col_2);
                                    color3 = (Button) addItemDialog.findViewById(R.id.col_3);
                                    color4 = (Button) addItemDialog.findViewById(R.id.col_4);
                                    color5 = (Button) addItemDialog.findViewById(R.id.col_5);
                                    color6 = (Button) addItemDialog.findViewById(R.id.col_6);


                                } else {
                                    isCategoryAddedToItemPopup = false;
                                }
                            }
                        }
                    }

                    for (int i = 0; i < undefineCategory.size(); i++) {


                        Item itm = undefineCategory.get(i);
                        itm.setCategory(null);
                        if (itm.isChecked()) {
                            itm.setCategory(category);
                        }

                        manager.editItem(itm, context);

                        if (catAdapter.isSearched) {
                            if (catAdapter.catSelectedItems.size() > 0) {
                                for (int x = 0; x < catAdapter.catSelectedItems.size(); x++) {
                                    Item itm1 = catAdapter.catSelectedItems.get(i);
                                    itm1.setCategory(category);
                                    manager.editItem(itm1, context);
                                }
                            }
                        }


                        // }
                    }

                    boolean alreadyAssignedCategory = false;

                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                if (!doesExist) {
                    addCatDialog.dismiss();
                    Config.catDialogIsShowing = false;

                    //clearElements();
                }
                catAdapter.isSearched = false;

            }
        });

        addCatDialog.show();
    }

    /*
        Used to insert a discount, edit and delete.
     */
    public void showDiscountPopUp(final Discount discountToEdit) {

        final Dialog discountDialog = new Dialog(this, R.style.Custom_Dialog);
        discountDialog.setContentView(R.layout.add_discount_pop_up);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(discountDialog.getWindow().getAttributes());

        if (getTab() == 10) {
            lp.width = width * 4 / 10;
            lp.height = height * 2 / 3;
        } else {
            lp.width = width * 5 / 10;
            lp.height = (int) (height * (2.5 / 3));
        }

        lp.gravity = Gravity.CENTER;

        discountDialog.getWindow().setAttributes(lp);

        final EditText discName = (EditText) discountDialog.findViewById(R.id.discountNameEditText);
        final EditText discAmount = (EditText) discountDialog.findViewById(R.id.percentageEditText);
        ToggleButton toggleAmount = (ToggleButton) discountDialog.findViewById(R.id.toggleButton1);
        Button addDisc = (Button) discountDialog.findViewById(R.id.btn_add_discount);
        ImageButton btnBack = (ImageButton) discountDialog.findViewById(R.id.backButton);
        Button btnDelete = (Button) discountDialog.findViewById(R.id.btn_delete);
        TextView dialogTitle = (TextView) discountDialog.findViewById(R.id.dialog_title);

        color1 = (Button) discountDialog.findViewById(R.id.col_1);
        color2 = (Button) discountDialog.findViewById(R.id.col_2);
        color3 = (Button) discountDialog.findViewById(R.id.col_3);
        color4 = (Button) discountDialog.findViewById(R.id.col_4);
        color5 = (Button) discountDialog.findViewById(R.id.col_5);
        color6 = (Button) discountDialog.findViewById(R.id.col_6);

        discAmount.setFilters(new InputFilter[]{new DecimalDigitsInputFilter(7, 2)});

        clearBackgroundDrawables();
        color1.setBackgroundResource(R.drawable.ic_col_one_selected);
        itemColor = Config.COLOR_1;

        if (discountToEdit != null) {
            discount = discountToEdit;
            btnDelete.setVisibility(View.VISIBLE);

            dialogTitle.setText(context.getString(R.string.editDis));
            discName.setText(discountToEdit.getDiscount_name());
            discAmount.setText(discountToEdit.getDiscount_value() + "");

            if (discount.getDiscount_type().equals("%")) {
                toggleAmount.setChecked(true);
            } else {
                toggleAmount.setChecked(false);
            }

            clearBackgroundDrawables();
            switch (discountToEdit.getDiscount_color()) {
                case 0:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
                case 1:
                    color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                    break;
                case 2:
                    color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                    break;
                case 3:
                    color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                    break;
                case 4:
                    color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                    break;
                case 5:
                    color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                    break;
                default:
                    color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                    break;
            }

            itemColor = discountToEdit.getDiscount_color();

        } else {
            discount = new Discount();
            discount.setDiscount_type(Config.DISCOUNT_TYPE_2);
        }

        toggleAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discount.setDiscount_type(Config.DISCOUNT_TYPE_1);
                } else {
                    discount.setDiscount_type(Config.DISCOUNT_TYPE_2);
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discountDialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(context.getString(R.string.sureDeleteDis))
                        .setCancelable(false)
                        .setPositiveButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                manager = new DBManager();
                                manager.deleteDiscount(discountToEdit.getDiscount_id(), context);
                                dialog.dismiss();
                                discountDialog.dismiss();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        //handling button clicks and setting the item color
        color1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color1.setBackgroundResource(R.drawable.ic_col_one_selected);
                itemColor = Config.COLOR_1;
            }
        });
        color2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color2.setBackgroundResource(R.drawable.ic_col_two_selected);
                itemColor = Config.COLOR_2;
            }
        });
        color3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color3.setBackgroundResource(R.drawable.ic_col_three_selected);
                itemColor = Config.COLOR_3;
            }
        });
        color4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color4.setBackgroundResource(R.drawable.ic_col_four_selected);
                itemColor = Config.COLOR_4;
            }
        });
        color5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color5.setBackgroundResource(R.drawable.ic_col_five_selected);
                itemColor = Config.COLOR_5;
            }
        });
        color6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearBackgroundDrawables();
                color6.setBackgroundResource(R.drawable.ic_col_six_selected);
                itemColor = Config.COLOR_6;
            }
        });

        addDisc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!Config.isOnline) {
                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                    return;
                }

                String name = discName.getText().toString().trim();
                String amount = discAmount.getText().toString().trim();

                if (name.length() == 0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.disNameMissing), context.getString(R.string.alert));
                    return;
                }
                manager = new DBManager();
                final Query query = manager.returnDB(context).createAllDocumentsQuery();
                ArrayList<Discount> allDiscounts = manager.loadDiscounts(context, query);
                boolean doesExist = false;

                if (discountToEdit == null) {

                    for (int i = 0; i < allDiscounts.size(); i++) {
                        if (allDiscounts.get(i).getDiscount_name().toLowerCase().equals(name.toLowerCase())) {
                            doesExist = true;
                            Toast.makeText(context, context.getString(R.string.discountExist), Toast.LENGTH_LONG).show();
                            return;

                        } else {
                            doesExist = false;
                        }
                    }
                } else {
                    for (int i = 0; i < allDiscounts.size(); i++) {
                        if (allDiscounts.get(i).getDiscount_name().toLowerCase().equals(name.toLowerCase()) && !discountToEdit.getDiscount_name().equalsIgnoreCase(name)) {
                            doesExist = true;
                            Toast.makeText(context, context.getString(R.string.discountExist), Toast.LENGTH_LONG).show();
                            return;

                        } else {
                            doesExist = false;
                        }
                    }
                }


                if (amount.length() != 0) {

                    if (amount.equals(".")) {
                        showHintDialog(BaseActivity.this, context.getString(R.string.invalidPrice), context.getString(R.string.alert));
                        return;
                    }
                    discount.setDiscount_value(Double.parseDouble(amount));

                    if (discount.getDiscount_type().equals(Config.DISCOUNT_TYPE_1) && Double.parseDouble(amount) > 100.0) {
                        showHintDialog(BaseActivity.this, context.getString(R.string.discountHigher), context.getString(R.string.alert));
                        return;
                    }

                    if (discount.getDiscount_type().equals(Config.DISCOUNT_TYPE_2) && Double.parseDouble(amount) > MAX_AMOUNT) {
                        showHintDialog(BaseActivity.this, context.getString(R.string.exceededDiscount), context.getString(R.string.alert));
                        return;
                    }
                } else {
                    discount.setDiscount_value(0);
                }

                discount.setDiscount_name(name);
                discount.setDiscount_color(itemColor);

                try {
                    //adding a discount
                    if (discountToEdit == null) {
                        doesExist = manager.saveDiscount(context, discount);
                        if (doesExist) {
                            //Toast.makeText(context, Config.DISCOUNT_ALREADY_EXISTS, Toast.LENGTH_LONG).show();
                            //return;
                        } else {
                            Toast.makeText(context, context.getString(R.string.disSaveSuccess), Toast.LENGTH_LONG).show();
                        }
                    } else {
                        //editing a discount
                        doesExist = manager.editDiscount(context, discount);
                        if (doesExist) {
                            // Toast.makeText(context, Config.DISCOUNT_ALREADY_EXISTS, Toast.LENGTH_LONG).show();
                            //return;
                        } else {
                            Toast.makeText(context, context.getString(R.string.disEditedSuccess), Toast.LENGTH_LONG).show();
                        }
                    }
                    closeCatListner = true;
                } catch (CouchbaseLiteException e) {
                    e.printStackTrace();
                }

                if (!doesExist) {
                    discountDialog.dismiss();
                }
            }
        });

        discountDialog.show();
    }

    public void showAddVariableItemPrice(final Item variableItem, final ProductsFragment parentfrag) {

        addVariablePriceDialog = new Dialog(this, R.style.Custom_Dialog);
        addVariablePriceDialog.setContentView(R.layout.add_variable_price_pop_up);
        addVariablePriceDialog.setCancelable(true);
        addVariablePriceDialog.setCanceledOnTouchOutside(false);

        ImageButton btnBack = (ImageButton) addVariablePriceDialog.findViewById(R.id.backButton);
        Button addVariablePrice = (Button) addVariablePriceDialog.findViewById(R.id.btn_add_variable_price);

        final EditText variablePrice = (EditText) addVariablePriceDialog.findViewById(R.id.variablePriceEditText);

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVariablePriceDialog.dismiss();
            }
        });

        addVariablePrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String varPrice = variablePrice.getText().toString().trim();

                if (varPrice.equals("")) {
                    Toast.makeText(context, context.getString(R.string.enterItemPrice), Toast.LENGTH_LONG).show();
                    return;
                }
                if (varPrice.equals(".")) {
                    variablePrice.setText("");
                    return;
                }

                if (Double.parseDouble(varPrice) > MAX_AMOUNT) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.exceededPrice), context.getString(R.string.alert));
                    return;
                }

                Item itemToSend = new Item();
                itemToSend.setItemTotal(varPrice);
                itemToSend.setItem_id(variableItem.getItem_id());
                itemToSend.setItem_name(variableItem.getItem_name() + "-Variable");
                itemToSend.setQuantity(1);

                PriceVariation variation = new PriceVariation();
                variation.setPrice(Double.parseDouble(varPrice));
                variation.setVariation("Variable");
                variation.setIsRegular(true);
                variation.setStk_count(variableItem.getPriceVariations().get(0).getStk_count());
                //variation.setStk_count(variableItem.getPriceVariations().get(0).getStk_count()-1);


                List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
                priceVariations.add(0, variation);
                //isBasePrice = true;

                itemToSend.setPriceSelected(variation);
                itemToSend.setPriceVariations(priceVariations);

                itemToSend.setItemType(Config.ITEM);
                parentfrag.itemToAdd.sendData(itemToSend);

                addVariablePriceDialog.dismiss();

            }


        });

        addVariablePriceDialog.show();
    }

    public void showAddVariableDiscount(final Discount variableDiscount, final ProductsFragment parentfrag) {

        addVariableDiscountDialog = new Dialog(this, R.style.Custom_Dialog);
        addVariableDiscountDialog.setContentView(R.layout.add_variable_discount_pop_up);
        addVariableDiscountDialog.setCancelable(true);
        addVariableDiscountDialog.setCanceledOnTouchOutside(false);

        ImageButton btnBack = (ImageButton) addVariableDiscountDialog.findViewById(R.id.backButton);
        Button addVariableDiscount = (Button) addVariableDiscountDialog.findViewById(R.id.btn_add_variable_discount);
        ToggleButton toggleAmount = (ToggleButton) addVariableDiscountDialog.findViewById(R.id.toggleButton1);

        final EditText variableDis = (EditText) addVariableDiscountDialog.findViewById(R.id.variablePercentageEditText);

        final Discount discountToSend = new Discount();

        discountToSend.setDiscount_type(Config.DISCOUNT_TYPE_2);

        toggleAmount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    discountToSend.setDiscount_type(Config.DISCOUNT_TYPE_1);
                } else {
                    discountToSend.setDiscount_type(Config.DISCOUNT_TYPE_2);
                }

            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addVariableDiscountDialog.dismiss();
            }
        });

        addVariableDiscount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String varDiscount = variableDis.getText().toString().trim();

                if (varDiscount.equals("")) {
                    Toast.makeText(context, context.getString(R.string.enterDiscount), Toast.LENGTH_LONG).show();
                    return;
                }

                if (varDiscount.equals(".")) {
                    variableDis.setText("");
                    return;
                }

                if (Cart.totalBill == 0) {
                    showHintDialog(context, context.getString(R.string.emptyCart), context.getString(R.string.alert));
                    return;
                }

                if (discountToSend.getDiscount_type().equals(Config.DISCOUNT_TYPE_1) && Double.parseDouble(varDiscount) > 100.0) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.discountHigher), context.getString(R.string.alert));
                    return;
                }

                if (discountToSend.getDiscount_type().equals(Config.DISCOUNT_TYPE_2) && Double.parseDouble(varDiscount) > MAX_AMOUNT) {
                    showHintDialog(BaseActivity.this, context.getString(R.string.exceededDiscount), context.getString(R.string.alert));
                    return;
                }

                if (discountToSend.getDiscount_type().equals(Config.DISCOUNT_TYPE_2) && Double.parseDouble(varDiscount) > Cart.totalBill) {
                    showHintDialog(context, context.getString(R.string.discountHigherTotal), context.getString(R.string.alert));
                    return;
                }

                discountToSend.setDiscount_value(Double.parseDouble(varDiscount));

                Item discItem = new Item();
                discItem.setQuantity(1);
                discItem.setItem_name(variableDiscount.getDiscount_name() + "-Discount");
                discItem.setItemTotal("-20");
                discItem.setItemActualTotal(0);
                discItem.setItemType(Config.DISCOUNT);
                //discItem.setBillDiscountType(discountToSend.getDiscount_type());
                //discItem.setBillDiscountValue(discountToSend.getDiscount_value());
                discItem.setItemDiscount(discountToSend);
                parentfrag.itemToAdd.sendData(discItem);

                addVariableDiscountDialog.dismiss();

            }


        });

        addVariableDiscountDialog.show();
    }


    /**
     * shows messages in a dialog
     *
     * @param context
     * @param message
     * @param title
     */
    protected final void showHintDialog(Context context, String message, String title) {
        new AlertDialog.Builder(context).setMessage(message).setTitle(title).setPositiveButton(context.getString(R.string.done), null).create().show();
    }

    private void clearElements() {
        variantList = null;
        adapter = null;
        priceVariations.clear();
        itemName = null;
        itemCat = null;
        itemDesc = null;
        itemPrice = null;
        itemSku = null;
        itemColor = 0;
        itemImage = null;
    }

    //handle item delete : incoming from adapter
    public void removePrice(int position) {
        if (priceVariations.get(position).getVariation().equals("Regular")) {
            //price.setText("");
        }

        priceVariations.get(position).setPrice(0.00);
        priceVariations.get(position).setSku("");
        priceVariations.get(position).setIsRegular(false);
        priceVariations.remove(position);
        adapter.notifyDataSetChanged();
        Toast.makeText(context, context.getString(R.string.pricepointDeletedSuccess), Toast.LENGTH_LONG).show();

    }

    //function to get the tab type
    public int getTab() {
        int tab = 0;
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels = metrics.widthPixels;
        int heightPixels = metrics.heightPixels;

        float scaleFactor = metrics.density;
        float widthDp = widthPixels / scaleFactor;
        float heightDp = heightPixels / scaleFactor;

        float smallestWidth = Math.min(widthDp, heightDp);

        if (smallestWidth > 720) {
            //Device is a 10" tablet
            tab = 10;
        } else if (smallestWidth > 600) {
            //Device is a 7" tablet
            tab = 7;
        }

        return tab;
    }

    public void showIssueReceiptPopUp() {

        final Dialog issuereceiptDialog = new Dialog(this, R.style.Custom_Dialog);
        issuereceiptDialog.setContentView(R.layout.issue_receipt);
        issuereceiptDialog.setCancelable(true);
        issuereceiptDialog.setCanceledOnTouchOutside(false);

        Button back = (Button) issuereceiptDialog.findViewById(R.id.btn_back);

        Button email = (Button) issuereceiptDialog.findViewById(R.id.emailButton);
        Button text = (Button) issuereceiptDialog.findViewById(R.id.textButton);
        Button cancel = (Button) issuereceiptDialog.findViewById(R.id.noReceiptButton);
        final TextView emailText = (TextView) issuereceiptDialog.findViewById(R.id.txt_change);

        emailText.setText(context.getString(R.string.totalOf) + "  Rs " + String.format("%.2f", TransactionViewModel.transactionVal));


        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailDialog = new Dialog(context, R.style.Custom_Dialog);
                emailDialog.setContentView(R.layout.receipt_email);
                emailDialog.setCancelable(true);
                emailDialog.setCanceledOnTouchOutside(false);

                Button sendEmail = (Button) emailDialog.findViewById(R.id.emailSendButton);
                final TextView emailText = (TextView) emailDialog.findViewById(R.id.emailAddress);
                ImageButton backButton = (ImageButton) emailDialog.findViewById(R.id.backButton);

                sendEmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //emailDialog.dismiss();
                        String email = emailText.getText().toString().trim();
                        if (email.equalsIgnoreCase("")) {
                            Toast.makeText(context, context.getString(R.string.emailMissing) + email, Toast.LENGTH_LONG).show();
                        } else {
                            boolean isValid = isValidEmail(email);
                            if (isValid) {
                                if (checkInternet(context)) {
                                    //showProgressDialog(context, context.getResources().getString(R.string.pleaseWait));
                                    PayableService.sendReceipt(context, BaseActivity.this, null, email, TransactionViewModel.transactionId);
                                } else {
                                    showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), context.getString(R.string.forgotPasswordInvalid), Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emailDialog.dismiss();
                    }
                });
                emailDialog.show();
            }
        });
        text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog textDialog = new Dialog(context, R.style.Custom_Dialog);
                textDialog.setContentView(R.layout.receipt_text);
                textDialog.setCancelable(true);
                textDialog.setCanceledOnTouchOutside(false);

                Button sendSms = (Button) textDialog.findViewById(R.id.textSendButton);
                final TextView phoneNo = (TextView) textDialog.findViewById(R.id.phoneNumber);
                ImageButton backButton = (ImageButton) textDialog.findViewById(R.id.backButton);

                sendSms.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //textDialog.dismiss();
                        String phoneNumber = phoneNo.getText().toString().trim();

                        if (phoneNumber == null || phoneNumber.equals("")) {
                            showHintDialog(context, context.getString(R.string.mobileMissing), context.getString(R.string.alert));
                            return;
                        }
                        if (phoneNumber.length() != 10) {
                            showHintDialog(context, context.getString(R.string.invalidMobileNumber), context.getString(R.string.alert));
                            return;
                        }
                        if (!phoneNumber.matches("[0-9]+")) {
                            showHintDialog(context, context.getString(R.string.invalidMobileNumber), context.getString(R.string.alert));
                            return;
                        }

                        if (!phoneNumber.equalsIgnoreCase("")) {
                            if (checkInternet(context)) {
                                //showProgressDialog(context, context.getResources().getString(R.string.pleaseWait));
                                PayableService.sendSms(context, BaseActivity.this, null, phoneNumber, TransactionViewModel.transactionId);
                            } else {
                                showHintDialog(context, context.getString(R.string.no_internet), context.getString(R.string.alert));
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Invalid phone no " + phoneNo, Toast.LENGTH_LONG).show();
                        }
                    }
                });

                backButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        textDialog.dismiss();
                    }
                });
                textDialog.show();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issuereceiptDialog.dismiss();
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                issuereceiptDialog.dismiss();
            }
        });

        issuereceiptDialog.show();
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    public static boolean isValidEmail(final String userEmail) {
        Pattern pattern;
        Matcher matcher;
        // final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = android.util.Patterns.EMAIL_ADDRESS;
        matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }

    /**
     * check whether the internet is available or not
     *
     * @param context
     * @return
     */
    public static boolean checkInternet(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            Config.isOnline = true;
            // Log.v("INTERNET", "Available");
            return true;
        }
        return false;
    }

    public static void didReceiveSendReceiptResults(Context context, int responseCode, JSONObject response) {
        if (responseCode == Config.OK) {
            String error = null;
            try {
                if (response.get("error") != null) {
                    error = response.get("error").toString();
                    if (error.equalsIgnoreCase("no user")) {
                        Toast.makeText(context, context.getString(R.string.emailWrong), Toast.LENGTH_LONG).show();

                    }
                } else {
                    emailDialog.dismiss();
                }
            } catch (Exception e) {
                Toast.makeText(context, "cc", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(context, SignInActivity.class);
                context.startActivity(intent);
            }

        } else {
            Toast.makeText(context, context.getString(R.string.serverError), Toast.LENGTH_LONG).show();
            emailDialog.dismiss();
        }

    }

    public static void didReceiveSendSmsResults(Context context, int responseCode, JSONObject response) {
        if (responseCode == Config.OK) {
            String error = null;
            try {
                if (response.get("error") != null) {
                    error = response.get("error").toString();
                    if (error.equalsIgnoreCase("no user")) {
                        Toast.makeText(context, context.getString(R.string.emailWrong), Toast.LENGTH_LONG).show();

                    }
                } else {
                    Toast.makeText(context, context.getString(R.string.sentSmsSuccess), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(context, "Exception in msg sending response", Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(context, SignInActivity.class);
//                context.startActivity(intent);
            }

        } else {
            Toast.makeText(context, context.getString(R.string.serverError), Toast.LENGTH_LONG).show();
        }

    }

    // limit discount amount to two decimal places
    public class DecimalDigitsInputFilter implements InputFilter {

        Pattern mPattern;

        public DecimalDigitsInputFilter(int digitsBeforeZero, int digitsAfterZero) {
            mPattern = Pattern.compile("[0-9]{0," + (digitsBeforeZero - 1) + "}+((\\.[0-9]{0," + (digitsAfterZero - 1) + "})?)||(\\.)?");
        }

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

            Matcher matcher = mPattern.matcher(dest);
            if (!matcher.matches())
                return "";
            return null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ClearFromRecentService", "Service Destroyed");
    }

}
