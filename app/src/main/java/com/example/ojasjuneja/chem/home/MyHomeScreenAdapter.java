package com.example.ojasjuneja.chem.home;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.ojasjuneja.chem.TagClass;

import java.util.Locale;

/**
 * Created by Ojas Juneja on 7/27/2015.
 */
public class MyHomeScreenAdapter extends FragmentStatePagerAdapter {

    private Bundle bundle;

   public MyHomeScreenAdapter(FragmentManager fm,Bundle bundle)
   {
      super(fm);
      this.bundle = bundle;
   }


    @Override
    public Fragment getItem(int position) {
        return MyHomeScreenFragment.newInstanse(position,bundle);
    }

    @Override
    public int getCount() {
        return TagClass.TABS;
    }

    @Override
    public CharSequence getPageTitle(int position)
    {
        Locale l = Locale.getDefault();
        String name;
        switch(position) {
            case 0:  name = TagClass.FLASHCARDS ;
                break;
            case 1: name = TagClass.MYLISTS ;
                break;
            case 2: name = TagClass.MYACCOUNT;
                break;
            default:
                name = TagClass.FLASHCARDS;
        }
        return name.toUpperCase(l);

    }
}
