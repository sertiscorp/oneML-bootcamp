import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import org.sertiscorp.oneml.alpr.*;

public class VehicleDetectorApp {

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("", "../../assets/images");
        String path = basePath + "/vehicle-detect-set/vehicle/cars.jpg";

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();

        VehicleDetector detector = new VehicleDetector(manager);
        Utils utils = new Utils(manager);
        Image img = utils.readImageCV(path);

        VehicleDetectorResult result = detector.detect(img);

        System.out.println("Vehicles: " + result.getSize());

        ScoreList scores = result.getScores();
        BBoxList bboxes = result.getBBoxes();

        for (int i = 0; i < result.getSize(); i++) {
            System.out.println("Vehicle " + i);
            System.out.println(String.format("Score: %.6f", scores.get(i)));

            BBox box = bboxes.get(i);
            String out = String.format("BBox[top=%.6f,left=%.6f,bottom=%.6f,right=%.6f]", box.getTop(), box.getLeft(), box.getBottom(), box.getRight());
            System.out.println(out);
        }


    }
}