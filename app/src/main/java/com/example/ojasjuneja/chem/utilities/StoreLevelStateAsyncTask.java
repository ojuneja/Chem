package com.example.ojasjuneja.chem.utilities;

import android.os.AsyncTask;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.MyHomeScreenFragment;

/**
 * Created by Ojas Juneja on 8/8/2015.
 */
public class StoreLevelStateAsyncTask extends AsyncTask<String,Void,String>
{
    private int level;
    private int times;
    private String compoundName;
    public StoreLevelStateAsyncTask(String compoundName,int level,int times)
    {
        this.level = level;
        this.times = times;
        this.compoundName = compoundName;
    }

    @Override
    protected String doInBackground(String... listName) {
        GlobalVariables.database.setTableName(listName[0]);
        GlobalVariables.database.updateData( GlobalVariables.database.getSQLDatabaseInstanse(),
                compoundName,level,GlobalVariables.STATE_NOT_SYNC,times);
        GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + listName[0].split(TagClass.DATABASE_LISTNAME_DELIMITER)[1]);
        GlobalVariables.database.updateDataList(GlobalVariables.database.getSQLDatabaseInstanse(), listName[0], GlobalVariables.STATE_NOT_SYNC);
        return listName[0];
    }

    @Override
    public void onPostExecute(String listName)
    {
        MyHomeScreenFragment.updateFlashCardsAdaptor(true,level);
    }





}