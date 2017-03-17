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

import static edu.umd.umiacs.clip.tools.io.AllFiles.REMOVE_OLD_FILE;
import edu.umd.umiacs.clip.tools.lang.LuceneUtils;
import java.io.File;
import java.io.IOException;
import static java.lang.Float.parseFloat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import static java.util.stream.IntStream.range;
import java.util.stream.Stream;
import org.apache.commons.lang3.tuple.Pair;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectorsImpl;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.AbstractCache;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import static edu.umd.umiacs.clip.tools.io.AllFiles.lines;
import static edu.umd.umiacs.clip.tools.io.AllFiles.readAllLines;
import static edu.umd.umiacs.clip.tools.io.AllFiles.write;
import static java.util.stream.Collectors.joining;

/**
 *
 * @author Mossaab Bagdouri
 */
public class WordVectorUtils {

    /*
    * Adapted from https://raw.githubusercontent.com/deeplearning4j/deeplearning4j/master/deeplearning4j-nlp-parent/deeplearning4j-nlp/src/main/java/org/deeplearning4j/models/embeddings/loader/WordVectorSerializer.java
    * Copyright 2015 Skymind,Inc.
    * @author Adam Gibson
    * @author raver119
     */
    public static WordVectors loadTxt(File vectorsFile) {
        AbstractCache cache = new AbstractCache<>();
        INDArray arrays[] = lines(vectorsFile.toPath()).
                map(line -> line.split(" ")).
                filter(fields -> fields.length > 2).
                map(split -> {
                    VocabWord word = new VocabWord(1.0, split[0]);
                    word.setIndex(cache.numWords());
                    cache.addToken(word);
                    cache.addWordToIndex(word.getIndex(), split[0]);
                    float[] vector = new float[split.length - 1];
                    range(1, split.length).parallel().
                            forEach(i -> vector[i - 1] = parseFloat(split[i]));
                    return Nd4j.create(vector);
                }).toArray(size -> new INDArray[size]);

        INDArray syn = Nd4j.vstack(arrays);

        InMemoryLookupTable lookupTable = new InMemoryLookupTable.Builder().
                vectorLength(arrays[0].columns()).
                useAdaGrad(false).cache(cache).useHierarchicSoftmax(false).
                build();
        Nd4j.clearNans(syn);

        lookupTable.setSyn0(syn);

        WordVectorsImpl vectors = new WordVectorsImpl();
        vectors.setLookupTable(lookupTable);
        vectors.setVocab(cache);
        return vectors;
    }

    public static WordVectors loadTxt(String path) {
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

    public static void enStem(String input, String output) {
        Set<String> allWords = new HashSet<>();
        List<String> lines = new ArrayList<>();
        lines(input).map(line -> line.split(" ")).
                filter(fields -> fields.length > 2).
                map(fields -> Pair.of(fields[0].equals("</s>") ? fields[0] : LuceneUtils.enStem(fields[0]), Stream.of(fields).skip(1).collect(joining(" ")))).
                filter(pair -> !allWords.contains(pair.getLeft())).
                peek(pair -> allWords.add(pair.getLeft())).
                map(pair -> pair.getLeft() + " " + pair.getRight()).
                forEach(lines::add);
        lines.add(0, lines.size() + " " + (lines.get(0).split(" ").length - 1));
        write(output, lines, REMOVE_OLD_FILE);
    }

    public static void subset(String input, String output, Set<String> words) {
        subset(input, output, words, false);
    }

    public static void main(String[] args) {
        WordVectors w2v = loadTxt("/fs/clip-arqat/mossaab/questions/1.txt");
        lines("/fs/clip-arqat/mossaab/questions/1.txt").
                map(line -> line.split(" ")[0]).
                forEach(word -> System.out.println(word + ":" + w2v.similarity("upgrades", word)));
    }
}
