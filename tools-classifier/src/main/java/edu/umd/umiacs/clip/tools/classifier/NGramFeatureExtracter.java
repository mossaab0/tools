package edu.umd.umiacs.clip.tools.classifier;

import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import edu.umd.umiacs.clip.tools.lang.LangUtils;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;

/**
 *
 * @author mossaab
 */
public class NGramFeatureExtracter {

    private final int n;
    private final Map<String, Integer> features;

    public NGramFeatureExtracter(int n, Collection<String> collection) {
        this.n = n;
        List<String> list = collection.stream().
                map(line -> LangUtils.ngrams(line.replaceAll("\\s+", " "), 1, n, "_", true)).
                flatMap(line -> Stream.of(line.split(" "))).distinct().
                collect(toList());
        features = range(0, list.size()).boxed().collect(toMap(i -> list.get(i), i -> i + 1));
    }

    public NGramFeatureExtracter(int n, String path) {
        this(n, readAllLines(path));
    }

    public int size() {
        return features.size();
    }

    public String getFeatures(String input) {
        return getFeaturesMap(input, 1).
                entrySet().stream().sorted((p1, p2) -> Double.compare(p1.getKey(), p2.getKey())).
                map(pair -> " " + pair.getKey() + ":" + pair.getValue()).
                reduce("", String::concat).trim();
    }

    public Map<Integer, Double> getFeaturesMap(String input, double val) {
        return Stream.of(LangUtils.ngrams(input.replaceAll("\\s+", " "), 1, n, "_", true).split(" ")).
                filter(features::containsKey).map(features::get).
                collect(groupingBy(identity(), reducing(0d, e -> val, Double::sum)));
    }

    public String getFeatures(List<String> input, double power) {
        return range(0, input.size()).boxed().parallel().
                map(i -> getFeaturesMap(input.get(i), 1 / Math.pow(power, i))).
                flatMap((map -> map.entrySet().stream())).
                collect(groupingBy(Entry::getKey, reducing(0d, Entry::getValue, Double::sum))).
                entrySet().stream().sorted((p1, p2) -> Double.compare(p1.getKey(), p2.getKey())).
                map(pair -> " " + pair.getKey() + ":" + pair.getValue()).
                reduce("", String::concat).trim();
    }
}
