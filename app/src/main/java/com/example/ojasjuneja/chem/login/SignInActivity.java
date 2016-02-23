package com.example.ojasjuneja.chem.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.SplashActivity;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.HomeScreenActivity;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Ojas Juneja on 7/26/2015.
 */
public class SignInActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private String strFullName;
    private String strUserName;
    private EditText textUserName;
    private EditText textPassword;
    private TextView textViewError = null;
    private ProfileTracker mProfileTracker;
    private static SignInActivity signInActivity;

    private FacebookCallback<LoginResult> mcallBackFacebook = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {
            if(Profile.getCurrentProfile() == null) {
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile1, Profile profile2) {
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                            Profile.setCurrentProfile(profile2);
                        }
                    };
                    mProfileTracker.startTracking();
                }
                final Profile profile = Profile.getCurrentProfile();
                strFullName = profile.getName();
                strUserName = profile.getId();

            Uri profilePictureURL = profile.getProfilePictureUri(GlobalVariables.FACEBOOK_IMAGE_WIDTH, GlobalVariables.FACEBOOK_IMAGE_HEIGHT);
            SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
            editor.putString(TagClass.LOGIN_USERNAME,strUserName );
            editor.putString(TagClass.LOGIN_FULLNAME,strFullName );
            editor.putString(TagClass.LOGIN_TYPE, TagClass.FACEBOOK);
            editor.putString(TagClass.LOGIN_IMAGE_URL, profilePictureURL.toString());
            editor.apply();
            AsyncMakeLocalDatabase asyncMakeLocalDatabase = new AsyncMakeLocalDatabase(BitmapFactory.decodeResource(getResources(), R.drawable.beaker));
            asyncMakeLocalDatabase.execute(new String[]{strUserName, TagClass.FACEBOOK, profilePictureURL.toString()});
        }

        @Override
        public void onCancel() {

        }

        @Override
        public void onError(FacebookException exception) {
            // App code
        }
    };





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(HomeScreenActivity.homeScreenActivity !=null)
        {
            HomeScreenActivity.homeScreenActivity.finish();
        }
        facebookLogin();
        signInActivity = this;
        textUserName = (EditText) findViewById(R.id.editText_username);
        textPassword = (EditText) findViewById(R.id.editText_password);
        Button buttonLogin = (Button) findViewById(R.id.login_button);
        TextView textSignUp = (TextView) findViewById(R.id.text_sign_up);
        TextView textForgotPassword = (TextView) findViewById(R.id.text_forgot_password);
        buttonLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (checkUsernameAndPassword()) {
                    VerifyCredentials verifyCredentials = new VerifyCredentials(SignInActivity.this);
                    verifyCredentials.execute(new String[]{});
                }
            }
        });
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
                LoginManager.getInstance().registerCallback(callbackManager,mcallBackFacebook);
        textSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        textForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(SignInActivity.this, ForgotPasswordUsernameActivity.class);
                startActivity(i);
            }
        });

    }

    boolean checkUsernameAndPassword()
    {
        if(textUserName.getText().toString().length()!=0 && textPassword.getText().toString().length()!=0)
        {
            return true;
        }
        if(textUserName.getText().toString().length()==0)
        {
            textUserName.setError(TagClass.ERROR_BLANK_FIELD);
        }
        if(textPassword.getText().toString().length()==0)
        {
            textPassword.setError(TagClass.ERROR_BLANK_FIELD);
        }

        return false;
    }

    void userNamePasswordNotMatch()
    {
        textViewError = (TextView)findViewById(R.id.text_error);
        textViewError.setText(TagClass.ERROR_USERNAME_PASSWORD);
    }


    void facebookLogin()
    {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_signin);
        LoginButton login = (LoginButton)findViewById(R.id.login_button_facebook);
        login.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
        login.registerCallback(callbackManager, mcallBackFacebook);
    }




    void callHomeScreenActivity(String loginType,String facebookImageURL,Bitmap profileImage)
    {

        MyUtilities.dismiss();
        GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + strUserName);
        GlobalVariables.database.createListTable(GlobalVariables.database.getSQLDatabaseInstanse());
        if(textViewError!=null)
        textViewError.setError(null);
        Intent intent = new Intent(this,HomeScreenActivity.class);
        intent.putExtra(TagClass.LOGIN_FULLNAME,strFullName);
        intent.putExtra(TagClass.LOGIN_USERNAME,strUserName);
        intent.putExtra(TagClass.LOGIN_TYPE,loginType);
        intent.putExtra(TagClass.LOGIN_IMAGE,profileImage);
        intent.putExtra(TagClass.LOGIN_IMAGE_URL,facebookImageURL);
        startActivity(intent);
        finish();
        SplashActivity.splashActivityObject.finish();
    }


    class AsyncMakeLocalDatabase extends AsyncTask<String,Void,String[]> {
        Bitmap bitmap;
        AsyncMakeLocalDatabase(Bitmap bitmap)
        {
            this.bitmap = bitmap;
        }
        @Override
        protected String[] doInBackground(String... loginDetails) {
            try {
                ParseQuery<ParseObject> queryListTable = ParseQuery.getQuery(TagClass.PREFIX_LIST_NAMES + loginDetails[0]);
                List<ParseObject> objectListNames = queryListTable.find();
                if (objectListNames.size() > 0) {
                    for (ParseObject objectListName:objectListNames) {
                        String listName = (String) objectListName.get(TagClass.LIST_NAME);
                        GlobalVariables.hashMapMyListsName.put(listName,(Integer)objectListName.get(TagClass.FRAGMENT_INFO));
                        GlobalVariables.hashMapMyListsCompoundType.put(listName,(String)objectListName.get(TagClass.COMPOUND_TYPE));
                        ParseQuery<ParseObject> queryListDetails = ParseQuery.getQuery(listName).setLimit(GlobalVariables.LIMIT);
                        List<ParseObject> objectListDetails = queryListDetails.find();
                        GlobalVariables.database.setTableName(TagClass.PREFIX_LIST_NAMES + loginDetails[0]);
                        GlobalVariables.database.createListTable(GlobalVariables.database.getSQLDatabaseInstanse());
                        GlobalVariables.database.insertDataListNames(GlobalVariables.database.getSQLDatabaseInstanse(),
                                listName, GlobalVariables.STATE_SYNC);
                        GlobalVariables.database.setTableName(listName);
                        GlobalVariables.database.createTable(GlobalVariables.database.getSQLDatabaseInstanse());
                        for (ParseObject objectListValues : objectListDetails) {
                            GlobalVariables.database.insertData(GlobalVariables.database.getSQLDatabaseInstanse(),
                                    (String) objectListValues.get(TagClass.COLOUMN_NAME_COMPOUNDS), (Integer) objectListValues.get(TagClass.COLOUMN_NAME_LEVEL),
                                    GlobalVariables.STATE_SYNC, (Integer) objectListValues.get(TagClass.COLOUMN_NAME_TIMES));
                        }
                    }
                }
                ParseQuery<ParseObject> queryPlayListTable = ParseQuery.getQuery(TagClass.CURRENT_PLAYLIST);
                List<ParseObject> objectPlayListNames = queryPlayListTable.find();
                if(objectPlayListNames.size() > 0)
                {
                  for(ParseObject objectPlayList:objectPlayListNames)
                  {
                      if(objectPlayList.get(TagClass.LOGIN_USERNAME).equals(strUserName))
                      {
                          GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES_LIST, Context.MODE_PRIVATE);
                          SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
                          editor.clear();
                          editor.putString(TagClass.CURRENT_PLAY_LIST, (String) objectPlayList.get(TagClass.CURRENT_PLAY_LIST));
                          editor.putBoolean(TagClass.SHUFFLE_FLAG, (boolean) objectPlayList.get(TagClass.SHUFFLE_FLAG));
                          editor.apply();
                      }
                  }
                }
            } catch (ParseException e) {
                Log.e(TagClass.EXCEPTIONCATCH, "", e);
            }
            return loginDetails;
        }
        @Override
        protected void onPostExecute(String [] loginDetails)
        {
            callHomeScreenActivity(loginDetails[1], loginDetails[2], bitmap);
        }
    }


    private class VerifyCredentials extends AsyncTask<String,Void,String> {
        private WeakReference<SignInActivity> signInActivityWeakReference;
        HashMap<String,String> hashMapLoginCeredentials;
        VerifyCredentials(SignInActivity signInActivity)
        {
            signInActivityWeakReference = new WeakReference<>(signInActivity);
        }

        @Override
        protected String doInBackground(String... params) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(TagClass.LOGIN_DATABASE);
            String strStatus = "False";

            try {

                List<ParseObject> objects = query.find();
                for (ParseObject parseObject : objects) {
                    ArrayList arrayListCredentials = (ArrayList)parseObject.get(TagClass.LOGIN_USERNAMEPASSSWORD);
                    if(textUserName.getText().toString().equals(arrayListCredentials.get(0))
                            && textPassword.getText().toString().equals(arrayListCredentials.get(1)))
                    {
                        hashMapLoginCeredentials = new HashMap<>();
                        hashMapLoginCeredentials.put(TagClass.LOGIN_FIRSTNAME,(String)parseObject.get(TagClass.LOGIN_FIRSTNAME));
                        hashMapLoginCeredentials.put(TagClass.LOGIN_LASTNAME,(String)parseObject.get(TagClass.LOGIN_LASTNAME));
                        hashMapLoginCeredentials.put(TagClass.LOGIN_USERNAME,(String)arrayListCredentials.get(0));
                        strStatus = "True";
                    }

                }
            }
            catch(ParseException e)
            {
                Log.d(TagClass.EXCEPTIONCATCH,e.getMessage());
            }
            return strStatus;
        }

        @Override
        protected void onPostExecute(String strStatus)
        {
            MyUtilities.progressDialog(SignInActivity.this,TagClass.SIGN_IN_WAIT_MESSAGE);
            SignInActivity signInActivity = signInActivityWeakReference.get();
            if(strStatus.equals("True")) {
                SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
                editor.putString(TagClass.LOGIN_FULLNAME, hashMapLoginCeredentials.get(TagClass.LOGIN_FIRSTNAME) + " " + hashMapLoginCeredentials.get(TagClass.LOGIN_LASTNAME));
                editor.putString(TagClass.LOGIN_USERNAME, hashMapLoginCeredentials.get(TagClass.LOGIN_USERNAME));
                editor.putString(TagClass.LOGIN_TYPE, TagClass.NORMAL);
                editor.putString(TagClass.LOGIN_IMAGE_URL, "");
                editor.apply();
                Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.beaker);
                strFullName = hashMapLoginCeredentials.get(TagClass.LOGIN_FIRSTNAME) + " " + hashMapLoginCeredentials.get(TagClass.LOGIN_LASTNAME);
                strUserName = textUserName.getText().toString();
                AsyncMakeLocalDatabase asyncMakeLocalDatabase = new AsyncMakeLocalDatabase(defaultImage);
                asyncMakeLocalDatabase.execute(new String[]{strUserName, TagClass.NORMAL, ""});
            }
            else
            {
                MyUtilities.dismiss();
                signInActivity.userNamePasswordNotMatch();
            }

        }
    }

    @Override
     public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager.onActivityResult(requestCode, resultCode, data)) return;
    }


    }
