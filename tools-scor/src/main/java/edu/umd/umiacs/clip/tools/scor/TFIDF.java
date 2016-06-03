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

import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import gnu.trove.map.TObjectIntMap;
import gnu.trove.map.hash.TObjectIntHashMap;
import static java.lang.Integer.min;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class TFIDF extends Scorer {

    private final transient TObjectIntMap<String> DF;
    protected final int N;

    public TFIDF(String dfPath) {
        DF = new TObjectIntHashMap<>();
        List<String> lines = readAllLines(dfPath);
        N = new Integer(lines.get(0).split("\\s+")[1]);
        for (int i = 1; i < lines.size(); i++) {
            String[] pair = lines.get(i).split("\\s+");
            int df = new Integer(pair[1]);
            DF.put(pair[0], df);
        }
    }

    public TFIDF(TObjectIntMap<String> DF, int N) {
        this.DF = DF;
        this.N = N;
    }

    @Override
    public double score(String query, String text) {
        Map<String, Double> queryVec = tfidf(query);
        Map<String, Double> textVec = tfidf(text);
        return dot(queryVec, textVec) / (norm(queryVec) * norm(textVec));
    }

    public Map<String, Integer> tf(String doc) {
        return Stream.of(doc.split(" ")).
                filter(word -> !word.isEmpty()).
                collect(groupingBy(identity(), reducing(0, e -> 1, Integer::sum)));
    }

    private Map<String, Double> tfidf(String doc) {
        return tf(doc).entrySet().stream().
                collect(toMap(entry -> entry.getKey(),
                                entry -> entry.getValue() * idf(df(entry.getKey()))));
    }

    private double dot(Map<String, Double> v1, Map<String, Double> v2) {
        return v1.keySet().parallelStream().filter(v2::containsKey).
                mapToDouble(word -> v1.get(word) * v2.get(word)).sum();
    }

    private double norm(Map<String, Double> vec) {
        return vec.isEmpty() ? 1
                : vec.values().parallelStream().mapToDouble(v -> v * v).sum();
    }

    public int df(String word) {
        return DF.containsKey(word) ? min(N, DF.get(word)) : 1;
    }

    private double idf(double df) {
        return Math.log(N / df);
    }
}
