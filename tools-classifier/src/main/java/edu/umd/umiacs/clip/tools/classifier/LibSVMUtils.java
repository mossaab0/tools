/**
 * Tools Classifier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.umd.umiacs.clip.tools.classifier;

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toMap;

/**
 *
 * @author Mossaab Bagdouri
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
        if (features.isEmpty()) {
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

    public static Map<Integer, Pair<Float, Float>> learnScalingModel(List<String> training) {
        return training.stream().map(LibSVMUtils::split).map(Triple::getMiddle).
                flatMap(List::stream).
                collect(groupingBy(Pair::getKey, ConcurrentHashMap::new,
                        reducing(Pair.of(0f, 0f),
                                pair -> Pair.of(pair.getRight(), pair.getRight()),
                                (p1, p2) -> Pair.of(min(p1.getLeft(), p2.getLeft()),
                                        max(p1.getRight(), p2.getRight()))))).
                entrySet().stream().
                filter(entry -> entry.getValue().getLeft().floatValue() != entry.getValue().getRight().floatValue()).
                collect(toMap(Entry::getKey,
                        entry -> Pair.of(entry.getValue().getLeft(),
                                entry.getValue().getRight() - entry.getValue().getLeft())));
    }

    public static Map<Integer, Pair<Double, Double>> learnZscoringModel(List<String> training) {
        return training.stream().map(LibSVMUtils::split).map(Triple::getMiddle).
                flatMap(List::stream).
                collect(groupingBy(Pair::getKey, ConcurrentHashMap::new,
                        reducing(new ArrayList<Float>(),
                                pair -> asList(pair.getRight()),
                                (p1, p2) -> Stream.of(p1, p2).flatMap(List::stream).collect(toList())))).
                entrySet().stream().
                map(entry -> Pair.of(entry.getKey(), entry.getValue().stream().mapToDouble(f -> f).toArray())).
                collect(toMap(Entry::getKey,
                        entry -> Pair.of(new Mean().evaluate(entry.getValue()), new StandardDeviation().evaluate(entry.getValue()))));
    }

    public static List<String> applyScalingModel(Map<Integer, Pair<Float, Float>> model, List<String> examples) {
        return examples.stream().map(LibSVMUtils::split).
                map(triple -> triple.getLeft() + String.join(" ",
                        triple.getMiddle().stream().
                        map(pair -> Pair.of(pair.getLeft(),
                                !model.containsKey(pair.getLeft()) ? 1f
                                : ((pair.getRight() - model.get(pair.getLeft()).getLeft())
                                / model.get(pair.getLeft()).getRight()))).
                        //map(pair -> Pair.of(pair.getKey(), 2 * pair.getRight() - 1)).
                        filter(pair -> pair.getValue() != 0f).
                        map(pair -> pair.getLeft() + ":" + pair.getRight()).collect(toList()))
                        + triple.getRight()).
                collect(toList());
    }

    public static List<String> applyZscoringModel(Map<Integer, Pair<Double, Double>> model, List<String> examples) {
        return examples.stream().map(LibSVMUtils::split).
                map(triple -> triple.getLeft() + String.join(" ",
                        triple.getMiddle().stream().
                        map(pair -> Pair.of(pair.getLeft(),
                                (!model.containsKey(pair.getLeft()) || model.get(pair.getLeft()).getRight() == 0) ? 1f
                                : ((pair.getRight() - model.get(pair.getLeft()).getLeft())
                                / model.get(pair.getLeft()).getRight()))).
                        filter(pair -> pair.getRight().floatValue() != 0f).
                        map(pair -> pair.getLeft() + ":" + pair.getRight().floatValue()).collect(toList()))
                        + triple.getRight()).
                collect(toList());
    }

    public static Triple<String, List<Pair<Integer, Float>>, String> split(String line) {
        String[] fields = line.split(" ");
        if (fields[0].isEmpty() || fields.length == 1) {
            return Triple.of(fields[0], asList(), "");
        }
        StringBuilder prefix = new StringBuilder();
        int i = 0;
        for (; i < fields.length; i++) {
            int index = fields[i].indexOf(":");
            if (index < 0 || !fields[i].substring(0, index).matches("[0-9]+")) {
                prefix.append(fields[i]).append(" ");
            } else {
                break;
            }
        }

        List<Pair<Integer, Float>> pairs = new ArrayList<>();

        for (; i < fields.length; i++) {
            if (fields[i].startsWith("#")) {
                break;
            }
            int index = fields[i].indexOf(":");
            pairs.add(Pair.of(new Integer(fields[i].substring(0, index)), new Float(fields[i].substring(index + 1))));
        }

        StringBuilder suffix = new StringBuilder();
        for (; i < fields.length; i++) {
            suffix.append(" ").append(fields[i]);
        }

        return Triple.of(prefix.toString(), pairs, suffix.toString());
    }

    public static Pair<List<String>, List<String>> scale(Pair<List<String>, List<String>> pair) {
        Map<Integer, Pair<Float, Float>> model = learnScalingModel(pair.getLeft());
        return Pair.of(applyScalingModel(model, pair.getLeft()), applyScalingModel(model, pair.getRight()));
    }

    public static List<String> filter(List<String> training, int threshold) {
        Set<Integer> filtered = training.stream().
                flatMap(line -> Stream.of(line.split(" ")).skip(1)).
                map(pair -> new Integer(pair.substring(0, pair.indexOf(":")))).
                collect(groupingBy(i -> i, counting())).
                entrySet().stream().filter(entry -> entry.getValue() >= threshold).
                map(Entry::getKey).collect(toSet());
        return training.stream().map(line -> line.split(" ")).
                map(fields -> fields[0] + " " + String.join(" ",
                        Stream.of(fields).skip(1).
                        filter(pair -> filtered.contains(new Integer(pair.substring(0, pair.indexOf(":"))))).
                        collect(toList()))).
                map(String::trim).
                collect(toList());
    }
}
