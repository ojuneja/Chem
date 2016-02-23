package com.example.ojasjuneja.chem.utilities;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.TagClass;

/**
 * Created by Ojas Juneja on 8/25/2015.
 */
public class MyDialogCreateList extends DialogFragment
{
    private String textListName;
    private String strUserName;
    private int fragmentInfo;
    private String strOperation;
    @Override
    public Dialog onCreateDialog(Bundle bundle)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(TagClass.ENTER_NAME);
        Bundle args = getArguments();
        strUserName = args.getString(TagClass.LOGIN_USERNAME);
        fragmentInfo = args.getInt(TagClass.FRAGMENT_INFO);
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton(TagClass.OK, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                textListName = input.getText().toString();
                boolean duplicateListFlag = true;

                GlobalVariables.database.setTableName(textListName+TagClass.DATABASE_LISTNAME_DELIMITER+strUserName);
                try {
                    Cursor cursor = GlobalVariables.database.getAllData(GlobalVariables.database.getSQLDatabaseInstanse());
                }
                catch (SQLiteException e) {
                    if (e.getMessage().toString().contains("no such table")) {
                        GlobalVariables.database.createTable(GlobalVariables.database.getSQLDatabaseInstanse());
                        duplicateListFlag = false;
                    }
                }
                if (getTargetFragment() != null && !textListName.equals("") && !duplicateListFlag) {
                    Intent i = new Intent();
                    i.putExtra(TagClass.CREATE_LIST, true);
                    i.putExtra(TagClass.LIST_NAME, textListName+TagClass.DATABASE_LISTNAME_DELIMITER+strUserName);
                    i.putExtra(TagClass.FRAGMENT_INFO, fragmentInfo);
                    getTargetFragment().onActivityResult(getTargetRequestCode(), Activity.RESULT_OK, i);

                } else
                    input.setError(TagClass.ERROR_BLANK_FIELD);
            }
        });
        builder.setNegativeButton(TagClass.CANCEL, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        return builder.create();
    }
}