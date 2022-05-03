package com.sertiscorp.oneml.onemlsimpleapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.sertiscorp.oneml.onemlsimpleapp.helper.FaceProp;
import com.sertiscorp.oneml.onemlsimpleapp.helper.TestHelper;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.sertiscorp.oneml.face.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.rules.ExpectedException;

@RunWith(AndroidJUnit4.class)
public class FaceIDTest {
    private static final String TAG = "FaceIDTest";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private static final String ROOT_REGISTER_SET_DIR_NAME = "register-set";
    private static final String ROOT_EVALUATE_SET_DIR_NAME = "evaluate-set";
    private static final String ROOT_DISTRACT_SET_DIR_NAME = "distract-set";

    private static Context ctx;
    private static HashMap<String, List<Bitmap>> regImageMapping;
    private static HashMap<String, List<Bitmap>> evalImageMapping;
    private static HashMap<String, List<Bitmap>> distImageMapping;

    private static FaceId faceId;
    private final boolean isAvgEmbRegister = true;

    @BeforeClass
    public static void beforeClass() throws IOException {
        ctx = InstrumentationRegistry.getInstrumentation().getContext();
        List<FaceProp> regFaceProps = TestHelper.getAssetDatasetFaceProps(ctx, ROOT_REGISTER_SET_DIR_NAME, null);
        regImageMapping = TestHelper.createImageMapping(ctx, regFaceProps);
        List<FaceProp> evalFaceProps = TestHelper.getAssetDatasetFaceProps(ctx, ROOT_EVALUATE_SET_DIR_NAME, null);
        evalImageMapping = TestHelper.createImageMapping(ctx, evalFaceProps);
        List<FaceProp> distFaceProps = TestHelper.getAssetDatasetFaceProps(ctx, ROOT_DISTRACT_SET_DIR_NAME, null);
        distImageMapping = TestHelper.createImageMapping(ctx, distFaceProps);
    }

