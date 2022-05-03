package com.sertiscorp.oneml.onemlsimpleapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sertiscorp.oneml.onemlsimpleapp.helper.FaceProp;
import com.sertiscorp.oneml.onemlsimpleapp.helper.TestHelper;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.sertiscorp.oneml.face.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class FaceDetectorTest {
    private static final String TAG = "FaceDetectorTest";

    private static final String ROOT_DATASET_DIR_NAME = "face-detect-set";

    private static Context ctx;
    private static List<FaceProp> faceProps;
    private static FaceDetector faceDetector;

    @BeforeClass
    public static void beforeClass() throws IOException {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        faceProps = TestHelper.getAssetDatasetFaceProps(ctx, ROOT_DATASET_DIR_NAME, null);

        faceDetector = OneMLApiFactory.createFaceDetector();
    }

    @Test
    public void testInitialization_success(){
        FaceDetector faceDetector = OneMLApiFactory.createFaceDetector();
        assertNotNull(faceDetector);
    }

    @Test
    public void testDetect_success() throws IOException{
        for( FaceProp faceProp: faceProps) {
            InputStream is = ctx.getResources().getAssets().open(faceProp.srcPath);
            Bitmap bitmap = faceProp.readImageBitmap(is);

            ImageBatch imgs = ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));
            FaceDetectorResultList results = faceDetector.detect(imgs);

            if (faceProp.id.equals("face") || faceProp.id.equals("multi-face")) {

                Bitmap draw = bitmap.copy(Bitmap.Config.ARGB_8888, true);
                Canvas canvas = new Canvas(draw);
                Paint p = new Paint();
                p.setStyle(Paint.Style.STROKE);
                p.setAntiAlias(true);
                p.setFilterBitmap(true);
                p.setColor(Color.RED);
                p.setStrokeWidth(10);

                for (FaceDetectorResult detectionResult : results) {
                    assertTrue(detectionResult.getSize() > 0);

                    BBoxList bboxes = detectionResult.getBBoxes();
                    ScoreList scores = detectionResult.getScores();
                    Landmark5List landmarks = detectionResult.getLandmarks();
                    FacePoseList poses = detectionResult.getPoses();

                    int nLandmarks = FaceJNI.getLandmarkSize();

                    for (int i = 0; i < bboxes.size(); i++) {
                        BBox bbox = bboxes.get(i);

                        int x1 = (int) bbox.getLeft();
                        int y1 = (int) bbox.getTop();
                        int x2 = (int) bbox.getRight();
                        int y2 = (int) bbox.getBottom();
                        canvas.drawLine(x1, y1, x2, y1, p); //up
                        canvas.drawLine(x1, y1, x1, y2, p); //left
                        canvas.drawLine(x1, y2, x2, y2, p); //down
                        canvas.drawLine(x2, y1, x2, y2, p); //right

                        Landmark5 l = landmarks.get(i);
                        for (int j = 0; j < nLandmarks; j++) {
                            Log.d(TAG, String.format("Landmark %d: (%.6f, %.6f)", j, l.getX().get(j), l.getY().get(j)));
                            canvas.drawPoint(l.getX().get(j), l.getY().get(j), p);
                        }

                        Log.d(TAG, String.format("Result=[%f %f %f %f] score=%f", bbox.getLeft(),
                                bbox.getRight(),
                                bbox.getTop(),
                                bbox.getBottom(),
                                scores.get(i)));

                        Log.d(TAG, String.format("Estimated pose=%s", poses.get(i)));
                    }

                }
            } else{
                // "no-face" dataset
                assertEquals(0, results.get(0).getSize());
            }
        }
    }
}
