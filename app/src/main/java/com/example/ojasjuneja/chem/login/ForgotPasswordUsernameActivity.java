package com.example.ojasjuneja.chem.login;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.GMailSender;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ojas Juneja on 8/20/2015.
 */
public class ForgotPasswordUsernameActivity extends AppCompatActivity {

    private EditText editText_email;
    private Button button_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password_username);
        editText_email = (EditText) findViewById(R.id.edit_enter_email);
        button_email = (Button) findViewById(R.id.submit);
        button_email.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                AsyncTaskForgotCredentials asyncTaskForgotCredentials = new AsyncTaskForgotCredentials();
                asyncTaskForgotCredentials.execute(new String[]{editText_email.getText().toString()});
            }
        });

    }


    class AsyncTaskForgotCredentials extends AsyncTask<String, Void, String> {

        private boolean idFound = false;
        private String emailAddress,fullName;
        @Override
        protected String doInBackground(String... emailID) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(TagClass.LOGIN_DATABASE);
            ArrayList <String>arrayListUsernamePassword = new ArrayList();

            try {
                List<ParseObject> objects = query.find();
                for (ParseObject parseObject : objects) {
                    if(parseObject.get(TagClass.LOGIN_EMAILADDRESS).equals(emailID[0]))
                    {
                        arrayListUsernamePassword = (ArrayList<String>)parseObject.get(TagClass.LOGIN_USERNAMEPASSSWORD);
                        emailAddress = (String)parseObject.get(TagClass.LOGIN_EMAILADDRESS);
                        fullName = (String)parseObject.get(TagClass.LOGIN_FIRSTNAME) + parseObject.get(TagClass.LOGIN_LASTNAME);
                        idFound = true;
                        break;
                    }
                }
                if(idFound)
                {
                    try {
                        GMailSender sender = new GMailSender(TagClass.DEVELOPER_EMAIL, TagClass.DEVELOPER_PASSWORD);
                        sender.sendMail(TagClass.FORGOT_CREDENTIALS_SUBJECT,
                                "Hi" + fullName + "\n\n" + TagClass.FORGOT_CREDENTIALS_BODY_USER + arrayListUsernamePassword.get(0)
                                        + "\n" + TagClass.FORGOT_CREDENTIALS_BODY_PASSWORD + arrayListUsernamePassword.get(1) +
                                        TagClass.FORGOT_CREDENTIALS_BODY_ENDING, TagClass.DEVELOPER_EMAIL,emailAddress );
                    }
                    catch(Exception e)
                    {
                        Log.e(TagClass.EXCEPTIONCATCH,"",e);
                        MyUtilities.showSnackBar(ForgotPasswordUsernameActivity.this,TagClass.ERROR_EMAIL_IMPROPER);
                    }
                }
            }
            catch(ParseException e)
            {
                Log.e(TagClass.EXCEPTIONCATCH,"",e);
            }
            return emailID[0];
        }

        @Override
        public void onPostExecute(String emailID)
        {
            if(idFound)
            {
                ForgotPasswordUsernameActivity.this.finish();
                MyUtilities.showSnackBar(ForgotPasswordUsernameActivity.this, TagClass.ERROR_EMAIL_IMPROPER);
            }
        }

    }
}
