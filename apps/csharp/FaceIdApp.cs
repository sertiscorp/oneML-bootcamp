using OneML.Face;

class FaceIdApp {
    static void Print(FaceIdResult result) {
        string precision = "F6";
        Console.WriteLine("status: " + result.GetReturnStatus());
        Console.WriteLine("Is identifiable: " + BooleanToInt(result.IsIdentifiable()));
        Console.WriteLine("Id: " + result.GetId());
        Console.WriteLine("Nearest node distance: " + result.GetNearestNodeDistance().ToString(precision));
        Console.WriteLine("Combined distance: " + result.GetCombinedDistance().ToString(precision));
    }

    static void Print(FaceIdResultList results) {
        foreach (FaceIdResult result in results) {
            Print(result);
        }
    }

    static int BooleanToInt(bool val) {
        return val ? 1 : 0;
    }

    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        FaceEmbedder embedder = new FaceEmbedder(manager);
        FaceId faceId = new FaceId(embedder, manager);
        Utils utils = new Utils(manager);

        string basePath = @"../../assets/images";

        string path1 = basePath + @"/register-set/Colin_Powell/colin_powell_0074.jpg";
        Image img1 = utils.ReadImageCV(path1);

        string path2 = basePath + @"/register-set/Colin_Powell/colin_powell_0078.jpg";
        Image img2 = utils.ReadImageCV(path2);

        string path3 = basePath + @"/register-set/George_Robertson/george_robertson_0000.jpg";
        Image img3 = utils.ReadImageCV(path3);

        string path4 = basePath + @"/register-set/George_Robertson/george_robertson_0002.jpg";
        Image img4 = utils.ReadImageCV(path4);

        // isTheSamePerson
        bool same1 = faceId.IsTheSamePerson(img1, img2);
        Console.WriteLine("Is the same person: " + BooleanToInt(same1));

        FaceEmbedderResult result = embedder.Embed(img1);
        SamePersonResult sameResult = faceId.IsTheSamePerson(result.GetEmbedding(), img2);
        Console.WriteLine("Is the same person: " + BooleanToInt(sameResult.first));

        // registerId
        int size1 = faceId.RegisterId("Colin_Powell", result.GetEmbedding());
        Console.WriteLine("Registered size is: " + size1);

        Embedding emb = faceId.RegisterId("George_Robertson", img3);
        string embSample = "[";
        for (int i = 0; i < 5; i++) {
            embSample += emb[i].ToString("F6") + ", ";
        }
        embSample = embSample.Substring(0, embSample.Length-2) + "]";
        Console.WriteLine("Registered emb is: " + embSample);

        // predict
        Print(faceId.Predict(img2));

        // updateEmbeddingDynamically
        faceId.UpdateEmbeddingDynamically("George_Robertson", img3, emb);
        Print(faceId.Predict(img4));

        faceId.UpdateEmbeddingDynamically("Colin_Powell", sameResult.second, result.GetEmbedding());

        Print(faceId.Predict(img1));

        // updateEmbedding
        faceId.UpdateEmbedding("George_Robertson", emb);
        Print(faceId.Predict(img3));

        // removeId
        faceId.RemoveId("George_Robertson");
        faceId.RemoveId("Colin_Powell");

        // getIds
        IdList idList = faceId.GetIds();
        Console.WriteLine("Gallery size: " + idList.Count());

        UsageReport report = faceId.GetUsage();
        report.ToLog();
    }
}