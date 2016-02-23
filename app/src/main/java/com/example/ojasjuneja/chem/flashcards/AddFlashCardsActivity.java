package com.example.ojasjuneja.chem.flashcards;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.util.LruCache;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.ojasjuneja.chem.GlobalVariables;
import com.example.ojasjuneja.chem.R;
import com.example.ojasjuneja.chem.TagClass;
import com.example.ojasjuneja.chem.utilities.LRUCacheClass;
import com.example.ojasjuneja.chem.utilities.MyUtilities;
import com.example.ojasjuneja.chem.utilities.UserPicture;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.io.IOException;

/**
 * Created by Ojas Juneja on 8/25/2015.
 */
public class AddFlashCardsActivity extends AppCompatActivity {

    private EditText editTextUpper, editTextLower;
    private ImageView imageViewUpper, imageViewLower;
    private LruCache lruCache;
    private String folderPath;
    private Bitmap bitmapImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_flash_cards);
        lruCache = LRUCacheClass.getCache();
        folderPath = MyUtilities.makePath();
        editTextUpper = (EditText) findViewById(R.id.textView_1);
        editTextLower = (EditText) findViewById(R.id.textView_2);
        imageViewUpper = (ImageView) findViewById(R.id.imageView_1);
        imageViewLower = (ImageView) findViewById(R.id.imageView_2);
        FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setPosition(FloatingActionButton.POSITION_RIGHT_CENTER).build();
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        // repeat many times:
        ImageView itemIconCamera = new ImageView(this);
        itemIconCamera.setImageResource(R.drawable.camera);
        SubActionButton buttonCamera = itemBuilder.setContentView(itemIconCamera).build();
        ImageView itemIconGallery = new ImageView(this);
        itemIconGallery.setImageResource(R.drawable.gallery);
        SubActionButton buttonGallery = itemBuilder.setContentView(itemIconGallery).build();
        FloatingActionMenu actionMenuUpper = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonCamera)
                .addSubActionView(buttonGallery)
                .attachTo(actionButton)
                .build();
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, GlobalVariables.SELECT_UPPER_IMAGE);
            }
        });
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, TagClass.SELECT_PICTURE), GlobalVariables.SELECT_UPPER_IMAGE);
            }
        });

        FloatingActionButton actionButtonLower = new FloatingActionButton.Builder(this).setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT).build();
        SubActionButton.Builder itemBuilderLower = new SubActionButton.Builder(this);
        ImageView itemIconCameraLower = new ImageView(this);
        itemIconCameraLower.setImageResource(R.drawable.camera);
        SubActionButton buttonCameraLower = itemBuilderLower.setContentView(itemIconCameraLower).build();
        ImageView itemIconGalleryLower = new ImageView(this);
        itemIconGalleryLower.setImageResource(R.drawable.gallery);
        SubActionButton buttonGalleryLower = itemBuilderLower.setContentView(itemIconGalleryLower).build();
        FloatingActionMenu actionMenuLower = new FloatingActionMenu.Builder(this)
                .addSubActionView(buttonCameraLower)
                .addSubActionView(buttonGalleryLower)
                .attachTo(actionButtonLower)
                .build();
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == this.RESULT_OK) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == GlobalVariables.SELECT_UPPER_IMAGE) {
                setImage(imageViewUpper, data);
            } else if (requestCode == GlobalVariables.SELECT_LOWER_IMAGE) {
                setImage(imageViewLower, data);
            }
        }
    }



    void setImage(ImageView imageView,Intent data) {
        ContentResolver cr = getContentResolver();
        try {
            Uri selectedImageUri = data.getData();
            bitmapImage = new UserPicture(selectedImageUri, cr).getBitmap();
            imageView.setImageBitmap(bitmapImage);
        } catch (IOException e) {
            Log.e(TagClass.EXCEPTIONCATCH,"",e);
        }
    }


}