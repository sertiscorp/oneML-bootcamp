package com.sertiscorp.oneml.onemlsimpleapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.erikagtierrez.multiple_media_picker.Gallery;

import org.sertiscorp.oneml.face.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MainActivity extends AppCompatActivity {
    private final String TAG = "MainActivity";

    private String personId;
    private FaceEmbedder faceEmbedder;
    private FaceId faceId;
    private FaceDetector faceDetector;
    private  Utils utils;
    static final int REGISTER_IMAGE = 1;  // The request code
    static final int PREDICT_IMAGE = 2;  // The request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }

        faceEmbedder = OneMLApiFactory.createFaceEmbedder();
        faceId = OneMLApiFactory.createFaceId(faceEmbedder);
        faceDetector = OneMLApiFactory.createFaceDetector();
        utils = OneMLApiFactory.createUtils();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onRegisterClick(View view) {
        EditText textName = findViewById(R.id.inputName);
        personId = textName.getText().toString();
        if (personId.trim().isEmpty()) {
            Toast.makeText(this, "ID should not be empty.", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(this, Gallery.class);
        intent.putExtra("title", "Select media");
        intent.putExtra("mode", 1);
        intent.putExtra("maxSelection", 15);
        startActivityForResult(intent, REGISTER_IMAGE);
    }

    public void onPredictClick(View view) {
        Intent intent = new Intent(this, Gallery.class);
        intent.putExtra("title", "Select a predicted image");
        intent.putExtra("mode", 1);
        intent.putExtra("maxSelection", 1);
        startActivityForResult(intent, PREDICT_IMAGE);
    }

    private static void printLandmark(String text, Landmark5Array array) {
        int landmark_size = (int) array.size();

        System.out.print("landmark " + text + ": [");
        for (int j = 0; j < landmark_size; j++) {
            System.out.print(String.format("%.6f", array.get(j)));
            if (j < landmark_size - 1) {
                System.out.print(", ");
            }
        }
        System.out.println("]");
    }

    @SuppressLint("DefaultLocale")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        ArrayList<String> selectionResult = data.getStringArrayListExtra("result");
        if (selectionResult.size() == 0) {
            Toast.makeText(this, "An image is not selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        switch (requestCode) {
            case REGISTER_IMAGE:
                Toast.makeText(this, "Registering ID", Toast.LENGTH_SHORT).show();
                ImageBatch regImgBatch = new ImageBatch();
                try {
                    List<Bitmap> bitmaps = new ArrayList<>();
                    for (int i = 0; i < selectionResult.size(); i++) {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file://" + selectionResult.get(i)));
                        bitmaps.add(bitmap);
                    }

                    ImageBatch imgs = ImageUtils.bitmapsToImageBatch(bitmaps);
                    FaceDetectorResultList results = faceDetector.detect(imgs);

                    for (int i = 0; i < results.size() ; i++){
                        FaceDetectorResult detectionResult = results.get(i);
                        if (detectionResult.getSize() > 0) {
                            BBoxList bboxes = detectionResult.getBBoxes();
                            ScoreList scores = detectionResult.getScores();
                            Landmark5List landmarks = detectionResult.getLandmarks();

                            Log.d(TAG, String.format("Found %d faces", bboxes.size()));
                            for (int j = 0; j < bboxes.size(); j++) {
                                BBox bbox = bboxes.get(j);
                                Log.d(TAG, String.format("BBox %d: (%.6f, %.6f, %.6f, %.6f)", j, bbox.getLeft(), bbox.getTop(), bbox.getRight(), bbox.getBottom()));

                                Log.d(TAG, String.format("Score %d: %.6f", j, scores.get(j)));

                                Landmark5 landmark = landmarks.get(j);

                                printLandmark("x", landmark.getX());
                                printLandmark("y", landmark.getY());
                            }

                            regImgBatch.add(utils.cropAlignFaceLandmark(imgs.get(i), landmarks.get(0)));
                        } else {
                            Toast.makeText(this, "Not found any face", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (IllegalStateException e) {
                    Toast.makeText(this, "Empty registered images", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (regImgBatch.size() > 0) {
                    faceId.registerId(personId, regImgBatch, true);
                    Toast.makeText(this, String.format("Registered %s with %d images", personId, regImgBatch.size()), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "No face found", Toast.LENGTH_SHORT).show();
                }

                break;
            case PREDICT_IMAGE:
                Toast.makeText(this, "Predicting...", Toast.LENGTH_SHORT).show();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse("file://" + selectionResult.get(0)));
                    ImageBatch imgs =  ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));

                    FaceDetectorResultList faceBoxes = faceDetector.detect(imgs);
                    FaceDetectorResult detectionResult = faceBoxes.get(0);

                    if (detectionResult.getBBoxes().size() > 0) {
                        BBoxList bboxes = detectionResult.getBBoxes();
                        ScoreList scores = detectionResult.getScores();
                        Landmark5List landmarks = detectionResult.getLandmarks();

                        Log.d(TAG, String.format("Found %d faces", bboxes.size()));
                        for (int j = 0; j < bboxes.size(); j++) {
                            BBox bbox = bboxes.get(j);
                            Log.d(TAG, String.format("BBox %d: (%.6f, %.6f, %.6f, %.6f)", j, bbox.getLeft(), bbox.getTop(), bbox.getRight(), bbox.getBottom()));

                            Log.d(TAG, String.format("Score %d: %.6f", j, scores.get(j)));

                            Landmark5 landmark = landmarks.get(j);

                            printLandmark("x", landmark.getX());
                            printLandmark("y", landmark.getY());
                        }

                        ImageBatch predImgBatch = new ImageBatch();
                        predImgBatch.add(utils.cropAlignFaceLandmark(imgs.get(0), landmarks.get(0)));

                        FaceIdResultList results = faceId.predict(predImgBatch);
                        FaceIdResult result = results.get(0);

                        String id;
                        if (result.isIdentifiable()) {
                            id = result.getId();
                        } else {
                            id = "UNKNOWN";
                        }
                        @SuppressLint("DefaultLocale") String resText = String.format("Predict=%s\nDist=%.5f CombinedDist=%.5f",
                                id, result.getNearestNodeDistance(), result.getCombinedDistance());
                        Toast.makeText(this, resText, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "No face found", Toast.LENGTH_SHORT).show();
                    }
                } catch (IllegalStateException e) {
                    Toast.makeText(this, "Empty predict image", Toast.LENGTH_SHORT).show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }
}
