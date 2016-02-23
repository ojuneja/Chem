package com.example.ojasjuneja.chem.flashcards;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ojasjuneja.chem.R;

/**
 * Created by Ojas Juneja on 8/25/2015.
 */
public class MakeFlashCardsAdaptor extends RecyclerView.Adapter<MakeFlashCardsAdaptor.ViewHolder> {

    private int noOfItems = 0;

    void updateData(int noOfItems)
    {
      this.noOfItems = noOfItems;
    }

    @Override
    public MakeFlashCardsAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_make_flash_cards_row,parent));
    }

    @Override
    public void onBindViewHolder(MakeFlashCardsAdaptor.ViewHolder holder, int position) {
        holder.bindData();
    }

    @Override
    public int getItemCount() {
        return noOfItems;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewCompounds;
        ImageView imageViewCompounds;

        public ViewHolder(View v){
            super(v);
            textViewCompounds = (TextView)v.findViewById(R.id.textView_compounds);
            imageViewCompounds = (ImageView)v.findViewById(R.id.imageView_compounds);

        }

        void bindData()
        {

        }
    }
}
