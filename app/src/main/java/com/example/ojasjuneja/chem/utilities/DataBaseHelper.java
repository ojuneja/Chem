package com.example.ojasjuneja.chem.utilities;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ojasjuneja.chem.TagClass;

/**
 * Created by Ojas Juneja on 8/3/2015.
 */
public class DataBaseHelper extends SQLiteOpenHelper{

    private String strTableName = "";
    public DataBaseHelper(Context context) {
        super(context, TagClass.DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* db.execSQL("DROP TABLE IF EXISTS" + TagClass.TABLE_NAME_COMPOUNDS);
        onCreate(db);*/
    }

    public void setTableName(String tableName)
    {
        strTableName = tableName;
    }

    String getTableName()
    {
        return strTableName;
    }

    public void dropTable(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + strTableName);
    }


    public void createTable(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table if not exists " + getTableName() + " (" + TagClass.COLOUMN_NAME_COMPOUNDS + " TEXT PRIMARY KEY,"
                 + TagClass.COLOUMN_NAME_LEVEL + " INTEGER," + TagClass.COLOUMN_NAME_STATE + " INTEGER," + TagClass.COLOUMN_NAME_TIMES + " INTEGER)");
    }

    public void createListTable(SQLiteDatabase sqLiteDatabase)
    {
        sqLiteDatabase.execSQL("create table if not exists " + getTableName() + " (" + TagClass.LIST_NAME + " TEXT PRIMARY KEY," + TagClass.COLOUMN_NAME_STATE + " INTEGER,"
         + TagClass.COMPOUND_TYPE + " TEXT," + TagClass.FRAGMENT_INFO + " INTEGER)");
    }


    public boolean insertData(SQLiteDatabase sqLiteDatabase,String compound,int level,int state,int times)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TagClass.COLOUMN_NAME_COMPOUNDS,compound);
        contentValues.put(TagClass.COLOUMN_NAME_LEVEL,level);
        contentValues.put(TagClass.COLOUMN_NAME_STATE, state);
        contentValues.put(TagClass.COLOUMN_NAME_TIMES, times);
        long result = sqLiteDatabase.insert(getTableName(), null, contentValues);
        return result != -1;
    }

    public boolean insertDataListNames(SQLiteDatabase sqLiteDatabase,String listName,int state)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TagClass.LIST_NAME,listName);
        contentValues.put(TagClass.COLOUMN_NAME_STATE, state);
        long result = sqLiteDatabase.insert(getTableName(), null, contentValues);
        return result != -1;
    }

    public boolean updateData(SQLiteDatabase sqLiteDatabase,String compound,int level,int state,int times)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TagClass.COLOUMN_NAME_COMPOUNDS,compound);
        contentValues.put(TagClass.COLOUMN_NAME_LEVEL,level);
        contentValues.put(TagClass.COLOUMN_NAME_STATE, state);
        contentValues.put(TagClass.COLOUMN_NAME_TIMES, times);
        String [] whereArgs = {compound};
        int check = sqLiteDatabase.update(getTableName(), contentValues, TagClass.COLOUMN_NAME_COMPOUNDS + " =?", whereArgs);
        return true;
    }


    public boolean updateDataList(SQLiteDatabase sqLiteDatabase,String listName,int state)
    {
        ContentValues contentValues = new ContentValues();
        contentValues.put(TagClass.LIST_NAME,listName);
        contentValues.put(TagClass.COLOUMN_NAME_STATE, state);
        String [] whereArgs = {listName};
        int check = sqLiteDatabase.update(getTableName(), contentValues,TagClass.LIST_NAME + " =?",whereArgs);
        return true;
    }


    public Cursor getAllData(SQLiteDatabase sqLiteDatabase)
    {
        return sqLiteDatabase.rawQuery("Select * from " + getTableName(), null);
    }


    public SQLiteDatabase getSQLDatabaseInstanse()
    {
        return this.getWritableDatabase();
    }


    public Cursor getDataByPrimaryKey(SQLiteDatabase sqLiteDatabase,String compound)
    {
        return sqLiteDatabase.rawQuery("Select * from " + getTableName() + " where " + TagClass.COLOUMN_NAME_COMPOUNDS + " =?", new String[]{compound});
    }

    public Integer deleteByPrimaryKey(SQLiteDatabase sqLiteDatabase,String compound)
    {
        int count =  sqLiteDatabase.delete(getTableName(),"" + TagClass.COLOUMN_NAME_COMPOUNDS + " = ?", new String[]{"" + compound});
        return count;
    }



}
