package edu.umd.umiacs.clip.tools.classifier;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;

public class NFoldsSplitter {

    public static List<Pair<List<String>, List<String>>> split(List<String> list, int folds) {
        List<Pair<List<String>, List<String>>> split = new ArrayList<>();
        for (int i = 0; i < folds; i++) {
            int testStart = Math.round(list.size() * i / (float) folds);
            int testEnd = Math.round(list.size() * (i + 1) / (float) folds);
            List<String> training = new ArrayList<>(list.subList(0, testStart));
            training.addAll(list.subList(testEnd, list.size()));
            split.add(Pair.of(training, list.subList(testStart, testEnd)));
        }
        return split;
    }
}
