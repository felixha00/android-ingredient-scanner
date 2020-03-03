package com.example.ingrediscan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Serializable;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.FileUtils;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;




import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class view_image_taken extends AppCompatActivity implements Serializable {

    File savedPhoto=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_taken);
        String path = getIntent().getStringExtra("imageTaken");
        //File photo = (File) getIntent().getSerializableExtra("imagetaken1");


        if(path!=null) {
            final Uri fileUri = Uri.parse(getIntent().getStringExtra("imageTaken")); //
            savedPhoto = new File(getRealPathFromURI(fileUri));
        }else{
            savedPhoto = new File(getIntent().getStringExtra("imageTaken"));
        }

        // *NOTE: This crashes program if you try and take the file from a documents app.
        if (savedPhoto.exists()){
            Bitmap image = BitmapFactory.decodeFile(savedPhoto.getAbsolutePath());
            ProportionalImageView piv = (ProportionalImageView) findViewById(R.id.taken_image);
            piv.setImageBitmap(image);
        }
        else {
            finish();
        }

        ImageButton confirmImage = (ImageButton)findViewById(R.id.confirm_image);
        confirmImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startOCR(savedPhoto);
                }
        });

        final ImageButton returnToCamera = (ImageButton)findViewById(R.id.back);
        returnToCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
        });

    }

    public void startOCR(File obj) {
        Intent intent = new Intent(this, OCRActivity.class);
        intent.putExtra("pickImage", obj);
        startActivity(intent);
    }

    private String getRealPathFromURI(Uri contentURI) {
        try {
            String result;
            Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                result = contentURI.getPath();
            } else {
                cursor.moveToFirst();
                int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                result = cursor.getString(idx);
                cursor.close();
            }
            return result;
        }
        catch (IllegalStateException e) {
            finish();
            return null;
        }
    }
}

