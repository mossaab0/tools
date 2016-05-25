/**
 * Tools Lang
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
package edu.umd.umiacs.clip.tools.lang;

import static java.lang.Double.compare;
import static java.lang.Math.log;
import static java.lang.Math.round;
import static java.lang.Math.sqrt;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import org.apache.commons.lang3.tuple.Triple;

/**
 *
 * @author Mossaab Bagdouri
 */
public final class TFIDFVector {

    public Map<String, Double> termToTFIDF;

    public TFIDFVector() {
        termToTFIDF = new HashMap<>();
    }

    public TFIDFVector(Collection<Triple<String, Integer, Integer>> terms, double logNumDocs) {
        double one_over_n_j = 1.0d
                / terms.parallelStream().mapToInt(Triple::getMiddle).sum();
        termToTFIDF = terms.stream().collect(toMap(Triple::getLeft,
                triple -> (triple.getMiddle() * one_over_n_j)
                * (logNumDocs - log(triple.getRight()))));
        normalize();
    }

    //Display top 10 terms
    @Override
    public String toString() {
        return String.join(" | ",
                termToTFIDF.entrySet().stream().
                sorted((e1, e2) -> compare(e2.getValue(), e1.getValue())).limit(10).
                map(e -> e.getKey() + " -> " + (round(e.getValue() * 100.0d) / 100.0d)).
                collect(toList()));
    }

    public void normalize() {
        double mult = 1d / sqrt(dot(this));
        termToTFIDF = termToTFIDF.entrySet().stream().
                collect(toMap(Entry::getKey, entry -> entry.getValue() * mult));
    }

    //Assumes normalized vectors
    public double dot(TFIDFVector other) {
        return termToTFIDF.entrySet().parallelStream().
                mapToDouble(entry -> entry.getValue()
                        * other.termToTFIDF.getOrDefault(entry.getKey(), 0d)).
                sum();
    }
}
