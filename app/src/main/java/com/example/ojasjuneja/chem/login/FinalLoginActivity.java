package com.example.ojasjuneja.chem.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.SplashActivity;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.home.HomeScreenActivity;
import com.example.ojasjuneja.chem.utilities.GMailSender;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.parse.ParseObject;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by Ojas Juneja on 7/24/2015.
 */
public class FinalLoginActivity extends AppCompatActivity {

    private Toolbar  toolbar = null;
    private EditText textViewPassword;
    private EditText textViewConfirmPassword;
    private HashMap<String,String> hashMapCredentials;
    private Button buttonSubmit;
    private String strUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finallogin);
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        if(toolbar!=null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
         Bundle extra = getIntent().getExtras();
        hashMapCredentials = (HashMap)extra.getSerializable(TagClass.LOGIN_CREDENTIALS);
        textViewPassword = (EditText)findViewById(R.id.editText_enter_password);
        textViewConfirmPassword = (EditText)findViewById(R.id.editText_confirm_password);
        listenerPasswordField();
        buttonSubmit = (Button)findViewById(R.id.submit_login_button);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (passwordMatchValidation()) {
                    hashMapCredentials.put(TagClass.LOGIN_PASSWORD, textViewPassword.getText().toString());
                    SendLoginDetailsClass sendLoginDetailsClass = new SendLoginDetailsClass(FinalLoginActivity.this, hashMapCredentials);
                    sendLoginDetailsClass.execute(new String[]{hashMapCredentials.get(TagClass.LOGIN_FIRSTNAME) + " " + hashMapCredentials.get(TagClass.LOGIN_LASTNAME)});
                }
            }
        });

    }


    void listenerPasswordField()
    {
        textViewPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                passwordValidation(textViewPassword);

            }
        });


    }

    void passwordValidation(TextView textView)
    {
        if (textView.getText().toString().length() <= 0 )
            textView.setError(TagClass.ERROR_BLANK_FIELD);
        else if(textView.getText().toString().length() <= 6 )
        {
            textView.setError(TagClass.ERROR_PASSWORD_LENGTH);
        }
        else
            textView.setError(null);
    }

    boolean passwordMatchValidation()
    {
        if(!textViewPassword.getText().toString().equals(textViewConfirmPassword.getText().toString()))
        {
            textViewConfirmPassword.setError(TagClass.ERROR_PASSWORD_MATCH);
        }
        else if(textViewPassword.getText().toString().length()<=0 && textViewConfirmPassword.getText().toString().length()<=0)
        {
            textViewPassword.setError(TagClass.ERROR_BLANK_FIELD);
            textViewConfirmPassword.setError(TagClass.ERROR_BLANK_FIELD);
        }
        else if(textViewPassword.getText().toString().length()<=0)
        {
            textViewPassword.setError(TagClass.ERROR_BLANK_FIELD);
        }
        else if(textViewConfirmPassword.getText().toString().length()<=0)
        {
            textViewConfirmPassword.setError(TagClass.ERROR_BLANK_FIELD);
        }
        else  if(textViewPassword.getError()==null && textViewConfirmPassword.getError()==null)
        {
            textViewConfirmPassword.setError(null);
            return true;
        }
        return false;
    }

    public void OnCredentialsCreated(String strName,String loginType,Bitmap defaultImage,String url) {
        Intent intent = new Intent(FinalLoginActivity.this, HomeScreenActivity.class);
        intent.putExtra(TagClass.LOGIN_FULLNAME,strName);
        intent.putExtra(TagClass.LOGIN_TYPE,loginType);
        intent.putExtra(TagClass.LOGIN_IMAGE,defaultImage);
        intent.putExtra(TagClass.LOGIN_IMAGE_URL, url);
        intent.putExtra(TagClass.LOGIN_USERNAME, strUserName);
        startActivity(intent);
        finish();
        LoginActivity.loginActivityObject.finish();
        if(SplashActivity.splashActivityObject != null)
        {
            SplashActivity.splashActivityObject.finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private class SendLoginDetailsClass extends AsyncTask<String,Void,String>
    {
        private WeakReference<FinalLoginActivity> finalLoginActivityWeakReference;
        private HashMap<String,String> hashMapCredentials;
        public SendLoginDetailsClass(FinalLoginActivity finalLoginActivity,HashMap<String,String> hashMapCredentials)
        {
            finalLoginActivityWeakReference = new WeakReference<>(finalLoginActivity);
            this.hashMapCredentials = hashMapCredentials;
        }
        @Override
        protected String doInBackground(String... strName)
        {
                ParseObject parseObject = new ParseObject(TagClass.LOGIN_DATABASE);
                parseObject.put(TagClass.LOGIN_FIRSTNAME, hashMapCredentials.get(TagClass.LOGIN_FIRSTNAME));
                parseObject.put(TagClass.LOGIN_LASTNAME, hashMapCredentials.get(TagClass.LOGIN_LASTNAME));
                parseObject.put(TagClass.LOGIN_EMAILADDRESS, hashMapCredentials.get(TagClass.LOGIN_EMAILADDRESS));
                parseObject.addAll(TagClass.LOGIN_USERNAMEPASSSWORD, Arrays.asList(hashMapCredentials.get(TagClass.LOGIN_USERNAME), hashMapCredentials.get(TagClass.LOGIN_PASSWORD)));
                try {
                    GMailSender sender = new GMailSender(TagClass.DEVELOPER_EMAIL, TagClass.DEVELOPER_PASSWORD);
                    sender.sendMail(TagClass.FORGOT_CREDENTIALS_SUBJECT,
                            "Hi" + hashMapCredentials.get(TagClass.LOGIN_FIRSTNAME) + hashMapCredentials.get(TagClass.LOGIN_LASTNAME)
                                    + "\n\n\n" + TagClass.FORGOT_CREDENTIALS_BODY_ENDING, TagClass.DEVELOPER_EMAIL, hashMapCredentials.get(TagClass.LOGIN_EMAILADDRESS));
                    parseObject.save();
                } catch (Exception e) {
                    Log.e(TagClass.EXCEPTIONCATCH, "", e);
                    MyUtilities.showSnackBar(FinalLoginActivity.this, TagClass.ERROR_EMAIL_IMPROPER);
                }
            return strName[0];
        }

        @Override
        protected void onPostExecute(String strName)
        {
            GlobalVariables.sharedpreferences = getSharedPreferences(TagClass.MyPREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = GlobalVariables.sharedpreferences.edit();
            editor.putString(TagClass.LOGIN_FULLNAME, hashMapCredentials.get(TagClass.LOGIN_FIRSTNAME) + " " + hashMapCredentials.get(TagClass.LOGIN_LASTNAME));
            editor.putString(TagClass.LOGIN_USERNAME, hashMapCredentials.get(TagClass.LOGIN_USERNAME));
            editor.putString(TagClass.LOGIN_TYPE,TagClass.NORMAL );
            editor.putString(TagClass.LOGIN_IMAGE_URL, "");
            editor.apply();
            strUserName = hashMapCredentials.get(TagClass.LOGIN_USERNAME);
            FinalLoginActivity finalLoginActivity = finalLoginActivityWeakReference.get();
            Bitmap defaultImage = BitmapFactory.decodeResource(getResources(), R.drawable.beaker);
            finalLoginActivity.OnCredentialsCreated(strName,TagClass.NORMAL,defaultImage,"");
        }

    }

}


