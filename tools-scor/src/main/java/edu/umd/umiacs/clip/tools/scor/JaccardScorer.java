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

import java.io.Serializable;
import java.util.Set;
import static java.util.stream.Collectors.toSet;
import java.util.stream.Stream;

/**
 *
 * @author Mossaab Bagdouri
 */
public class JaccardScorer extends Scorer implements Serializable {

    @Override
    public Set<String> getProcessedQuery(String query) {
        return Stream.of(query.split(" ")).filter(word -> !word.isEmpty()).collect(toSet());
    }
    
    @Override
    public Set<String> getProcessedText(String text) {
        return Stream.of(text.split(" ")).filter(word -> !word.isEmpty()).collect(toSet());
    }

    @Override
    public double scoreProcessed(Object query, Object text) {
        int denom = denom((Set<String>) query, (Set<String>) text);
        return denom == 0 ? 0 : intersect((Set<String>)query, (Set<String>)text) / (double) denom;
    }

    private int intersect(Set<String> a, Set<String> b) {
        return (int) a.stream().filter(b::contains).count();
    }

    public int denom(Set<String> a, Set<String> b) {
        return (int) Stream.concat(a.stream(), b.stream()).distinct().count();
    }
}
