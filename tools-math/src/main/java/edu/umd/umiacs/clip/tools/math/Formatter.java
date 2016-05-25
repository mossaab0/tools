/**
 * Tools Math
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
package edu.umd.umiacs.clip.tools.math;

import static java.lang.Math.round;
import java.util.Arrays;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

/**
 *
 * @author Mossaab Bagdouri
 */
public class Formatter {

    public static double format(double d) {
        return Double.isFinite(d) ? round(d * 100) / 100d : d;
    }

    public static Pair<Double, Double> format(Pair<Double, Double> pair) {
        return Pair.of(format(pair.getLeft()), format(pair.getRight()));
    }

    public static Triple<Double, Double, Double> format(Triple<Double, Double, Double> triple) {
        return Triple.of(format(triple.getLeft()), format(triple.getMiddle()), format(triple.getRight()));
    }

    public static List<Double> format(List<Double> list) {
        return list.stream().map(Formatter::format).collect(toList());
    }

    public static List<Double> format(double[] array) {
        return Arrays.stream(array).map(Formatter::format).boxed().collect(toList());
    }
}
