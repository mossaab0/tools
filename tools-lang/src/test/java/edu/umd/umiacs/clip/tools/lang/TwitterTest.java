package edu.umd.umiacs.clip.tools.lang;

import static edu.umd.umiacs.clip.tools.lang.TwitterNormalizer.replaceArQuestionRemoveRTAndUserAndURLSepPunctLowCase;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mossaab Bagdouri
 */
public class TwitterTest {

    @Test
    public void normalize() {
        String input = "@IsaKft Oh yeah, that's even worse! Where's his Blue Jays pride?? ;)";
        String output = replaceArQuestionRemoveRTAndUserAndURLSepPunctLowCase(input);
        assertEquals(output, "USER oh yeah , that ' s even worse ! where ' s his blue jays pride ? ? EMOTICONWINK");
    }
}
