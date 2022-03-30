import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sertiscorp.oneml.face.*;

public class FaceVerificationApp {

    static int booleanToInt(boolean val) {
        return val ? 1 : 0;
    }

    public static void main(String[] args) throws IOException {
        LicenseManager manager = new LicenseManager();
        manager.activateTrial();

        Path basePath = Paths.get("", "../../assets/images");
        File file1 = new File(basePath + "/register-set/Colin_Powell/colin_powell_0074.jpg");
        File file2 = new File(basePath + "/evaluate-set/Colin_Powell/colin_powell_0097.jpg");
        File file3 = new File(basePath + "/register-set/George_Robertson/george_robertson_0000.jpg");
        File file4 = new File(basePath + "/evaluate-set/George_Robertson/george_robertson_0009.jpg");

        FaceEmbedder embedder = new FaceEmbedder(manager);
        FaceId faceId = new FaceId(embedder, manager);
        FaceDetector detector = new FaceDetector(manager);
        Utils utils = new Utils(manager);

        Image img1 = utils.readImageCV(file1.getAbsolutePath());
        Image img2 = utils.readImageCV(file2.getAbsolutePath());
        Image img3 = utils.readImageCV(file3.getAbsolutePath());
        Image img4 = utils.readImageCV(file4.getAbsolutePath());

        ImageBatch register_batch = new ImageBatch();
        register_batch.add(img1);
        register_batch.add(img3);

        ImageBatch eval_batch = new ImageBatch();
        eval_batch.add(img2);
        eval_batch.add(img4);

        FaceDetectorResultList register_results = detector.detect(register_batch);
        FaceDetectorResultList eval_results = detector.detect(eval_batch);

        Image align_img1 = utils.cropAlignFaceLandmark(img1, register_results.get(0).getLandmarks().get(0));
        Image align_img3 = utils.cropAlignFaceLandmark(img3, register_results.get(1).getLandmarks().get(0));
        Image align_img2 = utils.cropAlignFaceLandmark(img2, eval_results.get(0).getLandmarks().get(0));
        Image align_img4 = utils.cropAlignFaceLandmark(img4, eval_results.get(1).getLandmarks().get(0));

        faceId.registerId("colin_powell", align_img1);
        faceId.registerId("george_robertson", align_img3);

        ImageBatch predict_batch = new ImageBatch();
        predict_batch.add(align_img2);
        predict_batch.add(align_img4);
        FaceIdResultList results = faceId.predict(predict_batch);

        // Run with registration & predict style to print scores for validating with other language applications
        // since isTheSamePerson API doesn't provide any score.
        System.out.println("First person nearest node distance: " + String.format("%.8f", results.get(0).getNearestNodeDistance()));
        System.out.println("Second person nearest node distance: " + String.format("%.8f", results.get(1).getNearestNodeDistance()));
        System.out.println("First person: " + results.get(0).getId());
        System.out.println("Second person: " + results.get(1).getId());

        // Also test the actual face verification API (IsTheSamePerson)
        boolean same1 = faceId.isTheSamePerson(img1, img2);
        System.out.println("Is the same person (colin_powell): " + booleanToInt(same1));

        boolean same2 = faceId.isTheSamePerson(img3, img4);
        System.out.println("Is the same person (george_robertson): " + booleanToInt(same2));
    }
}