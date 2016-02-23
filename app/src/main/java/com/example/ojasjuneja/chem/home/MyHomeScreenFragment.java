package com.example.ojasjuneja.chem.home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.flashcards.FlashCardsAdaptor;
import com.example.ojasjuneja.chem.flashcards.MakeFlashCardsActivity;
import com.example.ojasjuneja.chem.myaccount.MyAccountData;
import com.example.ojasjuneja.chem.myaccount.MyAccountRecyclerViewAdaptor;
import com.example.ojasjuneja.chem.mylist.CreateListActivity;
import com.example.ojasjuneja.chem.mylist.MyListAdaptor;
import com.example.ojasjuneja.chem.utilities.ActionBarCallBack;
import com.example.ojasjuneja.chem.utilities.DividerItemDecoration;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyDialogCreateList;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.getbase.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ojas Juneja on 7/27/2015.
 */
public class MyHomeScreenFragment extends Fragment {

    private RecyclerView recyclerViewFlashCards;
    private   View rootView = null;
    private static int tabPosition = 0;
    private RecyclerView recyclerViewMyAccount;
    private String strListName;
    private String strFullName;
    private String strUserName;
    private String strLoginType;
    private Bitmap profilePic;
    private String facebookImageUrl;
    private static MyAccountData myAccountData;
    private FloatingActionButton floatingActionButtonCreateListSymbol;
    private FloatingActionButton floatingActionButtonCreateListDefinition;
    private FloatingActionButton floatingActionButtonCreateListMake;
    private static FlashCardsAdaptor flashCardsAdaptor;
    OnListItemSelectedListener mListener;
    LruCache lruCache;
    private Bundle bundle;
    private static int index = 0;
    private static MyAccountRecyclerViewAdaptor recyclerViewAdaptor;
    private static MyListAdaptor myListAdaptor;
    private   MyDialogSelectOption myDialogCreateList;

    public static MyHomeScreenFragment newInstanse(int position,Bundle bundle)
    {
       Bundle args = new Bundle();
       args.putBundle(TagClass.ACCOUNTDETAILS, bundle);
        args.putInt(TagClass.TABPOSITION, position);
        MyHomeScreenFragment myHomeScreenFragment =  new MyHomeScreenFragment();
        myHomeScreenFragment.setArguments(args);
        return myHomeScreenFragment;
    }


