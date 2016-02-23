package com.example.ojasjuneja.chem.login;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Ojas Juneja on 7/24/2015.
 */
public class LoginActivity extends AppCompatActivity{

    private Toolbar toolbar = null;
    private EditText textViewFirstName = null;
    private EditText textViewLastName = null;
    private EditText textViewEmailAddress = null;
    private EditText textViewUserName = null;
    private Button buttonNext;
    private ProgressDialog progress;
    private HashMap<String,String> hashMapCredentials;
    public static LoginActivity loginActivityObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginActivityObject = this;
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        textViewFirstName = (EditText)findViewById(R.id.editText_first_name);
        textViewLastName = (EditText)findViewById(R.id.editText_last_name);
        textViewEmailAddress = (EditText)findViewById(R.id.editText_email);
        textViewUserName = (EditText)findViewById(R.id.editText_username);
        buttonNext = (Button)findViewById(R.id.next_login_button);
        hashMapCredentials = new HashMap<>();
        listenerEditTextFirstName();
        listenerEditTextLastName();
        listenerEditTextUserName();
        listenerEditTextEmailAddress();
        buttonNext.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (checkNoLoginErrors()) {
                    callNextLoginPage();
                }
            }
        });

    }
    void listenerEditTextFirstName()
    {
        textViewFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letterValidation(textViewFirstName);

            }
        });
    }

    void listenerEditTextLastName()
    {
        textViewLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                letterValidation(textViewLastName);

            }
        });
    }

    void listenerEditTextUserName()
    {
        textViewUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                userNameValidation(textViewUserName);

            }
        });
    }


    void listenerEditTextEmailAddress()
    {
        textViewEmailAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                emailAddressValidation(textViewEmailAddress);

            }
        });
    }

    void callNextLoginPage()
    {
        progress = new ProgressDialog(LoginActivity.this, AlertDialog.THEME_HOLO_LIGHT);
        progress.setTitle("Loading");
        progress.setCancelable(false);
        progress.setMessage("We are almost there...");
        progress.show();
        GetLoginDetailsClass sendLoginDetailsClass = new GetLoginDetailsClass(LoginActivity.this);
        sendLoginDetailsClass.execute(new String[]{textViewEmailAddress.getText().toString(), textViewUserName.getText().toString()});

    }

    void goToNextPage(boolean duplicateUserNameFlag,boolean duplicateEmailFlag)
    {
        progress.dismiss();
        if(duplicateUserNameFlag)
        {
            textViewUserName.setError(TagClass.ERROR_USERNAME_ALREADY_IN_USE);
        }
        if(duplicateEmailFlag)
        {
            textViewEmailAddress.setError(TagClass.ERROR_EMAIL_ALREADY_IN_USE);
        }
        if(!duplicateEmailFlag && !duplicateUserNameFlag)
        {
            textViewUserName.setError(null);
            textViewEmailAddress.setError(null);
            hashMapCredentials.put(TagClass.LOGIN_FIRSTNAME, textViewFirstName.getText().toString());
            hashMapCredentials.put(TagClass.LOGIN_LASTNAME, textViewLastName.getText().toString());
            hashMapCredentials.put(TagClass.LOGIN_EMAILADDRESS, textViewEmailAddress.getText().toString());
            hashMapCredentials.put(TagClass.LOGIN_USERNAME, textViewUserName.getText().toString());
            Intent intent = new Intent(LoginActivity.this, FinalLoginActivity.class);
            intent.putExtra(TagClass.LOGIN_CREDENTIALS, hashMapCredentials);
            startActivity(intent);
        }
    }

    void letterValidation(TextView textView)
    {
        if (textView.getText().toString().length() <= 0 )
            textView.setError(TagClass.ERROR_BLANK_FIELD);
        else if(nonAlphabetsCheck(textView.getText().toString()))
            textView.setError(TagClass.ERROR_SPECIAL_CHARACTERS_DIGITS);
        else
            textView.setError(null);
    }

    void userNameValidation(TextView textView)
    {

        if (textView.getText().toString().length() <= 0 )
            textView.setError(TagClass.ERROR_BLANK_FIELD);
        else if(textView.getText().toString().length() <= 4 )
            textView.setError(TagClass.ERROR_USERNAME_LENGTH);
        else
            textView.setError(null);
    }

    void emailAddressValidation(TextView textView)
    {
        if (textView.getText().toString().length() <= 0 )
            textView.setError(TagClass.ERROR_BLANK_FIELD);
        else if(!emailPatterMatcher(textView.getText().toString()))
        {
            textView.setError(TagClass.ERROR_EMAIL_IMPROPER);
        }
        else
            textView.setError(null);
    }

    boolean emailPatterMatcher(String emailAddress)
    {
        Pattern pattern = Pattern.compile("\\S+@\\w+\\.com");
        Matcher m = pattern.matcher(emailAddress);
        return m.find();
    }

    boolean nonAlphabetsCheck(String strName)
    {
        char[] chars = strName.toCharArray();

        for (char c : chars) {
            if(!Character.isLetter(c)) {
                return true;
            }
        }

        return false;
    }

    boolean checkNoLoginErrors()
    {
        if(textViewFirstName.getText().toString().length()!=0
        && textViewLastName.getText().toString().length()!=0
        && textViewEmailAddress.getText().toString().length()!=0
        && textViewUserName.getText().toString().length()!=0)
        {
             if(textViewFirstName.getError()==null
                    && textViewLastName.getError()==null
                    && textViewEmailAddress.getError()==null
                    && textViewUserName.getError()==null)
            {
                return true;
            }
            else
             {
                 return false;
             }
        }
        else
        {
            emptyFieldErrors();
            return false;
        }
    }

    void emptyFieldErrors()
    {
        if(textViewFirstName.getText().toString().length()==0)
            textViewFirstName.setError(TagClass.ERROR_BLANK_FIELD);
        if(textViewLastName.getText().toString().length()==0)
            textViewLastName.setError(TagClass.ERROR_BLANK_FIELD);
        if(textViewEmailAddress.getText().toString().length()==0)
            textViewEmailAddress.setError(TagClass.ERROR_BLANK_FIELD);
        if(textViewUserName.getText().toString().length()==0)
            textViewUserName.setError(TagClass.ERROR_BLANK_FIELD);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(TagClass.LOGIN_CREDENTIALS, hashMapCredentials);
    }

    private class GetLoginDetailsClass extends AsyncTask<String,Void,String> {
        private WeakReference<LoginActivity>loginActivityWeakReference;
        boolean duplicateUserNameFlag = false;
        boolean duplicateEmailFlag = false;
        GetLoginDetailsClass(LoginActivity loginActivity)
        {
            loginActivityWeakReference = new WeakReference<>(loginActivity);
        }
        @Override
        protected String doInBackground(String... credentials) {

            ArrayList<String> arrayCredentials = new ArrayList<>();

            for(String credential:credentials)
            {
                arrayCredentials.add(credential);
            }
            ParseQuery<ParseObject> query = ParseQuery.getQuery(TagClass.LOGIN_DATABASE);

            try {

                List<ParseObject> objects = query.find();
                for (ParseObject parseObject : objects) {
                    ArrayList arrayListCredentials = (ArrayList)parseObject.get(TagClass.LOGIN_USERNAMEPASSSWORD);
                    if(arrayCredentials.get(0).equals(parseObject.get(TagClass.LOGIN_EMAILADDRESS).toString()))
                    {
                        duplicateEmailFlag = true;
                    }
                    if(arrayCredentials.get(1).equals(arrayListCredentials.get(0)))
                    {
                        duplicateUserNameFlag = true;
                    }
                }
            }
            catch(ParseException e)
            {
                Log.d(TagClass.EXCEPTIONCATCH, e.getMessage());
            }
            return "";
            }

        @Override
        protected void onPostExecute(String strDummy)
        {
            LoginActivity loginActivity = loginActivityWeakReference.get();
            loginActivity.goToNextPage(duplicateUserNameFlag, duplicateEmailFlag);

        }

    }

}
