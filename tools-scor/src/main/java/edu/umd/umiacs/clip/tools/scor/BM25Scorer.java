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
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Map;
import org.apache.lucene.index.IndexReader;

/**
 *
 * @author Mossaab Bagdouri
 */
public class BM25Scorer extends TFIDF {

    private float k1 = 0.09f;
    private float b = 0.5f;
    private float avgdl = 21;
    private float[] cache;

    public BM25Scorer(String dfPath) {
        super(dfPath);
    }

    public BM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
        cache = new float[(int) (avgdl * 10)];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = k1 * (1 - b + b * (i / avgdl));
        }
    }

    public BM25Scorer(IndexReader ir, String field) {
        super(ir, field);
        k1 = 1.2f;
        b = 0.75f;
        try {
            avgdl = ir.getSumTotalTermFreq(field) / (float) ir.numDocs();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        cache = new float[(int) (avgdl * 10)];
        for (int i = 0; i < cache.length; i++) {
            cache[i] = k1 * (1 - b + b * (i / avgdl));
        }
    }

    @Override
    public Map<String, Integer> getProcessedText(String text) {
        return tf(text);
    }

    @Override
    public Map<String, Integer> getProcessedText(int docid) {
        return tf(docid);
    }

    @Override
    public float scoreProcessed(Object query, Object text) {
        Map<String, Integer> docTerms = (Map<String, Integer>) text;
        int length = docTerms.values().stream().mapToInt(f -> f).sum();
        return (float) ((Map<String, Integer>) query).entrySet().stream().
                filter(entry -> docTerms.containsKey(entry.getKey())).
                mapToDouble(pair -> pair.getValue()
                        * bm25(docTerms.get(pair.getKey()), df(pair.getKey()), length)).
                sum();
    }

    private float idf(float df) {
        return (float) Math.log1p((N - df + 0.5) / (df + 0.5));
    }

    protected float bm25(float tf, float df, int length) {
        float denom;
        if (length >= cache.length) {
            denom = tf + k1 * (1 - b + b * (length / avgdl));
        } else {
            denom = tf + cache[length];
        }
        return idf(df) * tf * (k1 + 1) / denom;
    }
}
