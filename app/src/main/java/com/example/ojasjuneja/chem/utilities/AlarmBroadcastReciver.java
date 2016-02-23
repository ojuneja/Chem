package com.example.ojasjuneja.chem.utilities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.example.ojasjuneja.chem.TagClass;

/**
 * Created by Ojas Juneja on 8/17/2015.
 */
public class AlarmBroadcastReciver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(MyUtilities.isOnline(context)) {
            Bundle bundle = intent.getExtras();
            String userName = (String) bundle.get(TagClass.LOGIN_USERNAME);
            MyAsyncTaskUpdateList asyncTaskUpdateList = new MyAsyncTaskUpdateList(context,(String) bundle.get(TagClass.TYPE_BACKGROUND_OPERATION));
            asyncTaskUpdateList.execute(userName);
        }
    }


}