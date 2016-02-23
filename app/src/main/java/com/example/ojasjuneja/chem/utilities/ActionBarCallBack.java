package com.example.ojasjuneja.chem.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.MyHomeScreenFragment;
import com.example.ojasjuneja.chem.mylist.MyListDetailsActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Ojas Juneja on 8/9/2015.
 */
public class ActionBarCallBack implements ActionMode.Callback {

    private Context context;
    private String strCompoundName;
    private int position;
    private String strListName;
    private ArrayList<String> arrayListCompounds;

    public ActionBarCallBack(Context context, String strName, int position, ArrayList<String> arrayListCompounds) {
        this.position = position;
        this.context = context;
        this.strListName = strName;
        this.arrayListCompounds = arrayListCompounds;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        mode.getMenuInflater().inflate(R.menu.menu_contextual, menu);
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        if (arrayListCompounds == null)
            mode.setTitle(strListName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[0]);
        else
            mode.setTitle("" + (position + 1));
        return false;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_item:
                AsyncTaskDeleteDataFromDatabase asyncTaskDeleteDataFromDatabase = new AsyncTaskDeleteDataFromDatabase();
                Map<String, String> map = (Map<String, String>) GlobalVariables.sharedpreferences.getAll();
                if (arrayListCompounds == null) {
                    if (map.size() != 0) {
                        SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
                        if (strListName.equals(map.get(TagClass.CURRENT_PLAY_LIST))) {
                            editor.clear();
                            editor.apply();
                            ArrayList<String> arrayListCompounds = new ArrayList<>();
                            Object []objectCompoundsData = GlobalVariables.hashMapCompoundsData.keySet().toArray();
                            for (int i = 0; i <= 5; i++) {
                                arrayListCompounds.add((String) objectCompoundsData[i]);
                            }
                            MyHomeScreenFragment.addAndCallFlashCardsFragment(arrayListCompounds,GlobalVariables.LOAD_SYMBOL_FRAGMENT,TagClass.INORGANIC_COMPOUNDS);

                        }
                    }
                    GlobalVariables.hashMapMyListsName.remove(strListName);
                    GlobalVariables.hashMapMyListsCompoundType.remove(strListName);
                    MyHomeScreenFragment.callMyListFragmentItemRemoved(position);
                    MyHomeScreenFragment.refreshMyAccount(strListName,TagClass.DELETE);
                    asyncTaskDeleteDataFromDatabase.execute(new String[]{strListName, TagClass.LIST_NAME});
                    MyUtilities.setDeletedListsName(strListName);

                } else {
                    strCompoundName = arrayListCompounds.get(position);
                    arrayListCompounds.remove(position);
                    MyListDetailsActivity.refreshAdaptor(position);
                    asyncTaskDeleteDataFromDatabase.execute(new String[]{strListName, TagClass.COMPOUND_NAME, strCompoundName});
                    MyUtilities.setDeletedCompoundsName(strCompoundName,strListName);
                    if(arrayListCompounds.size() == 0)
                    {
                        asyncTaskDeleteDataFromDatabase.execute(new String[]{strListName, TagClass.LIST_NAME});
                        MyUtilities.setDeletedListsName(strListName);
                    }
                }
                break;
            default:
                break;
        }
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mode.finish();
    }

    class AsyncTaskDeleteDataFromDatabase extends AsyncTask<String, Void, Void> {
        @Override
        protected Void doInBackground(String... nameAndType) {
            GlobalVariables.database.setTableName(nameAndType[0]);
            if (nameAndType[1].equals(TagClass.LIST_NAME)) {
                String strUserName = nameAndType[0].split(TagClass.DATABASE_LISTNAME_DELIMITER)[1];
                GlobalVariables.database.dropTable(GlobalVariables.database.getSQLDatabaseInstanse());
                GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + strUserName);
                GlobalVariables.database.updateDataList(GlobalVariables.database.getSQLDatabaseInstanse(), nameAndType[0], GlobalVariables.STATE_NOT_SYNC);
                try {
                    ParseQuery<ParseObject> queryPlayListTable = ParseQuery.getQuery(TagClass.CURRENT_PLAYLIST);
                    List<ParseObject> objectUserNames = queryPlayListTable.find();
                    for (ParseObject objectUserName : objectUserNames) {
                        if (objectUserName.get(TagClass.LOGIN_USERNAME).equals(strUserName)) {
                            if(objectUserName.get(TagClass.CURRENT_PLAY_LIST).equals(nameAndType[0]))
                            {
                                objectUserName.deleteInBackground();
                            }

                        }
                    }
                }
                catch(ParseException e)
                {
                    Log.e(TagClass.EXCEPTIONCATCH,"",e);
                }

            } else {
                GlobalVariables.database.deleteByPrimaryKey(GlobalVariables.database.getSQLDatabaseInstanse(), nameAndType[2]);
                GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + nameAndType[0].split(TagClass.DATABASE_LISTNAME_DELIMITER)[1]);
                GlobalVariables.database.updateDataList(GlobalVariables.database.getSQLDatabaseInstanse(), nameAndType[0], GlobalVariables.STATE_NOT_SYNC);
            }
            return null;
        }

    }
}
