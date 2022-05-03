package com.sertiscorp.oneml.onemlsimpleapp;

import android.graphics.Bitmap;

import java.nio.ByteBuffer;
import java.util.List;

import org.sertiscorp.oneml.face.Image;
import org.sertiscorp.oneml.face.ImageBatch;


public class ImageUtils {

    public static byte[] bitmapToByteArray(Bitmap bitmap){
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        short outChannel = 3;

        int[] pixelColorValues = new int[width * height];
        bitmap.getPixels(pixelColorValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        byte[] byteArray = new byte[width * height * outChannel];
        for (int i = 0; i < pixelColorValues.length; i++) {
            int pixelValue = pixelColorValues[i];

            // Put RGB into float array with normalization
            byteArray[3 * i] = (byte) ((pixelValue >> 16) & 0xFF);
            byteArray[3 * i + 1] = (byte) ((pixelValue >> 8) & 0xFF);
            byteArray[3 * i + 2] = (byte) (pixelValue & 0xFF);
        }

        return byteArray;
    }

    public static ByteBuffer bitmapToBuffer(Bitmap bitmap){
        ByteBuffer buffer = java.nio.ByteBuffer.allocateDirect(bitmap.getWidth()*bitmap.getHeight()*3);
        byte[] byteArray = bitmapToByteArray(bitmap);
        buffer.put(byteArray);
        buffer.rewind();

        return buffer;
    }

    public static Image bitmapToImage(Bitmap bitmap){
        ByteBuffer buffer = bitmapToBuffer(bitmap);
        return new Image(bitmap.getWidth(), bitmap.getHeight(), (short) 24, buffer);
    }

    public static ImageBatch bitmapsToImageBatch(List<Bitmap> bitmaps){
        ImageBatch batch = new ImageBatch();
        for (Bitmap bitmap : bitmaps) {
            batch.add(bitmapToImage(bitmap));
        }

        return batch;
    }
}
