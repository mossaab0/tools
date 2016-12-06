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

import static edu.umd.umiacs.clip.tools.io.AllFiles.lines;
import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import static edu.umd.umiacs.clip.tools.io.AllFiles.REMOVE_OLD_FILE;
import static edu.umd.umiacs.clip.tools.io.AllFiles.write;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectorsImpl;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author Mossaab Bagdouri
 */
public class WordVectorUtils {

    /*
    * Adapted from https://github.com/deeplearning4j/deeplearning4j/blob/e1a82ad6c4e2da20c48ae483915bd203f1cd36f8/deeplearning4j-scaleout/deeplearning4j-nlp/src/main/java/org/deeplearning4j/models/embeddings/loader/WordVectorSerializer.java
    * Copyright 2015 Skymind,Inc.
    * @author Adam Gibson
    * @author raver119
     */
    public static WordVectors loadTxt(File vectorsFile) throws IOException {
        VocabCache cache = new AbstractCache();
        List<INDArray> arrays = lines(vectorsFile.toPath()).
                map(line -> line.split(" ")).
                filter(fields -> fields.length > 2).
                map(split -> {
                    String word = split[0];
                    VocabWord word1 = new VocabWord(1.0, word);
                    word1.setIndex(cache.numWords());
                    cache.addToken(word1);
                    cache.addWordToIndex(cache.numWords(), word);
                    //cache.putVocabWord(word);
                    INDArray row = Nd4j.create(Nd4j.createBuffer(split.length - 1));
                    for (int i = 1; i < split.length; i++) {
                        row.putScalar(i - 1, Float.parseFloat(split[i]));
                    }
                    return row;
                }).collect(toList());

        INDArray syn = Nd4j.create(new int[]{arrays.size(), arrays.get(0).columns()});
        for (int i = 0; i < syn.rows(); i++) {
            syn.putRow(i, arrays.get(i));
        }

        InMemoryLookupTable lookupTable = (InMemoryLookupTable) new InMemoryLookupTable.Builder()
                .vectorLength(arrays.get(0).columns())
                .useAdaGrad(false).cache(cache)
                .useHierarchicSoftmax(false)
                .build();
        Nd4j.clearNans(syn);
        lookupTable.setSyn0(syn);

        WordVectorsImpl vectors = new WordVectorsImpl();
        vectors.setLookupTable(lookupTable);
        vectors.setVocab(cache);
        return vectors;
    }

    public static WordVectors loadTxt(String path) throws IOException {
        return loadTxt(new File(path));
    }

    public static void subset(String input, String output, Set<String> words, boolean loadOldVectorToMemory) {
        Set<String> allWords = new HashSet<>(words);
        allWords.add("</s>");
        List<String> lines = new ArrayList<>();
        (loadOldVectorToMemory ? readAllLines(input).parallelStream() : lines(input)).
                filter(line -> {
                    String[] fields = line.split(" ");
                    return fields.length > 2 && allWords.contains(fields[0]);
                }).forEach(lines::add);
        lines.add(0, lines.size() + " " + (lines.get(0).split(" ").length - 1));
        write(output, lines, REMOVE_OLD_FILE);
    }

    public static void subset(String input, String output, Set<String> words) {
        subset(input, output, words, false);
    }
}
