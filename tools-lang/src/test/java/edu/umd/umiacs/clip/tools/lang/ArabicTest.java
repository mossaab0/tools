package edu.umd.umiacs.clip.tools.lang;

import edu.umd.umiacs.clip.tools.lang.ArabicUtils;
import edu.umd.umiacs.clip.tools.lang.EmoticonUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mossaab Bagdouri
 */
public class ArabicTest {

    @Test
    public void punctuation() {
        String raw = "كيف الأحوال؟ :-)";
        String reference = "كيف الاحوال ؟ " + EmoticonUtils.EMOTICON_HAPPY;
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void redundancy() {
        String raw = "كلالالالالالالالالالالا!!!";
        String reference = "كلا !!!";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void numbers() {
        String raw = "1430.1";
        String reference = "1430.1";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void href() {
        String raw = "<a href=\"http://www.roofing-dayton.info\">Dayton Roofers</a>";
        String reference = "http://www.roofing-dayton.info Dayton Roofers";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void email() {
        String raw = "test@domain.com_there";
        String reference = "test@domain.com _ there";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void hashtag() {
        String raw = "#هاش_تاج";
        String reference = "#هاش_تاج";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void nfkc() {
        String raw = "\uFEFC ﻼ";
        String reference = "لا لا";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }

    @Test
    public void weird() {
        String raw = "" + ArabicUtils.GAF_THREE_DOTS_ABOVE;
        String reference = "ك";
        String tokenized = ArabicUtils.patternTonkenize(ArabicUtils.normalizeFull(raw), true);
        assertEquals(reference, tokenized);
    }
}
