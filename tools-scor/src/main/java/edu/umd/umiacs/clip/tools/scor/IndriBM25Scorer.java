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

import java.util.ArrayList;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.lucene.index.IndexReader;

/**
 *
 * @author Mossaab Bagdouri
 */
public class IndriBM25Scorer extends BM25Scorer {

    public IndriBM25Scorer(IndexReader ir, String field) {
        super(ir, field);
    }

    @Override
    public Object getProcessedQuery(String query) {
        String[] tokens = query.replace("(", "( ").replace(")", " ) ").replaceAll("\\s+\\(", "(").trim().split("\\s+");
        List<Pair<List<Pair<String, Float>>, Float>> processed = new ArrayList<>();
        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if (token.startsWith("#syn(") || token.startsWith("#wsyn(")) {
                float df = 0;
                boolean isWeighted = token.startsWith("#wsyn(");
                List<Pair<String, Float>> list = new ArrayList<>();
                for (i++; i < tokens.length; i++) {
                    token = tokens[i];
                    if (token.equals(")")) {
                        break;
                    }
                    float weight = 1f;
                    if (isWeighted) {
                        weight = new Float(tokens[i++]);
                        token = tokens[i];
                    }
                    list.add(Pair.of(token, weight));
                    df += weight * df(token);
                }
                processed.add(Pair.of(list, idf(df)));
            } else {
                processed.add(Pair.of(asList(Pair.of(token, 1f)), idf(df(token))));
            }
        }
        return processed;
    }

    @Override
    public float scoreProcessed(Object query, Object text) {
        Map<String, Integer> docTerms = (Map<String, Integer>) text;
        int length = docTerms.values().stream().mapToInt(f -> f).sum();

        return (float) ((List<Pair<List<Pair<String, Float>>, Float>>) query).parallelStream().
                mapToDouble(p -> bm25((float) p.getLeft().parallelStream().
                        mapToDouble(pair -> pair.getRight() * docTerms.getOrDefault(pair.getLeft(), 0)).
                        sum(),
                        p.getRight(),
                        length)).sum();
    }
}
