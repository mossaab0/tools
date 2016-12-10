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
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.apache.commons.lang3.tuple.Triple;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.groupingBy;

/**
 *
 * @author Mossaab Bagdouri
 */
public class PSQW2VBM25Scorer extends W2VBM25Scorer {

    public PSQW2VBM25Scorer(String dfPath) {
        super(dfPath);
    }

    public PSQW2VBM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
    }

    @Override
    public Map<String, Integer> getProcessedText(String text) {
        return Stream.of(text.split(" ")).
                filter(word -> !word.isEmpty()).
                collect(groupingBy(identity(), reducing(0, e -> 1, Integer::sum)));
    }

    @Override
    public List<Map<String, Float>> getProcessedQuery(String query) {
        return Stream.of(query.split(" ")).
                filter(word -> !word.isEmpty()).distinct().
                map(this::getWeightedTerm).collect(toList());
    }

    private Map<String, Float> getWeightedTerm(String term) {
        Map<String, Float> map = new HashMap<>();
        int df = df(term);
        map.put(term, 1f);
        if (df > 1) {
            Collection<String> words = getWord2Vec().wordsNearest(term, nearestTerms);
            words.forEach(word -> map.put(word, (float) getWord2Vec().similarity(term, word)));
            float sum = (float) map.values().parallelStream().mapToDouble(d -> d).sum();
            map.keySet().forEach(key -> map.put(key, map.get(key) / sum));
        }
        return map;
    }

    @Override
    public float scoreProcessed(Object query, Object text) {
        return scoreWeighted((List<Map<String, Float>>) query, (Map<String, Integer>) text);
    }

    private float scoreWeighted(List<Map<String, Float>> wightedQuery, Map<String, Integer> docTerms) {
        int length = docTerms.values().parallelStream().mapToInt(f -> f).sum();
        return (float) wightedQuery.parallelStream().mapToDouble(weightedTerm -> {
            List<Triple<Float, Float, Float>> list = weightedTerm.entrySet().stream().
                    filter(entry -> docTerms.containsKey(entry.getKey())).
                    map(entry -> ImmutableTriple.of(docTerms.get(entry.getKey()).floatValue(), (float) df(entry.getKey()), entry.getValue())).
                    collect(toList());
            float averageTF = (float) list.parallelStream().mapToDouble(triple -> triple.getLeft() * triple.getRight()).sum();
            float averageDF = (float) list.parallelStream().mapToDouble(triple -> triple.getMiddle() * triple.getRight()).sum();
            return bm25(averageTF, averageDF, length);
        }).sum();
    }
}