    @Before
    public void before() {
        faceId = OneMLApiFactory.createFaceId();

        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            ImageBatch batch = ImageUtils.bitmapsToImageBatch(entry.getValue());
            Embedding vector = faceId.registerId(entry.getKey(), batch, isAvgEmbRegister);

            // Regardless of isAvgEmbRegister, the length of the returned vector should be equal the length of embedding.
            assertEquals(FaceJNI.getEmbeddingSize(), vector.size());
            assertNotNull(vector);
        }
    }

    @Test
    public void testInitialization_success(){
        FaceId faceId = OneMLApiFactory.createFaceId();
        assertNotNull(faceId);
    }

    @Test
    public void testPredict_known_ids() {
        for (HashMap.Entry<String, List<Bitmap>> entry : evalImageMapping.entrySet()) {
            String id = entry.getKey();
            for (Bitmap bitmap : entry.getValue()) {
                ImageBatch batch = ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));

                FaceIdResultList results = faceId.predict(batch);
                String predictedId = results.get(0).getId();
                double dist = results.get(0).getCombinedDistance();
                Log.i(TAG, String.format("testPredict_known_ids: id=%s predictedId=%s dist=%.5f", id, predictedId, dist));
                assertEquals(id, predictedId);
            }
        }
    }

    @Test
    public void testPredict_unknown_id() {
        for (HashMap.Entry<String, List<Bitmap>> entry : distImageMapping.entrySet()) {
            for (Bitmap bitmap : entry.getValue()) {
                ImageBatch batch = ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));
                FaceIdResultList results = faceId.predict(batch);
                String predictedId = results.get(0).getId();
                double dist = results.get(0).getCombinedDistance();
                Log.i(TAG, String.format("testPredict_unknown_id: id=unknown predictedId=%s dist=%.5f", predictedId, dist));
                assertFalse(results.get(0).isIdentifiable());
            }
        }
    }

    @Test
    public void testPredict_no_registered_id() {
        faceId = OneMLApiFactory.createFaceId();

        for (HashMap.Entry<String, List<Bitmap>> entry : evalImageMapping.entrySet()) {
            for (Bitmap bitmap : entry.getValue()) {
                ImageBatch batch = ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));
                thrown.expect(RuntimeException.class);
                thrown.expectMessage("predict: no ID has been registered yet");
                FaceIdResultList results = faceId.predict(batch);
            }
        }
    }

    @Test
    public void testRegisterIdEmbs_success() {
        FaceEmbedder faceEmbedder = OneMLApiFactory.createFaceEmbedder();
        faceId = OneMLApiFactory.createFaceId(faceEmbedder);

        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = "new" + entry.getKey();
            faceId.registerId(id, TestHelper.embedImageList(faceEmbedder, entry.getValue()).get(0));

            // Validate registered ID
            IdList idSet = faceId.getIds();
            assertTrue(idSet.contains(id));
        }

        int totalRegisteredId = faceId.getIds().size();
        // Try to register same IDs again
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = "new" + entry.getKey();
            faceId.registerId(id, TestHelper.embedImageList(faceEmbedder, entry.getValue()).get(0));

            // Validate registered ID
            IdList ids = faceId.getIds();
            assertTrue(ids.contains(id));
            // Size should not change.
            assertEquals(totalRegisteredId, ids.size());
        }
    }

    @Test
    public void testRegisterIdImages_success() {
        faceId = OneMLApiFactory.createFaceId();

        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = "new" + entry.getKey();

            ImageBatch batch = ImageUtils.bitmapsToImageBatch(entry.getValue());
            // If isAvgEmbRegister is false, only the first image is embedded.
            Embedding vector = faceId.registerId(id, batch, false);
            assertEquals(FaceJNI.getEmbeddingSize(), vector.size());

            // Validate registered ID
            IdList idSet = faceId.getIds();
            assertTrue(idSet.contains(id));
        }

        int totalRegisteredId = faceId.getIds().size();
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = "new" + entry.getKey();

            ImageBatch batch = ImageUtils.bitmapsToImageBatch(entry.getValue());
            // If isAvgEmbRegister is false, only the first image is embedded.
            Embedding vector = faceId.registerId(id, batch, false);
            assertEquals(FaceJNI.getEmbeddingSize(), vector.size());

            // Validate registered ID
            IdList ids = faceId.getIds();
            assertTrue(ids.contains(id));
            // Size should not change.
            assertEquals(totalRegisteredId, ids.size());
        }
    }

    @Test
    public void testRegisterIdImages_average_emb_success() {
        faceId = OneMLApiFactory.createFaceId();

        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = "new" + entry.getKey();

            ImageBatch batch = ImageUtils.bitmapsToImageBatch(entry.getValue());
            Embedding vector = faceId.registerId(id, batch, true);
            // If isAvgEmbRegister is false, only the first image is embedded.
            assertEquals(FaceJNI.getEmbeddingSize(), vector.size());
            assertNotNull(vector);

            // Validate registered ID
            IdList ids = faceId.getIds();
            assertTrue(ids.contains(id));
        }
    }

    @Test
    public void testRemoveId_success() {
        List<String> deletedIds = new ArrayList<>();

        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = entry.getKey();
            faceId.removeId(id);
            deletedIds.add(id);

            IdList ids = faceId.getIds();
            assertFalse(ids.contains(id));

            // Validate if classification is still good
            for (HashMap.Entry<String, List<Bitmap>> evalEntry : evalImageMapping.entrySet()) {
                String evalId = evalEntry.getKey();
                for (Bitmap bitmap : evalEntry.getValue()) {
                    ImageBatch batch = ImageUtils.bitmapsToImageBatch(Collections.singletonList(bitmap));
                    if (ids.isEmpty()) {
                        thrown.expect(RuntimeException.class);
                        thrown.expectMessage("predict: no ID has been registered yet");
                        FaceIdResultList results = faceId.predict(batch);
                        continue;
                    }

                    FaceIdResultList results = faceId.predict(batch);
                    String predictedId = results.get(0).getId();

                    if (deletedIds.contains(evalId)) {
                        // This evalId has been already deleted
                        assertNotEquals(evalId, predictedId);
                        assertFalse(results.get(0).isIdentifiable());
                    } else {
                        assertEquals(evalId, predictedId);
                        assertTrue(results.get(0).isIdentifiable());
                    }
                }
            }
        }
    }

    @Test
    public void testIsTheSamePerson_same_person() {
        // Compare faces of the same person between registration set and evaluation set.
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            List<Bitmap> faces1 = entry.getValue();
            List<Bitmap> faces2 = regImageMapping.get(entry.getKey());
            Log.d(TAG, String.format("id=%s", entry.getKey()));

            for (int i = 0; i < faces1.size(); i++) {
                Image face1 = ImageUtils.bitmapToImage(faces1.get(i));
                for (int j = i; j < faces2.size(); j++) {
                    Image face2 = ImageUtils.bitmapToImage(faces2.get(j));
                    assertTrue(faceId.isTheSamePerson(face1, face2));
                }
            }
        }
    }

    @Test
    public void testIsTheSamePerson_different_person() {
        for (HashMap.Entry<String, List<Bitmap>> entry1 : regImageMapping.entrySet()) {
            String id1 = entry1.getKey();
            List<Bitmap> faces1 = entry1.getValue();
            for (HashMap.Entry<String, List<Bitmap>> entry2 : regImageMapping.entrySet()) {
                String id2 = entry2.getKey();
                if (id1.equals(id2)) {
                    // Skip the same ID
                    continue;
                }
                List<Bitmap> faces2 = entry2.getValue();
                // Randomly pick images to speed up testing.
                int i = (int) (faces1.size() * Math.random());
                int j = (int) (faces1.size() * Math.random());
                Image face1 = ImageUtils.bitmapToImage(faces1.get(i));
                Image face2 = ImageUtils.bitmapToImage(faces2.get(j));

                assertFalse(faceId.isTheSamePerson(face1, face2));
            }
        }
    }

    @Test
    public void testIsTheSamePerson_with_embedding_same_person() {
        // Compare faces of the same person between registration set and evaluation set.
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {

            List<Bitmap> faces1 = entry.getValue();
            List<Bitmap> faces2 = regImageMapping.get(entry.getKey());
            for (int i = 0; i < faces1.size(); i++) {
                ImageBatch batch = ImageUtils.bitmapsToImageBatch(Collections.singletonList(faces1.get(i)));
                Embedding vectors = faceId.registerId("0", batch, true);
                for (int j = i; j < faces2.size(); j++) {
                    Image face2 = ImageUtils.bitmapToImage(faces2.get(j));
                    SamePersonResult result = faceId.isTheSamePerson(vectors.toArray(), face2);
                    assertTrue(result.getSame());
                    assertNotNull(result.getEmbedding());
                }
            }
        }
    }

    @Test
    public void testIsTheSamePerson_with_embedding_different_person() {
        for (HashMap.Entry<String, List<Bitmap>> entry1 : regImageMapping.entrySet()) {
            String id1 = entry1.getKey();
            List<Bitmap> faces1 = entry1.getValue();
            for (HashMap.Entry<String, List<Bitmap>> entry2 : regImageMapping.entrySet()) {
                String id2 = entry2.getKey();
                if (id1.equals(id2)) {
                    // Skip the same ID
                    continue;
                }
                List<Bitmap> faces2 = entry2.getValue();
                // Randomly pick images to speed up testing.
                int i = (int) (faces1.size() * Math.random());
                int j = (int) (faces1.size() * Math.random());
                Image face1 = ImageUtils.bitmapToImage(faces1.get(i));
                Image face2 = ImageUtils.bitmapToImage(faces2.get(j));

                ImageBatch batch = new ImageBatch();
                batch.add(face1);
                Embedding vectors = faceId.registerId("0", batch, true);
                SamePersonResult result = faceId.isTheSamePerson(vectors.toArray(), face2);

                assertFalse(result.getSame());
                assertNotNull(result.getEmbedding());
            }
        }
    }

    @Test
    public void testGetIds_success() {
        IdList idSet = faceId.getIds();
        assertEquals(regImageMapping.size(), idSet.size());
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            assertTrue(idSet.contains(entry.getKey()));
        }
    }

    @Test
    public void testUpdateEmbeddingDynamically_success() {
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = entry.getKey();
            float[] origEmb = TestHelper.genRandomEmb(FaceJNI.getEmbeddingSize());
            for (Bitmap bitmap : entry.getValue()) {
                Image face = ImageUtils.bitmapToImage(bitmap);
                faceId.updateEmbeddingDynamically(id, face, origEmb);
            }
        }
    }


    @Test
    public void testUpdateEmbedding_success() {
        for (HashMap.Entry<String, List<Bitmap>> entry : regImageMapping.entrySet()) {
            String id = entry.getKey();
            float[] newEmb = TestHelper.genRandomEmb(FaceJNI.getEmbeddingSize());
            faceId.updateEmbedding(id, newEmb);
        }
    }
}
