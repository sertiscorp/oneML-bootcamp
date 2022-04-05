import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sertiscorp.oneml.face.*;

public class FaceDetectorApp {

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("", "../../assets/images");
        String path = basePath + "/face-detect-set/face/0.jpg";

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();
        FaceDetector detector = new FaceDetector(manager);
        Utils utils = new Utils(manager);

        Image img = utils.readImageCV(path);
        FaceDetectorResult result = detector.detect(img);
        System.out.println("Faces: " + result.getSize());

        BBoxList boxes = result.getBBoxes();
        Landmark5List landmarks = result.getLandmarks();
        ScoreList scores = result.getScores();
        FacePoseList poses = result.getPoses();

        for (int i = 0; i < result.getSize(); i++) {
            System.out.println("Face " + i);
            System.out.println(String.format("Score: %.6f", scores.get(i)));
            System.out.println("Pose: " + poses.get(i));

            BBox box = boxes.get(i);
            String out = String.format("BBox: [(%.6f, %.6f), (%.6f, %.6f)]", box.getTop(), box.getLeft(), box.getBottom(), box.getRight());
            System.out.println(out);

            int n_landmarks = Face.getLandmarkSize();
            Landmark5 landmark = landmarks.get(i);
            for (int j = 0; j < n_landmarks; j++) {
                System.out.println(String.format("Landmark %d: (%.6f, %.6f)", j, landmark.getX().get(j), landmark.getY().get(j)));
            }
        }
    }
}