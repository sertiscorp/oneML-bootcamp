package com.sertiscorp.oneml.onemlsimpleapp;

import android.content.Context;
import android.util.Log;

import org.sertiscorp.oneml.face.*;

public class OneMLApiFactory {

    private static final String TAG = "OneMLApiFactory";
    private static String mLicenseKey = "";

    public static void setLicenseKey(String licenseKey){
        mLicenseKey = licenseKey;
    }

    private static LicenseManager createLicenseManager(){
        LicenseManager manager = new LicenseManager();
        if (mLicenseKey != null && mLicenseKey.length() > 0){
            manager.setKey(mLicenseKey);
            manager.activateKey();
        }else{
            manager.activateTrial();
        }
        boolean valid = manager.validateActivation() == LicenseStatus.Ok;
        Log.d(TAG, "license: machine_code=" + manager.getMachineCode());
        Log.d(TAG, "license: valid_activation=" + valid);
        Log.d(TAG, "license: activation_type=" + manager.getActivationType());
        if (valid){
            Log.d(TAG, "license: expiry_date=" + manager.getActivationExpiryDate());
        }
        return manager;
    }

    public static FaceId createFaceId(FaceEmbedder faceEmbedder){
        LicenseManager manager = createLicenseManager();
        FaceId faceId = new FaceId(faceEmbedder, manager);
        return faceId;
    }

    public static FaceId createFaceId() {
        LicenseManager manager = createLicenseManager();
        FaceId faceId = new FaceId(manager);
        return faceId;
    }

    public static FaceDetector createFaceDetector(){
        LicenseManager manager = createLicenseManager();
        FaceDetector faceDetector = new FaceDetector(manager);
        return faceDetector;
    }

    public static FaceEmbedder createFaceEmbedder(){
        LicenseManager manager = createLicenseManager();
        FaceEmbedder faceEmbedder = new FaceEmbedder(manager);
        return faceEmbedder;
    }

    public static Utils createUtils(){
        LicenseManager manager = createLicenseManager();
        Utils utils = new Utils(manager);
        return utils;
    }
}
