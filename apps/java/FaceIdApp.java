import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;

import java.util.List;
import java.util.StringJoiner;

import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.util.function.Supplier;

import org.sertiscorp.oneml.face.*;

public class FaceIdApp {

    static String joinAsString(Iterable<Double> it) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (Double i: it) {
            joiner.add(String.format("%.6f", i));
        }

        return joiner.toString();
    }

    static void print(FaceIdResult result) {
        System.out.println("Is identifiable: " + booleanToInt(result.isIdentifiable()));
        System.out.println("Id: " + result.getId());
        System.out.println(String.format("Nearest node distance: %.6f", result.getNearestNodeDistance()));
        System.out.println(String.format("Combined distance: %.6f", result.getCombinedDistance()));
    }

    static void print(FaceIdResultList results) {
        for (FaceIdResult result: results) {
            print(result);
        }
    }

    static int booleanToInt(boolean val) {
        return val ? 1 : 0;
    }

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("", "../../assets/images");

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();
        Utils utils = new Utils(manager);

        String path1 = basePath + "/register-set/Colin_Powell/colin_powell_0074.jpg";
        Image img1 = utils.readImageCV(path1);

        String path2 = basePath + "/register-set/Colin_Powell/colin_powell_0078.jpg";
        Image img2 = utils.readImageCV(path2);

        String path3 = basePath + "/register-set/George_Robertson/george_robertson_0000.jpg";
        Image img3 = utils.readImageCV(path3);

        String path4 = basePath + "/register-set/George_Robertson/george_robertson_0002.jpg";
        Image img4 = utils.readImageCV(path4);

        FaceEmbedder embedder = new FaceEmbedder(manager);
        FaceId faceId = new FaceId(embedder, manager);

        // isTheSamePerson
        boolean same1 = faceId.isTheSamePerson(img1, img2);
        System.out.println("Is the same person: " + booleanToInt(same1));

        FaceEmbedderResult result = embedder.embed(img1);
        SamePersonResult sameResult = faceId.isTheSamePerson(result.getEmbedding(), img2);
        System.out.println("Is the same person: " + booleanToInt(sameResult.getSame()));

        // registerId
        int size1 = faceId.registerId("Colin_Powell", result.getEmbedding());
        System.out.println("Registered size is: " + size1);

        Embedding emb_ = faceId.registerId("George_Robertson", img3);
        float[] emb = emb_.toArray();
        Supplier<DoubleStream> supplier = () -> IntStream.range(0, emb.length).mapToDouble(i -> emb[i]);
        List<Double> sample = supplier.get().limit(5).boxed().collect(Collectors.toList());
        System.out.println("Registered emb is: " + joinAsString(sample));

        // predict
        print(faceId.predict(img2));

        // updateEmbeddingDynamically
        faceId.updateEmbeddingDynamically("George_Robertson", img3, emb);
        print(faceId.predict(img4));

        faceId.updateEmbeddingDynamically("Colin_Powell", sameResult.getEmbedding(), result.getEmbedding());

        print(faceId.predict(img1));

        // updateEmbedding
        faceId.updateEmbedding("George_Robertson", emb);
        print(faceId.predict(img3));

        // removeId
        faceId.removeId("George_Robertson");
        faceId.removeId("Colin_Powell");

        // getIds
        IdList idList = faceId.getIds();
        System.out.println("Gallery size: " + idList.size());

        UsageReport report = faceId.getUsage();
        report.toLog();

        embedder.delete();
        faceId.delete();
        utils.delete();
    }
}