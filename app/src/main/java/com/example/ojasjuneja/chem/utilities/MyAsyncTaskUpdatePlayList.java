package com.example.ojasjuneja.chem.utilities;

import android.os.AsyncTask;
import android.util.Log;

import com.example.ojasjuneja.chem.TagClass;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

/**
 * Created by Ojas Juneja on 8/20/2015.
 */
public class MyAsyncTaskUpdatePlayList extends AsyncTask<String,Void,String> {

    private boolean shuffleFlag = false;
    public MyAsyncTaskUpdatePlayList(boolean shuffleFlag)
    {
        this.shuffleFlag =  shuffleFlag;
    }
    @Override
    protected String doInBackground(String... usernameAndPlaylist) {
        try {
            if (!usernameAndPlaylist[1].equals("")) {
                ParseQuery<ParseObject> queryPlayListTable = ParseQuery.getQuery(TagClass.CURRENT_PLAYLIST);
                List<ParseObject> objectPlayListNames = queryPlayListTable.find();
                if (objectPlayListNames.size() > 0) {
                    for (ParseObject objectPlayList : objectPlayListNames) {
                        if (objectPlayList.get(TagClass.LOGIN_USERNAME).equals(usernameAndPlaylist[0])) {
                            objectPlayList.put(TagClass.LOGIN_USERNAME, usernameAndPlaylist[0]);
                            objectPlayList.put(TagClass.CURRENT_PLAY_LIST, usernameAndPlaylist[1]);
                            objectPlayList.put(TagClass.SHUFFLE_FLAG, shuffleFlag);
                            objectPlayList.saveInBackground();
                            return null;
                        }
                    }
                } else {
                    ParseObject parseObject = new ParseObject(TagClass.CURRENT_PLAYLIST);
                    parseObject.put(TagClass.LOGIN_USERNAME, usernameAndPlaylist[0]);
                    parseObject.put(TagClass.CURRENT_PLAY_LIST, usernameAndPlaylist[1]);
                    parseObject.put(TagClass.SHUFFLE_FLAG, shuffleFlag);
                    parseObject.saveInBackground();
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TagClass.EXCEPTIONCATCH,"",e);
        }
        return null;
    }

}
