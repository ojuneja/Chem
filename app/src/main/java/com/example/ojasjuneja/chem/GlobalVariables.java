package com.example.ojasjuneja.chem;

import android.content.SharedPreferences;

import com.example.ojasjuneja.chem.utilities.DataBaseHelper;

import java.util.HashMap;

/**
 * Created by Ojas Juneja on 7/27/2015.
 */
public class GlobalVariables {

    public static final int ACCOUNTTYPE1 = 1;
    public static final int ACCOUNTTYPE2 = 2;
    public static final int FLASH_CARD_SIZE = 1;
    public static final int FACEBOOK_IMAGE_WIDTH = 80;
    public static final int FACEBOOK_IMAGE_HEIGHT = 80;
    public static SharedPreferences sharedpreferences;
    public static final int LOAD_SYMBOL_FRAGMENT = 0;
    public static final int LOAD_DEFINITION_FRAGMENT = 1;
    public static final int REQUEST_CODE_LIST_NAME = 0;
    public static final int REQUEST_CODE_ITEM_ADDED = 100;
    public static final int REQUEST_CODE_LEVEL = 200;
    public static final int REQUEST_CODE_LIST_TYPE = 300;
    public static HashMap<String,String> hashMapCompoundsData = new HashMap<>();
    public static HashMap<String,String> hashMapCompoundsDataReverse = new HashMap<>();
    public static HashMap<String,String> hashMapCompoundsDataOrganic = new HashMap<>();
    public static HashMap<String,String> hashMapCompoundsDataReverseOrganic = new HashMap<>();
    public static HashMap<String,Integer> hashMapMyListsName = new HashMap<>();
    public static HashMap<String,String> hashMapMyListsCompoundType = new HashMap<>();
    public static final int LEVEL_SKIP = 0;
    public static final int LEVEL_CORRECT = 1;
    public static final int LEVEL_WRONG = 2;
    public static final int STATE_SYNC = 0;
    public static final int STATE_NOT_SYNC = 1;
    public static DataBaseHelper database = null;
    public static final int DEFAULT_TIMES = 0;
    public static final int EXTRA_POSITION = 1;
    public static final int LIMIT = 1000;
    public static final int CUSTOMIZED_FRAGMENT = 1;
    public static final int SELECT_UPPER_IMAGE = 0;
    public static final int SELECT_LOWER_IMAGE = 1;

}
