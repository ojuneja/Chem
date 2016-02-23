package com.example.ojasjuneja.chem.mylist;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;

/**
 * Created by Ojas Juneja on 8/6/2015.
 */
public class MyListAdaptor extends RecyclerView.Adapter<MyListAdaptor.ViewHolder> {

    private OnListItemSelected onListItemSelected;
    public  interface OnListItemSelected
    {
        void onItemClick(TextView textViewMyList);
        void onLongItemClick(TextView textViewMyList,int position);
    }

    public void onListItemSelectedListener(OnListItemSelected onListItemSelected)
    {
        this.onListItemSelected = onListItemSelected;
    }

    @Override
    public MyListAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_mylist_row,parent,false));
    }

    @Override
    public void onBindViewHolder(MyListAdaptor.ViewHolder holder, int position) {
        holder.bindView(position);
    }

    @Override
    public int getItemCount() {
        return GlobalVariables.hashMapMyListsName.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewListName;
        public ViewHolder(View itemView) {
            super(itemView);
            textViewListName = (TextView)itemView.findViewById(R.id.text_list_name);
            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    if(onListItemSelected!=null)
                    {
                        onListItemSelected.onItemClick(textViewListName);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if(onListItemSelected!=null)
                    {
                        onListItemSelected.onLongItemClick(textViewListName,getPosition());
                    }
                    return true;
                }
            });
        }

        public void bindView(int position)
        {
            if(GlobalVariables.hashMapMyListsName.size()>0) {
                String listName = (String) GlobalVariables.hashMapMyListsName.keySet().toArray()[position];
                listName = listName.split(TagClass.DATABASE_LISTNAME_DELIMITER)[0];
                textViewListName.setText(listName);
            }
        }

    }
}
