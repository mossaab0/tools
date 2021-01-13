package edu.umd.umiacs.clip.tools.lang;

import static edu.umd.umiacs.clip.tools.lang.LuceneUtils.arStem;
import static edu.umd.umiacs.clip.tools.lang.LuceneUtils.enStem;
import static edu.umd.umiacs.clip.tools.lang.LuceneUtils.frStem;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mossaab Bagdouri
 */
public class LuceneTest {

    @Test
    public void stem() {
        assertEquals(arStem("المفكرون"), "مفكر");
        assertEquals(enStem("countries"), "countri");
        assertEquals(frStem("villes"), "vile");
    }
}
