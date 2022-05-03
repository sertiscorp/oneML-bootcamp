package com.sertiscorp.oneml.onemlsimpleapp.helper;

import android.content.Context;
import android.graphics.Bitmap;

import com.sertiscorp.oneml.onemlsimpleapp.ImageUtils;

import org.sertiscorp.oneml.face.FaceEmbedder;
import org.sertiscorp.oneml.face.FaceEmbedderResult;
import org.sertiscorp.oneml.face.FaceEmbedderResultList;
import org.sertiscorp.oneml.face.ImageBatch;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TestHelper {


    /**
     * Concatenate paths like 'Paths.get('/path/to', 'file').toString()'.
     * Paths class is from `java.nio.file.Paths` which is available from Android API 26.
     * It is not compatible with Telpo devices.
     *
     * @param names A varargs of names
     * @return A path which all names joins with '/'
     */
    public static String pathJoin(String... names) {
        StringBuffer buff = new StringBuffer();
        for(int i = 0; i < names.length; i++){
            if (i > 0){
                // Append '/' before add the next name
                buff.append("/");
            }
            buff.append(names[i]);
        }
        return buff.toString();
    }

    public static List<FaceProp> getAssetDatasetFaceProps(Context ctx, String rootDatasetDir, String rootStoragePath) throws IOException {
        List<FaceProp> faceProps = new ArrayList<>();
        for (String label : ctx.getResources().getAssets().list(rootDatasetDir)) {
            String labelDirPath = pathJoin(rootDatasetDir, label);
            String[] filenames = ctx.getResources().getAssets().list(labelDirPath);
            for (String filename : filenames) {
                String srcPath = pathJoin(labelDirPath, filename);

                String outputFilename;
                if (filename.indexOf(".") > 0) {
                    outputFilename = filename.substring(0, filename.lastIndexOf(".")) + ".data";
                } else {
                    outputFilename = filename + ".data";
                }
                String dstPath = pathJoin(rootStoragePath, "mace_output", label, outputFilename);
                faceProps.add(new FaceProp(label, srcPath, dstPath));
            }
        }
        return faceProps;
    }

    public static List<FaceProp> getStorageDatasetFaceProps(String rootDirPath, String rootStoragePath) {
        List<FaceProp> faceProps = new ArrayList<>();
        File rootDir = new File(rootDirPath);
        String[] labels = rootDir.list();
        for (String label : labels) {
            // List image files of each label.
            String labelDirPath = pathJoin(rootDirPath, label);
            File labelDir = new File(labelDirPath);
            String[] filenames = labelDir.list();
            for (String filename : filenames) {
                String srcPath = pathJoin(labelDirPath, filename);
                String outputFilename;
                if (filename.indexOf(".") > 0) {
                    outputFilename = filename.substring(0, filename.lastIndexOf("."));
                } else {
                    outputFilename = filename;
                }
                String dstPath = pathJoin(rootStoragePath, "mace_output", label, outputFilename);
                faceProps.add(new FaceProp(label, srcPath, dstPath));
            }
        }
        return faceProps;
    }

    /**
     * Create mapping between id and a list of images. Loaded images are not resized.
     * This mapping is intended to use with ID registration in FaceId instance.
     *
     * @param ctx
     * @param faceProps
     * @return
     * @throws IOException
     */
    public static HashMap<String, List<Bitmap>> createImageMapping(Context ctx, List<FaceProp> faceProps) throws IOException{
        HashMap<String, List<Bitmap>> mapping = new HashMap<>();
        for (FaceProp faceProp: faceProps) {
            InputStream is = ctx.getResources().getAssets().open(faceProp.srcPath);
            Bitmap bitmap = faceProp.readImageBitmap(is);
            List<Bitmap> bitmaps = mapping.get(faceProp.id);
            if (bitmaps == null){
                bitmaps = new ArrayList<Bitmap>();
                mapping.put(faceProp.id, bitmaps);
            }
            bitmaps.add(bitmap);
        }
        return mapping;
    }

    /**
     * Create a list of embedding vectors from a list of bitmap images.
     *
     * @param faceEmbedder
     * @param bitmaps
     * @return
     */
    public static List<float[]> embedImageList(FaceEmbedder faceEmbedder, List<Bitmap> bitmaps){
        List<float[]> embs = new ArrayList<>();

        ImageBatch batch = ImageUtils.bitmapsToImageBatch(bitmaps);
        FaceEmbedderResultList results = faceEmbedder.embed(batch);
        for (FaceEmbedderResult result: results) {
            embs.add(result.getEmbedding());
        }
        return embs;
    }

    /**
     * Generate a random embedding vector
     *
     * @param embLen Length of an embedding vector
     * @return
     */
    public static float[] genRandomEmb(int embLen){
        float[] emb = new float[embLen];
        for (int i = 0; i < embLen; i++) {
            // Generate a value between -1.0 and 1.0
            emb[i] = (float) ((Math.random() * 2) - 1.0);
        }
        return emb;
    }

    /**
     * Generate a random input vector
     *
     * @param inputLen Length of a input vector
     * @return
     */
    public static float[] genRandomInput(int inputLen){
        float[] input = new float[inputLen];
        for (int i = 0; i < inputLen; i++) {
            // Generate a value between 0 and 1.0
            input[i] = (float) Math.random();
        }
        return input;
    }

}
