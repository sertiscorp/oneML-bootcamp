/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.sertiscorp.oneml.onemlcamera;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.hardware.camera2.CameraCharacteristics;
import android.media.ImageReader.OnImageAvailableListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Size;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.LinkedList;
import java.util.List;
import org.sertiscorp.oneml.onemlcamera.customview.OverlayView;
import org.sertiscorp.oneml.onemlcamera.env.BorderedText;
import org.sertiscorp.oneml.onemlcamera.env.ImageUtils;
import org.sertiscorp.oneml.onemlcamera.env.Logger;
import org.sertiscorp.oneml.onemlcamera.classifier.SimilarityClassifier;
import org.sertiscorp.oneml.onemlcamera.tracking.MultiBoxTracker;

import org.sertiscorp.oneml.face.*;

/**
 * An activity that uses a TensorFlowMultiBoxDetector and ObjectTracker to detect and then track
 * objects.
 */
public class DetectorActivity extends CameraActivity implements OnImageAvailableListener {
  private static final Logger LOGGER = new Logger();

  // Minimum detection confidence to track a detection.
  private static final boolean MAINTAIN_ASPECT = false;

  private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

  private static final boolean SAVE_PREVIEW_BITMAP = false;
  private static final float TEXT_SIZE_DIP = 10;
  OverlayView trackingOverlay;
  private Integer sensorOrientation;

  private long lastProcessingTimeMs;
  private Bitmap rgbFrameBitmap = null;
  private Bitmap croppedBitmap = null;

  private boolean computingDetection = false;
  private boolean addPending = false;

  private long timestamp = 0;

  private Matrix frameToCropTransform;
  private Matrix cropToFrameTransform;

  private MultiBoxTracker tracker;

  private BorderedText borderedText;

  // Face detector
  private FaceDetector faceDetector;

  // Face identifier
  private FaceId faceId;

  // Utils
  private Utils utils;

  private FloatingActionButton fabAdd;


  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    fabAdd = findViewById(R.id.fab_add);
    fabAdd.setOnClickListener(view -> onAddClick());

    LicenseManager manager = new LicenseManager();
    manager.activateTrial();

