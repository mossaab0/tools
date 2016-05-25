package edu.umd.umiacs.clip.tools.lang;

import edu.umd.umiacs.clip.tools.lang.EmoticonUtils;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Mossaab
 */
public class EmoticonTest {
    // http://en.wikipedia.org/wiki/List_of_emoticons

    @Test
    public void happy() {
        String tokens[] = ":-) :) :o) :] :3 :c) :> =] 8) =) :} :^) :っ)".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_HAPPY);
        }
    }

    @Test
    public void laughing() {
        String tokens[] = ":-D :D 8-D 8D x-D xD X-D XD =-D =D B^D".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_LAUGHING);
        }
    }

    @Test
    public void tearsOfHappiness() {
        String tokens[] = ":'-) :')".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_HAPPY);
        }
    }

    @Test
    public void sad() {
        String tokens[] = ">:[ :-( :( :-c :c :-< :っC :< :-[ :[ :{".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_SAD);
        }
    }

    @Test
    public void crying() {
        String tokens[] = ":'-( :'(".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_SAD);
        }
    }

    @Test
    public void horror() {
        String tokens[] = "D:< D: D8 D; D= DX v.v D-':".split("\\s+");
        for (String token : tokens) {
            //System.out.println(token + " => " + EmoticonUtils.normalizeFaces(token));
            //assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_SAD);
        }
    }

    @Test
    public void surprise() {
        String tokens[] = ">:O :-O :O :-o :o 8-0 O_O o-o O_o o_O o_o O-O".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_SURPRISE);
        }
        assertEquals(EmoticonUtils.normalizeFaces("exoskeleton"), "exoskeleton");
    }

    @Test
    public void kiss() {
        String tokens[] = ":* :^* ('}{')".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_KISS);
        }
    }

    @Test
    public void wink() {
        String tokens[] = ";-) ;) *-) *) ;-] ;] ;D ;^)".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_WINK);
        }
        assertEquals(EmoticonUtils.normalizeFaces("exd"), "exd");
    }

    @Test
    public void tongue() {
        String tokens[] = ">:P :-P :P X-P x-p xp XP :-p :p =p :-Þ :Þ :þ :-þ :-b :b".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_TONGUE);
        }
        assertEquals(EmoticonUtils.normalizeFaces("experiment"), "experiment");
    }

    @Test
    public void annoyed() {
        String tokens[] = ">:\\ >:/ :-/ :-. :/ :\\ =/ =\\ :L =L :S >.<".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_ANNOYED);
        }
        assertEquals(EmoticonUtils.normalizeFaces("EXL"), "EXL");
    }

    @Test
    public void embarassed() {
        String tokens[] = ":$".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_EMBARASSED);
        }
    }

    @Test
    public void silent() {
        String tokens[] = ":-X :X :-# :#".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_SILENT);
        }
    }

    @Test
    public void angel() {
        String tokens[] = "O:-) 0:-3 0:3 0:-) 0:) 0;^)".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_ANGEL);
        }
    }

    @Test
    public void evil() {
        String tokens[] = ">:) >;) >:-)".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_EVIL);
        }
    }

    @Test
    public void devil() {
        String tokens[] = "}:-) }:) 3:-) 3:)".split("\\s+");
        for (String token : tokens) {
            assertEquals(EmoticonUtils.normalizeFaces(token), EmoticonUtils.EMOTICON_EVIL);
        }
    }

    @Test
    public void love() {
        String raw = "💗";
        String reference = EmoticonUtils.EMOTICON_LOVE;
        String tokenized = EmoticonUtils.normalizeFaces(raw);
        assertEquals(reference, tokenized);
    }
}
