package com.example.ojasjuneja.chem.myaccount;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.pkmmte.view.CircularImageView;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;

/**
 * Created by Ojas Juneja on 7/28/2015.
 */
public class MyAccountRecyclerViewAdaptor extends RecyclerView.Adapter<MyAccountRecyclerViewAdaptor.ViewHolder> {


    private Context context;
    private Bitmap bitmapImage;
    private List<Map<String, ?>> listMyAccountData;
    private OnItemClickListener itemClickListener;
    private String facebookImageUrl;
    private LruCache lruCacheImage;
    public interface OnItemClickListener
    {
        void onOverflowItemClick(View v);
        void onListItemSelected(TextView textView);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener)
    {
        itemClickListener = onItemClickListener;
    }

    public MyAccountRecyclerViewAdaptor(Context myContext,List<Map<String, ?>> listMyAccountData,String facebookImageUrl, LruCache lruCacheImage)
    {
        context = myContext;
        this.listMyAccountData = listMyAccountData;
        this.facebookImageUrl = facebookImageUrl;
        this.lruCacheImage = lruCacheImage;
    }



    @Override
    public MyAccountRecyclerViewAdaptor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = null;
       switch(viewType)
       {
           case GlobalVariables.ACCOUNTTYPE1:
               v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_myaccount_typeone,viewGroup,false);
               break;
           case GlobalVariables.ACCOUNTTYPE2:
               v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_myaccount_typetwo,viewGroup,false);
               break;

       }
        return new ViewHolder(v,viewType);
    }

    @Override
    public void onBindViewHolder(MyAccountRecyclerViewAdaptor.ViewHolder viewHolder, int position) {
        Map<String,?> map = listMyAccountData.get(position);
        viewHolder.bindData(map);
    }

    @Override
    public int getItemCount() {
        return listMyAccountData.size();
    }

    @Override
    public int getItemViewType(int position)
    {
        Map<String,?> map = listMyAccountData.get(position);
        return (Integer)map.get(TagClass.MYACCOUNT_TYPE);
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        public View vView;
        public int viewType;
        TextView textViewTitle;
        TextView textViewFullName;
        ImageView imageViewIcon,imageViewDownArrow;
        RelativeLayout relativeLayoutTypeOne;
        CircularImageView circledImageViewPic;
        public ViewHolder(View v,int viewType)
        {
            super(v);
            this.viewType = viewType;
            vView = v;
            textViewTitle = (TextView)vView.findViewById(R.id.text_title);
            textViewFullName = (TextView)vView.findViewById(R.id.text_full_name);
            imageViewIcon = (ImageView)vView.findViewById(R.id.imageView_icon);
            circledImageViewPic = (CircularImageView)vView.findViewById(R.id.circular_Image);
            imageViewDownArrow = (ImageView)vView.findViewById(R.id.image_down_arrow);
            relativeLayoutTypeOne = (RelativeLayout)vView.findViewById(R.id.relativeLayout);
            if(imageViewDownArrow != null)
            {
                imageViewDownArrow.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        if(itemClickListener!=null)
                        {
                            itemClickListener.onOverflowItemClick(imageViewDownArrow);
                        }
                    }
                });
            }
            vView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(itemClickListener!=null)
                    {
                        itemClickListener.onListItemSelected(textViewTitle);
                    }
                }
            });


        }

        void bindData(Map<String,?> myAccountData) {
            switch(viewType)
            {
                case GlobalVariables.ACCOUNTTYPE1:
                    try {
                        String strName = ((String) myAccountData.get(TagClass.LOGIN_FULLNAME)).toUpperCase();
                        textViewFullName.setText(strName);
                        circledImageViewPic.setImageBitmap((Bitmap) myAccountData.get(TagClass.MYACCOUNT_IMAGE));
                        imageViewDownArrow.setImageResource((Integer) myAccountData.get(TagClass.MYACCOUNT_DOWNARROW));
                        if (!facebookImageUrl.equals("")) {
                            bitmapImage = (Bitmap) lruCacheImage.get(facebookImageUrl);
                            if (bitmapImage == null) {
                                DownloadImageFacebook downloadImageFacebook = new DownloadImageFacebook(circledImageViewPic);
                                downloadImageFacebook.execute(new String[]{facebookImageUrl});
                            } else {
                                circledImageViewPic.setImageBitmap(bitmapImage);
                            }
                        }
                        relativeLayoutTypeOne.setBackgroundResource(R.drawable.green_banner);
                    }
                    catch (OutOfMemoryError ee)
                    {
                        Log.e(TagClass.EXCEPTIONCATCH,"",ee);
                    }
                    break;
                case GlobalVariables.ACCOUNTTYPE2:
                    textViewTitle.setText((String) myAccountData.get(TagClass.MYACCOUNT_TITLE));
                    imageViewIcon.setImageResource((Integer) myAccountData.get(TagClass.MYACCOUNT_IMAGE));
                    break;
                default:
                    break;
            }

        }
        }


    private class DownloadImageFacebook extends AsyncTask<String,Void,Bitmap>
    {
        private WeakReference<CircularImageView> circularImageViewWeakReference;
        DownloadImageFacebook(CircularImageView circularImageView)
        {
            circularImageViewWeakReference =  new WeakReference<>(circularImageView);
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
                    lruCacheImage.put(url,bitmapImage);
                }
            }
            return bitmapImage;
        }

        @Override
        protected void onPostExecute(Bitmap bitmapImage)
        {
            CircularImageView circularImageView = circularImageViewWeakReference.get();
            circularImageView.setImageBitmap(bitmapImage);
        }
    }



}
