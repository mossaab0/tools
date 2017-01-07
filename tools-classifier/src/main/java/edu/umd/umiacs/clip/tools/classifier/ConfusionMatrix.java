/**
 * Tools Classifier
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
package edu.umd.umiacs.clip.tools.classifier;

import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import static java.lang.Math.max;
import java.util.Arrays;
import java.util.IntSummaryStatistics;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static java.util.stream.IntStream.range;

/**
 *
 * @author Mossaab Bagdouri
 */
public class ConfusionMatrix {

    public int TP, TN, FP, FN;

    public float getF1() {
        return 2f * TP / (2f * TP + FN + FP);
    }
    
    public static ConfusionMatrix loadLibSVM(String goldPath, String predPath, double... cutoffs) {
        int[] gold = readAllLines(goldPath).stream().
                mapToInt(line -> new Integer(line.split("\\s+")[0]))
                .toArray();
        IntSummaryStatistics stats = Arrays.stream(gold).summaryStatistics();
        double cutoff = stats.getMin() == stats.getMax() ? cutoffs[0] : ((stats.getMax() - stats.getMin()) / 2);
        List<Boolean> goldList = Arrays.stream(gold).boxed().map(i -> i > cutoff).collect(toList());
        List<Boolean> predList = readAllLines(predPath).stream().map(pred -> new Double(pred) > cutoff).collect(toList());
        ConfusionMatrix cm = new ConfusionMatrix();
        int total = max(goldList.size(), predList.size());
        cm.TP = (int) range(0, total).filter(i -> goldList.get(i) && predList.get(i)).count();
        cm.FP = (int) range(0, total).filter(i -> !goldList.get(i) && predList.get(i)).count();
        cm.FN = (int) range(0, total).filter(i -> goldList.get(i) && !predList.get(i)).count();
        cm.TN = total - (cm.TP + cm.FP + cm.FN);
        return cm;
    }
}
