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
import static java.util.stream.IntStream.range;

/**
 *
 * @author Mossaab Bagdouri
 */
public class Kappa {

    //Adapted from http://janajarecki.com/?p=569
    //Reference: http://conservancy.umn.edu/bitstream/handle/11299/99941/1/v03n4p537.pdf
    //TODO: Generalize to multiple categories: http://www2.sas.com/proceedings/sugi22/POSTERS/PAPER241.PDF
    public static double dichotomeousFleissCuzick(int[] judges, int[] pos) {
        double n_bar = Arrays.stream(judges).average().getAsDouble();
        double p_bar = Arrays.stream(pos).sum() / (pos.length * n_bar);
        double sum_ni_pi_qi = range(0, judges.length).
                mapToDouble(i -> pos[i] * (1 - pos[i] / (double) judges[i])).sum();
        return 1 - sum_ni_pi_qi / (judges.length * (n_bar - 1) * p_bar * (1 - p_bar));
    }
}
