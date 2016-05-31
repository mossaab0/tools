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
import java.util.HashMap;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import java.util.stream.Stream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class VanillaW2VBM25Scorer extends W2VBM25Scorer {

    private final Map<String, String> map = new HashMap<>();
    
    public VanillaW2VBM25Scorer(String dfPath) {
        super(dfPath);
    }

    public VanillaW2VBM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
    }

    @Override
    public double score(String query, String text) {
        return super.score(expand(query), text);
    }

    private String expand(String query) {
        String expansion = map.get(query);
        if (expansion == null) {
            expansion = String.join(" ", Stream.of(query.split(" ")).
                    filter(word -> !word.isEmpty() && df(word) > 1).
                    map(word -> word + " "
                            + String.join(" ", getWord2Vec().wordsNearest(word, nearestTerms))).
                    collect(toList()));
            map.put(query, expansion);
        }
        return expansion;
    }
}
