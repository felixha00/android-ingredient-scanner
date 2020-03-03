package com.example.ingrediscan;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.example.ingrediscan.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

import static android.app.Activity.RESULT_CANCELED;

public class CameraFragment extends Fragment {

    File photoFile = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FloatingActionButton cameraButton;
        CardView pickImageCard, openCameraCard;

        View myView = inflater.inflate(R.layout.fragment_camera, container, false);
        pickImageCard = (CardView) myView.findViewById(R.id.pick_image_card);
        pickImageCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        openCameraCard = (CardView) myView.findViewById(R.id.open_camera_card);
        openCameraCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCamera(); // OPENS CAMERA
            }
        });

        return myView;

    }

    //Saves the image file into a respective intent.
    public void openViewImageTaken(Uri uri) {
        Intent intent = new Intent(getActivity(), view_image_taken.class);
        intent.putExtra("imageTaken", uri.toString());
        startActivity(intent);

    }


    private static final int GALLERY_REQUEST_CODE = 1;
    private static final int CAMERA_PIC_REQUEST = 0;

    // Picks image from gallery
    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        // Sets the type as image/*. This ensures only components of type image are selected
        intent.setType("image/*");
        //We pass an extra array with the accepted mime types. This will ensure only components with these MIME types as targeted.
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        // Launching the Intent
        startActivityForResult(intent, 1);


    }

    // Opens default camera
    private void openCamera() {
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, 0);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {

                case 0: // TAKE A PICTURE REQUEST
                    if (data != null) {
                        // Uri takenImage = data.getData();
                        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                        //Byte bytes[] = data.getByteArrayExtra("data");

                        if (thumbnail != null) {
                            String str = saveImage(thumbnail);
                            Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getActivity(), view_image_taken.class);
                            intent.putExtra("imageTaken1", str);
                            startActivity(intent);

                        }
                    }
                    break;
                case 1: // CHOOSE FROM GALLERY REQUEST
                    if (data != null) {
                        Uri selectedImage = data.getData();
                        openViewImageTaken(selectedImage);
                    }
                    break;

            }


        }
    }

    private final String IMAGE_DIRECTORY = "/Ingrediscan";

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);

        try {
            File f = new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM),"imagetaken.png");
            f.createNewFile();

            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(getActivity(),
                    new String[]{f.getPath()},
                    new String[]{"image/png"}, null);
            fo.close();
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());

            MediaStore.Images.Media.insertImage(getActivity().getContentResolver(),f.getAbsolutePath(),f.getName(),f.getName());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }


        return "";
    }

    String currentPhotoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "imagetaken";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".png",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /*private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go

            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }*/

}



