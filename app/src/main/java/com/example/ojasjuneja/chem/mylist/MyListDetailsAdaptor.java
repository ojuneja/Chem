package com.example.ojasjuneja.chem.mylist;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.DownloadImageCompound;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

import java.util.ArrayList;

/**
 * Created by Ojas Juneja on 8/6/2015.
 */
public class MyListDetailsAdaptor extends RecyclerView.Adapter<MyListDetailsAdaptor.ViewHolder> {

    private ArrayList<String> arrayListCompoundsData = new ArrayList<>();
    private int fragmentInfo;
    private OnListItemSelectedInterface onListItemSelectedInterface;
    private String compoundType;
    private Bitmap bitmap;
    private LruCache lruCache;

    MyListDetailsAdaptor(String compoundType,LruCache lruCache)
    {
        this.compoundType = compoundType;
        this.lruCache = lruCache;
    }

    public interface OnListItemSelectedInterface
    {
        void onLongItemClick(int position);
    }

    public void onListItemSelected(OnListItemSelectedInterface onListItemSelectedInterface)
    {
        this.onListItemSelectedInterface = onListItemSelectedInterface;
    }

    void putData(ArrayList<String> arrayListCompoundsData,int fragmentInfo)
    {
        this.arrayListCompoundsData = arrayListCompoundsData;
        this.fragmentInfo = fragmentInfo;
    }


    @Override
    public MyListDetailsAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS) && fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_list_details_row_image,parent,false);
        }
        else
        {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_list_details_row,parent,false);
        }
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyListDetailsAdaptor.ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        return arrayListCompoundsData.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewCompounds;
        ImageView imageViewCompounds;
        public ViewHolder(View itemView) {
            super(itemView);
            if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS) && fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
            {
                imageViewCompounds = (ImageView)itemView.findViewById(R.id.imageView_my_list_details_compound);
            }
             else textViewCompounds = (TextView)itemView.findViewById(R.id.textView_my_list_details_compound);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if(onListItemSelectedInterface!=null)
                    {

                        onListItemSelectedInterface.onLongItemClick(getPosition());
                    }
                    return true;
                }
            });
        }

        void bindData(int position)
        {
            if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT && compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
            {
                bitmap = (Bitmap) lruCache.get(arrayListCompoundsData.get(position));
                if(bitmap == null)
                {
                    DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageViewCompounds,lruCache);
                    downloadImageCompound.execute(new String[]{arrayListCompoundsData.get(position)});
                }
                else
                    imageViewCompounds.setImageBitmap((Bitmap)lruCache.get(arrayListCompoundsData.get(position)));
            }
            else if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
            textViewCompounds.setText(MyUtilities.chemicalNotation(arrayListCompoundsData.get(position)));
            else  if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT)
            {
                textViewCompounds.setText(arrayListCompoundsData.get(position));
            }
        }
    }

}
