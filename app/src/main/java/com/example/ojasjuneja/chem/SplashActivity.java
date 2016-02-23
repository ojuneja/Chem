package com.example.ojasjuneja.chem;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.example.ojasjuneja.chem.home.HomeScreenActivity;
import com.example.ojasjuneja.chem.login.LoginActivity;
import com.example.ojasjuneja.chem.login.SignInActivity;
import com.example.ojasjuneja.chem.utilities.DataBaseHelper;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener{

    private String strFullName;
    private Bitmap bitmapImage;
    private SliderLayout mDemoSlider;
    private Button signIn,signUp;
    private String strUserName;
    public static Activity splashActivityObject;
    private AlarmManager alarmMgr;
    private PendingIntent alarmIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_loggedin);
        splashActivityObject = this;
        LRUCacheClass lruCacheClass = new LRUCacheClass();
        lruCacheClass.setLRUCache();
        GlobalVariables.database = new DataBaseHelper(this);
      /*  try {
            alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(this, AlarmBroadcastReciver.class);
            intent.setAction(TagClass.INTENT);
            alarmIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
            alarmMgr.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + AlarmManager.INTERVAL_DAY,
                    AlarmManager.INTERVAL_DAY, alarmIntent);
        }
        catch(Exception e)
        {
            Log.e(TagClass.EXCEPTIONCATCH,"",e);
        }*/
        loadChemicalCompounds();
    }


    void callHomeScreenActivity(String loginType,String imageURL)
    {
        Intent intent = new Intent(this,HomeScreenActivity.class);
        intent.putExtra(TagClass.LOGIN_FULLNAME,strFullName);
        intent.putExtra(TagClass.LOGIN_TYPE, loginType);
        intent.putExtra(TagClass.LOGIN_IMAGE,bitmapImage);
        intent.putExtra(TagClass.LOGIN_IMAGE_URL, imageURL);
        intent.putExtra(TagClass.LOGIN_USERNAME, strUserName);
        startActivity(intent);
        finish();
    }

    void loadChemicalCompounds()
    {
        DownloadCompoundData downloadCompoundData = new DownloadCompoundData(this);
        downloadCompoundData.execute(new String[]{TagClass.API_URL_INORGANIC,TagClass.API_URL_ORGANIC});
    }

    void loadUIAndActivities()
    {
        GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES, Context.MODE_PRIVATE);
        Map<String,String> map =  (Map<String,String>)GlobalVariables.sharedpreferences.getAll();

        if(map.size() != 0)
        {
            bitmapImage = BitmapFactory.decodeResource(getResources(), R.drawable.beaker);
            strFullName = map.get(TagClass.LOGIN_FULLNAME);
            strUserName = map.get(TagClass.LOGIN_USERNAME);
            if(map.get(TagClass.LOGIN_TYPE).equals(TagClass.FACEBOOK))
            {
                callHomeScreenActivity(TagClass.FACEBOOK, map.get(TagClass.LOGIN_IMAGE_URL));
            }
            else if(map.get(TagClass.LOGIN_TYPE).equals(TagClass.NORMAL))
            {
                callHomeScreenActivity(TagClass.NORMAL,"");
            }
        }
        else
        {
            setContentView(R.layout.activity_splash);
            mDemoSlider = (SliderLayout)findViewById(R.id.slider);
            HashMap<String,Integer> url_maps = new HashMap<>();
            url_maps.put(TagClass.SPLASH_SCREEN1,R.drawable.green);
            url_maps.put(TagClass.SPLASH_SCREEN2,R.drawable.green);
            url_maps.put(TagClass.SPLASH_SCREEN3, R.drawable.green);
            url_maps.put(TagClass.SPLASH_SCREEN4, R.drawable.green);
            for(String name : url_maps.keySet()){
                TextSliderView textSliderView = new TextSliderView(this);
                // initialize a SliderLayout
                textSliderView
                        .image(url_maps.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);

                //add your extra information
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra",name);

                mDemoSlider.addSlider(textSliderView);
            }
            mDemoSlider.setCustomIndicator((PagerIndicator) findViewById(R.id.custom_indicator));
            signIn = (Button)findViewById(R.id.button_signin_splash);
            signIn.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SplashActivity.this,SignInActivity.class);
                    startActivity(intent);
                }
            });
            signUp =  (Button)findViewById(R.id.button_signup_splash);
            signUp.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(SplashActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    class  DownloadCompoundData extends AsyncTask<String, Void, String[]>
    {

        private WeakReference<SplashActivity> splashActivityWeakReference;
        DownloadCompoundData(SplashActivity splashActivity)
        {
            splashActivityWeakReference = new WeakReference<>(splashActivity);
        }
        @Override
        protected String[] doInBackground(String... urls) {
            InputStream streamData = null;
            int i = 0;
            String jsonStringOrganic="";
            String jsonStringInOrganic="";
            String line;
            for(String url:urls) {
                streamData = MyUtilities.getHttpConnection(url);
                if (streamData != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(streamData));
                    StringBuilder out = new StringBuilder();
                    try {
                        while ((line = reader.readLine()) != null) {
                            out.append(line);
                        }
                        reader.close();
                        if(i == 0) {
                            jsonStringInOrganic = out.toString();
                        }
                        else
                        {
                            jsonStringOrganic = out.toString();
                        }
                        i++;
                    } catch (IOException ex) {
                        Log.e(TagClass.EXCEPTIONCATCH, "", ex);
                    }
                }
            }
            String [] jsonString = {jsonStringInOrganic,jsonStringOrganic};
            return jsonString;
        }

        @Override
        protected void onPostExecute(String[] jsonString)
        {
            try {
                JSONObject jsonObjInorganic = new JSONObject(jsonString[0]);
                JSONObject jsonObjOrganic = new JSONObject(jsonString[1]);
                JSONArray jsonArrayInorganic =  (JSONArray)jsonObjInorganic.get(TagClass.KEY_DEFINITIONS);
                JSONArray jsonArrayOrganic =  (JSONArray)jsonObjOrganic.get(TagClass.KEY_DEFINITIONS);
                for (int i = 0; i < jsonArrayInorganic.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayInorganic.get(i);

                    String symbol = (String) jsonObject.get(TagClass.KEY_SYMBOL);
                    String strDefinition = (String) jsonObject.get(TagClass.KEY_DEFINITION);
                   GlobalVariables.hashMapCompoundsData.put(symbol, strDefinition);
                   GlobalVariables.hashMapCompoundsDataReverse.put(strDefinition, symbol);
                }
                for (int i = 0; i < jsonArrayOrganic.length(); i++) {
                    JSONObject jsonObject = (JSONObject) jsonArrayOrganic.get(i);
                    String strDefinition = (String) jsonObject.get(TagClass.KEY_DEFINITION);
                    JSONObject jsonObjectInner  =  jsonObject.getJSONObject(TagClass.KEY_IMAGE);
                    String symbol = (String)jsonObjectInner.get(TagClass.IMAGE_URL);
                    GlobalVariables.hashMapCompoundsDataOrganic.put(symbol, strDefinition);
                    GlobalVariables.hashMapCompoundsDataReverseOrganic.put(strDefinition, symbol);
                }
                if(GlobalVariables.hashMapCompoundsData.size() > 0 && GlobalVariables.hashMapCompoundsDataOrganic.size() > 0)
                {

                    SplashActivity splashActivity = splashActivityWeakReference.get();
                    splashActivity.loadUIAndActivities();
                }

                //Code will be used in future
                /*byte [] byteData = MyUtilities.serializeHashMap(GlobalVariables.hashMapCompundsData);
                if(myDataBase.insertData(byteData))
                {
                    myDataBase.getAllData();
                }*/

            }
            catch(Exception e)
            {
                Log.e(TagClass.EXCEPTIONCATCH,"",e);
            }
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_splash, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SplashActivity", "--- Splash Activity destroyed ---");

    }


    @Override
    public void onSliderClick(BaseSliderView baseSliderView) {

    }
}
