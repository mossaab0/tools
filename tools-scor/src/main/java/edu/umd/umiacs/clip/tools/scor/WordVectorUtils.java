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

import static edu.umd.umiacs.clip.tools.io.AllFiles.BUFFER_SIZE;
import static edu.umd.umiacs.clip.tools.io.AllFiles.lines;
import static edu.umd.umiacs.clip.tools.io.AllFiles.write;
import static edu.umd.umiacs.clip.tools.io.AllFiles.REMOVE_OLD_FILE;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import static java.nio.charset.CodingErrorAction.IGNORE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.newInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.LineIterator;
import org.deeplearning4j.models.embeddings.inmemory.InMemoryLookupTable;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectorsImpl;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.models.word2vec.wordstore.inmemory.InMemoryLookupCache;
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
        BufferedReader reader = new BufferedReader(new InputStreamReader(new BZip2CompressorInputStream(new BufferedInputStream(newInputStream(vectorsFile.toPath()), BUFFER_SIZE)), UTF_8.newDecoder().onMalformedInput(IGNORE)));
        VocabCache cache = new InMemoryLookupCache();

        LineIterator iter = IOUtils.lineIterator(reader);
        String line;
        boolean hasHeader = false;
        if (iter.hasNext()) {
            line = iter.nextLine();    // skip header line
            //look for spaces
            if (!line.contains(" ")) {
                hasHeader = true;
            }
        }

        //reposition buffer to be one line ahead
        if (hasHeader) {
            iter.close();
            iter = IOUtils.lineIterator(reader);
            iter.nextLine();
        }

        List<INDArray> arrays = new ArrayList<>();
        while (iter.hasNext()) {
            line = iter.nextLine();
            String[] split = line.split(" ");
            String word = split[0];
            VocabWord word1 = new VocabWord(1.0, word);
            cache.addToken(word1);
            cache.addWordToIndex(cache.numWords(), word);
            word1.setIndex(cache.numWords());
            cache.putVocabWord(word);
            INDArray row = Nd4j.create(Nd4j.createBuffer(split.length - 1));
            for (int i = 1; i < split.length; i++) {
                row.putScalar(i - 1, Float.parseFloat(split[i]));
            }
            arrays.add(row);
        }

        INDArray syn = Nd4j.create(new int[]{arrays.size(), arrays.get(0).columns()});
        for (int i = 0; i < syn.rows(); i++) {
            syn.putRow(i, arrays.get(i));
        }

        InMemoryLookupTable lookupTable = (InMemoryLookupTable) new InMemoryLookupTable.Builder()
                .vectorLength(arrays.get(0).columns())
                .useAdaGrad(false).cache(cache)
                .build();
        Nd4j.clearNans(syn);
        lookupTable.setSyn0(syn);

        iter.close();

        WordVectorsImpl vectors = new WordVectorsImpl();
        vectors.setLookupTable(lookupTable);
        vectors.setVocab(cache);
        return vectors;
    }

    public static void subset(String input, String output, Set<String> words) {
        Set<String> allWords = new HashSet<>(words);
        allWords.add("</s>");
        List<String> lines = new ArrayList<>();
        lines(input).
                filter(line -> {
                    String[] fields = line.split(" ");
                    return fields.length > 2 && allWords.contains(fields[0]);
                }).forEach(lines::add);
        lines.add(0, lines.size() + " " + (lines.get(0).split(" ").length - 1));
        write(output, lines, REMOVE_OLD_FILE);
    }
}
