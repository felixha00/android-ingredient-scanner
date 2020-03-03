package com.example.ingrediscan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.SparseArray;
import android.widget.TextView;
import android.widget.Toast;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.google.api.services.vision.v1.model.TextAnnotation;

//import org.jetbrains.annotations.NotNull;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class OCRActivity extends AppCompatActivity implements Serializable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr);
        final File scanImage = (File)getIntent().getSerializableExtra("pickImage");
        if (scanImage != null) {
            getTextFromImage(scanImage);
        }
    }

    public void getTextFromImage(final File scanImage){


        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("YOUR_API_KEY"));



        final Vision vision = visionBuilder.build();

        // Create new thread
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                // Convert photo to byte array
                try {
                    InputStream is = new FileInputStream(scanImage);
                    byte[] photoData = IOUtils.toByteArray(is);
                    is.close();

                    Image inputImage = new Image();
                    inputImage.encodeContent(photoData);

                    Feature desiredFeature = new Feature();
                    desiredFeature.setType("FACE_DETECTION");

                    AnnotateImageRequest request = new AnnotateImageRequest();
                    request.setImage(inputImage);
                    request.setFeatures(Arrays.asList(desiredFeature));


                    BatchAnnotateImagesRequest batchRequest =
                            new BatchAnnotateImagesRequest();

                    batchRequest.setRequests(Arrays.asList(request));


                    BatchAnnotateImagesResponse batchResponse =
                            vision.images().annotate(batchRequest).execute();

                    final TextAnnotation text = batchResponse.getResponses()
                            .get(0).getFullTextAnnotation();

                    Toast.makeText(getApplicationContext(),
                            text.getText(), Toast.LENGTH_LONG).show();
                }
                catch (IOException e){

                }


            }
        });

        /*
        // TURN FILE INTO BITMAP
        String filePath = scanImage.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //

        TextRecognizer tr = new TextRecognizer.Builder(getApplicationContext()).build();

        if (!tr.isOperational()){
           // Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(this, "Low storage", Toast.LENGTH_LONG).show();
                //Log.w(TAG, getString(R.string.low_storage_error));
            }
            else {
                Toast.makeText(getApplicationContext(),"Could not get text", Toast.LENGTH_SHORT).show();
            }

        }
        else{
            Frame frame = new Frame.Builder().setBitmap(bitmap).build();

            SparseArray<TextBlock> items = tr.detect(frame);
            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < items.size(); ++i){
                TextBlock myItem = items.valueAt(i);
                sb.append(myItem.getValue());
                sb.append("\n");
            }

            TextView tv = (TextView) findViewById(R.id.ocrText);
            tv.setText(sb.toString());

        }
        */
    }

    //Displays/Creates output from OCR
    //OCR step 3
    /*
    private void doOCR(File scanImage){

        // TURN FILE INTO BITMAP
        String filePath = scanImage.getPath();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
        //

        Vision.Builder visionBuilder = new Vision.Builder(
                new NetHttpTransport(),
                new AndroidJsonFactory(),
                null);

        visionBuilder.setVisionRequestInitializer(
                new VisionRequestInitializer("YOUR_API_KEY"));

        Vision vision = visionBuilder.build();
        Feature desiredFeature = new Feature();
        desiredFeature.setType("TEXT_DETECTION");

        final List<Feature> featureList = new ArrayList<>();
        featureList.add(desiredFeature);

        final List<AnnotateImageRequest> annotateImageRequests = new ArrayList<>();

        AnnotateImageRequest annotateImageReq = new AnnotateImageRequest();
        annotateImageReq.setFeatures(featureList);
        annotateImageReq.setImage(getImageEncodeImage(bitmap));
        annotateImageRequests.add(annotateImageReq);

        new AsyncTask<Object, Void, String>() {
            @Override
            protected String doInBackground(Object... params) {
                try {

                    HttpTransport httpTransport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = GsonFactory.getDefaultInstance();

                    //VisionRequestInitializer requestInitializer = new VisionRequestInitializer(CLOUD_VISION_API_KEY);

                    Vision.Builder builder = new Vision.Builder(httpTransport, jsonFactory, null);
                    //builder.setVisionRequestInitializer(requestInitializer);

                    Vision vision = builder.build();

                    BatchAnnotateImagesRequest batchAnnotateImagesRequest = new BatchAnnotateImagesRequest();
                    batchAnnotateImagesRequest.setRequests(annotateImageRequests);

                    Vision.Images.Annotate annotateRequest = vision.images().annotate(batchAnnotateImagesRequest);
                    annotateRequest.setDisableGZipContent(true);
                    BatchAnnotateImagesResponse response = annotateRequest.execute();
                    //return convertResponseToString(response);
                } catch (GoogleJsonResponseException e) {
                    //Log.d(TAG, "failed to make API request because " + e.getContent());
                } catch (IOException e) {
                   // Log.d(TAG, "failed to make API request because of other IOException " + e.getMessage());
                }
                return "Cloud Vision API request failed. Check logs for details.";
            }

            protected void onPostExecute(String result) {
                TextView tv = (TextView) findViewById(R.id.ocrText);
                tv.setText(result);


            }
        }.execute();


    }
    */

        /*
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 1; // 1 - means max size. 4 - means maxsize/4 size. Don't use value <4, because you need more memory in the heap to store your data.
        Bitmap scanBitmap = BitmapFactory.decodeFile(scanImage.getAbsolutePath(), options);
    */






    //OCR starts
    //OCR step 1


    @NonNull
    private Image getImageEncodeImage(Bitmap bitmap) {
        Image base64EncodedImage = new Image();
        // Convert the bitmap to a JPEG
        // Just in case it's a format that Android understands but Cloud Vision
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, byteArrayOutputStream);
        byte[] imageBytes = byteArrayOutputStream.toByteArray();
        // Base64 encode the JPEG
        base64EncodedImage.encodeContent(imageBytes);
        return base64EncodedImage;
    }


}
