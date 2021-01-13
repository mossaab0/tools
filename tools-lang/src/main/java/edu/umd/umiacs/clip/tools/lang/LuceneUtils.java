/**
 * Tools Lang
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
package edu.umd.umiacs.clip.tools.lang;

import java.io.IOException;
import java.io.StringReader;
import org.apache.lucene.analysis.Analyzer;
import static org.apache.lucene.analysis.CharArraySet.EMPTY_SET;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.ar.ArabicAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.en.KStemFilter;
import org.apache.lucene.analysis.fr.FrenchAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;

/**
 *
 * @author Mossaab Bagdouri
 */
public class LuceneUtils {

    private static final Analyzer NO_STOP_WORDS_ANALYZER = new StandardAnalyzer(EMPTY_SET);
    private static final Analyzer STANDARD_ANALYZER = new StandardAnalyzer();
    private static final Analyzer AR = new ArabicAnalyzer();
    private static final Analyzer FR = new FrenchAnalyzer();
    private static final Analyzer EN = new EnglishAnalyzer();
    private static final Analyzer KROVETZ = new Analyzer() {
        @Override
        protected TokenStreamComponents createComponents(String fieldName) {
            final Tokenizer source = new StandardTokenizer();
            return new TokenStreamComponents(source, new KStemFilter(source));
        }
    };

    public static String arStem(String s) {
        return stem(AR, s);
    }

    public static String frStem(String s) {
        return stem(FR, s);
    }

    public static String enStem(String s) {
        return stem(EN, s);
    }

    public static String tokenizeUnstopped(String s) {
        return stem(NO_STOP_WORDS_ANALYZER, s);
    }

    public static String tokenizeStopped(String s) {
        return stem(STANDARD_ANALYZER, s);
    }

    public static String krovetzStem(String s) {
        return stem(KROVETZ, s.toLowerCase());
    }

    public static String stem(Analyzer analyzer, String s) {
        StringBuilder sb = new StringBuilder();
        try {
            try ( TokenStream stream = analyzer.tokenStream(null, new StringReader(s))) {
                CharTermAttribute cattr = stream.addAttribute(CharTermAttribute.class);
                stream.reset();
                while (stream.incrementToken()) {
                    sb.append(cattr.toString()).append(" ");
                }
                stream.end();
            }
            return sb.toString().replaceAll("\\s+", " ").trim();
        } catch (IOException e) {
            e.printStackTrace();
            return s.replaceAll("\\s+", " ").trim();
        }
    }

    public static void main(String[] args) {
        System.out.println(arStem("المفكرون"));
        System.out.println(enStem("countries"));
        System.out.println(frStem("villes"));
    }
}
