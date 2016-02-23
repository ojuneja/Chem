package com.example.ojasjuneja.chem.myaccount;

import android.graphics.Bitmap;
import android.graphics.Color;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.PercentFormatter;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Created by Ojas Juneja on 8/13/2015.
 */
public class MyPerformanceAdaptor extends RecyclerView.Adapter<MyPerformanceAdaptor.ViewHolder> {

    private HashMap<String,Integer> hashMapPerformance;
    private HashMap<String,String> hashMapOverallPerformance;
    private Object []objectCompounds;
    private ArrayList<Entry> arrayListYValues;
    private ArrayList<String> arrayListXValues;
    private ArrayList<Integer> colors;
    private String listName;
    private LruCache lruCache;
    private Bitmap bitmap;
    private int level;
    private String type = "";
    private ArrayList<String> arrayListOverallPerformance;

    MyPerformanceAdaptor(LruCache lruCache)
    {
        hashMapPerformance = new HashMap<>();
        this.lruCache = lruCache;
    }

    void putData(HashMap<String,Integer> hashMapPerformance, HashMap<String,String> hashMapOverallPerformance,ArrayList<String> arrayListXValues,ArrayList<Entry> arrayListYValues,
                 ArrayList<Integer> colors,String listName,String type,ArrayList<String> arrayListOverallPerformance)
    {
        this.type = type;
        this.hashMapOverallPerformance = hashMapOverallPerformance;
        this.hashMapPerformance = hashMapPerformance;
        this.listName = listName;
        this.colors = colors;
        this.arrayListYValues = arrayListYValues;
        this.arrayListXValues  = arrayListXValues;
        if(type.equals(TagClass.OVERALL_PERFORMANCE)) {
            objectCompounds = hashMapOverallPerformance.keySet().toArray();
            this.arrayListOverallPerformance = arrayListOverallPerformance;
        }
        else
        {
            objectCompounds = hashMapPerformance.keySet().toArray();
        }

    }

