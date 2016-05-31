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
import java.util.Map;
import java.util.stream.Stream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class BM25Scorer extends TFIDF {

    private final double k1 = 0.09;
    private final double b = 0.5;
    private final double avgdl = 21;

    public BM25Scorer(String dfPath) {
        super(dfPath);
    }

    public BM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
    }

    @Override
    public double score(String query, String text) {
        Map<String, Integer> docTerms = tf(text);
        int length = docTerms.values().stream().mapToInt(f -> f).sum();
        return Stream.of(query.split(" ")).filter(word -> !word.isEmpty()).
                filter(docTerms::containsKey).
                mapToDouble(word -> bm25(docTerms.get(word), df(word), length)).sum();
    }
    
    private double idf(double df) {
        return Math.log((N - df + 0.5) / (df + 0.5));
    }

    protected double bm25(double tf, double df, double length) {
        return idf(df) * tf * (k1 + 1) / (tf + k1 * (1 - b + b * (length / avgdl)));
    }
}
