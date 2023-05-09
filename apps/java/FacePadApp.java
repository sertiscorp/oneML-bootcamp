import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.sertiscorp.oneml.face.*;

public class FacePadApp {

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("", "../../assets/images");
        String path = basePath + "/pad-rgb-set/spoof/1.jpg";

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();
        FacePad padRgb = new FacePad(PadType.Rgb, manager);
        FacePad padPaper = new FacePad(PadType.Paper, manager);
        Utils utils = new Utils(manager);

        // PAD RGB
        Image img = utils.readImageCV(path);
        FacePadResult result = padRgb.classify(img);

        System.out.println("Status: " + result.getReturnStatus());
        System.out.println(String.format("Spoof probability: %.6f", result.getSpoofProb()));
        System.out.println("Spoof classification: " + result.isSpoof());

        // PAD PAPER
        path = basePath + "/pad-paper-set/spoof/1.jpg";
        img = utils.readImageCV(path);
        result = padPaper.classify(img);

        System.out.println("Status: " + result.getReturnStatus());
        System.out.println(String.format("Spoof probability: %.6f", result.getSpoofProb()));
        System.out.println("Spoof classification: " + result.isSpoof());

        padPaper.delete();
        padRgb.delete();
        utils.delete();
    }
}