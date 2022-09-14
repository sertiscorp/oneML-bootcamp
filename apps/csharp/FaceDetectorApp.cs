using OneML.Face;

class FaceDetectorApp {
    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        using (FaceDetector detector = new FaceDetector(manager))
        using (Utils utils = new Utils(manager))
        {
            string basePath = @"../../assets/images/";
            string path = basePath + @"face-detect-set/face/0.jpg";
            Image image = utils.ReadImageCV(path);
            FaceDetectorResult result = detector.Detect(image);
            Console.WriteLine("Faces: " + result.GetSize());

            ScoreList scores = result.GetScores();
            BBoxList bboxes = result.GetBBoxes();
            Landmark5List landmarks = result.GetLandmarks();
            FacePoseList poses = result.GetPoses();
            StatusCodeList statuses = result.GetReturnStatus();

            string precision = "F6";
            for (int i = 0; i < result.GetSize(); i++) {
                Console.WriteLine("Face " + i);
                Console.WriteLine("status: " + statuses[i]);
                Console.WriteLine("Score: " + scores[i].ToString(precision));
                Console.WriteLine("Pose: " + poses[i]);

                BBox box = bboxes[i];
                string boxString = "BBox: [(" + box.top.ToString(precision) + ", " + box.left.ToString(precision) + ", " + box.bottom.ToString(precision) + ", " + box.right.ToString(precision) + ")]";
                Console.WriteLine(boxString);

                Landmark5 landmark = landmarks[i];
                for (int j = 0; j < Face.GetLandmarkSize(); j++) {
                    Console.WriteLine("Landmark " + j + ": (" + landmark.x[j].ToString(precision) + ", " + landmark.y[j].ToString(precision) + ")");
                }
            }

            UsageReport report = detector.GetUsage();
            report.ToLog();
        }
    }
}