package com.example.ojasjuneja.chem.mylist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.MyHomeScreenFragment;
import com.example.ojasjuneja.chem.utilities.ActionBarCallBack;
import com.example.ojasjuneja.chem.utilities.DividerItemDecoration;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

/**
 * Created by Ojas Juneja on 8/6/2015.
 */
public class MyListDetailsActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private static MyListDetailsAdaptor myListDetailsAdaptor;
    private TextView textViewToolbar;
    private  String strListName;
    private LruCache lruCacheImage;
    private ArrayList<String>arrayListCompounds;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list_details);
        toolbar = (Toolbar)findViewById(R.id.tool_bar_my_list_details);
        textViewToolbar = (TextView)findViewById(R.id.text_my_list_details);
        setSupportActionBar(toolbar);
        Bundle args = getIntent().getExtras();
        strListName = (String)args.get(TagClass.LIST_NAME);
        textViewToolbar.setText( strListName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[0]);
        RecyclerView recyclerViewMyListDetails = (RecyclerView)findViewById(R.id.recycler_my_list_details);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyListDetails.setLayoutManager(linearLayoutManager);
        recyclerViewMyListDetails.addItemDecoration(
                new DividerItemDecoration(this, null, true, true));
        recyclerViewMyListDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewMyListDetails.setHasFixedSize(true);
        lruCacheImage = LRUCacheClass.getCache();
        myListDetailsAdaptor = new MyListDetailsAdaptor(GlobalVariables.hashMapMyListsCompoundType.get(strListName),lruCacheImage);
        recyclerViewMyListDetails.setAdapter(myListDetailsAdaptor);
        myListDetailsAdaptor.onListItemSelected(new MyListDetailsAdaptor.OnListItemSelectedInterface() {
            @Override
            public void onLongItemClick(int position) {
                ActionBarCallBack actionBarCallBack = new ActionBarCallBack(getApplicationContext(),strListName, position, arrayListCompounds);
                startActionMode(actionBarCallBack);
            }
        });
        AsyncTaskGetMyListData asyncTaskGetMyListData = new AsyncTaskGetMyListData(myListDetailsAdaptor);
        asyncTaskGetMyListData.execute(new String[]{strListName});

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_list_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
          int id = item.getItemId();

       if (id == R.id.action_shuffle_set) {
           GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
           SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
           editor.clear();
           editor.putString(TagClass.CURRENT_PLAY_LIST, strListName);
           editor.putBoolean(TagClass.SHUFFLE_FLAG, true);
           editor.apply();
           setMyPlayList();
           MyUtilities.showSnackBar(this, TagClass.SET_SHUFFLE_PLAYLIST_MESSAGE);
        }
        else if(id == R.id.action_set)
        {
            GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
            editor.clear();
            editor.putString(TagClass.CURRENT_PLAY_LIST, strListName);
            editor.apply();
            setMyPlayList();
            MyUtilities.showSnackBar(this, TagClass.SET_PLAYLIST_MESSAGE);
        }
        else if(id == R.id.action_add_more)
        {
            Intent intent = new Intent(this,CreateListActivity.class);
            intent.putExtra(TagClass.LIST_NAME, strListName);
            intent.putExtra(TagClass.COMPOUND_TYPE, GlobalVariables.hashMapMyListsCompoundType.get(strListName));
            startActivityForResult(intent, GlobalVariables.REQUEST_CODE_ITEM_ADDED);

        }
        else if(id == R.id.action_add_more_customized)
        {

        }
        return super.onOptionsItemSelected(item);
    }


    void setMyPlayList()
    {
        MyHomeScreenFragment.addAndCallFlashCardsFragment(arrayListCompounds, GlobalVariables.hashMapMyListsName.get(strListName), GlobalVariables.hashMapMyListsCompoundType.get(strListName));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != Activity.RESULT_OK) return;
        if(requestCode == GlobalVariables.REQUEST_CODE_ITEM_ADDED)
        {
            Bundle args = data.getExtras();
            AsyncTaskGetMyListData asyncTaskGetMyListData = new AsyncTaskGetMyListData(myListDetailsAdaptor);
            asyncTaskGetMyListData.execute(new String[]{(String)args.get(TagClass.LIST_NAME)});
        }
    }

    class AsyncTaskGetMyListData extends AsyncTask<String, Void, String> {
        private ArrayList<String> arrayListCompoundsDataMyListAsync;
        private WeakReference<MyListDetailsAdaptor> myListDetailsAdaptorWeakReference;

        AsyncTaskGetMyListData(MyListDetailsAdaptor myListDetailsAdaptor)
        {
            myListDetailsAdaptorWeakReference = new WeakReference<>(myListDetailsAdaptor);
        }

        @Override
        protected String doInBackground(String... listName) {
            GlobalVariables.database.setTableName(listName[0]);
            Cursor cursorData = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
            if(cursorData.getCount() == 0)
            {
                showMessage(listName[0].split(TagClass.DATABASE_LISTNAME_DELIMITER)[0],TagClass.NO_DATA);
            }
            else {
                arrayListCompoundsDataMyListAsync = new ArrayList<>();
                    while (cursorData.moveToNext()) {
                        arrayListCompoundsDataMyListAsync.add(cursorData.getString(0));
                    }
                }
            return listName[0];
        }

        @Override
        public void onPostExecute(String strListName)
        {
            MyListDetailsAdaptor myListDetailsAdaptor = myListDetailsAdaptorWeakReference.get();
            putData(arrayListCompoundsDataMyListAsync);
            myListDetailsAdaptor.putData(arrayListCompoundsDataMyListAsync, GlobalVariables.hashMapMyListsName.get(strListName));
            myListDetailsAdaptor.notifyDataSetChanged();
            }
    }

    public  static void refreshAdaptor(int position)
    {
        myListDetailsAdaptor.notifyItemRemoved(position);
    }

    void putData(ArrayList<String> arrayListCompounds)
    {
        this.arrayListCompounds = arrayListCompounds;
    }


    public void showMessage(String title,String message)
    {
        AlertDialog.Builder builder =  new AlertDialog.Builder(this);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(message);
        builder.show();
    }

    }
