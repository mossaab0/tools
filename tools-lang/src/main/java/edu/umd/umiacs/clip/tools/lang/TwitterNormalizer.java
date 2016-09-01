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

import static edu.umd.umiacs.clip.tools.lang.LuceneUtils.arStem;
import static edu.umd.umiacs.clip.tools.lang.LuceneUtils.enStem;
import static java.util.Arrays.asList;

/**
 *
 * @author Mossaab Bagdouri
 */
public class TwitterNormalizer {

    public static String normalizeArTweet(String s) {
        return String.join(" ", asList(ArabicUtils.normalizeFull(s.replaceAll("\\s+", " ").replace("RT ", " ").replaceAll("@[^ ]+", " USER ").replaceAll("http[^ ]+", " URL ").replace("#", "HASHTAG").replace("_", "UNDERSCORE").replaceAll("\\s+", " ").trim()).replaceAll("(.+?)\\1{2,}", "$1").split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})"))).replace("HASHTAG", "#");
    }

    public static String normalizeAndStemArTweet(String s) {
        return arStem(normalizeArTweet(s));
    }

    public static String normalizeEnTweet(String s) {
        return String.join(" ", asList(EmoticonUtils.normalizeFaces(s.replaceAll("\\s+", " ").replace("RT ", " ").replaceAll("@[^ ]+", " USER ").replaceAll("http[^ ]+", " URL ").replace("#", "HASHTAG").replaceAll("\\s+", " ").trim()).replaceAll("(.+?)\\1{2,}", "$1").split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})"))).replace("HASHTAG", "#");
    }

    public static String normalizeAndStemEnTweet(String s) {
        return enStem(normalizeEnTweet(s));
    }

    public static String normalizeEnTweetDeep(String s) {
        return String.join(" ", asList(normalizeEnTweetDeepKeepPunctuation(s).split("\\s+|(?=\\p{Punct})|(?<=\\p{Punct})")));
    }

    public static String normalizeEnTweetDeepKeepPunctuation(String s) {
        return EmoticonUtils.removeFaces(s.replaceAll("\\s+", " ").replace("RT ", " ").replaceAll("@[^ ]+", " ").replaceAll("http[^ ]+", " ").replace("#", " ").replaceAll("\\s+", " ").trim()).replaceAll("(.+?)\\1{2,}", "$1");
    }

    public static void main(String[] args) {
        String input = "لماذا يصعب علي فهم هذا الأمر؟! #سؤال_وجودي";
        String output = normalizeArTweet(input);
        System.out.println(output);
    }
}
