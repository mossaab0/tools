package edu.umd.umiacs.clip.tools.lang;

import static edu.umd.umiacs.clip.tools.lang.LangUtils.charNgrams;
import static java.util.Arrays.asList;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mossaab Bagdouri
 */
public class LangTest {

    @Test
    public void ngram() {
        //String input = "How are you?\n?today?? :-)";
        //System.out.println(ngrams(input, 1, 3, "_", false));
        //System.out.println(toFreqMap(""));
        //System.out.println(tokenizeKeepPunctuation(input));
        //System.out.println(charNgrams("abc", 2, 3, true));
        assertEquals(charNgrams("abc", 2, 3, true), asList("Sa", "ab", "bc", "cE", "SSa", "Sab", "abc", "bcE", "cEE"));
    }
}
