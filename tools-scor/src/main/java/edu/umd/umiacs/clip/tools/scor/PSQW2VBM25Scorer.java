/**
 * Tools Scor
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
package edu.umd.umiacs.clip.tools.scor;

import gnu.trove.map.TObjectIntMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import static java.util.function.Function.identity;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Mossaab Bagdouri
 */
public class PSQW2VBM25Scorer extends W2VBM25Scorer {

    private final Map<String, List<Map<String, Double>>> weightedQueries = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Double>> weightedTerms = new ConcurrentHashMap<>();
    
    public PSQW2VBM25Scorer(String dfPath) {
        super(dfPath);
    }

    public PSQW2VBM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
    }

    @Override
    public double score(String query, String text) {
        return scoreWeighted(getWeightedQuery(query), text);
    }

    private List<Map<String, Double>> getWeightedQuery(String query) {
        List<Map<String, Double>> weightedQuery = weightedQueries.get(query);
        if (weightedQuery == null) {
            weightedQuery = Stream.of(query.split(" ")).
                    filter(word -> !word.isEmpty()).distinct().
                    map(this::getWeightedTerm).collect(toList());
            weightedQueries.put(query, weightedQuery);
        }
        return weightedQuery;
    }

    private Map<String, Double> getWeightedTerm(String term) {
        Map<String, Double> oldMap = weightedTerms.get(term);
        if (oldMap == null) {
            Map<String, Double> map = new HashMap<>();
            int df = df(term);
            map.put(term, 1d);
            if (df > 1) {
                Collection<String> words = getWord2Vec().wordsNearest(term, nearestTerms);
                words.forEach(word -> map.put(word, getWord2Vec().similarity(term, word)));
                double sum = map.values().parallelStream().mapToDouble(d -> d).sum();
                map.keySet().forEach(key -> map.put(key, map.get(key) / sum));
            }
            weightedTerms.put(term, map);
            oldMap = map;
        }
        return oldMap;
    }

    private double scoreWeighted(List<Map<String, Double>> wightedQuery, String text) {
        Map<String, Integer> docTerms = Stream.of(text.split(" ")).
                filter(word -> !word.isEmpty()).
                collect(groupingBy(identity(), reducing(0, e -> 1, Integer::sum)));
        int length = docTerms.values().stream().mapToInt(f -> f).sum();

        return wightedQuery.parallelStream().mapToDouble(weightedTerm -> {
            List<Triple<Double, Double, Double>> list = weightedTerm.entrySet().stream().
                    filter(entry -> docTerms.containsKey(entry.getKey())).
                    map(entry -> ImmutableTriple.of(docTerms.get(entry.getKey()).doubleValue(), (double) df(entry.getKey()), entry.getValue())).
                    collect(toList());
            double averageTF = list.parallelStream().mapToDouble(triple -> triple.getLeft() * triple.getRight()).sum();
            double averageDF = list.parallelStream().mapToDouble(triple -> triple.getMiddle() * triple.getRight()).sum();
            return bm25(averageTF, averageDF, length);
        }).sum();
    }

    public PSQW2VBM25Scorer loadWeights(String file) {
        readAllLines(file).stream().forEach(line -> {
            int index = line.indexOf("=>");
            String key = line.substring(0, index).trim();
            List<Map<String, Double>> value = new ArrayList<>();
            while (line.contains("{")) {
                line = line.substring(line.indexOf("{") + 1);
                Map<String, Double> map = Stream.of(line.substring(0, line.indexOf("}")).trim().split(", ")).
                        collect(Collectors.toMap(pair -> pair.substring(0, pair.indexOf("=")),
                                pair -> new Double(pair.substring(pair.indexOf("=") + 1))));
                value.add(map);
            }
            weightedQueries.put(key, value);
        });
        return this;
    }
}
