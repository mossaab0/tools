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

/**
 *
 * @author Mossaab Bagdouri
 */
public class EmoticonUtils {

    public static final String EMOTICON_HAPPY = "EMOTICONHAPPY";
    public static final String EMOTICON_LAUGHING = "EMOTICONLAUGHING";
    public static final String EMOTICON_SAD = "EMOTICONSAD";
    public static final String EMOTICON_SURPRISE = "EMOTICONSURPRISE";
    public static final String EMOTICON_KISS = "EMOTICONKISS";
    public static final String EMOTICON_WINK = "EMOTICONWINK";
    public static final String EMOTICON_TONGUE = "EMOTICONTONGUE";
    public static final String EMOTICON_ANNOYED = "EMOTICONANNOYED";
    public static final String EMOTICON_EMBARASSED = "EMOTICONEMBARASSED";
    public static final String EMOTICON_SILENT = "EMOTICONSILENT";
    public static final String EMOTICON_ANGEL = "EMOTICONANGEL";
    public static final String EMOTICON_EVIL = "EMOTICONEVIL";
    public static final String EMOTICON_LOVE = "EMOTICONLOVE";
    public static final String EMOTICON_BROKEN_HEART = "EMOTICONBROKENHEART";
    public static final String EMOTICON_MUSIC = "EMOTICONMUSIC";

    private static final String EYESBROWS = ">?";
    private static final String DEVIL = "[>}3]";
    private static final String HALO = "[0O]";
    private static final String EYES = "[xX8B:=]";
    private static final String WINK = "[;*]";
    private static final String TEAR = "'?";
    private static final String NOSE = "[-o^っc]?";
    private static final String SMILE = "[})3>\\]]+";
    private static final String LAUGH = "[Dd]+";
    private static final String UNHAPPY = "[{(c<C\\[]+";
    private static final String TONGUE = "[PpÞþb]+";
    private static final String KISS = "[\\*]+";
    private static final String SURPRISE = "[oO0]+";
    private static final String ANNOYED = "[/\\\\LS.]+";
    private static final String SILENT = "[X#]+";
    private static final String H_L_FACE = "\\(? ?";
    private static final String H_R_FACE = " ?\\)?";

    private static String replaceEmoticon(String input, String regex, String replacement) {
        return input.matches(regex) ? " " + replacement + " " : input;
    }

    public static String normalizeFaces(String sentence) {
        StringBuilder sb = new StringBuilder();
        sentence = sentence.replace("☹", " " + EMOTICON_SAD + " ")
                .replace("💔", " " + EMOTICON_BROKEN_HEART + " ")
                .replace("</3", " " + EMOTICON_BROKEN_HEART + " ")
                .replace("<3", " " + EMOTICON_LOVE + " ")
                .replaceAll("[😍😗-😚💋💌💑-💓💕-💟]", " " + EMOTICON_LOVE + " ")
                .replaceAll("[♩♪♫♬♭♮♯]", " " + EMOTICON_MUSIC + " ")
                .replaceAll("[☻☺😂😊😄😃]", " " + EMOTICON_HAPPY + " ");
        for (String s : sentence.split("\\s+")) {
            if (s.matches("x+")) {
                s = EMOTICON_KISS;
            } else {
                s = replaceEmoticon(s, H_L_FACE + "[♡♥][⌣‿][♡♥]" + H_R_FACE, EMOTICON_LOVE);
                s = s.replaceAll("[♡♥]", " " + EMOTICON_LOVE + " ");
                s = replaceEmoticon(s, HALO + ";\\^\\)", EMOTICON_ANGEL);
                s = replaceEmoticon(s, HALO + EYES + NOSE + SMILE, EMOTICON_ANGEL);
                s = replaceEmoticon(s, DEVIL + EYES + NOSE + SMILE, EMOTICON_EVIL);
                s = replaceEmoticon(s, DEVIL + ";" + NOSE + SMILE, EMOTICON_EVIL);
                s = replaceEmoticon(s, EYES + TEAR + NOSE + SMILE, EMOTICON_HAPPY);
                s = replaceEmoticon(s, EYESBROWS + EYES + TEAR + NOSE + SURPRISE, EMOTICON_SURPRISE);
                s = replaceEmoticon(s, EYES + NOSE + LAUGH, EMOTICON_LAUGHING);
                s = replaceEmoticon(s, "ه{3,}", EMOTICON_HAPPY);
                s = replaceEmoticon(s, H_L_FACE + "\\^[_Oo-]\\^" + H_R_FACE, EMOTICON_HAPPY);
                s = replaceEmoticon(s, H_L_FACE + "[0Oo][_-][oO0]" + H_R_FACE, EMOTICON_SURPRISE);
                s = replaceEmoticon(s, H_L_FACE + "[｡。]?[◕ʘ][⌣‿]+[◕ʘ][｡。]?" + H_R_FACE, EMOTICON_HAPPY);
                s = replaceEmoticon(s, EYES + NOSE + KISS, EMOTICON_KISS);
                s = replaceEmoticon(s, H_L_FACE + "'\\}\\{'" + H_R_FACE, EMOTICON_KISS);
                s = replaceEmoticon(s, WINK + NOSE + SMILE, EMOTICON_WINK);
                s = replaceEmoticon(s, WINK + NOSE + LAUGH, EMOTICON_WINK);
                s = replaceEmoticon(s, EYESBROWS + EYES + NOSE + TONGUE, EMOTICON_TONGUE);
                s = replaceEmoticon(s, EYES + NOSE + SILENT, EMOTICON_SILENT);
                s = replaceEmoticon(s, EYESBROWS + EYES + NOSE + ANNOYED, EMOTICON_ANNOYED);
                s = replaceEmoticon(s, ">\\.<", EMOTICON_ANNOYED);
                s = replaceEmoticon(s, EYES + NOSE + "\\$", EMOTICON_EMBARASSED);
                s = replaceEmoticon(s, EYESBROWS + "[:=]" + TEAR + NOSE + UNHAPPY, EMOTICON_SAD);
                s = replaceEmoticon(s, H_L_FACE + "['TＴ;:][_Oo▽.]['TＴ;:]" + H_R_FACE, EMOTICON_SAD);
            }
            sb.append(s).append(" ");
        }
        return sb.toString().replaceAll("\\s+", " ").trim();
    }
    
