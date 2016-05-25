package edu.umd.umiacs.clip.tools.classifier;

import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.Long.min;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.ops.transforms.Transforms;

/**
 *
 * @author mossaab
 */
public class D2VFeatureExtracter {

    private final WordVectors word2vec;

    private TObjectIntMap<String> DF;
    private long N;

    public D2VFeatureExtracter(String word2vecPath, String vocabPath) {
        DF = new TObjectIntHashMap<>();
        List<String> lines = readAllLines(vocabPath);
        N = new Long(lines.get(0).split(" ")[1]);
        for (int i = 1; i < lines.size(); i++) {
            String[] pair = lines.get(i).split(" ");
            int df = new Integer(pair[1]);
            DF.put(pair[0], df);
        }
        try {
            word2vec = WordVectorSerializer.loadGoogleModel(new File(word2vecPath), false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public D2VFeatureExtracter(String word2vecPath) {
        try {
            word2vec = WordVectorSerializer.loadGoogleModel(new File(word2vecPath), false);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public List<Double> getStemmedVector(String text) {
        INDArray vector = Nd4j.zeros(200);
        Stream.of(text.split("\\s+")).parallel().
                filter(word -> word2vec.vocab().containsWord(word)).
                map(word2vec::getWordVectorMatrix).forEach(vector::addi);
        return getNormalized(vector);
    }

    public List<Double> getIDFStemmedVector(String text) {
        INDArray vector = Nd4j.zeros(200);
        Map<String, Double> tfidf = tfidf(text);
        Stream.of(text.split("\\s+")).parallel().
                filter(word -> word2vec.vocab().containsWord(word)).
                map(word -> word2vec.getWordVectorMatrix(word).mul(tfidf.get(word))).
                forEach(vector::addi);
        return getNormalized(vector);
    }

    private static List<Double> getNormalized(INDArray vector) {
        INDArray normalized = Transforms.unitVec(vector);
        return range(0, 200).boxed().
                map(normalized::getDouble).collect(toList());
    }

    private Map<String, Integer> tf(String doc) {
        return Stream.of(doc.split(" ")).parallel().
                filter(word -> !word.isEmpty()).
                collect(groupingBy(identity(), reducing(0, e -> 1, Integer::sum)));
    }

    private Map<String, Double> tfidf(String doc) {
        return tf(doc).entrySet().stream().
                collect(toMap(entry -> entry.getKey(),
                                entry -> entry.getValue() * idf(df(entry.getKey()))));
    }

    private long df(String word) {
        return DF.containsKey(word) ? min(N, DF.get(word)) : 1;
    }

    private double idf(double df) {
        return Math.log(N / df);
    }

    public String getSimilarWordWithHighDF(String word) {
        if (!word2vec.vocab().containsWord(word)) {
            return word;
        }
        String candidate = word2vec.wordsNearest(word, 1).stream().collect(toList()).get(0);
        return df(candidate) > df(word) ? candidate : word;
    }
}