    public interface OnListItemSelectedListener
    {
       void onPopUpItemSelected(String loginType);
       void onListItemSelected(String strListName,String type);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {

            // Set the ListSelectionListener for communicating with the mainActivity
            mListener = (OnListItemSelectedListener) activity;

        } catch (ClassCastException e) {
             Log.d(TagClass.EXCEPTIONCATCH,e.getMessage());
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        if(args!=null) {
            tabPosition = args.getInt(TagClass.TABPOSITION);
            bundle = args.getBundle(TagClass.ACCOUNTDETAILS);
            strFullName = (String) bundle.get(TagClass.LOGIN_FULLNAME);
            strLoginType = (String) bundle.get(TagClass.LOGIN_TYPE);
            profilePic = (Bitmap) bundle.get(TagClass.LOGIN_IMAGE);
            facebookImageUrl = (String) bundle.get(TagClass.LOGIN_IMAGE_URL);
            strUserName = (String) bundle.get(TagClass.LOGIN_USERNAME);

        }
        lruCache = LRUCacheClass.getCache();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanse) {

        if(savedInstanse!=null)
        {
            if(savedInstanse.get(TagClass.POSITION)!=null)
            index = (Integer)savedInstanse.get(TagClass.POSITION);
        }
        GlobalVariables.sharedpreferences = getActivity().getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
        Map map =  GlobalVariables.sharedpreferences.getAll();
        if(map.size()!=0)
        {
            strListName = (String)map.get(TagClass.CURRENT_PLAY_LIST);
            if(strListName == null)
            {
                strListName = "";
            }
        }
        else
        {
            strListName = "";
        }
        if(tabPosition == 0)
        {
             rootView = inflater.inflate(R.layout.fragment_flashcards, container, false);
            recyclerViewFlashCards = (RecyclerView)rootView.findViewById(R.id.recycler_flash_cards);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewFlashCards.setLayoutManager(linearLayoutManager);
            if(strListName.equals("")) {
               callFreshFragment();
            }
            else
            {
                AsyncTaskGetMyListDataBeginning asyncTaskGetMyListDataBeginning = new AsyncTaskGetMyListDataBeginning();
                asyncTaskGetMyListDataBeginning.execute(new String[]{strListName});
            }


        }
        if(tabPosition == 1)
        {
            rootView = inflater.inflate(R.layout.fragment_mylist, container, false);
            floatingActionButtonCreateListSymbol = (FloatingActionButton)rootView.findViewById(R.id.action_create_list_symbol);
            floatingActionButtonCreateListDefinition = (FloatingActionButton)rootView.findViewById(R.id.action_create_list_definition);
            floatingActionButtonCreateListMake = (FloatingActionButton)rootView.findViewById(R.id.action_make_list);
                    floatingActionButtonCreateListSymbol.setOnClickListener(new View.OnClickListener(){

                        @Override
                        public void onClick(View v) {
                            myDialogCreateList = MyDialogSelectOption.newInstanse(strUserName,GlobalVariables.LOAD_SYMBOL_FRAGMENT);
                            myDialogCreateList.setTargetFragment(MyHomeScreenFragment.this, GlobalVariables.REQUEST_CODE_LIST_TYPE);
                            myDialogCreateList.show(getFragmentManager(), "");
                }
            });
            floatingActionButtonCreateListDefinition.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    myDialogCreateList = MyDialogSelectOption.newInstanse(strUserName,GlobalVariables.LOAD_DEFINITION_FRAGMENT);
                    myDialogCreateList.setTargetFragment(MyHomeScreenFragment.this, GlobalVariables.REQUEST_CODE_LIST_TYPE);
                    myDialogCreateList.show(getFragmentManager(), "");
                }
            });
            floatingActionButtonCreateListMake.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    createListDialog();
                }
            });
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            RecyclerView recyclerViewMyList = (RecyclerView)rootView.findViewById(R.id.recycler_my_list);
            recyclerViewMyList.setLayoutManager(linearLayoutManager);
            recyclerViewMyList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            myListAdaptor = new MyListAdaptor();
            recyclerViewMyList.setAdapter(myListAdaptor);
            myListAdaptor.onListItemSelectedListener(new MyListAdaptor.OnListItemSelected() {

                @Override
                public void onItemClick(TextView textViewMyList) {
                    mListener.onListItemSelected(textViewMyList.getText().toString() + TagClass.DATABASE_LISTNAME_DELIMITER
                            + strUserName,TagClass.LIST_NAME);
                }

                @Override
                public void onLongItemClick(TextView textViewMyList,int position) {
                    ActionBarCallBack actionBarCallBack = new ActionBarCallBack(getActivity(),textViewMyList.getText().toString() + TagClass.DATABASE_LISTNAME_DELIMITER
                            + strUserName,position,null);
                    getActivity().startActionMode(actionBarCallBack);
                }
            });

        }
        if(tabPosition == 2)
        {
            myAccountData = new MyAccountData(strFullName,profilePic);
            myAccountData.generateData();
            rootView = inflater.inflate(R.layout.fragment_myaccount,container,false);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            recyclerViewMyAccount = (RecyclerView)rootView.findViewById(R.id.recycler_my_account);
            recyclerViewMyAccount.setLayoutManager(linearLayoutManager);
            recyclerViewAdaptor = new MyAccountRecyclerViewAdaptor(getActivity(),myAccountData.getMyAccountDataList(),facebookImageUrl, lruCache);
            recyclerViewMyAccount.addItemDecoration(
                    new DividerItemDecoration(getActivity(), null, false, true));
            recyclerViewMyAccount.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
            recyclerViewMyAccount.setAdapter(recyclerViewAdaptor);
            recyclerViewAdaptor.setOnItemClickListener(new MyAccountRecyclerViewAdaptor.OnItemClickListener() {
                @Override
                public void onOverflowItemClick(View v) {
                    PopupMenu popupMenu = new PopupMenu(getActivity(), v);
                    MenuInflater menuInflater = popupMenu.getMenuInflater();
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.logout_popup:
                                    mListener.onPopUpItemSelected(strLoginType);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    menuInflater.inflate(R.menu.menu_my_account, popupMenu.getMenu());
                    popupMenu.show();
                }

                @Override
                public void onListItemSelected(TextView textView) {
                    if(textView != null)
                    {
                    String listName = textView.getText().toString();
                    if (listName.contains(TagClass.OVERALL_PERFORMANCE)) {

                    } else if (listName.contains(TagClass.PERFORMANCE)) {
                        listName = listName.split(" ")[0];
                        mListener.onListItemSelected(listName + TagClass.DATABASE_LISTNAME_DELIMITER
                                + strUserName, TagClass.PERFORMANCE);
                    }
                    else
                    {
                        mListener.onListItemSelected(listName, listName);
                    }
                }
                }
            });
        }
        return rootView;
    }

    void createListDialog()
    {
        Bundle args = new Bundle();
        args.putInt(TagClass.FRAGMENT_INFO, GlobalVariables.CUSTOMIZED_FRAGMENT);
        args.putString(TagClass.LOGIN_USERNAME, strUserName);
        MyDialogCreateList myDialogCreateList = new MyDialogCreateList();
        myDialogCreateList.setArguments(args);
        myDialogCreateList.setTargetFragment(MyHomeScreenFragment.this,GlobalVariables.REQUEST_CODE_LIST_TYPE);
        myDialogCreateList.show(getFragmentManager(), "");
    }


    public  void callFreshFragment()
    {
        ArrayList<String> arrayListCompounds = new ArrayList<>();
        Object []objectCompoundsData = GlobalVariables.hashMapCompoundsData.keySet().toArray();
            for (int i = 0; i <= 5; i++) {
                arrayListCompounds.add((String)objectCompoundsData[i]);
            makeRecyclerView(arrayListCompounds,GlobalVariables.LOAD_SYMBOL_FRAGMENT,TagClass.INORGANIC_COMPOUNDS);
        }
    }


    public static void refreshMyAccount(String listName,String operation)
    {

        MyUtilities.dismiss();
        myAccountData.refreshData(listName,operation);
        recyclerViewAdaptor.notifyDataSetChanged();
    }

    public void makeRecyclerView(ArrayList<String> arrayListCompounds,int fragmentInfo,String compoundType)
    {
        flashCardsAdaptor =  new FlashCardsAdaptor(getActivity(),fragmentInfo,arrayListCompounds, lruCache,compoundType);
        recyclerViewFlashCards.setAdapter(flashCardsAdaptor);
        addAndCallFlashCardsFragment(arrayListCompounds, fragmentInfo,compoundType);
    }


    public static void callMyListFragment()
    {
        myListAdaptor.notifyDataSetChanged();
    }

    public static void callMyListFragmentItemRemoved(int position)
    {
        myListAdaptor.notifyItemRemoved(position);
    }

    public static void callFlashCardsFragment()
    {
        flashCardsAdaptor.notifyDataSetChanged();
    }

    public static void addAndCallFlashCardsFragment(ArrayList<String> arrayListCompounds, int fragmentInfo,String compoundType)
    {
        flashCardsAdaptor.putData(arrayListCompounds, fragmentInfo,compoundType);
        callFlashCardsFragment();
    }


    public static void updateFlashCardsAdaptor(boolean updateFlag,int level)
    {
        if(updateFlag)
        {
           flashCardsAdaptor.putLevel(level);
            flashCardsAdaptor.notifyDataSetChanged();
        }
    }

            @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        getActivity().getIntent().putExtra(TagClass.POSITION,index);
        super.onSaveInstanceState(savedInstanceState);
    }

    class AsyncTaskGetMyListDataBeginning extends AsyncTask<String, Void, String> {
        private ArrayList<String> arrayListCompoundsDataMyList;
        protected String doInBackground(String... listName) {
            GlobalVariables.database.setTableName(listName[0]);
            Cursor cursorData = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
            arrayListCompoundsDataMyList = new ArrayList<>();
            while (cursorData.moveToNext()) {
                arrayListCompoundsDataMyList.add(cursorData.getString(0));
                    }
            return listName[0];
        }

        @Override
        public void onPostExecute(String strListName)
        {
            makeRecyclerView(arrayListCompoundsDataMyList,GlobalVariables.hashMapMyListsName.get(strListName),GlobalVariables.hashMapMyListsCompoundType.get(strListName));
        }
    }

    public static class MyDialogSelectOption extends DialogFragment{

        public static MyDialogSelectOption newInstanse(String userName,int fragmentInfo)
        {
            Bundle args = new Bundle();
            args.putString(TagClass.LOGIN_USERNAME, userName);
            args.putInt(TagClass.FRAGMENT_INFO, fragmentInfo);
            MyDialogSelectOption myDialogSelectOption =  new MyDialogSelectOption();
            myDialogSelectOption.setArguments(args);
            return myDialogSelectOption;
        }
        @Override
        public Dialog onCreateDialog(final Bundle bundle) {
            final Bundle args = getArguments();
            CharSequence [] choices = {TagClass.ORGANIC_COMPOUNDS,TagClass.INORGANIC_COMPOUNDS};
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(TagClass.CHOOSE);
            builder.setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent();
                    if (which == 0) {
                        i.putExtra(TagClass.COMPOUND_TYPE, TagClass.ORGANIC_COMPOUNDS);
                    }
                    else
                    {
                        i.putExtra(TagClass.COMPOUND_TYPE, TagClass.INORGANIC_COMPOUNDS);
                    }
                    i.putExtra(TagClass.LOGIN_USERNAME,(String)args.get(TagClass.LOGIN_USERNAME));
                    i.putExtra(TagClass.FRAGMENT_INFO, (Integer) args.get(TagClass.FRAGMENT_INFO));
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);
                }
            });
            return builder.create();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == GlobalVariables.REQUEST_CODE_LIST_TYPE) {
           if(myDialogCreateList != null) {
               myDialogCreateList.dismiss();
           }
            Bundle args = data.getExtras();
            if(args!=null) {
                if ((Integer) args.get(TagClass.FRAGMENT_INFO) == GlobalVariables.CUSTOMIZED_FRAGMENT) {
                    Intent intent = new Intent(getActivity(), MakeFlashCardsActivity.class);
                    startActivity(intent);

                } else {
                    Intent intent = new Intent(getActivity(), CreateListActivity.class);
                    intent.putExtra(TagClass.LOGIN_USERNAME, (String) args.get(TagClass.LOGIN_USERNAME));
                    intent.putExtra(TagClass.FRAGMENT_INFO, (Integer) args.get(TagClass.FRAGMENT_INFO));
                    intent.putExtra(TagClass.COMPOUND_TYPE, (String) args.get(TagClass.COMPOUND_TYPE));
                    startActivity(intent);
                }
            }
        }

    }

    }
