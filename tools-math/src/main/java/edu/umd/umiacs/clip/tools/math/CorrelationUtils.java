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

import static edu.umd.umiacs.clip.tools.math.MathUtils.minMaxScale;
import static java.lang.Math.max;
import static java.util.Comparator.comparing;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.math3.stat.correlation.KendallsCorrelation;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

/**
 *
 * @author Mossaab Bagdouri
 */
public class CorrelationUtils {

    public static double tauAP(final double[] xUnsorted, final double[] yUnsorted) {
        Pair<double[], double[]> pairs = sort(xUnsorted, yUnsorted);
        double[] x = pairs.getLeft();
        double[] y = pairs.getRight();
        return (2 * range(0, x.length).parallel().mapToDouble(i
                -> range(0, x.length).parallel().filter(j -> x[j] > x[i] && y[j] > y[i]).count()
                / (double) range(0, x.length).parallel().filter(j -> x[j] > x[i]).count()
        ).filter(Double::isFinite).sum() / (x.length - 1)) - 1;
    }

    public static double tauGAP(final double[] xUnsorted, final double[] yUnsorted) {
        Pair<double[], double[]> pairs = sort(xUnsorted, yUnsorted);
        double[] x = pairs.getLeft();
        double[] y = pairs.getRight();
        return (2 * range(0, x.length).parallel().mapToDouble(i
                -> range(0, x.length).parallel().filter(j -> x[j] > x[i] && y[j] > y[i]).mapToDouble(j -> max(0, x[j] - x[i])).sum()
                / range(0, x.length).parallel().mapToDouble(j -> max(0, x[j] - x[i])).sum()
        ).filter(Double::isFinite).sum() / (x.length - 1)) - 1;
    }

    private static Pair<double[], double[]> sort(final double[] x, final double[] y) {
        List<Pair<Double, Double>> list = range(0, x.length).boxed().
                map(i -> Pair.of(x[i], y[i])).
                sorted(comparing((Pair<Double, Double> pair) -> pair.getLeft()).
                        reversed()).collect(toList());
        double[] xSorted = list.stream().mapToDouble(Pair::getLeft).toArray();
        double[] ySorted = list.stream().mapToDouble(Pair::getRight).toArray();
        return Pair.of(xSorted, ySorted);
    }

    public static double pr(final double[] xUnsorted, final double[] yUnsorted) {
        Pair<double[], double[]> pairs = sort(xUnsorted, yUnsorted);
        double[] x = minMaxScale(pairs.getLeft());
        double[] y = minMaxScale(pairs.getRight());
        return range(1, x.length).mapToDouble(i -> x[i]
                * (range(0, i).mapToDouble(j -> (x[j] - x[i]) * (y[j] - y[i])).sum())
                / (Math.sqrt((range(0, i).mapToDouble(j -> Math.pow(x[j] - x[i], 2)).sum())
                        * (range(0, i).mapToDouble(j -> Math.pow(y[j] - y[i], 2)).sum())))).
                sum() / range(1, x.length).mapToDouble(i -> x[i]).sum();

    }

    public static void main(String[] args) {
        double[] x = {0.9, 0.6, 0.4, 0.3, 0.2, 0.1};
        double[] y = {0.5, 0.6, 0.4, 0.3, 0.2, 0.1};
        System.out.println(new PearsonsCorrelation().correlation(x, y));
        System.out.println(new KendallsCorrelation().correlation(x, y));
        System.out.println(tauAP(x, y));
        System.out.println(tauGAP(x, y));
        System.out.println(pr(x, y));
    }
}
