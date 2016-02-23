package com.example.ojasjuneja.chem.utilities;

import android.database.Cursor;
import android.os.AsyncTask;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.home.HomeScreenActivity;

/**
 * Created by Ojas Juneja on 8/13/2015.
 */
public class GetLevelStateAsyncTask extends AsyncTask<String,Void,String>{

    private String compoundName;
    private int levelDatabase =  -1,level;
    private int times;
    public GetLevelStateAsyncTask(String compoundName,int level)
    {
        this.compoundName = compoundName;
        this.level = level;
    }

    @Override
    protected String doInBackground(String... listName) {
        GlobalVariables.database.setTableName(listName[0]);
        Cursor cursor = GlobalVariables.database.getDataByPrimaryKey(GlobalVariables.database.getSQLDatabaseInstanse(),compoundName);
      if(cursor.moveToNext()) {
          levelDatabase = cursor.getInt(1);
          times = cursor.getInt(3);
      }
        return listName[0];
    }

    @Override
    public void onPostExecute(String listName)
    {
        if(level == levelDatabase)
        {
            times = times + 1;
        }
        else
        {
            times = 1;
        }
        HomeScreenActivity.putDataInDatabase(listName, compoundName,level,times);
    }

}
