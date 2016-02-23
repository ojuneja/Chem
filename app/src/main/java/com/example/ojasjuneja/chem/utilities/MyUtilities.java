package com.example.ojasjuneja.chem.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.RelativeSizeSpan;
import android.text.style.SubscriptSpan;
import android.util.Log;
import android.view.View;

import com.example.ojasjuneja.chem.TagClass;
import com.gc.materialdesign.widgets.ProgressDialog;
import com.gc.materialdesign.widgets.SnackBar;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Ojas Juneja on 7/29/2015.
 */
public class MyUtilities {

    private static ProgressDialog progressDialog = null;
    private static ArrayList<String> arrayList = new ArrayList();
    private static HashMap<String,String> hashMap = new HashMap<>();
    private static SnackBar snackBar;

    // Download an image from online
    public Bitmap downloadImage(String url) {
        Bitmap bitmap = null;

        InputStream stream = getHttpConnection(url);
        if(stream!=null) {
            bitmap = BitmapFactory.decodeStream(stream);
            try {
                stream.close();
            }catch (IOException e1) {
                Log.e(TagClass.EXCEPTIONCATCH, "",e1);
                e1.printStackTrace();
            }
        }

        return bitmap;
    }

    public static byte[] serializeHashMap(HashMap hashMap)
    {
        byte[] yourBytes = null;
        try {
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(byteOut);
            out.writeObject(hashMap);
            yourBytes = byteOut.toByteArray();
        }
        catch(IOException e)
        {
            Log.e(TagClass.EXCEPTIONCATCH, "", e);
        }
        return yourBytes;
    }


    // Makes HttpURLConnection and returns InputStream
    public static InputStream getHttpConnection(String urlString) {
        InputStream stream = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.connect();
            if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                stream = httpConnection.getInputStream();
            }
        }  catch (UnknownHostException e1) {
            Log.d(TagClass.EXCEPTIONCATCH, "",e1);
            e1.printStackTrace();
        } catch (Exception ex) {
            Log.d(TagClass.EXCEPTIONCATCH, "",ex);
            ex.printStackTrace();
        }
        return stream;
    }

    public static SpannableStringBuilder chemicalNotation(String strCompound)
    {
        ArrayList arrayListPosition = findPositionNumbers(strCompound);
        SpannableStringBuilder cs = new SpannableStringBuilder(strCompound);//Your suppose String
        for(int i = 0;i<arrayListPosition.size();i++) {
            cs.setSpan(new SubscriptSpan(), (Integer)arrayListPosition.get(i), (Integer)arrayListPosition.get(i) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);//Setting span index
            cs.setSpan(new RelativeSizeSpan((float) 0.5), (Integer)arrayListPosition.get(i), (Integer)arrayListPosition.get(i) + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return cs;
    }

    public static ArrayList findPositionNumbers(String strCompound)
    {
        ArrayList <Integer>arrayListPosition = new ArrayList();
        for(int i = 0;i < strCompound.length();i++)
        {
            int value = strCompound.charAt(i) - '0';
            if(value == -2 || value == -5 || value == -3)
            {
                arrayListPosition.clear();
                break;
            }
            if(value>=0 && value <=9)
            {
                arrayListPosition.add(i);
            }

        }
        return arrayListPosition;
    }

    public static void progressDialog(Activity activity,String message)
    {
        progressDialog = new ProgressDialog(activity,message);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    public static void dismiss()
    {
        if(progressDialog !=null)
        progressDialog.dismiss();
    }

    public static void showSnackBar(Activity activity,String message)
    {
         snackBar = new SnackBar(activity,message,"CLOSE", new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                snackBar.dismiss();
            }
        });
        snackBar.show();
    }

    public static void setDeletedListsName(String listsName)
    {
        arrayList.add(listsName);
    }

    public static ArrayList<String> getDeletedListsName()
    {
        return arrayList;
    }
    public static void setDeletedCompoundsName(String compoundName,String listName)
    {
        hashMap.put(compoundName,listName);
    }

    public static HashMap<String,String> getDeletedCompoundsName()
    {
        return hashMap;
    }


    public static boolean isOnline(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in air plan mode it will be null
        return (netInfo != null && netInfo.isConnected());

    }

    public static String makePath()
    {
        File filepath = Environment.getExternalStorageDirectory();
        // Create a new folder in SD Card
        File dir = new File(filepath.getAbsolutePath()
                + "/ChemFlashcardsIMG/");
        dir.mkdirs();
        return dir.toString();
    }
}
