using OneML.Face;

class FaceEmbedderApp {
    static void PrintEmbedding(FaceEmbedderResult result) {
        Embedding emb = result.GetEmbedding();
        Console.WriteLine("Embedding size: " + emb.Count());

        string precision = "F6";
        string embSample = "[";
        float embSum = 0;
        for (int i = 0; i < emb.Count(); i++) {
            if (i < 5) {
                embSample += emb[i].ToString(precision) + ", ";
            }
            embSum += emb[i];
        }
        embSample = embSample.Substring(0, embSample.Length-2) + "]";

        Console.WriteLine("Embedding sample: " + embSample);
        Console.WriteLine("Embedding sum: " + embSum.ToString("F4"));
        Console.WriteLine("status: " + result.GetReturnStatus());
    }

    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        using (FaceEmbedder embedder = new FaceEmbedder(manager))
        using (Utils utils = new Utils(manager))
        {
            string basePath = @"../../assets/images/";
            string path = basePath + @"register-set/Colin_Powell/colin_powell_0074.jpg";
            Image img = utils.ReadImageCV(path);
            FaceEmbedderResult result1 = embedder.Embed(img);
            Console.WriteLine("Original Embedding");
            PrintEmbedding(result1);

            FaceEmbedderResult result2 = embedder.Embed(img, true);
            Console.WriteLine("Flipped Image Embedding");
            PrintEmbedding(result2);

        }
    }
}