package com.example.ojasjuneja.chem.flashcards;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.DownloadImageCompound;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyUtilities;

/**
 * Created by Ojas Juneja on 8/3/2015.
 */
public class FlashCardsMeaningFragment extends Fragment {

    private String compoundType;
    private String compoundName,compoundNameDefinition;
    private String type;
    private LruCache lruCacheImage;
    private Bitmap bitmapImage;

public static FlashCardsMeaningFragment newInstanse(String compoundNameDefinition,String compoundName,String type,String compoundType)
{
    Bundle args = new Bundle();
    args.putString(TagClass.COMPOUND_NAME_DEFINITION, compoundNameDefinition);
    args.putString(TagClass.COMPOUND_NAME, compoundName);
    args.putString(TagClass.TYPE_SYMBOL_DEFINITION, type);
    args.putString(TagClass.COMPOUND_TYPE, compoundType);
    FlashCardsMeaningFragment flashCardsMeaning =  new FlashCardsMeaningFragment();
    flashCardsMeaning.setArguments(args);
    return flashCardsMeaning;
}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);
        Bundle args = getArguments();
        type = (String)args.get(TagClass.TYPE_SYMBOL_DEFINITION);
        compoundName = (String)args.get(TagClass.COMPOUND_NAME);
        compoundType = (String)args.get(TagClass.COMPOUND_TYPE);
        compoundNameDefinition = (String)args.get(TagClass.COMPOUND_NAME_DEFINITION);
        lruCacheImage = LRUCacheClass.getCache();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanse) {
        View rootView = inflater.inflate(R.layout.fragment_flashcards_meaning,container,false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.image_compound_meaning);
        TextView textViewCompoundName = (TextView)rootView.findViewById(R.id.text_compound_meaning);
        TextView textCorrect = (TextView)rootView.findViewById(R.id.text_correct);
        TextView textIncorrect = (TextView)rootView.findViewById(R.id.text_incorrect);
        if(type.equals(TagClass.KEY_SYMBOL) && compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
        {
            bitmapImage = (Bitmap) lruCacheImage.get(compoundName);
            if(bitmapImage == null)
            {
                DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageView,lruCacheImage);
                downloadImageCompound.execute(new String[]{compoundName});
            }
            else
                imageView.setImageBitmap((Bitmap)lruCacheImage.get(compoundName));
        }
        else if(type.equals(TagClass.KEY_SYMBOL))
        textViewCompoundName.setText(MyUtilities.chemicalNotation(compoundName));
        else
        {
          textViewCompoundName.setText(compoundName);
        }
        textCorrect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(TagClass.COLOUMN_NAME_COMPOUNDS,compoundNameDefinition);
                i.putExtra(TagClass.COLOUMN_NAME_LEVEL, GlobalVariables.LEVEL_CORRECT);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });
        textIncorrect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent();
                i.putExtra(TagClass.COLOUMN_NAME_COMPOUNDS,compoundNameDefinition);
                i.putExtra(TagClass.COLOUMN_NAME_LEVEL, GlobalVariables.LEVEL_WRONG);
                getActivity().setResult(Activity.RESULT_OK, i);
                getActivity().finish();
            }
        });
        return rootView;
    }



    }
