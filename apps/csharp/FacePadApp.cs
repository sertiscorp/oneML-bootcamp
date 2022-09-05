using OneML.Face;

class FacePadApp {
    static void Main(string[] args) {
        LicenseManager manager = new LicenseManager();
        manager.ActivateTrial();

        FacePad pad_rgb = new FacePad(PadType.Rgb, manager);
        FacePad pad_paper = new FacePad(PadType.Paper, manager);
        Utils utils = new Utils(manager);

        // PAD RGB
        string basePath = @"../../../assets/images/";
        string path = basePath + @"pad-rgb-set/spoof/1.jpg";
        Image image = utils.ReadImageCV(path);
        FacePadResult result = pad_rgb.Classify(image);

        string precision = "F6";
        Console.WriteLine("Status: " + result.GetReturnStatus());
        Console.WriteLine("Spoof probability: " + result.GetSpoofProb().ToString(precision));
        Console.WriteLine("Spoof classification: " + result.IsSpoof());

        UsageReport report = pad_rgb.GetUsage();
        report.ToLog();

        // PAD PAPER
        path = basePath + @"pad-paper-set/spoof/1.jpg";
        image = utils.ReadImageCV(path);
        result = pad_paper.Classify(image);

        Console.WriteLine("Status: " + result.GetReturnStatus());
        Console.WriteLine("Spoof probability: " + result.GetSpoofProb().ToString(precision));
        Console.WriteLine("Spoof classification: " + result.IsSpoof());

        report = pad_paper.GetUsage();
        report.ToLog();
    }
}