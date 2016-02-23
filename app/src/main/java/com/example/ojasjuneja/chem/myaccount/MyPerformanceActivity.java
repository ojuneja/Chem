package com.example.ojasjuneja.chem.myaccount;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.DividerItemDecoration;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.github.mikephil.charting.data.Entry;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ojas Juneja on 8/13/2015.
 */
public class MyPerformanceActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String listName;
    private TextView textViewPerformance;
    private  MyPerformanceAdaptor myPerformanceAdaptor;
    private RecyclerView recyclerViewPerformance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_performance);
        Bundle args = getIntent().getExtras();
        listName = (String)args.get(TagClass.LIST_NAME);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        textViewPerformance = (TextView)findViewById(R.id.performance_title);
        textViewPerformance.setText(listName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[0] + TagClass.PERFORMANCE);
        recyclerViewPerformance = (RecyclerView)findViewById(R.id.recycler_my_performance);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewPerformance.setLayoutManager(linearLayoutManager);
        recyclerViewPerformance.addItemDecoration(
                new DividerItemDecoration(this, null, false, true));
        recyclerViewPerformance.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewPerformance.setHasFixedSize(true);
        myPerformanceAdaptor = new MyPerformanceAdaptor(LRUCacheClass.getCache());
        recyclerViewPerformance.setAdapter(myPerformanceAdaptor);
        AsyncTaskGetMyListData asyncTaskGetMyListData = new AsyncTaskGetMyListData(myPerformanceAdaptor);
        asyncTaskGetMyListData.execute(new String[]{(String)args.get(TagClass.LIST_NAME)});
    }


    class AsyncTaskGetMyListData extends AsyncTask<String, Void, String> {
        private HashMap<String,Integer> hashMapCompoundsAndDetails;
        private HashMap<String,String> hashMapCompoundsAndDetailsOverall;
        private WeakReference<MyPerformanceAdaptor> myPerformanceAdaptorWeakReference;
        private int skipped = 0,correct = 0,wrong = 0;
        private ArrayList<Entry> arrayListY;
        private ArrayList<String> arrayListX;
        private ArrayList<String> arrayListListNameOverallPerformance;
        private ArrayList<Integer> colors;

        AsyncTaskGetMyListData(MyPerformanceAdaptor myPerformanceAdaptor)
        {
            myPerformanceAdaptorWeakReference = new WeakReference<>(myPerformanceAdaptor);
        }
        @Override
        protected String doInBackground(String... listName) {
            hashMapCompoundsAndDetails = new HashMap<>();
            if(listName[0].equals(TagClass.OVERALL_PERFORMANCE))
            {
                hashMapCompoundsAndDetailsOverall = new HashMap<>();
                arrayListListNameOverallPerformance = new ArrayList<>();
                Object [] object = GlobalVariables.hashMapMyListsName.keySet().toArray();
                for(int i=0;i<GlobalVariables.hashMapMyListsName.size();i++) {
                    GlobalVariables.database.setTableName((String)object[i]);
                    Cursor cursorData = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
                    while (cursorData.moveToNext()) {
                        hashMapCompoundsAndDetails.put(cursorData.getString(0), cursorData.getInt(1));
                        if(cursorData.getInt(3) > 1 && cursorData.getInt(3) == GlobalVariables.LEVEL_WRONG)
                        {
                            hashMapCompoundsAndDetailsOverall.put(cursorData.getString(0),TagClass.LEARNING_CONSC);
                            arrayListListNameOverallPerformance.add((String)object[i]);
                        }
                    }
                }
            }
            else {
                hashMapCompoundsAndDetails = new HashMap<>();
                GlobalVariables.database.setTableName(listName[0]);
                Cursor cursorData = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
                while (cursorData.moveToNext()) {
                    hashMapCompoundsAndDetails.put(cursorData.getString(0), cursorData.getInt(1));
                }
            }
            return listName[0];
        }

        @Override
        public void onPostExecute(String strListName)
        {
            arrayListY = new ArrayList<>();
            arrayListX = new ArrayList<>();
            colors = new ArrayList<>();
            Object [] objectValues = hashMapCompoundsAndDetails.values().toArray();
            for(int i=0;i<objectValues.length;i++)
            {
                if(objectValues[i] == GlobalVariables.LEVEL_CORRECT)
                {
                    correct++;
                }
                else if(objectValues[i] == GlobalVariables.LEVEL_WRONG)
                {
                    wrong++;
                }
                else
                {
                    skipped++;
                }
            }
            if(correct!=0) {
                arrayListX.add(TagClass.CORRECT);
                arrayListY.add(new Entry((float) correct / hashMapCompoundsAndDetails.size(), 0));
                colors.add(Color.parseColor("#8BC34A"));
            }
            if(wrong!=0) {
                arrayListX.add(TagClass.WRONG);
                arrayListY.add(new Entry((float) wrong / hashMapCompoundsAndDetails.size(), 1));
                colors.add(Color.RED);
            }
            if(skipped!=0) {
                arrayListX.add(TagClass.SKIPPED);
                arrayListY.add(new Entry((float) skipped / hashMapCompoundsAndDetails.size(), 2));
                colors.add(Color.DKGRAY);
            }
            MyPerformanceAdaptor myPerformanceAdaptor = myPerformanceAdaptorWeakReference.get();
            if(strListName.equals(TagClass.OVERALL_PERFORMANCE))
            myPerformanceAdaptor.putData(hashMapCompoundsAndDetails,hashMapCompoundsAndDetailsOverall,arrayListX,
                    arrayListY, colors, strListName,TagClass.OVERALL_PERFORMANCE,arrayListListNameOverallPerformance);
            else
             myPerformanceAdaptor.putData(hashMapCompoundsAndDetails, null, arrayListX,
                     arrayListY, colors, strListName, TagClass.LIST_NAME,null);
            myPerformanceAdaptor.notifyDataSetChanged();
        }
    }

}
