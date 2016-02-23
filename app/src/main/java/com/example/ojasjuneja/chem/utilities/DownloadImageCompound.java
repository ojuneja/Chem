package com.example.ojasjuneja.chem.utilities;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

/**
 * Created by Ojas Juneja on 8/11/2015.
 */
public class DownloadImageCompound extends AsyncTask<String,Void,Bitmap>
{
    private WeakReference<ImageView> imageViewWeakReference;
    private LruCache  lruCache;
    public DownloadImageCompound(ImageView imageView,LruCache lruCache)
    {
        imageViewWeakReference = new WeakReference<>(imageView);
        this.lruCache = lruCache;
    }
    @Override
    protected Bitmap doInBackground(String... urls) {
        Bitmap bitmapImage = null;
        MyUtilities myUtilities =  new MyUtilities();
        for(String url:urls)
        {
            bitmapImage = myUtilities.downloadImage(url);
            if(bitmapImage!=null)
            {
                lruCache.put(url,bitmapImage);
            }
        }
        return bitmapImage;
    }

    @Override
    protected void onPostExecute(Bitmap bitmapImage)
    {
        ImageView imageView = imageViewWeakReference.get();
        if(imageView!=null)
        imageView.setImageBitmap(bitmapImage);
    }
}