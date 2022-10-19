import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sertiscorp.oneml.face.*;

public class EKYCApp {

    static void printResult(BBox bbox, Landmark5 lm, FacePose pose) {
        String bboxStr = String.format("BBox: BBox[top=%.6f,left=%.6f,bottom=%.6f,right=%.6f]", bbox.getTop(), bbox.getLeft(), bbox.getBottom(), bbox.getRight());
        System.out.println(bboxStr);
        System.out.println("Pose: " + pose);
        Landmark5Array X = lm.getX();
        Landmark5Array Y = lm.getY();
        String landmarkStr = String.format("Landmarks: FaceLandmark5[x1=%.6f,x2=%.6f,x3=%.6f,x4=%.6f,x5=%.6f,y1=%.6f,y2=%.6f,y3=%.6f,y4=%.6f,y5=%.6f]",
                                    X.get(0), X.get(1), X.get(2), X.get(3), X.get(4), 
                                    Y.get(0), Y.get(1), Y.get(2), Y.get(3), Y.get(4));
        System.out.println(landmarkStr);
    }

    public static void main(String[] args) throws IOException {

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();

        EKYC ekyc = new EKYC(manager);
        Utils utils = new Utils(manager);

        Path basePath = Paths.get("", "../../assets/images");
        String path1 = basePath + "/face-detect-set/face/8.jpg";
        String path2 = basePath + "/face-detect-set/face/9.jpg";
        Image image1 = utils.readImageCV(path1);
        Image image2 = utils.readImageCV(path2);

        EKYCOps ops = new EKYCOps(true, true);
        EKYCResult result = ekyc.run(image1, image2, ops, ops);

        BBoxList boxes = result.getBBoxes();
        Landmark5List landmarks = result.getLandmarks();
        FacePoseList poses = result.getFacePoses();
        int status = result.getReturnStatus();
        boolean same = result.isSamePerson();
        float distance = result.getDistance();

        System.out.println("Status: " + status);
        System.out.println("Same: " + (same ? 1 : 0));
        System.out.println(String.format("Distance: %.6f", distance));

        BBox bbox1 = boxes.get(0);
        Landmark5 lm1 = landmarks.get(0);
        FacePose pose1 = poses.get(0);
        
        System.out.println("Face 1");
        printResult(bbox1, lm1, pose1);

        BBox bbox2 = boxes.get(1);
        Landmark5 lm2 = landmarks.get(1);
        FacePose pose2 = poses.get(1);
        
        System.out.println("Face 2");
        printResult(bbox2, lm2, pose2);

        UsageReport report = ekyc.getUsage();
        report.toLog();

        ekyc.delete();
        utils.delete();
    }
}