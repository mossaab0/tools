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

import java.util.Arrays;
import java.util.DoubleSummaryStatistics;

/**
 *
 * @author Mossaab Bagdouri
 */
public class MathUtils {

    public static double[] minMaxScale(final double[] x) {
        DoubleSummaryStatistics s = Arrays.stream(x).summaryStatistics();
        return s.getMax() == 1 && s.getMin() == 0 ? x
                : Arrays.stream(x).
                map(d -> (d - s.getMin()) / (s.getMax() - s.getMin())).toArray();
    }
}
