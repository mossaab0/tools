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
import java.io.IOException;
import java.io.UncheckedIOException;
import static java.lang.Integer.min;
import static java.lang.Math.sqrt;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toMap;
import java.util.stream.Stream;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermsEnum;
import org.apache.lucene.util.BytesRef;

/**
 *
 * @author Mossaab Bagdouri
 */
public class TFIDF extends Scorer {

    private transient TObjectIntMap<String> DF;
    protected final int N;
    private IndexReader ir;
    private String field;

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

    public TFIDF(IndexReader ir, String field) {
        this.ir = ir;
        this.field = field;
        N = ir.numDocs();
    }

    public TFIDF(TObjectIntMap<String> DF, int N) {
        this.DF = DF;
        this.N = N;
    }

    public TObjectIntMap<String> getDF() {
        return DF;
    }

    public int getN() {
        return N;
    }

    public Map<String, Integer> tf(String doc) {
        return Stream.of(doc.split(" ")).
                filter(word -> !word.isEmpty()).
                collect(groupingBy(identity(), reducing(0, e -> 1, Integer::sum)));
    }

    public Map<String, Integer> tf(int docid) {
        Map<String, Integer> map = new HashMap<>();
        try {
            TermsEnum iter = ir.getTermVector(docid, field).iterator();
            BytesRef element;
            while ((element = iter.next()) != null) {
                map.put(element.utf8ToString(), (int) iter.totalTermFreq());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return map;
    }

    private Map<String, Float> tfidf(Object doc) {
        return ((doc instanceof Integer) ? tf((int) doc) : tf((String) doc)).
                entrySet().stream().
                collect(toMap(entry -> entry.getKey(),
                        entry -> entry.getValue() * idf(df(entry.getKey()))));
    }

    private float dot(Map<String, Float> v1, Map<String, Float> v2) {
        return (float) v1.keySet().parallelStream().filter(v2::containsKey).
                mapToDouble(word -> v1.get(word) * v2.get(word)).sum();
    }

    private float norm(Map<String, Float> vec) {
        return vec.isEmpty() ? 1
                : (float) sqrt(vec.values().parallelStream().mapToDouble(v -> v * v).sum());
    }

    public int df(String word) {
        if (DF == null) {
            try {
                return ir.docFreq(new Term(field, word));
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return DF.containsKey(word) ? min(N, DF.get(word)) : 1;
    }

    private float idf(float df) {
        return (float) Math.log(N / df);
    }

    @Override
    public float scoreProcessed(Object query, Object text) {
        Map<String, Float> queryVec = (Map<String, Float>) query;
        Map<String, Float> textVec = (Map<String, Float>) text;
        return dot(queryVec, textVec) / (norm(queryVec) * norm(textVec));
    }

    @Override
    public Object getProcessedQuery(String query) {
        return tf(query);
    }

    @Override
    public Object getProcessedText(String text) {
        return tfidf(text);
    }

    @Override
    public Object getProcessedText(int docid) {
        return tfidf(docid);
    }
    
    public String getField(){
        return field;
    }
}
