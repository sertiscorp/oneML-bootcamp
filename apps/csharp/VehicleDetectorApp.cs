using OneML.Alpr;

class VehicleDetectorApp {
    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        using (VehicleDetector detector = new VehicleDetector(manager))
        using (Utils utils = new Utils(manager))
        {
            string basePath = @"../../assets/images/";
            string path = basePath + @"vehicle-detect-set/vehicle/cars.jpg";

            Image img = utils.ReadImageCV(path);

            VehicleDetectorResult result = detector.Detect(img);
            Console.WriteLine("Vehicles: " + result.GetSize());

            ScoreList scores = result.GetScores();
            BBoxList bboxes = result.GetBBoxes();
            StatusCodeList statuses = result.GetReturnStatus();

            string precision = "F6";
            for (int i = 0; i < result.GetSize(); i++) {
                Console.WriteLine("Vehicle " + i);
                Console.WriteLine("status: " + statuses[i]);
                Console.WriteLine("Score: " + scores[i].ToString(precision));

                BBox box = bboxes[i];
                string print = "BBox[top=" + box.top.ToString(precision) + ",left=" + box.left.ToString(precision) + ",bottom=" + box.bottom.ToString(precision) + ",right=" + box.right.ToString(precision) + "]";
                Console.WriteLine(print);
            }

        }
    }
}