package com.example.ojasjuneja.chem.flashcards;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;

/**
 * Created by Ojas Juneja on 8/4/2015.
 */
public class FlashCardsMeaningActivity extends AppCompatActivity {

    private String compoundNameAnswer;
    private String compoundName;
    private String compoundType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_cards_meaning);
        Bundle extra = getIntent().getExtras();
        compoundType = (String)extra.get(TagClass.COMPOUND_TYPE);
        if(extra.get(TagClass.TYPE_SYMBOL_DEFINITION).equals(TagClass.KEY_DEFINITION)) {
            compoundNameAnswer = (String) extra.get(TagClass.KEY_DEFINITION);
            compoundName = (String) extra.get(TagClass.KEY_SYMBOL);

        }
        else
        {
         compoundNameAnswer = (String) extra.get(TagClass.KEY_SYMBOL);
         compoundName = (String) extra.get(TagClass.KEY_DEFINITION);
        }
        getFragmentManager().beginTransaction().setCustomAnimations(R.anim.rotate, R.anim.rotate).
                replace(R.id.container, FlashCardsMeaningFragment.newInstanse(compoundName,compoundNameAnswer,(String)extra.get(TagClass.TYPE_SYMBOL_DEFINITION),compoundType)).commit();
    }

    }
