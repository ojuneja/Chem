package com.example.ojasjuneja.chem.home;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.flashcards.FlashCardsAdaptor;
import com.example.ojasjuneja.chem.myaccount.MyPerformanceActivity;
import com.example.ojasjuneja.chem.mylist.MyListDetailsActivity;
import com.example.ojasjuneja.chem.utilities.GetLevelStateAsyncTask;
import com.example.ojasjuneja.chem.utilities.MyAsyncTaskUpdateList;
import com.example.ojasjuneja.chem.utilities.MyAsyncTaskUpdatePlayList;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.example.ojasjuneja.chem.utilities.StoreLevelStateAsyncTask;
import com.facebook.login.LoginManager;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Ojas Juneja on 7/23/2015.
 */
public class HomeScreenActivity extends AppCompatActivity implements MyHomeScreenFragment.OnListItemSelectedListener{

    private ViewPager pager;
    private Toolbar toolbar;
    private PagerSlidingTabStrip tabs;
    private TextView textViewHomeScreen;
    private String strUsername;
    public static HomeScreenActivity homeScreenActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homescreen);
        homeScreenActivity = this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        textViewHomeScreen = (TextView)findViewById(R.id.text_home_screen);
        textViewHomeScreen.setText(TagClass.FLASHCARDS);
        setSupportActionBar(toolbar);
        Bundle extra = getIntent().getExtras();
        strUsername = (String)extra.get(TagClass.LOGIN_USERNAME);
        GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
        Map<String,String> map =  (Map<String,String>)GlobalVariables.sharedpreferences.getAll();
        if(map.size()!=0)
        {
            if(map.get(TagClass.CURRENT_PLAY_LIST) != null) {
                if (map.get(TagClass.CURRENT_PLAY_LIST).contains((String) extra.get(TagClass.LOGIN_USERNAME))) {
                    extra.putString(TagClass.CURRENT_PLAY_LIST, map.get(TagClass.CURRENT_PLAY_LIST));
                }
                else
                {
                    extra.putString(TagClass.CURRENT_PLAY_LIST,"");
                }
            }
            else
            {
                extra.putString(TagClass.CURRENT_PLAY_LIST,"");
            }
        }
        MyHomeScreenAdapter myHomeScreenAdapter = new MyHomeScreenAdapter(getSupportFragmentManager(),extra);
        pager = (ViewPager)findViewById(R.id.pager);
        tabs = (PagerSlidingTabStrip)findViewById(R.id.sliding_tab);
        pager.setAdapter(myHomeScreenAdapter);
        tabs.setViewPager(pager);
        tabs.setTextColor(Color.parseColor("#FFFFFF"));
        tabs.setIndicatorColor(Color.parseColor("#FFFFFF"));
    }


    void logout()
    {
        String playList;
        boolean shuffleFlag;
        ArrayList arrayList = new ArrayList();
        MyUtilities.progressDialog(this,TagClass.SIGNING_OUT);
        MyAsyncTaskUpdateList asyncTaskUpdateList = new MyAsyncTaskUpdateList(this,TagClass.LOGOUT);
        asyncTaskUpdateList.execute(strUsername);
        GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
        Map map = GlobalVariables.sharedpreferences.getAll();
        if(map.size() == 1 && map.get(TagClass.CURRENT_PLAY_LIST)!=null)
        {
            playList = (String)map.get(TagClass.CURRENT_PLAY_LIST);
            shuffleFlag = false;
        }
        else if( map.get(TagClass.CURRENT_PLAY_LIST)!=null)
        {
            playList = (String)map.get(TagClass.CURRENT_PLAY_LIST);
            shuffleFlag = true;
        }
        else
        {
            playList = "";
            shuffleFlag = false;
        }
        arrayList.add(TagClass.MyPREFERENCES);
        arrayList.add(TagClass.MyPREFERENCES_LIST);
        clearEditor(arrayList);
        MyAsyncTaskUpdatePlayList myAsyncTaskUpdatePlayList = new MyAsyncTaskUpdatePlayList(shuffleFlag);
        myAsyncTaskUpdatePlayList.execute(new String[]{strUsername, playList});
    }


    public void clearEditor(ArrayList<String> preferences)
    {
        for(int i=0;i<preferences.size();i++) {
            GlobalVariables.sharedpreferences = getSharedPreferences(preferences.get(i), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
            editor.clear();
            editor.apply();
        }
    }

    @Override
    public void onPopUpItemSelected(String strLoginType) {
        MyUtilities.progressDialog(this, TagClass.LOGOUT);
        if(strLoginType.equals(TagClass.NORMAL)) {
            logout();
        }
        else if(strLoginType.equals(TagClass.FACEBOOK))
        {
            try {
                LoginManager.getInstance().logOut();
                logout();
            }
            catch(Exception e)
            {
                logout();
            }
        }

    }

    @Override
    public void onListItemSelected(String strListName,String type) {
        if(type.equals(TagClass.LIST_NAME)) {
            Intent intent = new Intent(this, MyListDetailsActivity.class);
            intent.putExtra(TagClass.LIST_NAME, strListName);
            startActivity(intent);
        }
        else if(type.equals(TagClass.OVERALL_PERFORMANCE))
        {
            Intent intent = new Intent(this, MyPerformanceActivity.class);
            intent.putExtra(TagClass.LIST_NAME, TagClass.OVERALL_PERFORMANCE);
            startActivity(intent);
        }
        else if(type.equals(TagClass.PERFORMANCE))
        {
            Intent intent = new Intent(this, MyPerformanceActivity.class);
            intent.putExtra(TagClass.LIST_NAME, strListName);
            startActivity(intent);
        }
        else if(type.equals(TagClass.REFER_FRIEND))
        {
            sendEmail("",TagClass.REFER_SUBJECT,TagClass.REFER_BODY);
        }
        else if(type.equals(TagClass.REPORT_BUG))
        {
            sendEmail(TagClass.DEVELOPER_EMAIL,TagClass.BUG_SUBJECT,"");
        }
    }

     void sendEmail(String TO,String EXTRA_SUBJECT,String EXTRA_TEXT) {
        String[] CC = {TagClass.DEVELOPER_EMAIL};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.setData(Uri.parse(TagClass.MAIL_TO));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, EXTRA_SUBJECT);
        emailIntent.putExtra(Intent.EXTRA_TEXT, EXTRA_TEXT);

        try {
            startActivity(Intent.createChooser(emailIntent, TagClass.SEND_MAIL));
            finish();
        }
        catch (android.content.ActivityNotFoundException ex) {
            MyUtilities.showSnackBar(this,TagClass.NO_EMAIL_INTENT);
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
        Map map =  GlobalVariables.sharedpreferences.getAll();
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == GlobalVariables.REQUEST_CODE_LEVEL) {
            FlashCardsAdaptor.increaseIndex();
            Bundle args = data.getExtras();
            if(map.size() !=0 ) {
                GetLevelStateAsyncTask getLevelStateAsyncTask = new GetLevelStateAsyncTask((String)args.get(TagClass.COLOUMN_NAME_COMPOUNDS),
                        (Integer)args.get(TagClass.COLOUMN_NAME_LEVEL));
                getLevelStateAsyncTask.execute(new String[]{(String) map.get(TagClass.CURRENT_PLAY_LIST)});
            }
            else
            {
                MyHomeScreenFragment.updateFlashCardsAdaptor(true,(Integer)args.get(TagClass.COLOUMN_NAME_LEVEL));
            }
        }
    }

    public static void putDataInDatabase(String listName,String compoundName,int level,int times)
    {
        StoreLevelStateAsyncTask storeLevelStateAsyncTask = new StoreLevelStateAsyncTask(compoundName,level,times);
        storeLevelStateAsyncTask.execute(new String[]{listName});
    }



}
