package com.sertiscorp.oneml.onemlsimpleapp.helper;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FaceProp {
    private String TAG = "FaceProp";

    public String id;
    public String srcPath;
    public String dstPath;
    public float[] embedding;

    public FaceProp(String id, String srcPath, String dstPath) {
        Log.i(TAG, String.format("FaceProp: srcPath=%s, dstPath=%s", srcPath, dstPath));
        this.id = id;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    /**
     * Read bytes of 3-channel image from input stream
     *
     * @param is
     * @param height
     * @param width
     * @return
     * @throws IOException
     */
    private static byte[] readStream(InputStream is, int height, int width) throws IOException {
        // Copy content of the image to byte-array
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[height * width * 3];

        while ((nRead = is.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        byte[] temporaryImageInMemory = buffer.toByteArray();
        buffer.close();
        is.close();
        return temporaryImageInMemory;
    }

    /**
     * Read an image stream as Bitmap
     *
     * @param is
     * @return
     */
    public Bitmap readImageBitmap(InputStream is) {
        return BitmapFactory.decodeStream(is);
    }
}
