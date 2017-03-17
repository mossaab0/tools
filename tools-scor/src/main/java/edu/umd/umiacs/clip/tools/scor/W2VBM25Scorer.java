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

import gnu.trove.map.TObjectIntMap;
import java.io.File;
import java.io.IOException;
import org.deeplearning4j.models.embeddings.wordvectors.WordVectors;

/**
 *
 * @author Mossaab Bagdouri
 */
public abstract class W2VBM25Scorer extends BM25Scorer {

    private transient WordVectors word2vec;
    public final int nearestTerms = 5;
    
    public W2VBM25Scorer(String dfPath) {
        super(dfPath);
    }

    public W2VBM25Scorer(TObjectIntMap<String> DF, int N) {
        super(DF, N);
    }

    public <T extends W2VBM25Scorer> T loadWord2Vec(String path) {
        word2vec = WordVectorUtils.loadTxt(new File(path));
        return (T) this;
    }

    public <T extends W2VBM25Scorer> T setWord2Vec(WordVectors model) {
        this.word2vec = model;
        return (T) this;
    }

    public WordVectors getWord2Vec() {
        return word2vec;
    }
}
