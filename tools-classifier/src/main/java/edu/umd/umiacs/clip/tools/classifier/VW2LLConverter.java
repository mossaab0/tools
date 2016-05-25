package edu.umd.umiacs.clip.tools.classifier;

import static edu.umd.umiacs.clip.tools.io.AllFiles.lines;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

public class VW2LLConverter {

    private static TObjectIntMap<String> map = new TObjectIntHashMap<>();
    private static final Set<String> set = new HashSet<>();

    private static int convertFeature(String feature) {
        return map.get(feature);
    }

    public static void addFeatures(String line) {
        for (String chunk : line.split("\\|")) {
            chunk = chunk.trim();
            if (!chunk.isEmpty()) {
                String words[] = chunk.split("\\s+");
                String prefix = words[0] + "_";
                for (int i = 1; i < words.length; i++) {
                    int index = words[i].indexOf(":");
                    if (index > 0) {
                        words[i] = words[i].substring(0, index);
                    }
                    set.add(prefix + words[i]);
                }
            }
        }
    }

    public static String convert(String line) {
        StringBuilder sb = new StringBuilder();
        for (String chunk : line.split("\\|")) {
            chunk = chunk.trim();
            if (!chunk.isEmpty()) {
                String words[] = chunk.split("\\s+");
                String prefix = words[0] + "_";
                for (int i = 1; i < words.length; i++) {
                    int index = words[i].indexOf(":");
                    String value;
                    if (index > 0) {
                        value = words[i].substring(index);
                        words[i] = words[i].substring(0, index);
                    } else {
                        value = ":1";
                    }
                    int feature = convertFeature(prefix + words[i]);
                    if (feature >= 0) {
                        sb.append(" ").append(feature).append(value);
                    }
                }
            }
        }
        Map<Integer, Float> m = new HashMap<>();
        Stream.of(sb.toString().trim().split("\\s+")).map(fv -> fv.split(":"))
                .forEach(kv -> {
                    int key = 1 + (new Integer(kv[0]));
                    m.put(key, new Float(kv[1]) + m.getOrDefault(key, 0f));
                });
        return line.substring(0, line.indexOf("\t")) + "\t" + m.entrySet().stream()
                .sorted((o1, o2) -> Integer.compare(o1.getKey(), o2.getKey()))
                .map(entry -> " " + entry.getKey() + ":" + entry.getValue())
                .reduce("", String::concat).trim();
    }

    public static void loadMap(String path) throws Exception {
        map = new TObjectIntHashMap<>(set.size());
        AtomicInteger i = new AtomicInteger();
        lines(path).forEach(line -> {
            if (set.contains(line)) {
                map.put(line, i.get());
            }
            i.incrementAndGet();
        });
        set.clear();
    }
}
