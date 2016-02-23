package com.example.ojasjuneja.chem.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.login.SignInActivity;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MyAsyncTaskUpdateList extends AsyncTask<String, Void, String> {
    private String tableName;
    private int state;
    private ParseObject parseObjectListDetails;
    private ArrayList<String> arrayListAdd;
    private String backgroundType;
    private HashMap<String,ParseObject> hashMapListNames, hashMapParseCompounds;
   private Context context;
    public MyAsyncTaskUpdateList(Context context,String backgroundType) {
        this.backgroundType = backgroundType;
        this.context = context;
    }

    @Override
    protected String doInBackground(String... userName) {
        if (backgroundType.equals(TagClass.LOGOUT)) {

            String masterTableName = TagClass.PREFIX_LIST_NAMES + userName[0];
            sendAndUpdateData(userName[0]);
            GlobalVariables.database.setTableName(masterTableName);
            Cursor cursor = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
            while (cursor.moveToNext()) {
                GlobalVariables.database.setTableName(cursor.getString(0));
                GlobalVariables.database.dropTable(GlobalVariables.database.getSQLDatabaseInstanse());
            }
            GlobalVariables.database.setTableName(masterTableName);
            GlobalVariables.database.dropTable(GlobalVariables.database.getSQLDatabaseInstanse());
            GlobalVariables.hashMapMyListsName.clear();
            GlobalVariables.hashMapMyListsCompoundType.clear();
            return TagClass.LOGOUT;
        }
        else if (backgroundType.equals(TagClass.TYPE_BACKGROUND_OPERATION)) {
            sendAndUpdateData(userName[0]);
        }
        return null;
    }



    void checkForDeletedLists()
    {
        try {
            if (MyUtilities.getDeletedListsName().size() != 0) {
                ArrayList<String> arrayListDeletedNames = MyUtilities.getDeletedListsName();
                for (String listNames : arrayListDeletedNames) {
                    ParseQuery<ParseObject> queryListTable = ParseQuery.getQuery(listNames);
                    List<ParseObject> objects = queryListTable.find();
                    if(objects.size()>0)
                    {
                        ParseObject.deleteAll(objects);
                    }
                    if (hashMapListNames.get(listNames) != null) {
                        hashMapListNames.get(listNames).delete();
                        hashMapListNames.remove(listNames);
                        arrayListAdd.remove(listNames);
                    }
                }
            }

        }
        catch(ParseException e)
        {
            Log.e(TagClass.EXCEPTIONCATCH, "", e);
        }
    }


    void checkForDeletedCompounds()
    {
        try {
            if (MyUtilities.getDeletedCompoundsName().size() != 0) {
                HashMap<String, String> hashMapListDetails = MyUtilities.getDeletedCompoundsName();
                Object[] objectListNames = hashMapParseCompounds.keySet().toArray();
                for (int i = 0; i < objectListNames.length; i++) {
                    if (hashMapListDetails.containsKey(objectListNames[i].toString())) {
                        hashMapParseCompounds.get(objectListNames[i].toString()).delete();
                        hashMapParseCompounds.remove(objectListNames[i].toString());
                    }
                }
            }
        }
        catch(ParseException e)
        {
            Log.e(TagClass.EXCEPTIONCATCH,"",e);
        }
    }

    void sendAndUpdateData(String userName) {
        tableName = TagClass.PREFIX_LIST_NAMES + userName;
        GlobalVariables.database.setTableName(tableName);
        Cursor cursor = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
        arrayListAdd = new ArrayList<>();

        while (cursor.moveToNext()) {
            state = cursor.getInt(1);
            if (state == GlobalVariables.STATE_NOT_SYNC) {
                arrayListAdd.add(cursor.getString(0));
            }
        }
        try {
            ParseQuery<ParseObject> queryListTable = ParseQuery.getQuery(tableName);
            List<ParseObject> objects = queryListTable.find();
            if (arrayListAdd.size() > 0) {
                if (objects.size() == 0) {
                    for (String list : arrayListAdd) {
                        addListName(tableName,list);
                        updateData(tableName, list, false,true);
                    }
                } else {
                    hashMapListNames = new HashMap<>();
                    for(ParseObject object:objects)
                    {
                        hashMapListNames.put((String) object.get(TagClass.LIST_NAME), object);
                    }
                    checkForDeletedLists();
                    for (String list : arrayListAdd) {
                        if (hashMapListNames.containsKey(list)) {
                            updateData(tableName, list, true,false);
                        } else {
                            updateData(tableName, list, false, false);
                        }
                    }
                }
            }
        } catch (ParseException e) {
            Log.e(TagClass.EXCEPTIONCATCH, "",e);
        }
    }

    void addListName(String tableName,String listName) {
        try {
            ParseObject parseObjectListName = new ParseObject(tableName);
            parseObjectListName.put(TagClass.LIST_NAME, listName);
                parseObjectListName.put(TagClass.FRAGMENT_INFO, GlobalVariables.hashMapMyListsName.get(listName));
                parseObjectListName.put(TagClass.COMPOUND_TYPE, GlobalVariables.hashMapMyListsCompoundType.get(listName));
            parseObjectListName.save();
        } catch (ParseException e) {
            Log.e(TagClass.EXCEPTIONCATCH, "", e);
        }
    }


    void updateData(String tableName, String listName, boolean editFlag,boolean zeroListFlag) {
        GlobalVariables.database.setTableName(listName);
        Cursor cursor = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
        try {

            if(editFlag)
            {
                hashMapParseCompounds = new HashMap<>();
                ParseQuery<ParseObject> queryListTable = ParseQuery.getQuery(listName);
                List<ParseObject> objectDetails = queryListTable.find();
                for(ParseObject objectCompound:objectDetails)
                {
                    hashMapParseCompounds.put((String)objectCompound.get(TagClass.COLOUMN_NAME_COMPOUNDS),objectCompound);
                }
                checkForDeletedCompounds();
                while(cursor.moveToNext())
                {
                    if (cursor.getInt(2) == GlobalVariables.STATE_NOT_SYNC)
                    {
                        if(hashMapParseCompounds.containsKey(cursor.getString(0)))
                        {
                            parseObjectListDetails = hashMapParseCompounds.get(cursor.getString(0));
                        } else {
                            parseObjectListDetails = new ParseObject(listName);
                        }
                        putDataIntoParse(parseObjectListDetails, cursor);
                    }
                }
            }
            else
            {
                if(!zeroListFlag)
                    addListName(tableName,listName);
                while (cursor.moveToNext()) {
                    parseObjectListDetails = new ParseObject(listName);
                    putDataIntoParse(parseObjectListDetails, cursor);
                }
            }
            GlobalVariables.database.setTableName(tableName);
            GlobalVariables.database.updateDataList(GlobalVariables.database.getSQLDatabaseInstanse(), listName,
                    GlobalVariables.STATE_SYNC);
        } catch (ParseException e) {
            Log.e(TagClass.EXCEPTIONCATCH, "", e);
        }
    }

    void putDataIntoParse(ParseObject parseObjectListDetails,Cursor cursor)
    {

        parseObjectListDetails.put(TagClass.COLOUMN_NAME_COMPOUNDS, cursor.getString(0));
        parseObjectListDetails.put(TagClass.COLOUMN_NAME_LEVEL, cursor.getInt(1));
        parseObjectListDetails.put(TagClass.COLOUMN_NAME_TIMES, cursor.getInt(3));
        parseObjectListDetails.saveInBackground();
        GlobalVariables.database.updateData(GlobalVariables.database.getWritableDatabase(), cursor.getString(0),
                cursor.getInt(1), GlobalVariables.STATE_SYNC, cursor.getInt(3));
    }

    @Override
    public void onPostExecute(String messageType)
    {
        if(messageType.equals(TagClass.LOGOUT))
        {
            Intent intent = new Intent(context,SignInActivity.class);
            context.startActivity(intent);
            MyUtilities.dismiss();
        }
    }

}