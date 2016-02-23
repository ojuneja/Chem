package com.example.ojasjuneja.chem.mylist;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.MyHomeScreenFragment;
import com.example.ojasjuneja.chem.utilities.DividerItemDecoration;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyDialogCreateList;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ojas Juneja on 8/4/2015.
 */
public class CreateListActivity extends AppCompatActivity {

private Toolbar toolbarSearch;
    private static CheckBox checkBoxSelectAll;
    private static Button buttonDone;
    private static Button buttonReset;
    private static Button buttonMoveTop;
    private static String strUserName;
    private static String strListName;
    private static int fragmentInfo;
    private static String compoundType;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_list);
        Bundle args = getIntent().getExtras();
        if (args.getString(TagClass.LIST_NAME) != null) {
            strListName = args.getString(TagClass.LIST_NAME);
            compoundType = args.getString(TagClass.COMPOUND_TYPE);
            AsyncTaskGetData asyncTaskGetData = new AsyncTaskGetData(this);
            asyncTaskGetData.execute(strListName);
            fragmentInfo = GlobalVariables.hashMapMyListsName.get(strListName);
        } else {
            strUserName = args.getString(TagClass.LOGIN_USERNAME);
            fragmentInfo = args.getInt(TagClass.FRAGMENT_INFO);
            compoundType = args.getString(TagClass.COMPOUND_TYPE);
            loadListsInfo(false, null);
        }
    }

    class AsyncTaskGetData extends AsyncTask<String,Void,String>
    {
        ArrayList<String> arrayListCompounds;
        private WeakReference<CreateListActivity> createListActivityWeakReference;
        AsyncTaskGetData(CreateListActivity createListActivity)
        {
            createListActivityWeakReference =  new WeakReference<>(createListActivity);
        }
        @Override
        protected String doInBackground(String... listName) {
            GlobalVariables.database.setTableName(listName[0]);
            arrayListCompounds = new ArrayList<>();
            Cursor cursorData = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());

            while(cursorData.moveToNext())
            {
                arrayListCompounds.add(cursorData.getString(0));
            }
            return listName[0];
        }

        @Override
        public void onPostExecute(String strListName)
        {
            CreateListActivity createListActivity = createListActivityWeakReference.get();
            createListActivity.loadListsInfo(true,arrayListCompounds);
        }
    }

    void loadListsInfo(boolean flag,ArrayList<String> arrayList)
    {
        toolbarSearch = (Toolbar) findViewById(R.id.tool_bar_create_flashcards);
        setSupportActionBar(toolbarSearch);
        buttonReset = (Button) findViewById(R.id.button_create_reset);
        buttonMoveTop = (Button) findViewById(R.id.button_create_move_top);
        checkBoxSelectAll = (CheckBox) findViewById(R.id.checkBox_select_all);
        buttonDone = (Button) findViewById(R.id.button_create_list);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,CreateListFragment.newInstanse(flag,arrayList)).commit();
    }

    public static class CreateListFragment extends Fragment {

        private CreateListAdaptor createListAdaptor;
        private LruCache lruCacheImage;
        private RecyclerView recyclerView;
        private ArrayList<String> arrayListCompounds;
        private Boolean editableFlag = false;
        private HashMap<String,String> hashMapUpdatedCompounds = null;
        public static CreateListFragment newInstanse(Boolean flag,ArrayList<String> arrayListCompounds )
        {
            Bundle args = new Bundle();
            args.putBoolean(TagClass.EDIT_FLAG,flag);
            args.putSerializable(TagClass.COMPOUNDS_PRESENT,arrayListCompounds);
            CreateListFragment createListFragment =  new CreateListFragment();
            createListFragment.setArguments(args);
            return createListFragment;
        }


        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
        }


        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setRetainInstance(true);
            setHasOptionsMenu(true);
            Bundle args = getArguments();
            arrayListCompounds = (ArrayList<String>) args.getSerializable(TagClass.COMPOUNDS_PRESENT);
             editableFlag = args.getBoolean(TagClass.EDIT_FLAG);
            lruCacheImage = LRUCacheClass.getCache();
            putAllFalse();
            CreateListActivity.checkBoxSelectAll.setChecked(false);

        }

        @Override
        public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanse) {
            View rootView = inflater.inflate(R.layout.fragment_create_list,container,false);
            if(editableFlag == true) {
                hashMapUpdatedCompounds = new HashMap();

                if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT && compoundType.equals(TagClass.ORGANIC_COMPOUNDS)) {
                    hashMapUpdatedCompounds.putAll(GlobalVariables.hashMapCompoundsDataOrganic);
                }
                else if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
                {
                    hashMapUpdatedCompounds.putAll(GlobalVariables.hashMapCompoundsData);
                }
                else  if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT && compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
                {
                    hashMapUpdatedCompounds.putAll(GlobalVariables.hashMapCompoundsDataReverseOrganic);
                }
                else if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT)
                {
                    hashMapUpdatedCompounds.putAll(GlobalVariables.hashMapCompoundsDataReverse);
                }
                for(int i=0;i<arrayListCompounds.size();i++)
                {
                    hashMapUpdatedCompounds.remove(arrayListCompounds.get(i));
                }
                arrayListCompounds.clear();
            }
            recyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_create_list);
            recyclerView.setHasFixedSize(true);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(linearLayoutManager);
            recyclerView.addItemDecoration(
                    new DividerItemDecoration(getActivity(), null));
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerView.setHasFixedSize(true);
            createListAdaptor = new CreateListAdaptor(lruCacheImage,fragmentInfo,editableFlag,hashMapUpdatedCompounds,compoundType);
            recyclerView.setAdapter(createListAdaptor);
            checkBoxSelectAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean checkBoxStatus = false;
                    if (CreateListActivity.checkBoxSelectAll.isChecked()) {
                        checkBoxStatus = true;
                    }
                    if (checkBoxStatus) {
                        putAllTrue();
                    } else {
                        putAllFalse();
                    }
                    createListAdaptor.notifyDataSetChanged();
                }
            });
            CreateListActivity.buttonReset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    putAllFalse();
                    CreateListActivity.checkBoxSelectAll.setChecked(false);
                    createListAdaptor.notifyDataSetChanged();
                }
            });

            CreateListActivity.buttonMoveTop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    recyclerView.scrollToPosition(0);
                }
            });
            CreateListActivity.buttonDone.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editableFlag) {
                        StoreDataAsyncTask storeDataAsyncTask = new StoreDataAsyncTask(CreateListAdaptor.hashMapCheckBox, GlobalVariables.hashMapMyListsName.get(strListName), compoundType);
                        storeDataAsyncTask.execute(new String[]{strListName});
                    } else
                        createListDialog();
                }
            });

            return rootView;
        }


        private void putAllTrue()
        {
            for (int i = 0; i < createListAdaptor.getItemCount(); i++) {
                CreateListAdaptor.hashMapCheckBox.put(i, true);
            }
        }

        private static void putAllFalse()
        {
            CreateListAdaptor.hashMapCheckBox.clear();
        }

        void createListDialog()
        {
            Bundle args = new Bundle();
            args.putInt(TagClass.FRAGMENT_INFO,fragmentInfo);
            args.putString(TagClass.LOGIN_USERNAME, strUserName);
            MyDialogCreateList myDialogCreateList = new MyDialogCreateList();
            myDialogCreateList.setArguments(args);
            myDialogCreateList.setTargetFragment(CreateListFragment.this,GlobalVariables.REQUEST_CODE_LIST_NAME);
            myDialogCreateList.show(getFragmentManager(), "");
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            if (resultCode != Activity.RESULT_OK) return;
            if (requestCode == GlobalVariables.REQUEST_CODE_LIST_NAME) {
                Bundle args = data.getExtras();
                if((Boolean)args.get(TagClass.CREATE_LIST)) {
                        MyUtilities.progressDialog(getActivity(), TagClass.WAIT_MESSAGE);
                        StoreDataAsyncTask storeDataAsyncTask = new StoreDataAsyncTask(CreateListAdaptor.hashMapCheckBox, (Integer) args.get(TagClass.FRAGMENT_INFO), compoundType);
                        storeDataAsyncTask.execute(new String[]{(String) args.get(TagClass.LIST_NAME)});
                    }
            }
        }

        @Override
        public void onCreateOptionsMenu(Menu menu,MenuInflater menuInflater) {
            menuInflater.inflate(R.menu.menu_search_create_list, menu);
            // Inflate the menu; this adds items to the action bar if it is present.
            MenuItem menuItem = menu.findItem(R.id.action_search);
            SearchView searchView = null;
            if (menuItem != null)
                searchView = (SearchView) menuItem.getActionView();

            if(searchView != null) {

                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        int position = findCompound(query);
                        if(position>=0)
                        {
                            recyclerView.scrollToPosition(position);
                        }
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        return true;
                    }
                });
            }
            super.onCreateOptionsMenu(menu, menuInflater);
        }

        int findCompound(String query)
        {
            int position = 0;
            query = query.toLowerCase();
            if(GlobalVariables.hashMapCompoundsData.size() > 0)
            {
                if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
                {

                    for(int i=0;i<GlobalVariables.hashMapCompoundsData.size();i++)
                    {
                        if(GlobalVariables.hashMapCompoundsData.keySet().toArray()[i].toString().toLowerCase().contains(query))
                        {
                            position = i;
                            break;
                        }
                    }
                }
                else
                {
                    for(int i=0;i<GlobalVariables.hashMapCompoundsData.size();i++)
                    {
                        if(GlobalVariables.hashMapCompoundsData.values().toArray()[i].toString().toLowerCase().contains(query))
                        {
                            position = i;
                            break;
                        }
                    }
                }
            }
            return position;
        }

        class StoreDataAsyncTask extends AsyncTask<String,Void,String>
        {
            private HashMap hashMapCheckBox;
            private int fragmentInformation;
            private String compoundType;
            StoreDataAsyncTask(HashMap hashMapCheckBox,int fragmentInformation,String compoundType)
            {
                this.hashMapCheckBox = hashMapCheckBox;
                this.fragmentInformation = fragmentInformation;
                this.compoundType = compoundType;
            }

            @Override
            protected String doInBackground(String... listName) {
                Object[] objectCompounds;
                   if (editableFlag) {
                       objectCompounds = hashMapUpdatedCompounds.keySet().toArray();
                   }
                   else if(fragmentInformation == GlobalVariables.LOAD_DEFINITION_FRAGMENT)
                   {
                       if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
                       {
                           objectCompounds = GlobalVariables.hashMapCompoundsDataOrganic.values().toArray();
                       }
                       else
                       objectCompounds = GlobalVariables.hashMapCompoundsData.values().toArray();
                   }
                   else {
                       if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
                       {
                           objectCompounds = GlobalVariables.hashMapCompoundsDataOrganic.keySet().toArray();
                       }
                       else
                        objectCompounds = GlobalVariables.hashMapCompoundsData.keySet().toArray();
                    }

                    for(int i = 0;i < hashMapCheckBox.size();i++)
                    {
                        int position = (Integer)hashMapCheckBox.keySet().toArray()[i];
                        if((Boolean)hashMapCheckBox.get(position))
                        {
                            GlobalVariables.database.insertData(GlobalVariables.database.getSQLDatabaseInstanse(),
                                    objectCompounds[position].toString(),GlobalVariables.LEVEL_SKIP,GlobalVariables.STATE_NOT_SYNC,GlobalVariables.DEFAULT_TIMES);
                        }
                    }
                return listName[0];
            }

            @Override
            public void onPostExecute(String listName)
            {
                GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + listName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[1]);
                GlobalVariables.hashMapMyListsName.put(listName, fragmentInfo);
                GlobalVariables.hashMapMyListsCompoundType.put(listName, compoundType);
                if(!editableFlag) {
                    GlobalVariables.database.insertDataListNames(GlobalVariables.database.getSQLDatabaseInstanse(), listName, GlobalVariables.STATE_NOT_SYNC);
                    MyHomeScreenFragment.callMyListFragment();
                }
                else
                {
                    GlobalVariables.database.updateDataList(GlobalVariables.database.getSQLDatabaseInstanse(), listName, GlobalVariables.STATE_NOT_SYNC);
                    Intent i = new Intent();
                    i.putExtra(TagClass.LIST_NAME,listName);
                    getActivity().setResult(Activity.RESULT_OK, i);
                }
                MyHomeScreenFragment.refreshMyAccount(listName,TagClass.ADD);
                getActivity().finish();
            }

        }

    }
}
