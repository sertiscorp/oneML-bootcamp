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

public class FaceEmbedderApp {

    static String joinAsString(Iterable<Double> it) {
        StringJoiner joiner = new StringJoiner(", ", "[", "]");
        for (Double i: it) {
            joiner.add(String.format("%.6f", i));
        }

        return joiner.toString();
    }

    public static void main(String[] args) throws IOException {
        Path basePath = Paths.get("", "../../assets/images");
        String path = basePath + "/register-set/Colin_Powell/colin_powell_0074.jpg";

        LicenseManager manager = new LicenseManager();
        manager.activateTrial();
        FaceEmbedder embedder = new FaceEmbedder(manager);
        Utils utils = new Utils(manager);

        Image img = utils.readImageCV(path);
        FaceEmbedderResult result = embedder.embed(img);

        System.out.println("Embedding size: " + Face.getEmbeddingSize());

        float[] embedding = result.getEmbedding();
        Supplier<DoubleStream> supplier = () -> IntStream.range(0, embedding.length).mapToDouble(i -> embedding[i]);
        List<Double> sample = supplier.get().limit(5).boxed().collect(Collectors.toList());

        System.out.println("Embedding sample: " + joinAsString(sample));
        System.out.println(String.format("Embedding sum: %.5f", supplier.get().sum()));

        UsageReport report = embedder.getUsage();
        report.toLog();
    }
}