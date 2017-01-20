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

import java.util.List;
import static java.util.stream.IntStream.range;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import org.apache.commons.lang3.tuple.Triple;

/**
 *
 * @author Mossaab Bagdouri
 *
 * The code below implements this paper https://doi.org/10.3115/992730.992783
 */
public class StatisticalTests {

    private static final Random RAND = new Random();
    private static final int NT = 1048576;

    private static double f1(int tp, int fp, int fn) {
        return 2 * tp / (double) (2 * tp + fp + fn);
    }

    public static double prec(int tp, int fp) {
        return tp / (double) (tp + fp);
    }

    public static double rec(int tp, int fn) {
        return tp / (double) (tp + fn);
    }

    public static Triple<Double, Double, Double> pRecPrecF1(List<Boolean> gold, List<Boolean> preds0, List<Boolean> preds1) {
        if (gold.size() != preds0.size()) {
            System.err.println(gold.size() + " != " + preds0.size());
            throw new RuntimeException();
        }

        if (gold.size() != preds1.size()) {
            System.err.println(gold.size() + " != " + preds1.size());
            throw new RuntimeException();
        }

        int pos = 0, tpboth = 0, tp0only = 0, tp1only = 0, fpboth = 0, fp0only = 0, fp1only = 0;
        for (int i = 0; i < gold.size(); i++) {
            if (gold.get(i)) {
                if (preds0.get(i)) {
                    if (preds1.get(i)) {
                        tpboth++;
                    } else {
                        tp0only++;
                    }
                } else if (preds1.get(i)) {
                    tp1only++;
                }
                pos++;
            } else if (preds0.get(i)) {
                if (preds1.get(i)) {
                    fpboth++;
                } else {
                    fp0only++;
                }
            } else if (preds1.get(i)) {
                fp1only++;
            }
        }

        return pRecPrecF1(pos, tpboth, tp0only, tp1only, fpboth, fp0only, fp1only);
    }

    private static int[] nextPair(int firstOnly, int secondOnly, int both) {
        int add = RAND.ints(firstOnly + secondOnly, 0, 2).sum();
        return new int[]{both + add, both + firstOnly + secondOnly - add};
    }

    private static Triple<Double, Double, Double> pRecPrecF1(int pos, int tpboth, int tp0only, int tp1only, int fpboth, int fp0only, int fp1only) {
        double delta_f1 = Math.abs(f1(tp0only + tpboth, fp0only + fpboth, pos - (tp0only + tpboth))
                - f1(tp1only + tpboth, fp1only + fpboth, pos - (tp1only + tpboth)));

        double delta_rec = Math.abs(rec(tp0only + tpboth, pos - (tp0only + tpboth))
                - rec(tp1only + tpboth, pos - (tp1only + tpboth)));

        double delta_prec = Math.abs(prec(tp0only + tpboth, fp0only + fpboth)
                - prec(tp1only + tpboth, fp1only + fpboth));

        AtomicInteger nc_f1 = new AtomicInteger();
        AtomicInteger nc_rec = new AtomicInteger();
        AtomicInteger nc_prec = new AtomicInteger();

        range(0, NT).forEach(j -> {
            int tp[] = nextPair(tp0only, tp1only, tpboth);
            int fp[] = nextPair(fp0only, fp1only, fpboth);

            if (Math.abs(f1(tp[0], fp[0], pos - tp[0]) - f1(tp[1], fp[1], pos - tp[1])) > delta_f1) {
                nc_f1.incrementAndGet();
            }
            if (Math.abs(rec(tp[0], pos - tp[0]) - rec(tp[1], pos - tp[1])) > delta_rec) {
                nc_rec.incrementAndGet();
            }
            if (Math.abs(prec(tp[0], fp[0]) - prec(tp[1], fp[1])) > delta_prec) {
                nc_prec.incrementAndGet();
            }
        });

        return Triple.of((nc_rec.get() + 1.) / (NT + 1), (nc_prec.get() + 1.) / (NT + 1), (nc_f1.get() + 1.) / (NT + 1));
    }

    public static void main(String[] args) {
        System.out.println(pRecPrecF1(103, 19, 28, 6, 5, 43, 9));
        //(4.0054283090321455E-5,0.03990169534521547,0.029660196628383036)
    }
}
