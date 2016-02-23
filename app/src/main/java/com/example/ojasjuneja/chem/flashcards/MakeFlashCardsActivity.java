package com.example.ojasjuneja.chem.flashcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.DividerItemDecoration;
import com.getbase.floatingactionbutton.AddFloatingActionButton;

/**
 * Created by Ojas Juneja on 8/25/2015.
 */
public class MakeFlashCardsActivity extends AppCompatActivity {


    AddFloatingActionButton addFloatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_flash_cards);
        TextView textViewMakeFlashCards = (TextView) findViewById(R.id.make_flashcards_title);
        textViewMakeFlashCards.setText(TagClass.MAKE_FLASH_CARDS);
        RecyclerView recyclerViewMyListDetails = (RecyclerView) findViewById(R.id.recycler_create_list_customized);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerViewMyListDetails.setLayoutManager(linearLayoutManager);
        recyclerViewMyListDetails.addItemDecoration(
                new DividerItemDecoration(this, null, true, true));
        recyclerViewMyListDetails.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewMyListDetails.setHasFixedSize(true);
        MakeFlashCardsAdaptor makeFlashCardsAdaptor = new MakeFlashCardsAdaptor();
        recyclerViewMyListDetails.setAdapter(makeFlashCardsAdaptor);
        addFloatingActionButton = (AddFloatingActionButton)findViewById(R.id.fab_add);
        addFloatingActionButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MakeFlashCardsActivity.this,AddFlashCardsActivity.class);
                startActivityForResult(i, GlobalVariables.REQUEST_CODE_ITEM_ADDED);
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) return;
        if (requestCode == GlobalVariables.REQUEST_CODE_ITEM_ADDED) {

        }
    }

}
