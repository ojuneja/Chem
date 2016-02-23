package com.example.ojasjuneja.chem.myaccount;

import android.graphics.Bitmap;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ojas Juneja on 7/28/2015.
 */
public class MyAccountData {

    private List<Map<String,?>> myAccountList;
    private String strFullName = "";
    private Bitmap profilePic;
    public List<Map<String, ?>> getMyAccountDataList() {
        return myAccountList;
    }

    public MyAccountData(String strFullName,Bitmap profilePic)
    {
         this.strFullName = strFullName;
         this.profilePic = profilePic;
    }

    public void generateData()
    {
        myAccountList = new ArrayList<>();
        HashMap item = new HashMap();
        item.put(TagClass.MYACCOUNT_TYPE, GlobalVariables.ACCOUNTTYPE1);
        item.put(TagClass.MYACCOUNT_IMAGE,profilePic);
        item.put(TagClass.LOGIN_FULLNAME,strFullName);
        item.put(TagClass.MYACCOUNT_DOWNARROW, R.drawable.down_arrow);
        myAccountList.add(item);
        item = new HashMap();
        item.put(TagClass.MYACCOUNT_TYPE,GlobalVariables.ACCOUNTTYPE2);
        item.put(TagClass.MYACCOUNT_TITLE,TagClass.OVERALL_PERFORMANCE + " " +TagClass.STATISTICS);
        item.put(TagClass.MYACCOUNT_IMAGE, R.drawable.performance);
        myAccountList.add(item);
        item = new HashMap();
        item.put(TagClass.MYACCOUNT_TYPE,GlobalVariables.ACCOUNTTYPE2);
        item.put(TagClass.MYACCOUNT_TITLE,TagClass.REPORT_BUG);
        item.put(TagClass.MYACCOUNT_IMAGE, R.drawable.bug);
        myAccountList.add(item);
        item = new HashMap();
        item.put(TagClass.MYACCOUNT_TYPE,GlobalVariables.ACCOUNTTYPE2);
        item.put(TagClass.MYACCOUNT_TITLE,TagClass.REFER_FRIEND);
        item.put(TagClass.MYACCOUNT_IMAGE, R.drawable.refer);
        myAccountList.add(item);
        item = new HashMap();
        item.put(TagClass.MYACCOUNT_TYPE,GlobalVariables.ACCOUNTTYPE2);
        item.put(TagClass.MYACCOUNT_TITLE,TagClass.PRIVACY_POLICY);
        item.put(TagClass.MYACCOUNT_IMAGE, R.drawable.privacy);
        myAccountList.add(item);
        Object[] object = GlobalVariables.hashMapMyListsName.keySet().toArray();
        for(int i=0;i<object.length;i++)
        {
            refreshData((String)object[i],TagClass.ADD);
        }
    }

    public void refreshData(String listName,String operation) {
        if(operation.equals(TagClass.DELETE))
        {
            myAccountList.remove(listName);
        }
        else
        {
            HashMap item = new HashMap();
            item.put(TagClass.MYACCOUNT_TYPE, GlobalVariables.ACCOUNTTYPE2);
            item.put(TagClass.MYACCOUNT_TITLE, listName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[0] + TagClass.PERFORMANCE);
            item.put(TagClass.MYACCOUNT_IMAGE, R.drawable.progress);
            myAccountList.add(item);
        }
    }

}
