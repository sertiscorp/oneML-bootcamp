using OneML.Face;

class FaceVerificationApp {
    static int BooleanToInt(bool val) {
        return val ? 1 : 0;
    }

    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateKey();

        FaceEmbedder embedder = new FaceEmbedder(manager);
        FaceDetector detector = new FaceDetector(manager);
        FaceId faceId = new FaceId(embedder, manager);
        Utils utils = new Utils(manager);

        string basePath = @"../../assets/images";
        string file1 = basePath + @"/register-set/Colin_Powell/colin_powell_0074.jpg";
        string file2 = basePath + @"/evaluate-set/Colin_Powell/colin_powell_0097.jpg";
        string file3 = basePath + @"/register-set/George_Robertson/george_robertson_0000.jpg";
        string file4 = basePath + @"/evaluate-set/George_Robertson/george_robertson_0009.jpg";

        Image img1 = utils.ReadImageCV(file1);
        Image img2 = utils.ReadImageCV(file2);
        Image img3 = utils.ReadImageCV(file3);
        Image img4 = utils.ReadImageCV(file4);

        ImageBatch registerBatch = new ImageBatch();
        registerBatch.Add(img1);
        registerBatch.Add(img3);

        ImageBatch evalBatch = new ImageBatch();
        evalBatch.Add(img2);
        evalBatch.Add(img4);

        FaceDetectorResultList registerResults = detector.Detect(registerBatch);
        FaceDetectorResultList evalResults = detector.Detect(evalBatch);

        Image alignImg1 = utils.CropAlignFaceLandmark(img1, registerResults[0].GetLandmarks()[0]);
        Image alignImg3 = utils.CropAlignFaceLandmark(img3, registerResults[1].GetLandmarks()[0]);
        Image alignImg2 = utils.CropAlignFaceLandmark(img2, evalResults[0].GetLandmarks()[0]);
        Image alignImg4 = utils.CropAlignFaceLandmark(img4, evalResults[1].GetLandmarks()[0]);

        faceId.RegisterId("colin_powell", alignImg1);
        faceId.RegisterId("george_robertson", alignImg3);

        ImageBatch predictBatch = new ImageBatch();
        predictBatch.Add(alignImg2);
        predictBatch.Add(alignImg4);
        FaceIdResultList results = faceId.Predict(predictBatch);

        // Run with registration & predict style to print scores for validating with other language applications
        // since isTheSamePerson API doesn't provide any score.
        string precision = "F8";
        Console.WriteLine("First person nearest node distance: " + results[0].GetNearestNodeDistance().ToString(precision));
        Console.WriteLine("Second person nearest node distance: " + results[1].GetNearestNodeDistance().ToString(precision));
        Console.WriteLine("First person: " + results[0].GetId());
        Console.WriteLine("Second person: " + results[1].GetId());

        // Also test the actual face verification API (IsTheSamePerson)
        bool same1 = faceId.IsTheSamePerson(img1, img2);
        Console.WriteLine("Is the same person (colin_powell): " + BooleanToInt(same1));

        bool same2 = faceId.IsTheSamePerson(img3, img4);
        Console.WriteLine("Is the same person (george_robertson): " + BooleanToInt(same2));
    }
}