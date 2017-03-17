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
import java.util.List;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;
import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.api.ops.impl.accum.distances.CosineSimilarity;
import org.nd4j.linalg.factory.Nd4j;

/**
 *
 * @author Mossaab Bagdouri
 */
public class D2VScorer extends Scorer {

    private transient WordVectors word2vec;

    public D2VScorer() {
    }

    public D2VScorer(WordVectors word2vec) {
        this.word2vec = word2vec;
    }

    public <T extends D2VScorer> T loadWord2Vec(String path) {
        word2vec = WordVectorUtils.loadTxt(new File(path));
        return (T) this;
    }

    public <T extends D2VScorer> T setWord2Vec(WordVectors model) {
        this.word2vec = model;
        return (T) this;
    }

    public WordVectors getWord2Vec() {
        return word2vec;
    }

    @Override
    public INDArray getProcessedText(String text) {
        INDArray vector = Nd4j.zeros(200);
        List<String> words = asList(text.split("\\s+"));
        int size = (int) words.parallelStream().
                filter(word2vec::hasWord).
                map(word2vec::getWordVectorMatrixNormalized).
                peek(vector::addi).
                count();
        return vector.divi(size);
    }

    @Override
    public INDArray getProcessedQuery(String query) {
        return getProcessedText(query);
    }

    @Override
    public float scoreProcessed(Object query, Object text) {
        float val = Nd4j.getExecutioner().
                execAndReturn(new CosineSimilarity((INDArray) query, (INDArray) text)).
                getFinalResult().floatValue();
        return Float.isFinite(val) ? val : 0;
    }

    @Override
    public Object getProcessedText(int docid) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
