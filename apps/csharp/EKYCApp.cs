using OneML.Face;

class EKYCApp {
    static EKYC ekyc = null!;
    static Utils utils = null!;

    static string precision = "F6";

    static void PrintResult(BBox bbox, Landmark5 lm, FacePose pose) {
        string bboxStr = string.Format("BBox: BBox[top={0:F6},left={1:F6},bottom={2:F6},right={3:F6}]", 
                                        bbox.top, bbox.left, bbox.bottom, bbox.right);
        Landmark5Array X = lm.x;
        Landmark5Array Y = lm.y;
        string lmString = string.Format("Landmarks: FaceLandmark5[x1={0:F6},x2={1:F6},x3={2:F6},x4={3:F6},x5={4:F6},y1={5:F6},y2={6:F6},y3={7:F6},y4={8:F6},y5={9:F6}]",
                                        X[0], X[1], X[2], X[3], X[4], Y[0], Y[1], Y[2], Y[3], Y[4]);
        Console.WriteLine(bboxStr);
        Console.WriteLine("Pose: " + pose);
        Console.WriteLine(lmString);
    }

    static void Run(string path1, string path2) {
        Image image1 = utils.ReadImageCV(path1);
        Image image2 = utils.ReadImageCV(path2);

        EKYCOps ops = new EKYCOps(true);
        EKYCResult result = ekyc.Run(image1, image2, ops, ops);

        BBoxList boxes = result.GetBBoxes();
        Landmark5List landmarks = result.GetLandmarks();
        FacePoseList poses = result.GetFacePoses();
        int status = result.GetReturnStatus();
        bool same = result.IsSamePerson();
        float distance = result.GetDistance();

        Console.WriteLine("Status: " + status);
        Console.WriteLine("Same: " + (same ? 1 : 0));
        Console.WriteLine("Distance: " + distance.ToString(precision));

        BBox bbox1 = boxes[0];
        Landmark5 lm1 = landmarks[0];
        FacePose pose1 = poses[0];
        
        Console.WriteLine("Face 1");
        PrintResult(bbox1, lm1, pose1);
        
        BBox bbox2 = boxes[1];
        Landmark5 lm2 = landmarks[1];
        FacePose pose2 = poses[1];
        
        Console.WriteLine("Face 2");
        PrintResult(bbox2, lm2, pose2);
    }

    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        string basePath = @"../../assets/images/";
        string path1 = basePath + @"face-detect-set/face/8.jpg";
        string path2 = basePath + @"face-detect-set/face/9.jpg";

        using (ekyc = new EKYC(manager))
        using (utils = new Utils(manager))
        {
            Run(path1, path2);
        }
    }
}