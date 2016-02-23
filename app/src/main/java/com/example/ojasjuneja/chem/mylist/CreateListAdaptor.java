package com.example.ojasjuneja.chem.mylist;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.DownloadImageCompound;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

import java.util.HashMap;

/**
 * Created by Ojas Juneja on 8/4/2015.
 */
public class CreateListAdaptor extends RecyclerView.Adapter<CreateListAdaptor.ViewHolder> {

    private int fragmentInfo;
    protected static HashMap hashMapCheckBox = new HashMap();
    private HashMap <String,String>hashMapCompounds;
    private boolean editableFlag;
    private Object[] object;
    private String compoundType;
    private LruCache lruCache;
    private Bitmap bitmapImage;

    CreateListAdaptor(LruCache lruCache,int fragmentInfo,boolean editableFlag,HashMap<String,String> hashMapCompounds,String compoundType) {
        this.fragmentInfo = fragmentInfo;
        this.editableFlag = editableFlag;
        this.hashMapCompounds = hashMapCompounds;
        this.compoundType = compoundType;
        this.lruCache = lruCache;
    }

    @Override
    public int getItemViewType(int position) {
        return fragmentInfo;
    }

    @Override
    public CreateListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS) && fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_create_list_row_image, parent, false);
        else
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_create_list_row, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CreateListAdaptor.ViewHolder holder, final int position) {
        holder.bindData(position);
          holder.checkBox.setOnClickListener(new View.OnClickListener() {
              @Override
                public void onClick(View v) {
                    boolean checkBoxStatus = false;
                    if (holder.checkBox.isChecked()) {
                        checkBoxStatus = true;
                    }
                   hashMapCheckBox.put(position, checkBoxStatus);
                    holder.checkBox.setChecked(checkBoxStatus);
                    notifyDataSetChanged();
                }
            });

    }

    @Override
    public int getItemCount() {
        if(editableFlag) {
            return hashMapCompounds.size();
        }
        else
        {
            if(compoundType.equals(TagClass.INORGANIC_COMPOUNDS))
            return GlobalVariables.hashMapCompoundsData.size();
            else
             return GlobalVariables.hashMapCompoundsDataOrganic.size();
        }
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView textView;
        ImageView imageView;

        public ViewHolder(View v) {
            super(v);
            checkBox = (CheckBox) v.findViewById(R.id.checkBox_compound);
            if(editableFlag) {
                if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS) && fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT)
                    imageView = (ImageView) v.findViewById(R.id.imageView_compund);
                else
                    textView = (TextView) v.findViewById(R.id.textView_compund);
                object = hashMapCompounds.keySet().toArray();
            }
            else if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS)) {
                if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT) {
                    textView = (TextView) v.findViewById(R.id.textView_compund);
                    object = GlobalVariables.hashMapCompoundsDataOrganic.values().toArray();
                }
                else {
                    imageView = (ImageView) v.findViewById(R.id.imageView_compund);
                    object = GlobalVariables.hashMapCompoundsDataOrganic.keySet().toArray();
                }
                }
            else {
                textView = (TextView) v.findViewById(R.id.textView_compund);
                if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT) {
                    object = GlobalVariables.hashMapCompoundsData.values().toArray();
                }
                else
                    object = GlobalVariables.hashMapCompoundsData.keySet().toArray();
                }
        }

        public void bindData(int position) {
          if(compoundType.equals(TagClass.ORGANIC_COMPOUNDS) && fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
              bitmapImage = (Bitmap) lruCache.get(object[position]);
              if(bitmapImage == null)
              {
                 DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageView,lruCache);
                  downloadImageCompound.execute(new String[]{(String)object[position]});
              }
              else
              imageView.setImageBitmap((Bitmap)lruCache.get(object[position]));
          }
          else if (fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
              textView.setText(MyUtilities.chemicalNotation((String) object[position]));
          }
         else if(fragmentInfo == GlobalVariables.LOAD_DEFINITION_FRAGMENT)
            {
                textView.setText((String)object[position]);
            }
            if (hashMapCheckBox.get(position) == null) {
                checkBox.setChecked(false);
            } else
                checkBox.setChecked((Boolean) hashMapCheckBox.get(position));
        }
    }



}
