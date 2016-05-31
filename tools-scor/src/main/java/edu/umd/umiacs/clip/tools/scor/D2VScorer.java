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

import java.io.File;
import java.io.IOException;
import static java.util.Arrays.asList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author Mossaab Bagdouri
 */
public class D2VScorer extends Scorer {

    private transient WordVectors word2vec;
    private final Map<String, INDArray> map = new HashMap<>();

    public <T extends D2VScorer> T loadWord2Vec(String path) {
        try {
            word2vec = WordVectorUtils.loadTxt(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (T) this;
    }

    public <T extends D2VScorer> T setWord2Vec(WordVectors model) {
        this.word2vec = model;
        return (T) this;
    }

    public WordVectors getWord2Vec() {
        return word2vec;
    }

    private INDArray getVector(String text) {
        INDArray vector = Nd4j.zeros(200);
        List<String> words = asList(text.split("\\s+"));
        words.parallelStream().filter(word -> word2vec.hasWord(word)).
                map(word -> word2vec.getWordVectorMatrix(word)).
                forEach(vector::addi);
        return vector.divi(words.size());
    }

    private INDArray getQueryVector(String query) {
        INDArray vector = map.get(query);
        if (vector == null) {
            vector = getVector(query);
            map.put(query, vector);
        }
        return vector;
    }

    @Override
    public double score(String query, String text) {
        return 1 - getQueryVector(query).distance2(getVector(text));
    }
}