    @Override
    public MyPerformanceAdaptor.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch(viewType)
        {
            case 0:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_performance_row_type_one,parent,false);
                break;
            default:
                if(type.equals(TagClass.OVERALL_PERFORMANCE) && viewType == 1)
                {
                 view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_performance_row_type_three,parent,false);
                }
                else
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_my_performance_row_type_two,parent,false);
        }

        return new ViewHolder(view,viewType);
    }

    @Override
    public void onBindViewHolder(MyPerformanceAdaptor.ViewHolder holder, int position) {
        holder.bindData(position);
    }

    @Override
    public int getItemCount() {
        if(type.equals(TagClass.OVERALL_PERFORMANCE)) {
            return hashMapOverallPerformance.size() + GlobalVariables.EXTRA_POSITION + GlobalVariables.EXTRA_POSITION;
        }
        else if(type.equals(TagClass.LIST_NAME)) {
            return hashMapPerformance.size() + GlobalVariables.EXTRA_POSITION;
        }
        else
            return 0;
    }

    @Override
    public int getItemViewType(int position)
    {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder
    {
        TextView textViewCompound,textViewPerformance,textViewAttentionMessage;
        ImageView imageViewCompounds;
        PieChart pieChart;
        int viewType;
        public ViewHolder(View itemView,int viewType) {
            super(itemView);
            this.viewType = viewType;
            pieChart = (PieChart)itemView.findViewById(R.id.pieChart);
            textViewCompound = (TextView)itemView.findViewById(R.id.textView_compound_performance);
            textViewPerformance = (TextView)itemView.findViewById(R.id.textView_level);
            textViewAttentionMessage = (TextView)itemView.findViewById(R.id.textView_compound_overall_performance);
            imageViewCompounds = (ImageView)itemView.findViewById(R.id.imageView_compound);


        }

        void overallPerformance(int position)
        {
            if (GlobalVariables.hashMapMyListsCompoundType.get(arrayListOverallPerformance.get(position-2)).equals(TagClass.ORGANIC_COMPOUNDS)
                    && GlobalVariables.hashMapMyListsName.get(arrayListOverallPerformance.get(position-2)) == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                String compound = (String) objectCompounds[position - 2];
                bitmap = (Bitmap) lruCache.get(compound);
                if (bitmap == null) {
                    DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageViewCompounds, lruCache);
                    downloadImageCompound.execute(new String[]{compound});
                } else
                    imageViewCompounds.setImageBitmap((Bitmap) lruCache.get(compound));
            } else if (GlobalVariables.hashMapMyListsName.get(arrayListOverallPerformance.get(position-2)) == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                textViewCompound.setText(MyUtilities.chemicalNotation((String) objectCompounds[position - 2]));
            } else {
                textViewCompound.setText((String) objectCompounds[position - 2]);
            }
            textViewPerformance.setText(hashMapOverallPerformance.get(objectCompounds[position - 2]));
            textViewPerformance.setTextColor(Color.RED);
        }

        void listPerformance(int position)
        {
            if (GlobalVariables.hashMapMyListsCompoundType.get(listName).equals(TagClass.ORGANIC_COMPOUNDS)
                    && GlobalVariables.hashMapMyListsName.get(listName) == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                String compound = (String) objectCompounds[position - 1];
                bitmap = (Bitmap) lruCache.get(compound);
                if (bitmap == null) {
                    DownloadImageCompound downloadImageCompound = new DownloadImageCompound(imageViewCompounds, lruCache);
                    downloadImageCompound.execute(new String[]{compound});
                } else
                    imageViewCompounds.setImageBitmap((Bitmap) lruCache.get(compound));
            } else if (GlobalVariables.hashMapMyListsName.get(listName) == GlobalVariables.LOAD_SYMBOL_FRAGMENT) {
                textViewCompound.setText(MyUtilities.chemicalNotation((String) objectCompounds[position - 1]));
            } else {
                textViewCompound.setText((String) objectCompounds[position - 1]);
            }
            level = hashMapPerformance.get(objectCompounds[position - 1]);
            if (level == GlobalVariables.LEVEL_CORRECT) {
                textViewPerformance.setText(TagClass.CORRECT);
                textViewPerformance.setTextColor(Color.parseColor("#8BC34A"));
            } else if (level == GlobalVariables.LEVEL_WRONG) {
                textViewPerformance.setText(TagClass.WRONG);
                textViewPerformance.setTextColor(Color.RED);
            } else {
                textViewPerformance.setText(TagClass.SKIPPED);
                textViewPerformance.setTextColor(Color.DKGRAY);
            }
        }

        void bindData(int position)
        {
            if(hashMapPerformance.size() > 1) {
                if (viewType == 0) {
                    PieDataSet dataSet = new PieDataSet(arrayListYValues, "");
                    dataSet.setSliceSpace(3f);
                    dataSet.setSelectionShift(5f);
                    dataSet.setColors(colors);
                    PieData data = new PieData(arrayListXValues, dataSet);
                    data.setValueFormatter(new PercentFormatter());
                    data.setValueTextSize(11f);
                    data.setValueTextColor(Color.WHITE);
                    pieChart.animateY(1500, Easing.EasingOption.EaseInOutQuad);
                    pieChart.setData(data);
                    pieChart.setUsePercentValues(true);
                    pieChart.setDrawHoleEnabled(true);
                    pieChart.setHoleColorTransparent(true);
                    pieChart.highlightValues(null);
                    pieChart.invalidate();
                } else if (type.equals(TagClass.OVERALL_PERFORMANCE) && viewType == 1) {
                    textViewAttentionMessage.setText(TagClass.ATTENTION);
                }else if(type.equals(TagClass.OVERALL_PERFORMANCE))
                {
                    overallPerformance(position);
                }
                else {
                    listPerformance(position);
                }
            }
        }
    }

}
