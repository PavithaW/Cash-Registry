package com.cbasolutions.cbapos.helper;

/**
 * Created by "Don" on 10/18/2017.
 * Class Functionality :-
 * This interface is used to pass data from MainActivity to ProductsFragment
 * Searchview is in the mainactivity and the grid is in the productsfragment
 * So the sequence is : MainActivity search string is passed to filter() in HomeFragment and HomeFragment passes search string to filter() in ProductsFragment
 */

public interface ProductSearch {

    void filter(String string);
}
