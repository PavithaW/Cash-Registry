package com.cbasolutions.cbapos.persistance;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.cbasolutions.cbapos.activity.Application;
import com.cbasolutions.cbapos.helper.Config;
import com.cbasolutions.cbapos.model.Category;
import com.cbasolutions.cbapos.model.Discount;
import com.cbasolutions.cbapos.model.Item;
import com.cbasolutions.cbapos.model.Payment;
import com.cbasolutions.cbapos.model.PriceVariation;
import com.cbasolutions.cbapos.model.Refund;
import com.cbasolutions.cbapos.model.Transaction;
import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Document;
import com.couchbase.lite.Query;
import com.couchbase.lite.QueryEnumerator;
import com.couchbase.lite.QueryRow;
import com.couchbase.lite.SavedRevision;
import com.couchbase.lite.UnsavedRevision;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.loopj.android.http.AsyncHttpClient.log;

/**
 * Created by "Don" on 10/31/2017.
 * Class Functionality :-
 */

public class DBManager {

    private Database mDatabase;

    /*
    Initializes the Database, must be called on top of every function.
    If not called, mDatabase is going to be null
     */
    private void initDB(Context context) {
        Application application = (Application) context.getApplicationContext();
        mDatabase = application.getDatabase();
    }

    public Database returnDB(Context context) {
        Application application = (Application) context.getApplicationContext();
        mDatabase = application.getDatabase();

        return mDatabase;
    }

    /*
    Saving an item to the database
     */
    public boolean saveItem(Context context, Item item) throws CouchbaseLiteException {
        initDB(context);
        boolean doesExist = false;

        Query query = mDatabase.createAllDocumentsQuery();
        QueryEnumerator result = query.run();

        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            String type = document.getProperty("type").toString();

            if (type != null && type.equals("item") && document.getProperty("item_name").toString().toLowerCase().equals(item.getItem_name().toLowerCase())) {
                doesExist = true;
            }
        }

        if (!doesExist) {
            //create a unique ID for item
            SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
            String owner = loginPreferences.getString("owner", null);
            String storeId = loginPreferences.getString("storeId", null);

            //String itemId = userName + "." + UUID.randomUUID();
            String itemId = Config.ITEM + "." + item.getItem_name()+ "." +storeId;
            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("type", "item");
            properties.put("item_id", itemId);
            properties.put("item_name", item.getItem_name());
            properties.put("category", item.getCategory());
            properties.put("color", item.getColor());
            properties.put("description", item.getDescription());
            properties.put("price_variations", item.getPriceVariations());
            //properties.put("sku", item.getSku());

            if (item.getImage() != null) {
                properties.put("image", item.getImage());
            }

            properties.put("owner", owner);
            properties.put("store_name", storeId);

            //String docId = userName + "." + UUID.randomUUID();
            Document document = mDatabase.getDocument(itemId);
            document.putProperties(properties);
        }