    // Real-time contour detection of multiple faces
    faceDetector = new FaceDetector(manager);
    faceId = new FaceId(manager);
    utils = new Utils(manager);
  }

  private void onAddClick() {
    addPending = true;
  }

  @Override
  public void onPreviewSizeChosen(final Size size, final int rotation) {
    final float textSizePx =
            TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, TEXT_SIZE_DIP, getResources().getDisplayMetrics());
    borderedText = new BorderedText(textSizePx);
    borderedText.setTypeface(Typeface.MONOSPACE);

    tracker = new MultiBoxTracker(this);

    previewWidth = size.getWidth();
    previewHeight = size.getHeight();

    sensorOrientation = rotation - getScreenOrientation();
    LOGGER.i("Camera orientation relative to screen canvas: %d", sensorOrientation);

    LOGGER.i("Initializing at size %dx%d", previewWidth, previewHeight);
    rgbFrameBitmap = Bitmap.createBitmap(previewWidth, previewHeight, Config.ARGB_8888);


    int targetW, targetH;
    if (sensorOrientation == 90 || sensorOrientation == 270) {
      targetH = previewWidth;
      targetW = previewHeight;
    }
    else {
      targetW = previewWidth;
      targetH = previewHeight;
    }
    int cropW = (int) (targetW / 2.0);
    int cropH = (int) (targetH / 2.0);

    croppedBitmap = Bitmap.createBitmap(cropW, cropH, Config.ARGB_8888);

    frameToCropTransform =
            ImageUtils.getTransformationMatrix(
                    previewWidth, previewHeight,
                    cropW, cropH,
                    sensorOrientation, MAINTAIN_ASPECT);

    cropToFrameTransform = new Matrix();
    frameToCropTransform.invert(cropToFrameTransform);

    trackingOverlay = findViewById(R.id.tracking_overlay);
    trackingOverlay.addCallback(
            canvas -> {
              tracker.draw(canvas);
              if (isDebug()) {
                tracker.drawDebug(canvas);
              }
            });

    tracker.setFrameConfiguration(previewWidth, previewHeight, sensorOrientation);
  }

  @Override
  protected void processImage() {
    ++timestamp;
    final long currTimestamp = timestamp;
    trackingOverlay.postInvalidate();

    // No mutex needed as this method is not reentrant.
    if (computingDetection) {
      readyForNextImage();
      return;
    }
    computingDetection = true;

    LOGGER.i("Preparing image " + currTimestamp + " for detection in bg thread.");

    rgbFrameBitmap.setPixels(getRgbBytes(), 0, previewWidth, 0, 0, previewWidth, previewHeight);

    readyForNextImage();

    final Canvas canvas = new Canvas(croppedBitmap);
    canvas.drawBitmap(rgbFrameBitmap, frameToCropTransform, null);
    // For examining the actual TF input.
    if (SAVE_PREVIEW_BITMAP) {
      ImageUtils.saveBitmap(rgbFrameBitmap);
    }

    FaceDetectorResult faces = faceDetector.detect(ImageUtils.bitmapToImage(croppedBitmap));

    if (faces.getSize() == 0) {
      updateResults(currTimestamp, new LinkedList<>());
    } else {
      runInBackground(
              () -> onFacesDetected(currTimestamp, faces, addPending));
    }
  }

  @Override
  protected int getLayoutId() {
    return R.layout.tfe_od_camera_connection_fragment_tracking;
  }

  @Override
  protected Size getDesiredPreviewFrameSize() {
    return DESIRED_PREVIEW_SIZE;
  }

  @SuppressLint("SetTextI18n")
  private void showAddFaceDialog(Image rec) {

    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    LayoutInflater inflater = getLayoutInflater();
    View dialogLayout = inflater.inflate(R.layout.image_edit_dialog, null);
    ImageView ivFace = dialogLayout.findViewById(R.id.dlg_image);
    TextView tvTitle = dialogLayout.findViewById(R.id.dlg_title);
    EditText etName = dialogLayout.findViewById(R.id.dlg_input);

    tvTitle.setText("Add Face");
    ivFace.setImageBitmap(ImageUtils.imageToBitmap(rec));
    etName.setHint("Input name");

    builder.setPositiveButton("OK", (dlg, i) -> {

        String name = etName.getText().toString();
        if (name.isEmpty()) {
            return;
        }
        faceId.registerId(name, rec);
        dlg.dismiss();
    });
    builder.setView(dialogLayout);
    builder.show();
  }

  private void updateResults(long currTimestamp, final List<SimilarityClassifier.Recognition> mappedRecognitions) {
    tracker.trackResults(mappedRecognitions, currTimestamp);
    trackingOverlay.postInvalidate();
    computingDetection = false;
  }

  private void onFacesDetected(long currTimestamp, FaceDetectorResult faces, boolean add) {
    final List<SimilarityClassifier.Recognition> mappedRecognitions = new LinkedList<>();

    BBoxList bboxes = faces.getBBoxes();
    Landmark5List landmarks = faces.getLandmarks();
    for (int j = 0; j < bboxes.size(); j++) {
      final RectF boundingBox = new RectF(bboxes.get(j).getLeft(), bboxes.get(j).getTop(), bboxes.get(j).getRight(), bboxes.get(j).getBottom());

      // maps crop coordinates to original
      cropToFrameTransform.mapRect(boundingBox);

      Image face = utils.cropAlignFaceLandmark(ImageUtils.bitmapToImage(croppedBitmap), landmarks.get(j));

      String label = "Unknown";
      float confidence = -1f;
      int color = Color.BLUE;

      if (add) {
        showAddFaceDialog(face);
        addPending = false;
      } else if (faceId.getIds().size() > 0) {
        final long startTime = SystemClock.uptimeMillis();
        final FaceIdResult resultsAux = faceId.predict(face);
        lastProcessingTimeMs = SystemClock.uptimeMillis() - startTime;

        if (resultsAux.isIdentifiable()) {
          color = Color.GREEN;
          confidence = 1 - (resultsAux.getNearestNodeDistance() / 2);
          label = resultsAux.getId();
        } else {
          color = Color.RED;
        }
      }

      if (getCameraFacing() == CameraCharacteristics.LENS_FACING_FRONT) {

        // camera is frontal so the image is flipped horizontally
        // flips horizontally
        Matrix flip = new Matrix();
        if (sensorOrientation == 90 || sensorOrientation == 270) {
          flip.postScale(1, -1, previewWidth / 2.0f, previewHeight / 2.0f);
        }
        else {
          flip.postScale(-1, 1, previewWidth / 2.0f, previewHeight / 2.0f);
        }
        flip.mapRect(boundingBox);
      }

      final SimilarityClassifier.Recognition result = new SimilarityClassifier.Recognition(
              "0", label, confidence, boundingBox);

      result.setColor(color);
      result.setLocation(boundingBox);
      result.setExtra(null);
      result.setCrop(null);
      mappedRecognitions.add(result);
    }

    updateResults(currTimestamp, mappedRecognitions);
  }
}
