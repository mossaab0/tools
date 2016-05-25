package edu.umd.umiacs.clip.tools.classifier;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author mossaab
 */
public class LibSVMUtils {

    public static Map<Integer, Double> sum(Map<Integer, Double> map1, Map<Integer, Double> map2) {
        Map<Integer, Double> map = new HashMap<>(map1);
        map2.entrySet().stream().forEach(entry -> map.put(entry.getKey(),
                entry.getValue() + map.getOrDefault(entry.getKey(), 0d)));
        return map;
    }

    public static String sum(String features1, String features2) {
        return asString(sum(asMap(features1), asMap(features2)));
    }

    public static List<String> sum(List<String> features1, List<String> features2) {
        return range(0, features1.size()).boxed().
                map(i -> sum(features1.get(i), features2.get(i))).collect(toList());
    }

    public static Map<Integer, Double> asMap(String features) {
        if(features.isEmpty()){
            return new HashMap<>();
        }
        return Stream.of(features.trim().split("\\s+")).
                map(p -> p.split(":")).
                collect(toMap(p -> new Integer(p[0]), p -> new Double(p[1])));
    }

    public static List<Map<Integer, Double>> asMap(List<String> features) {
        return features.stream().map(LibSVMUtils::asMap).collect(toList());
    }

    public static Map<Integer, Double> multiplyValues(Map<Integer, Double> map, double val) {
        return map.entrySet().stream().
                collect(toMap(Entry::getKey, entry -> val * entry.getValue()));
    }

    public static String multiplyValues(String features, double val) {
        return asString(multiplyValues(asMap(features), val));
    }

    public static String asString(Map<Integer, Double> map) {
        return String.join(" ", map.entrySet().stream().
                sorted((e1, e2) -> Integer.compare(e1.getKey(), e2.getKey())).
                filter(entry -> entry.getValue() != 0).
                map(entry -> entry.getKey() + ":" + entry.getValue()).
                collect(toList()));
    }

    public static List<String> asString(List<Map<Integer, Double>> map) {
        return map.stream().map(LibSVMUtils::asString).collect(toList());
    }

    public static List<String> addValues(Pair<Double, List<String>>... input) {
        return addValues(asList(input));
    }

    public static List<String> addValues(List<String>... input) {
        return addValues(Stream.of(input).map(list -> Pair.of(1d, list)).collect(toList()));
    }

    public static Map<Integer, Double> addToKeys(Map<Integer, Double> map, int add) {
        return map.entrySet().stream().
                collect(toMap(entry -> entry.getKey() + add, Entry::getValue));
    }

    public static List<Map<Integer, Double>> appendFeatures(List<Map<Integer, Double>> list1, List<Map<Integer, Double>> list2) {
        if (list1.isEmpty()) {
            return list2;
        } else if (list2.isEmpty()) {
            return list1;
        }
        int max = list1.parallelStream().map(Map::keySet).
                flatMap(Set::parallelStream).mapToInt(i -> i).max().getAsInt();
        return range(0, list1.size()).boxed().
                map(i -> sum(list1.get(i), addToKeys(list2.get(i), max))).
                collect(toList());
    }

    public static List<Map<Integer, Double>> appendFeatures(Collection<List<Map<Integer, Double>>> list) {
        return list.parallelStream().reduce(new ArrayList<>(),
                (list1, list2) -> appendFeatures(list1, list2));
    }

    public static List<String> appendFeatures(List<String>... list) {
        return asString(appendFeatures(Stream.of(list).parallel().
                map(LibSVMUtils::asMap).collect(toList())));
    }

    public static List<String> addValues(Collection<Pair<Double, List<String>>> input) {
        return range(0, input.stream().findAny().get().getRight().size()).boxed().map(i -> {
            return input.parallelStream().
                    map(pair -> multiplyValues(asMap(pair.getRight().get(i)), pair.getLeft())).
                    reduce(new HashMap<>(), (map1, map2) -> sum(map1, map2));
        }).map(LibSVMUtils::asString).collect(toList());
    }

    public static String appendLabel(String features, Object label) {
        return label.toString().trim() + " " + features.trim();
    }

    public static List<String> appendLabel(List<String> features, List label) {
        return range(0, features.size()).boxed().
                map(i -> appendLabel(features.get(i), label.get(i))).collect(toList());
    }

    public static Pair<String, String> split(String line) {
        line = line.replaceAll("\\s+", " ").trim();
        int index = line.indexOf(" ");
        return Pair.of(line.substring(0, index), line.substring(index).trim());
    }

    public static List<Pair<String, String>> split(List<String> lines) {
        return range(0, lines.size()).boxed().map(i -> split(lines.get(i))).collect(toList());
    }
}