        return doesExist;
    }


    /*
   Returns all Items in the database
    */
    public ArrayList<Item> loadItems(Context context, Query newQuery) {
        initDB(context);
        ArrayList<Item> allItems = new ArrayList<>();

        try {
            newQuery.setDescending(false);
            newQuery.setPrefixMatchLevel(1);
            QueryEnumerator result = newQuery.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("item")) {
                    Item item = new Item();
                    item.setItem_name(document.getProperty("item_name").toString());
                    item.setItem_id(document.getProperty("item_id").toString());

                    //construct the selected category
                    Gson gson = new Gson();

                    if (document.getProperty("category") != null) {
                        try {
                            if (document.getProperty("category") instanceof Category) {
                                Category category = (Category) document.getProperty("category");
                                item.setCategory(category);
                            } else {
                                JSONObject catObj = new JSONObject(document.getProperty("category").toString());
                                item.setCategory(gson.fromJson(catObj.toString(), Category.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (document.getProperty("description") != null) {
                        item.setDescription(document.getProperty("description").toString());
                    }

//                    if (document.getProperty("sku") != null) {
//                        item.setSku(document.getProperty("sku").toString());
//                    }

                    //construct hashmap to actual list
                    ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
                    JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();


                    List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
                    for (int i = 0; i < jArray.size(); i++) {
                        PriceVariation variant = gson.fromJson(jArray.get(i), PriceVariation.class);
                        priceVariations.add(variant);

//                        if(variant.getIsRegular()){
//                            item.setSku(variant.getSku());
//                        }
                    }

                    item.setPriceVariations(priceVariations);
                    item.setColor((Integer) document.getProperty("color"));

                    if (document.getProperty("image") != null) {
                        item.setImage(document.getProperty("image").toString());
                    }
                    allItems.add(item);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allItems;
    }


    /*
Returns all Item Ids in the database
*/
    public ArrayList<String> loadItemsId(Context context, Query newQuery) {
        initDB(context);
        final ArrayList<String> allItemsId = new ArrayList<>();
        final ArrayList<JSONObject> allItemsIdObj = new ArrayList<>();
        newQuery.setDescending(true);
        newQuery.setPrefixMatchLevel(1);

        try {

            QueryEnumerator result = newQuery.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("item")) {
                    String documentId = document.getId();
                    String documentName = document.getProperty("item_name").toString();

                    JSONObject obj = new JSONObject();
                    obj.put("itemId",documentId);
                    obj.put("itemName",documentName);

                    allItemsIdObj.add(obj);
                }

                Collections.sort(allItemsIdObj, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        String name1 = null;
                        String name2 = null;
                        try{
                            name1 = o1.getString("itemName");
                            name2 = o2.getString("itemName");

                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        return name1.toLowerCase().compareTo(name2.toLowerCase());
                    }

                });

            }

            for(int i=0; i< allItemsIdObj.size(); i++){
                String id = allItemsIdObj.get(i).getString("itemId");
                allItemsId.add(id);
            }

        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allItemsId;
    }


    public ArrayList<Item> loadItems(Context context) {
        initDB(context);
        ArrayList<Item> allItems = new ArrayList<>();

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            query.setDescending(true);
            query.setPrefixMatchLevel(1);
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("item")) {
                    Item item = new Item();
                    item.setItem_name(document.getProperty("item_name").toString());
                    item.setItem_id(document.getProperty("item_id").toString());

                    //construct the selected category
                    Gson gson = new Gson();

                    if (document.getProperty("category") != null) {
                        try {
                            if (document.getProperty("category") instanceof Category) {
                                Category category = (Category) document.getProperty("category");
                                item.setCategory(category);
                            } else {
                                JSONObject catObj = new JSONObject(document.getProperty("category").toString());
                                item.setCategory(gson.fromJson(catObj.toString(), Category.class));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if (document.getProperty("description") != null) {
                        item.setDescription(document.getProperty("description").toString());
                    }

                    //construct hashmap to actual list
                    ArrayList variantMap = (ArrayList) document.getProperty("price_variations");
                    JsonArray jArray = gson.toJsonTree(variantMap).getAsJsonArray();


                    List<PriceVariation> priceVariations = new ArrayList<PriceVariation>();
                    for (int i = 0; i < jArray.size(); i++) {
                        PriceVariation variant = gson.fromJson(jArray.get(i), PriceVariation.class);
                        priceVariations.add(variant);
                    }

                    item.setPriceVariations(priceVariations);
                    item.setColor((Integer) document.getProperty("color"));

                    if (document.getProperty("image") != null) {
                        item.setImage(document.getProperty("image").toString());
                    }
                    allItems.add(item);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allItems;
    }


    //updates an item
    public boolean editItem(final Item item, final Context context) {
        initDB(context);
        boolean doesExist = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                final Document document = row.getDocument();
                String id = null;
                String type = document.getProperty("type").toString();

                if (document.getProperty("item_id") != null) {
                    id = document.getProperty("item_id").toString();
                }

                if (type != null && type.equals("item") &&
                        item.getItem_id() != id &&
                        document.getProperty("item_name").toString().toLowerCase().equals(item.getItem_name().toLowerCase())) {
                    doesExist = true;
                } else {
                    if (type != null && type.equals("item") && item.getItem_id().equals(id)) {
                        try {
                            document.update(new Document.DocumentUpdater() {
                                @Override
                                public boolean update(UnsavedRevision newRevision) {
                                    Map<String, Object> properties = newRevision.getUserProperties();

                                    properties.put("item_name", item.getItem_name());
                                    properties.put("category", item.getCategory());
                                    properties.put("color", item.getColor());
                                    properties.put("description", item.getDescription());

                                    properties.put("price_variations", item.getPriceVariations());

                                    properties.put("image", item.getImage());

                                    SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
                                    String owner = loginPreferences.getString("owner", null);
                                    String storeId = loginPreferences.getString("storeId", null);
                                    properties.put("owner", owner);
                                    properties.put("store_name", storeId);
                                    newRevision.setUserProperties(properties);

                                    return true;
                                }
                            });
                        } catch (CouchbaseLiteException e) {
                            Log.d("POSApplication", "Update Er " + e.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            doesExist = true;
        }

        return doesExist;
    }

    public boolean deleteItem(String itemId, Context context) {
        initDB(context);
        boolean isSuccess = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("item_id") != null) {
                    id = document.getProperty("item_id").toString();
                }

                if (type != null && type.equals("item") && itemId == id) {
                    try {
                        document.delete();
                        isSuccess = true;
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "deleteError " + e.toString());
                        isSuccess = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            isSuccess = false;
        }

        return isSuccess;
    }


    /*
   Saving an item to the database
    */
    public boolean saveCategory(Context context, Category category) throws CouchbaseLiteException {
        initDB(context);
        //create a unique ID for item
        Query query = mDatabase.createAllDocumentsQuery();
        QueryEnumerator result = query.run();
        boolean doesExist = false;

        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            String type = document.getProperty("type").toString();

            if (type != null && type.equals("category") && document.getProperty("category_name").toString().toLowerCase().equals(category.getCategory_name().toLowerCase())) {
                doesExist = true;
            }
        }

        if (!doesExist) {

            SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
            String owner = loginPreferences.getString("owner", null);
            String storeId = loginPreferences.getString("storeId", null);
//            String catId = userName+"." + UUID.randomUUID();

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("type", "category");
            properties.put("category_id", category.getCategory_id());
            properties.put("category_name", category.getCategory_name());
            properties.put("color", category.getCatColor());

            properties.put("owner", owner);
            properties.put("store_name", storeId);

            //String docId = userName + "." + UUID.randomUUID();
            //String docId = category.getCategory_id();
            Document document = mDatabase.getDocument(category.getCategory_id());
            document.putProperties(properties);
        }
        return doesExist;
    }


    public ArrayList<Category> loadCategories(Context context, Query newQuery) {
        initDB(context);
        ArrayList<Category> allCategories = new ArrayList<>();

        try {
            //Query query = mDatabase.createAllDocumentsQuery();
            //Query query = mDatabase.createAllDocumentsQuery().toLiveQuery();
            QueryEnumerator result = newQuery.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                log.d("AAA LOAD CATS", "");
                if (type != null && type.equals("category")) {
                    Category category = new Category();
                    category.setCategory_name(document.getProperty("category_name").toString());

                    if (document.getProperty("color") != null) {
                        category.setCatColor((Integer) document.getProperty("color"));
                    }

                    if (document.getProperty("category_id") != null) {
                        category.setCategory_id(document.getProperty("category_id").toString());
                    }

                    allCategories.add(category);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allCategories;
    }


    public ArrayList<Category> loadCategories(Context context) {
        initDB(context);
        ArrayList<Category> allCategories = new ArrayList<>();

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("category")) {
                    Category category = new Category();
                    category.setCategory_name(document.getProperty("category_name").toString());

                    if (document.getProperty("color") != null) {
                        category.setCatColor((Integer) document.getProperty("color"));
                    }

                    if (document.getProperty("category_id") != null) {
                        category.setCategory_id(document.getProperty("category_id").toString());
                    }
                    allCategories.add(category);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allCategories;
    }

    public ArrayList<String> loadCategoriesId(Context context, Query newQuery) {

        initDB(context);
        ArrayList<String> allCategoryId = new ArrayList<>();
        ArrayList<JSONObject> allCategoryIdObj = new ArrayList<>();

        try {

            QueryEnumerator result = newQuery.run();
            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("category")) {
                    String documentId = document.getId();
                    String documentName = document.getProperty("category_name").toString();

                    JSONObject obj = new JSONObject();
                    obj.put("catId",documentId);
                    obj.put("catName",documentName);

                    allCategoryIdObj.add(obj);

                    //allCategoryId.add(documentId);
                }

                Collections.sort(allCategoryIdObj, new Comparator<JSONObject>() {
                    @Override
                    public int compare(JSONObject o1, JSONObject o2) {
                        String name1 = null;
                        String name2 = null;
                        try{
                            name1 = o1.getString("catName");
                            name2 = o2.getString("catName");

                        }catch(JSONException ex){
                            ex.printStackTrace();
                        }
                        return name1.toLowerCase().compareTo(name2.toLowerCase());
                    }

                });

            }

            for(int i=0; i< allCategoryIdObj.size(); i++){
                String id = allCategoryIdObj.get(i).getString("catId");
                allCategoryId.add(id);
            }

        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allCategoryId;
    }


    /*
 Returns all Items in the database
  */
    public int getItemCountByCategoryName(Context context, Query newQuery, String catName) {
        initDB(context);
        int catCount = 0;

        try {
            //Query query = mDatabase.createAllDocumentsQuery();
            //Query query = mDatabase.createAllDocumentsQuery().toLiveQuery();
            QueryEnumerator result = newQuery.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("item")) {

                    //construct the selected category
                    Gson gson = new Gson();

                    if (document.getProperty("category") != null) {
                        try {
                            Category category;
                            String s = document.getProperty("category").toString();
                            if (document.getProperty("category") instanceof Category) {
                                category = (Category) document.getProperty("category");
                            } else {
                                JSONObject catObj = new JSONObject(document.getProperty("category").toString());
                                category = gson.fromJson(catObj.toString(), Category.class);
                            }
                            if (category.getCategory_name().equals(catName)) {
                                catCount++;
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return catCount;
    }


    //edits a category
    public boolean editCategory(final Category category, Context context) {
        initDB(context);
        boolean doesExist = false;
        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;

                if (type != null && type.equals("category") &&
                        category.getCategory_id() != document.getProperty("category_id") &&
                        document.getProperty("category_name").toString().toLowerCase().equals(category.getCategory_name().toLowerCase())) {
                    doesExist = true;
                } else {
                    if (document.getProperty("category_id") != null) {
                        id = document.getProperty("category_id").toString();
                    }

                    if (type != null && id != null && type.equals("category") && category.getCategory_id().equals(id)) {
                        try {
                            document.update(new Document.DocumentUpdater() {
                                @Override
                                public boolean update(UnsavedRevision newRevision) {
                                    Map<String, Object> properties = newRevision.getUserProperties();

                                    properties.put("category_name", category.getCategory_name());
                                    properties.put("color", category.getCatColor());
                                    newRevision.setUserProperties(properties);

                                    return true;
                                }
                            });
                        } catch (CouchbaseLiteException e) {
                            Log.d("POSApplication", "Update Er " + e.toString());
                        }
                    }
                }

            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            doesExist = true;
        }

        return doesExist;
    }

    public boolean deleteCategory(String catId, Context context) {
        initDB(context);
        boolean isSuccess = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("category_id") != null) {
                    id = document.getProperty("category_id").toString();
                }

                if (type != null && id != null && type.equals("category") && catId == id) {
                    try {
                        document.delete();
                        isSuccess = true;
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "deleteError " + e.toString());
                        isSuccess = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            isSuccess = false;
        }

        return isSuccess;
    }


    public boolean editItemCategory(final Category category, Context context) {
        initDB(context);
        final boolean[] isSuccess = {false};
        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;

                //construct the selected category
                Gson gson = new Gson();
                LinkedHashMap selectedCat = (LinkedHashMap) document.getProperty("category");
                JSONObject catObj;
                try {
                    catObj = new JSONObject(selectedCat + "");
                    Category categoryLoaded = gson.fromJson(catObj.toString(), Category.class);
                    id = categoryLoaded.getCategory_id();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (type != null && id != null && type.equals("item") && category.getCategory_id().equals(id)) {
                    try {
                        document.update(new Document.DocumentUpdater() {
                            @Override
                            public boolean update(UnsavedRevision newRevision) {
                                Map<String, Object> properties = newRevision.getUserProperties();

                                properties.put("category", category);
                                newRevision.setUserProperties(properties);
                                isSuccess[0] = true;

                                return true;
                            }
                        });
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "Update Er " + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            isSuccess[0] = false;
        }

        return isSuccess[0];
    }

    /*
    Saving a discount to the database
    */
    public boolean saveDiscount(Context context, Discount discount) throws CouchbaseLiteException {
        initDB(context);

        Query query = mDatabase.createAllDocumentsQuery();
        QueryEnumerator result = query.run();
        boolean doesExist = false;

        for (Iterator<QueryRow> it = result; it.hasNext(); ) {
            QueryRow row = it.next();
            Document document = row.getDocument();
            String type = document.getProperty("type").toString();

            if (type != null && type.equals("discount") && document.getProperty("discount_name").toString().toLowerCase().equals(discount.getDiscount_name().toLowerCase())) {
                doesExist = true;
            }
        }

        if (!doesExist) {
            SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
            String owner = loginPreferences.getString("owner", null);
            String storeId = loginPreferences.getString("storeId", null);
            //create a unique ID for item
            //String discId = userName + "." + UUID.randomUUID();
            String discId = Config.DISCOUNT + "." + discount.getDiscount_name()+ "." +storeId;

            Map<String, Object> properties = new HashMap<String, Object>();
            properties.put("type", "discount");
            properties.put("discount_id", discId);
            properties.put("discount_name", discount.getDiscount_name());
            properties.put("discount_type", discount.getDiscount_type());
            properties.put("discount_value", discount.getDiscount_value());
            properties.put("color", discount.getDiscount_color());

            properties.put("owner", owner);
            properties.put("store_name", storeId);

            Document document = mDatabase.getDocument(discId);
            document.putProperties(properties);
        }
        return doesExist;
    }

    public ArrayList<Discount> loadDiscounts(Context context, Query newQuery) {
        initDB(context);
        ArrayList<Discount> allDiscounts = new ArrayList<>();

        try {
            QueryEnumerator result = newQuery.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                log.d("AAA ", "LOAD DIS");

                if (type != null && type.equals("discount")) {
                    Discount discount = new Discount();
                    discount.setDiscount_id(document.getProperty("discount_id").toString());
                    discount.setDiscount_name(document.getProperty("discount_name").toString());
                    discount.setDiscount_type(document.getProperty("discount_type").toString());
                    discount.setDiscount_value(Double.parseDouble(document.getProperty("discount_value").toString()));
                    if (document.getProperty("color") != null) {
                        discount.setDiscount_color((Integer) document.getProperty("color"));
                    }
                    allDiscounts.add(discount);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allDiscounts;
    }

    public boolean deleteDiscount(String discountId, Context context) {
        initDB(context);
        boolean isSuccess = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("discount_id") != null) {
                    id = document.getProperty("discount_id").toString();
                }

                if (type != null && id != null && type.equals("discount") && discountId == id) {
                    try {
                        document.delete();
                        isSuccess = true;
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "deleteError " + e.toString());
                        isSuccess = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            isSuccess = false;
        }

        return isSuccess;
    }

    public boolean editDiscount(Context context, final Discount discount) {
        initDB(context);
        boolean doesExist = false;
        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                final Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("discount_id") != null) {
                    id = document.getProperty("discount_id").toString();
                }

                if (type != null && type.equals("discount") &&
                        discount.getDiscount_id() != id &&
                        document.getProperty("discount_name").toString().toLowerCase().equals(discount.getDiscount_name().toLowerCase())) {
                    doesExist = true;
                } else {
                    if (type != null && id != null && type.equals("discount") && discount.getDiscount_id().equals(id)) {
                        try {
                            document.update(new Document.DocumentUpdater() {
                                @Override
                                public boolean update(UnsavedRevision newRevision) {
                                    Map<String, Object> properties = newRevision.getUserProperties();

                                    properties.put("discount_name", discount.getDiscount_name());
                                    properties.put("discount_type", discount.getDiscount_type());
                                    properties.put("discount_value", discount.getDiscount_value());
                                    properties.put("color", discount.getDiscount_color());
                                    newRevision.setUserProperties(properties);


                                    return true;
                                }
                            });
                        } catch (CouchbaseLiteException e) {
                            Log.d("POSApplication", "Update Er " + e.toString());
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return doesExist;
    }

    public static String invoiceNo = null;

    public SavedRevision saveTransaction(Context context, Transaction transaction) throws CouchbaseLiteException {
        initDB(context);
        invoiceNo = transaction.getInvoiceNo();
        //create a unique ID for item

//        String deviceID = Config.getDeviceID(context);

//        byte[] bytes = null;
//
//        try {
//            bytes = deviceID.getBytes("US-ASCII");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }

//        invoiceNo = null;
//        int invoice = 0;
//
//        if (bytes != null) {
//
//            for (byte ch : bytes) {
//                invoice += ch;
//            }
//
//            invoiceNo = Config.randomString(6) + invoice;
//        }

        SharedPreferences loginPreferences = context.getSharedPreferences("loginPrefs", 0);
        String owner = loginPreferences.getString("owner", null);
        String storeId = loginPreferences.getString("storeId", null);

        //String tId =  UUID.randomUUID().toString();

        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put("type", "transaction");
        properties.put("transaction_id", transaction.gettId());
        properties.put("tInvoiceId", transaction.getInvoiceNo());
        properties.put("isIncomplete",transaction.isIncomplete());
        properties.put("tDateTime", transaction.gettDateTime());
        properties.put("tTotal", transaction.gettValue());
        properties.put("items", transaction.gettItems());
        properties.put("selectedDiscount", transaction.gettDiscounts());
        properties.put("tRefund", transaction.getTRefund());
        properties.put("tPayments", transaction.getTransactionList());
        properties.put("tActualTotal", transaction.getInitialAmount());

        properties.put("owner", owner);
        properties.put("store_name", storeId);


        Document document = mDatabase.getDocument(transaction.gettId());
        return document.putProperties(properties);
    }

    public ArrayList<Transaction> loadTransactions(Context context) {
        initDB(context);
        ArrayList<Transaction> allTransactions = new ArrayList<>();

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();

                if (type != null && type.equals("transaction")) {
                    Transaction transaction = new Transaction();
                    transaction.settId(document.getProperty("transaction_id").toString());
                    transaction.setInvoiceNo(document.getProperty("tInvoiceId").toString());

                    //Number date = (Number) document.getProperty("tDateTime");

                    //transaction.settDateTime(new java.sql.Date(((Number) document.getProperty("tDateTime")).longValue()));
                    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                    String d = df.format(document.getProperty("tDateTime"));
                    java.util.Date date = null;

                    try {
                        date = df.parse(d);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    transaction.settDateTime(date);

                    transaction.settValue(((Number) document.getProperty("tTotal")).doubleValue());
                    transaction.setIncomplete((Boolean) document.getProperty("isIncomplete"));

                    Gson gson = new Gson();

                    ArrayList itemMap = (ArrayList) document.getProperty("items");
                    JsonArray jArray = gson.toJsonTree(itemMap).getAsJsonArray();

                    List<Item> itemList = new ArrayList<>();
                    for (int i = 0; i < jArray.size(); i++) {
                        Item item = gson.fromJson(jArray.get(i), Item.class);
                        itemList.add(item);
                    }

                    transaction.settItems(itemList);

                    ArrayList discountMap = (ArrayList) document.getProperty("selectedDiscount");
                    jArray = gson.toJsonTree(discountMap).getAsJsonArray();

                    List<Discount> discountList = new ArrayList<>();
                    for (int i = 0; i < jArray.size(); i++) {
                        Discount discount = gson.fromJson(jArray.get(i), Discount.class);
                        discountList.add(discount);
                    }

                    transaction.settDiscounts(discountList);

                    ArrayList refundMap = (ArrayList) document.getProperty("tRefund");
                    JsonArray rArray = gson.toJsonTree(refundMap).getAsJsonArray();

                    List<Refund> refundList = new ArrayList<>();
                    for (int i = 0; i < rArray.size(); i++) {
                        Refund refund = gson.fromJson(rArray.get(i), Refund.class);
                        refundList.add(refund);
                    }

                    transaction.setTRefund(refundList);

                    //transaction.settRefundAmount(((Number) document.getProperty("tRefund")).doubleValue());
                    //transaction.settReasonForRefund(document.getProperty("tReasonForRefund").toString());

                    ArrayList transactionMap = (ArrayList) document.getProperty("tPayments");
                    JsonArray trArray = gson.toJsonTree(transactionMap).getAsJsonArray();

                    List<Payment> transactionList = new ArrayList<>();
                    for (int i = 0; i < trArray.size(); i++) {
                        Payment payment = gson.fromJson(trArray.get(i), Payment.class);
                        //payment.setPaymentId(document.getProperty("t_id").toString());
                        transactionList.add(payment);
                    }


                    transaction.setTransactionList(transactionList);

                    allTransactions.add(transaction);
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return allTransactions;
    }

    public boolean editTransaction(final Transaction transaction, Context context) {
        initDB(context);
        boolean isSuccess = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                final Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("transaction_id") != null) {
                    id = document.getProperty("transaction_id").toString();
                }

                if (type != null && id != null && type.equals("transaction") && transaction.gettId().equals(id)) {
                    try {
                        document.update(new Document.DocumentUpdater() {
                            @Override
                            public boolean update(UnsavedRevision newRevision) {
                                Map<String, Object> properties = newRevision.getUserProperties();
                                properties.put("type", "transaction");
                                properties.put("transaction_id", transaction.gettId());
                                properties.put("tRefund", transaction.getTRefund());
                                properties.put("tDateTime", transaction.gettDateTime());
                                properties.put("tPayments", transaction.getTransactionList());
                                properties.put("isIncomplete",transaction.isIncomplete());
                                properties.put("tInvoiceId", transaction.getInvoiceNo());
                                properties.put("tDateTime", transaction.gettDateTime());
                                properties.put("tTotal", transaction.gettValue());
                                properties.put("items", transaction.gettItems());
                                properties.put("selectedDiscount", transaction.gettDiscounts());
                                properties.put("tRefund", transaction.getTRefund());
                                properties.put("tActualTotal", transaction.getInitialAmount());


                                newRevision.setUserProperties(properties);
                                return true;
                            }
                        });
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "Update Er " + e.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
        }

        return isSuccess;
    }

    public boolean deleteTransaction(String transactionId, Context context) {
        initDB(context);
        boolean isSuccess = false;

        try {
            Query query = mDatabase.createAllDocumentsQuery();
            QueryEnumerator result = query.run();

            for (Iterator<QueryRow> it = result; it.hasNext(); ) {
                QueryRow row = it.next();
                Document document = row.getDocument();
                String type = document.getProperty("type").toString();
                String id = null;
                if (document.getProperty("transaction_id") != null) {
                    id = document.getProperty("transaction_id").toString();
                }
                if (type != null && type.equals("transaction") && transactionId.equals(id)) {
                    try {
                        document.delete();
                        isSuccess = true;
                    } catch (CouchbaseLiteException e) {
                        Log.d("POSApplication", "deleteError " + e.toString());
                        isSuccess = false;
                    }
                }
            }
        } catch (Exception e) {
            Log.d("POSApplication", "Search Er " + e.toString());
            isSuccess = false;
        }

        return isSuccess;
    }


}
