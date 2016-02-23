package com.example.ojasjuneja.chem.flashcards;

import android.app.Activity;
import android.content.Intent;
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
import com.gc.materialdesign.views.ProgressBarDeterminate;

import java.util.ArrayList;

/**
 * Created by Ojas Juneja on 8/7/2015.
 */
public class FlashCardsAdaptor extends RecyclerView.Adapter<FlashCardsAdaptor.ViewHolder> {

    private static int index = 0;
    private String compoundType;
    private ArrayList<String> arrayListCompounds;
    private int fragmentInfo;
    private Activity context;
    private int level = GlobalVariables.LEVEL_SKIP;
    private int correctCount = 0,incorrectCount = 0,skippedCount = 0;
    private Bitmap bitmap;
    private LruCache lruCache;


    public FlashCardsAdaptor(Activity context,int fragmentInfo,ArrayList<String> arrayListCompounds,LruCache lruCache,String compoundType)
    {
        this.context = context;
        this.lruCache = lruCache;
        this.fragmentInfo = fragmentInfo;
        this.arrayListCompounds = arrayListCompounds;
        this.compoundType = compoundType;
    }


    @Override
    public FlashCardsAdaptor.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
       return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.fragment_flashcards_row,viewGroup,false));
    }

    public void putData(ArrayList<String> arrayListCompounds,int fragmentInfo,String compoundType)
    {
       this.arrayListCompounds = arrayListCompounds;
        this.fragmentInfo = fragmentInfo;
        this.compoundType = compoundType;
    }

    public static void increaseIndex()
    {
        ++index;
    }

    public void putLevel(int level)
    {
        this.level = level;
    }


    @Override
    public void onBindViewHolder(final FlashCardsAdaptor.ViewHolder holder, int position) {
        holder.bindData();
        holder.textViewTap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index < arrayListCompounds.size()) {
                    Intent intent = new Intent(context, FlashCardsMeaningActivity.class);
                    if (fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                        intent.putExtra(TagClass.COMPOUND_TYPE, compoundType);
                        intent.putExtra(TagClass.TYPE_SYMBOL_DEFINITION, TagClass.KEY_DEFINITION);
                        intent.putExtra(TagClass.KEY_SYMBOL, arrayListCompounds.get(index));
                        if(compoundType.equals(TagClass.INORGANIC_COMPOUNDS))
                        intent.putExtra(TagClass.KEY_DEFINITION, GlobalVariables.hashMapCompoundsData.get(arrayListCompounds.get(index)));
                        else
                        intent.putExtra(TagClass.KEY_DEFINITION, GlobalVariables.hashMapCompoundsDataOrganic.get(arrayListCompounds.get(index)));
                    } else {
                        intent.putExtra(TagClass.COMPOUND_TYPE, compoundType);
                        intent.putExtra(TagClass.TYPE_SYMBOL_DEFINITION, TagClass.KEY_SYMBOL);
                        if(compoundType.equals(TagClass.INORGANIC_COMPOUNDS))
                        intent.putExtra(TagClass.KEY_SYMBOL, GlobalVariables.hashMapCompoundsDataReverse.get(arrayListCompounds.get(index)));
                        else
                        intent.putExtra(TagClass.KEY_SYMBOL, GlobalVariables.hashMapCompoundsDataReverseOrganic.get(arrayListCompounds.get(index)));
                        intent.putExtra(TagClass.KEY_DEFINITION, arrayListCompounds.get(index));
                    }
                    context.startActivityForResult(intent, GlobalVariables.REQUEST_CODE_LEVEL);
                }
            }
        });
        holder.imageNext.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                increaseIndex();
                if (index > arrayListCompounds.size() - 1) {
                    holder.imageViewReload.setImageResource(R.drawable.reload);
                    holder.imageViewCompounds.setImageBitmap(null);
                    holder.textViewCompounds.setText("");
                    MyUtilities.showSnackBar(context,TagClass.PERFORMANCE_MESSAGE);
                }
                else {
                    holder.textViewCompounds.setText("");
                    if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT && compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
                    {
                        bitmap = (Bitmap) lruCache.get(arrayListCompounds.get(index));
                        if(bitmap == null)
                        {
                            DownloadImageCompound downloadImageCompound = new DownloadImageCompound(holder.imageViewCompounds,lruCache);
                            downloadImageCompound.execute(new String[]{arrayListCompounds.get(index)});
                        }
                        else
                            holder.imageViewCompounds.setImageBitmap((Bitmap)lruCache.get(arrayListCompounds.get(index)));
                    }
                    else if (fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                        holder.imageViewCompounds.setImageBitmap(null);
                        holder.imageViewReload.setImageBitmap(null);
                        holder.textViewCompounds.setText(arrayListCompounds.get(index));
                    } else {
                        holder.imageViewCompounds.setImageBitmap(null);
                        holder.imageViewReload.setImageBitmap(null);
                        holder.textViewCompounds.setText(arrayListCompounds.get(index));
                    }

                }
                if(index <= arrayListCompounds.size())
                {
                    ++skippedCount;
                    holder.textViewSkipped.setText(TagClass.SKIPPED_COMPOUNDS + skippedCount + TagClass.OUT_OF + arrayListCompounds.size() +  TagClass.COMPOUNDS);
                    holder.progressBarDeterminateSkipped.setProgress(skippedCount);
                }

            }
        });

        if(holder.imageViewReload != null)
        {
            holder.imageViewReload.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    putData(arrayListCompounds, fragmentInfo, compoundType);
                    notifyDataSetChanged();
                    index = 0;
                    correctCount = 0;
                    incorrectCount = 0;
                    skippedCount = 0;
                    holder.progressBarDeterminateCorrect.setProgress(0);
                    holder.progressBarDeterminateSkipped.setProgress(0);
                    holder.progressBarDeterminateInCorrect.setProgress(0);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return GlobalVariables.FLASH_CARD_SIZE;
    }


    class ViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageNext,imageViewCompounds,imageViewReload;
        TextView textViewCompounds;
        TextView textViewTap,textViewCorrect,textViewInCorrect,textViewSkipped;
        ProgressBarDeterminate progressBarDeterminateCorrect,progressBarDeterminateInCorrect,progressBarDeterminateSkipped;
        public ViewHolder(View itemView) {
            super(itemView);
            imageNext = (ImageView)itemView.findViewById(R.id.image_right_arrow);
            textViewTap = (TextView)itemView.findViewById(R.id.text_solution);
            imageViewCompounds =  (ImageView)itemView.findViewById(R.id.image_compounds);
            textViewCompounds = (TextView)itemView.findViewById(R.id.text_compounds);
            textViewCorrect = (TextView)itemView.findViewById(R.id.text_correctly);
            textViewInCorrect = (TextView)itemView.findViewById(R.id.text_incorrect);
            textViewSkipped = (TextView)itemView.findViewById(R.id.text_skipped);
            imageViewReload = (ImageView)itemView.findViewById(R.id.image_reload);
            progressBarDeterminateCorrect = (ProgressBarDeterminate)itemView.findViewById(R.id.progressDeterminate_correct);
            progressBarDeterminateInCorrect = (ProgressBarDeterminate)itemView.findViewById(R.id.progressDeterminate_incorrect);
            progressBarDeterminateSkipped = (ProgressBarDeterminate)itemView.findViewById(R.id.progressDeterminate_skipped);
            progressBarDeterminateCorrect.setMin(0);
            progressBarDeterminateInCorrect.setMin(0);
            progressBarDeterminateSkipped.setMin(0);
            progressBarDeterminateCorrect.setMax(arrayListCompounds.size());
            progressBarDeterminateInCorrect.setMax(arrayListCompounds.size());
            progressBarDeterminateSkipped.setMax(arrayListCompounds.size());

        }

        void bindData() {
            if(index == 0)
            {
                progressBarDeterminateCorrect.setMin(0);
                progressBarDeterminateInCorrect.setMin(0);
                progressBarDeterminateSkipped.setMin(0);
                progressBarDeterminateCorrect.setMax(arrayListCompounds.size());
                progressBarDeterminateInCorrect.setMax(arrayListCompounds.size());
                progressBarDeterminateSkipped.setMax(arrayListCompounds.size());
            }
            if (index > arrayListCompounds.size() - 1) {
                imageViewReload.setImageResource(R.drawable.reload);
                imageViewCompounds.setImageBitmap(null);
                textViewCompounds.setText("");
                MyUtilities.showSnackBar(context, TagClass.PERFORMANCE_MESSAGE);
            } else {
                if(fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT && compoundType.equals(TagClass.ORGANIC_COMPOUNDS))
                {
                    textViewCompounds.setText("");
                    imageViewReload.setImageBitmap(null);
                    bitmap = (Bitmap) lruCache.get(arrayListCompounds.get(index));
                    if(bitmap == null)
                    {
                        DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageViewCompounds,lruCache);
                        downloadImageCompound.execute(new String[]{arrayListCompounds.get(index)});
                    }
                    else
                        imageViewCompounds.setImageBitmap((Bitmap)lruCache.get(arrayListCompounds.get(index)));
                }
                else if (fragmentInfo == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                    imageViewCompounds.setImageBitmap(null);
                    imageViewReload.setImageBitmap(null);
                    textViewCompounds.setText(MyUtilities.chemicalNotation(arrayListCompounds.get(index)));
                } else {
                    imageViewCompounds.setImageBitmap(null);
                    imageViewReload.setImageBitmap(null);
                    textViewCompounds.setText(arrayListCompounds.get(index));
                }
            }
            if (index <= arrayListCompounds.size()) {
                    if (level == GlobalVariables.LEVEL_CORRECT) {
                        ++correctCount;
                        textViewCorrect.setText(TagClass.CORRECT_COMPOUNDS + correctCount + TagClass.OUT_OF + arrayListCompounds.size() + TagClass.COMPOUNDS);
                        progressBarDeterminateCorrect.setProgress(correctCount);
                    } else if (level == GlobalVariables.LEVEL_WRONG) {
                        ++incorrectCount;
                        textViewInCorrect.setText(TagClass.INCORRECT_COMPOUNDS + incorrectCount + TagClass.OUT_OF + arrayListCompounds.size() + TagClass.COMPOUNDS);
                        progressBarDeterminateInCorrect.setProgress(incorrectCount);
                    }
                   else {
                    textViewCorrect.setText(TagClass.CORRECT_COMPOUNDS + correctCount + TagClass.OUT_OF + arrayListCompounds.size() + TagClass.COMPOUNDS);
                    textViewInCorrect.setText(TagClass.INCORRECT_COMPOUNDS + incorrectCount + TagClass.OUT_OF + arrayListCompounds.size() + TagClass.COMPOUNDS);
                    textViewSkipped.setText(TagClass.SKIPPED_COMPOUNDS + skippedCount + TagClass.OUT_OF + arrayListCompounds.size() + TagClass.COMPOUNDS);
                }
            }
        }

    }
}