    public static String removeFaces(String sentence) {
        StringBuilder sb = new StringBuilder();
        sentence = sentence.replace("☹", " ")
                .replace("💔", " ")
                .replace("</3", " ")
                .replace("<3", " ")
                .replaceAll("[😍😗-😚💋💌💑-💓💕-💟]", " ")
                .replaceAll("[♩♪♫♬♭♮♯]", " ")
                .replaceAll("[☻☺😂😊😄😃]", " ");
        for (String s : sentence.split("\\s+")) {
            if (s.matches("x+")) {
                s = "";
            } else {
                s = replaceEmoticon(s, H_L_FACE + "[♡♥][⌣‿][♡♥]" + H_R_FACE, " ");
                s = s.replaceAll("[♡♥]", " ");
                s = replaceEmoticon(s, HALO + ";\\^\\)", " ");
                s = replaceEmoticon(s, HALO + EYES + NOSE + SMILE, " ");
                s = replaceEmoticon(s, DEVIL + EYES + NOSE + SMILE, " ");
                s = replaceEmoticon(s, DEVIL + ";" + NOSE + SMILE, " ");
                s = replaceEmoticon(s, EYES + TEAR + NOSE + SMILE, " ");
                s = replaceEmoticon(s, EYESBROWS + EYES + TEAR + NOSE + SURPRISE, " ");
                s = replaceEmoticon(s, EYES + NOSE + LAUGH, " ");
                s = replaceEmoticon(s, "ه{3,}", " ");
                s = replaceEmoticon(s, H_L_FACE + "\\^[_Oo-]\\^" + H_R_FACE, " ");
                s = replaceEmoticon(s, H_L_FACE + "[0Oo][_-][oO0]" + H_R_FACE, " ");
                s = replaceEmoticon(s, H_L_FACE + "[｡。]?[◕ʘ][⌣‿]+[◕ʘ][｡。]?" + H_R_FACE, " ");
                s = replaceEmoticon(s, EYES + NOSE + KISS, " ");
                s = replaceEmoticon(s, H_L_FACE + "'\\}\\{'" + H_R_FACE, " ");
                s = replaceEmoticon(s, WINK + NOSE + SMILE, " ");
                s = replaceEmoticon(s, WINK + NOSE + LAUGH, " ");
                s = replaceEmoticon(s, EYESBROWS + EYES + NOSE + TONGUE, " ");
                s = replaceEmoticon(s, EYES + NOSE + SILENT, " ");
                s = replaceEmoticon(s, EYESBROWS + EYES + NOSE + ANNOYED, " ");
                s = replaceEmoticon(s, ">\\.<", " ");
                s = replaceEmoticon(s, EYES + NOSE + "\\$", " ");
                s = replaceEmoticon(s, EYESBROWS + "[:=]" + TEAR + NOSE + UNHAPPY, " ");
                s = replaceEmoticon(s, H_L_FACE + "['TＴ;:][_Oo▽.]['TＴ;:]" + H_R_FACE, " ");
            }
            sb.append(s).append(" ");
        }
        return sb.toString().replaceAll("\\s+", " ").trim();
    }
}
