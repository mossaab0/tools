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

import java.text.Normalizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author Mossaab Bagdouri
 */
public class ArabicUtils {

    public static final char COMMA = '\u060C';

    public static final char HAMZA = '\u0621';
    public static final char ALEF_MADDA = '\u0622';
    public static final char ALEF_HAMZA_ABOVE = '\u0623';
    public static final char WAW_HAMZA = '\u0624';
    public static final char ALEF_HAMZA_BELOW = '\u0625';
    public static final char YEH_HAMZA = '\u0626';
    public static final char ALEF = '\u0627';
    public static final char BEH = '\u0628';
    public static final char TEH_MARBUTA = '\u0629';
    public static final char TEH = '\u062A';
    public static final char THEH = '\u062B';
    public static final char JEEM = '\u062C';
    public static final char HAH = '\u062D';
    public static final char KHAH = '\u062E';
    public static final char DAL = '\u062F';
    public static final char THAL = '\u0630';
    public static final char REH = '\u0631';
    public static final char ZAIN = '\u0632';
    public static final char SEEN = '\u0633';
    public static final char SHEEN = '\u0634';
    public static final char SAD = '\u0635';
    public static final char DAD = '\u0636';
    public static final char TAH = '\u0637';
    public static final char ZAH = '\u0638';
    public static final char AIN = '\u0639';
    public static final char GHAIN = '\u063A';

    public static final char TATWEEL = '\u0640';

    public static final char FEH = '\u0641';
    public static final char QAF = '\u0642';
    public static final char KAF = '\u0643';
    public static final char LAM = '\u0644';
    public static final char MEEM = '\u0645';
    public static final char NOON = '\u0646';
    public static final char HEH = '\u0647';
    public static final char WAW = '\u0648';
    public static final char ALEF_MAKSURA = '\u0649';
    public static final char YEH = '\u064A';
    public static final char FATHATAN = '\u064B';
    public static final char DAMMATAN = '\u064C';
    public static final char KASRATAN = '\u064D';
    public static final char FATHA = '\u064E';
    public static final char DAMMA = '\u064F';
    public static final char KASRA = '\u0650';
    public static final char SHADDA = '\u0651';
    public static final char SUKUN = '\u0652';

    public static final char INDIC_DIGIT_ZERO = '\u0660';
    public static final char INDIC_DIGIT_ONE = '\u0661';
    public static final char INDIC_DIGIT_TWO = '\u0662';
    public static final char INDIC_DIGIT_THREE = '\u0663';
    public static final char INDIC_DIGIT_FOUR = '\u0664';
    public static final char INDIC_DIGIT_FIVE = '\u0665';
    public static final char INDIC_DIGIT_SIX = '\u0666';
    public static final char INDIC_DIGIT_SEVEN = '\u0667';
    public static final char INDIC_DIGIT_EIGHT = '\u0668';
    public static final char INDIC_DIGIT_NINE = '\u0669';

    public static final char EXT_INDIC_DIGIT_ZERO = '\u06F0';
    public static final char EXT_INDIC_DIGIT_ONE = '\u06F1';
    public static final char EXT_INDIC_DIGIT_TWO = '\u06F2';
    public static final char EXT_INDIC_DIGIT_THREE = '\u06F3';
    public static final char EXT_INDIC_DIGIT_FOUR = '\u06F4';
    public static final char EXT_INDIC_DIGIT_FIVE = '\u06F5';
    public static final char EXT_INDIC_DIGIT_SIX = '\u06F6';
    public static final char EXT_INDIC_DIGIT_SEVEN = '\u06F7';
    public static final char EXT_INDIC_DIGIT_EIGHT = '\u06F8';
    public static final char EXT_INDIC_DIGIT_NINE = '\u06F9';

    public static final char DOTLESS_BEH = '\u066E';
    public static final char DOTLESS_QAF = '\u066F';
    public static final char SUPERSCRIPT_ALEF = '\u0670';
    public static final char ALEF_WASLA = '\u0671';
    public static final char ALEF_WAVY_HAMZA_ABOVE = '\u0672';
    public static final char ALEF_WAVY_HAMZA_BELOW = '\u0673';
    public static final char HIGH_HAMZA = '\u0674';
    public static final char HIGH_HAMZA_ALEF = '\u0675';
    public static final char HIGH_HAMZA_WAW = '\u0676';
    public static final char U_HIGH_HAMZA_ABOVE = '\u0677';
    public static final char HIGH_HAMZA_YEH = '\u0678';
    public static final char TTEH = '\u0679';
    public static final char TTEHEH = '\u067A';
    public static final char BEEH = '\u067B';
    public static final char TEH_RING = '\u067C';
    public static final char TEH_THREE_DOTS_DOWNWARDS = '\u067D';
    public static final char PEH = '\u067E';
    public static final char TEHEH = '\u067F';
    public static final char BEHEH = '\u0680';
    public static final char HAH_HAMZA_ABOVE = '\u0681';
    public static final char HAH_TWO_DOTS_ABOVE = '\u0682';
    public static final char NYEH = '\u0683';
    public static final char DYEH = '\u0684';
    public static final char HAH_THREE_DOTS_ABOVE = '\u0685';
    public static final char TCHEH = '\u0686';
    public static final char TCHEHEH = '\u0687';
    public static final char DDAL = '\u0688';
    public static final char DAL_DOT_BELOW_SMALL_TAH = '\u068B';
    public static final char DAHAL = '\u068C';
    public static final char DDAHAL = '\u068D';
    public static final char DUL = '\u068E';
    public static final char DAL_FOUR_DOTS_ABOVE = '\u0690';
    public static final char RREH = '\u0691';
    public static final char REH_RING = '\u0693';
    public static final char REH_SMALL_V_ABOVE = '\u0692';
    public static final char REH_DOT_BELOW_ABOVE = '\u0696';
    public static final char REH_TWO_DOTS_ABOVE = '\u0697';
    public static final char REH_FOUR_DOTS_ABOVE = '\u0699';
    public static final char SEEN_DOT_BELOW_ABOVE = '\u069A';
    public static final char SEEN_THREE_DOTS_BELOW = '\u069B';
    public static final char SEEN_THREE_DOTS_ABOVE_BELOW = '\u069C';
    public static final char SAD_TWO_DOTS_BELOW = '\u069D';
    public static final char SAD_THREE_DOTS_ABOVE = '\u069E';
    public static final char TAH_THREE_DOTS_ABOVE = '\u069F';
    public static final char AIN_THREE_DOTS_ABOVE = '\u06A0';
    public static final char DOTLESS_FEH = '\u06A1';
    public static final char VEH = '\u06A4';
    public static final char PEHEH = '\u06A6';
    public static final char QAF_ONE_DOT_ABOVE = '\u06A7';
    public static final char QAF_THREE_DOTS_ABOVE = '\u06A8';
    public static final char KEHEH = '\u06A9';
    public static final char GAF = '\u06AF';
    public static final char GAF_THREE_DOTS_ABOVE = '\u06B4';
    public static final char LAM_SMALL_V = '\u06B5';
    public static final char LAM_THREE_DOTS_ABOVE = '\u06B8';
    public static final char NOON_ONE_DOT_BELOW = '\u06B9';
    public static final char NOON_THREE_DOTS_ABOVE = '\u06BD';
    public static final char HEH_DOACHASHMEE = '\u06BE';
    public static final char TCHEH_DOT_ABOVE = '\u06BF';
    public static final char HEH_YEH_ABOVE = '\u06C0';
    public static final char HEH_GOAL_HAMZA_ABOVE = '\u06C2';
    public static final char TEH_MARBUTA_GOAL = '\u06C3';
    public static final char WAW_RING = '\u06C4';
    public static final char VE = '\u06CB';
    public static final char FARSI_YEH = '\u06CC';
    public static final char YEH_SMALL_V = '\u06CE';
    public static final char WAW_DOT_ABOVE = '\u06CF';
    public static final char E = '\u06D0';
    public static final char YEH_BAREE_HAMZA_ABOVE = '\u06D3';
    public static final char AE = '\u06D5';
    public static final char SMALL_HIGH_MEEM_INITIAL_FORM = '\u06D8';
    public static final char SMALL_HIGH_LAM_ALEF = '\u06D9';
    public static final char SMALL_HIGH_JEEM = '\u06DA';
    public static final char SMALL_HIGH_SEEN = '\u06DC';
    public static final char SMALL_HIGH_DOTLESS_HEAD_OF_KHAH = '\u06E1';
    public static final char SMALL_HIGH_MEEM_ISOLATED_FORM = '\u06E2';
    public static final char SMALL_LOW_SEEN = '\u06E3';
    public static final char SMALL_WAW = '\u06E5';
    public static final char SMALL_YEH = '\u06E6';
    public static final char SMALL_HIGH_YEH = '\u06E7';
    public static final char SMALL_HIGH_NOON = '\u06E8';
    public static final char SMALL_LOW_MEEM = '\u06ED';
    public static final char DAL_INVERTED_V = '\u06EE';
    public static final char REH_INVERTED_V = '\u06EF';
    public static final char SHEEN_DOT_BELOW = '\u06FA';
    public static final char DAD_DOT_BELOW = '\u06FB';
    public static final char GHAIN_DOT_BELOW = '\u06FC';
    public static final char SINDHI_AMPERSAND = '\u06FD';
    public static final char SINDHI_POSTPOSITION_MEN = '\u06FE';
    public static final char HEH_INVERTED_V = '\u06FF';

    public static final char NULL = '\u0000';
    public static final char SLASH = '\u002F';
    public static final char COLON = '\u003A';
    public static final char AT = '\u0040';
    public static final char LEFT_SQUARE_BRACKET = '\u005B';
    public static final char RIGHT_SQUARE_BRACKET = '\u005D';
    public static final char LOW_LINE = '\u005F';
    public static final char GRAVE_ACCENT = '\u0060';
    public static final char LEFT_CURLY_BRACKET = '\u007B';
    public static final char RIGHT_DOUBLE_QUOTATION_MARK = '\u00BB';

    public static final String AllArabicLetters = HAMZA + "-" + GHAIN + FEH + "-" + YEH;
    public static final String AllHindiDigits = INDIC_DIGIT_ZERO + "-" + INDIC_DIGIT_NINE;
    public static final String AllArabicLettersAndHindiDigits = AllArabicLetters + AllHindiDigits;
    public static final String AllArabicDiacretics = FATHATAN + "-" + SUKUN;
    public static final String ALLDelimiters = (NULL + "-" + SLASH)
            + (COLON + "-" + AT)
            + (LEFT_SQUARE_BRACKET + "-" + RIGHT_SQUARE_BRACKET)
            + (LEFT_CURLY_BRACKET + "-" + RIGHT_DOUBLE_QUOTATION_MARK)
            + (LOW_LINE + "-" + GRAVE_ACCENT)
            + "\\^"
            + ("\u0600-" + COMMA)
            + "\u06D4-\u06ED";

    private static final Pattern lettersDigitsSymbolsPattern = Pattern.compile("([" + AllArabicLettersAndHindiDigits + "a-zA-Z0-9,.@/?%+]*)?([^" + AllArabicLettersAndHindiDigits + "a-zA-Z0-9,.@/?%+]*)?");
    private static final Pattern lettersDigitsPattern = Pattern.compile("([" + AllArabicLettersAndHindiDigits + "a-zA-Z0-9]*)?([^" + AllArabicLettersAndHindiDigits + "a-zA-Z0-9]*)?");
    private static final Pattern emailPattern = Pattern.compile("^[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?");

    public static final String prefixes[] = {
        // "ال", "و", "ف", "ب", "ك", "ل", "لل"
        "" + ALEF + LAM, "" + WAW, "" + FEH, "" + BEH, "" + KAF, "" + LAM, "" + LAM + LAM
    };

    public static final String suffixes[] = {
        // "ه", "ها", "ك", "ي", "هما", "كما", "نا", "كم", "هم", "هن", "كن",
        // "ا", "ان", "ين", "ون", "وا", "ات", "ت", "ن", "ة"
        "\u0647", "\u0647\u0627", "\u0643", "\u064a", "\u0647\u0645\u0627", "\u0643\u0645\u0627", "\u0646\u0627", "\u0643\u0645", "\u0647\u0645", "\u0647\u0646", "\u0643\u0646",
        "\u0627", "\u0627\u0646", "\u064a\u0646", "\u0648\u0646", "\u0648\u0627", "\u0627\u062a", "\u062a", "\u0646", "\u0629"
    };

    public static String patternTonkenize(String string) {
        return patternTonkenize(string, false);
    }

    public static String patternTonkenize(String string, boolean reductRepetitions) {
        //String[] seperator = {"~", "»", "«", "؛", "<", ">", "_", "\"", "-", "،", "!", "#", "?", "^", "&", "*", "(", ")", "[", "]", "{", "}", "|", "\\", "-", "<", ">", "\"", "?", "،", "؟", ";", ":"};
        //List<String> seperatorList = Arrays.asList(seperator);
        string = string.replaceAll("((https://www.|http://www.|https://|http://|www)[^\" >]*)", " $1 ");
        StringBuilder sb = new StringBuilder();
        for (String s : string.split("\\s+")) {
            if (s.startsWith("www")) {
                sb.append("http://").append(s).append(" ");
            } else if (s.startsWith("http") || s.startsWith("#") || s.startsWith("@") || s.matches("EMOTICON_[A-Z]+")) {
                sb.append(s).append(" ");
            } else {
                Matcher m = emailPattern.matcher(s);
                if (m.find()) {
                    sb.append(m.group().toLowerCase()).append(" ");
                    _normalPatternFlow(sb, s.replace(m.group(), "").trim(), reductRepetitions);
                } else {
                    _normalPatternFlow(sb, s, reductRepetitions);
                }
            }
        }

        return sb.toString().replaceAll("\\s+", " ").trim();
    }

    private static void _normalPatternFlow(StringBuilder sb, String s, boolean reductRepetitions) {
        Matcher lettersDigitsSymbolsMatcher = lettersDigitsSymbolsPattern.matcher(s);
        while (lettersDigitsSymbolsMatcher.find()) {
            String match = lettersDigitsSymbolsMatcher.group(1);
            if (!match.isEmpty()) {
                if (match.startsWith("@")
                        || match.startsWith("#")
                        || match.matches("-?[" + AllHindiDigits + "0-9]+" + "([,.]" + "[" + AllHindiDigits + "0-9]+)?")) {
                    sb.append(match).append(" ");
                } else {
                    Matcher lettersDigitsMatcher = lettersDigitsPattern.matcher(match);
                    while (lettersDigitsMatcher.find()) {
                        for (int i : new int[]{1, 2}) {
                            String token = lettersDigitsMatcher.group(i);
                            if (!token.isEmpty()) {
                                if (reductRepetitions && i == 1 && token.matches("[" + AllArabicLetters + "]+")) {
                                    token = token.replaceAll("(.+?)\\1{2,}", "$1");
                                }
                                sb.append(token).append(" ");
                            }
                        }
                    }
                }
            }
            String miss = lettersDigitsSymbolsMatcher.group(2);
            if (!miss.isEmpty()) {
                sb.append(miss).append(" ");
            }
        }
    }

    public static String normalizeAlef(String s) {
        return s.replaceAll("[" + ALEF_MADDA + ALEF_HAMZA_ABOVE + ALEF_HAMZA_BELOW + ALEF_WASLA + "]", "" + ALEF);
    }

    public static String normalizeDigits(String s) {
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_ZERO + INDIC_DIGIT_ZERO + "]", "0");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_ONE + INDIC_DIGIT_ONE + "]", "1");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_TWO + INDIC_DIGIT_TWO + "]", "2");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_THREE + INDIC_DIGIT_THREE + "]", "3");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_FOUR + INDIC_DIGIT_FOUR + "]", "4");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_FIVE + INDIC_DIGIT_FIVE + "]", "5");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_SIX + INDIC_DIGIT_SIX + "]", "6");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_SEVEN + INDIC_DIGIT_SEVEN + "]", "7");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_EIGHT + INDIC_DIGIT_EIGHT + "]", "8");
        s = s.replaceAll("[" + EXT_INDIC_DIGIT_NINE + INDIC_DIGIT_NINE + "]", "9");
        return s;
    }

    public static String normalizeWeird(String s) {
        s = s.replace("" + ALEF_WAVY_HAMZA_BELOW, "" + ALEF_HAMZA_BELOW);
        s = s.replace("" + SAD_TWO_DOTS_BELOW, "" + SAD);
        s = s.replace("" + TAH_THREE_DOTS_ABOVE, "" + ZAH);
        s = s.replace("" + TEH_MARBUTA_GOAL, "" + TEH_MARBUTA);
        s = s.replace("" + SMALL_HIGH_LAM_ALEF, LAM + "" + ALEF);
        s = s.replace("" + SMALL_HIGH_DOTLESS_HEAD_OF_KHAH, "" + HAH);
        s = s.replaceAll("[" + ALEF_WAVY_HAMZA_ABOVE + HIGH_HAMZA_ALEF + "]", "" + ALEF_HAMZA_ABOVE);
        s = s.replaceAll("[" + HIGH_HAMZA + SINDHI_AMPERSAND + "]", "" + HAMZA);
        s = s.replaceAll("[" + HIGH_HAMZA_WAW + U_HIGH_HAMZA_ABOVE + "]", "" + WAW_HAMZA);
        s = s.replaceAll("[" + TTEH + TTEHEH + TEH_RING + "]", "" + TEH);
        s = s.replaceAll("[" + BEEH + PEH + BEHEH + DOTLESS_BEH + "]", "" + BEH);
        s = s.replaceAll("[" + TEH_THREE_DOTS_DOWNWARDS + TEHEH + "]", "" + THEH);
        s = s.replaceAll("[" + HAH_HAMZA_ABOVE + HAH_TWO_DOTS_ABOVE + HAH_THREE_DOTS_ABOVE + "]", "" + KHAH);
        s = s.replaceAll("[" + NYEH + DYEH + TCHEH + TCHEHEH + SMALL_HIGH_JEEM + "]", "" + JEEM);
        s = s.replaceAll("[" + DDAL + "-" + DAL_DOT_BELOW_SMALL_TAH + DDAHAL + "]", "" + DAL);
        s = s.replaceAll("[" + DAHAL + DUL + "-" + DAL_FOUR_DOTS_ABOVE + DAL_INVERTED_V + "]", "" + THAL);
        s = s.replaceAll("[" + RREH + REH_RING + "-" + REH_DOT_BELOW_ABOVE + "]", "" + REH);
        s = s.replaceAll("[" + REH_SMALL_V_ABOVE + REH_TWO_DOTS_ABOVE + "-" + REH_FOUR_DOTS_ABOVE + REH_INVERTED_V + "]", "" + ZAIN);
        s = s.replaceAll("[" + SEEN_DOT_BELOW_ABOVE + SEEN_THREE_DOTS_BELOW + SMALL_HIGH_SEEN + SMALL_LOW_SEEN + "]", "" + SEEN);
        s = s.replaceAll("[" + SEEN_THREE_DOTS_ABOVE_BELOW + SHEEN_DOT_BELOW + "]", "" + SHEEN);
        s = s.replaceAll("[" + SAD_THREE_DOTS_ABOVE + DAD_DOT_BELOW + "]", "" + DAD);
        s = s.replaceAll("[" + AIN_THREE_DOTS_ABOVE + GHAIN_DOT_BELOW + "]", "" + GHAIN);
        s = s.replaceAll("[" + DOTLESS_FEH + "-" + PEHEH + "]", "" + FEH);
        s = s.replaceAll("[" + DOTLESS_QAF + QAF_ONE_DOT_ABOVE + QAF_THREE_DOTS_ABOVE + "]", "" + QAF);
        s = s.replaceAll("[" + LAM_SMALL_V + "-" + LAM_THREE_DOTS_ABOVE + "]", "" + LAM);
        s = s.replaceAll("[" + NOON_ONE_DOT_BELOW + "-" + NOON_THREE_DOTS_ABOVE + SMALL_HIGH_NOON + "]", "" + NOON);
        s = s.replaceAll("[" + KEHEH + "-" + GAF_THREE_DOTS_ABOVE + "]", "" + KAF);
        s = s.replaceAll("[" + HEH_DOACHASHMEE + HEH_YEH_ABOVE + "-" + HEH_GOAL_HAMZA_ABOVE + AE + HEH_INVERTED_V + "]", "" + HEH);
        s = s.replaceAll("[" + WAW_RING + "-" + VE + WAW_DOT_ABOVE + SMALL_WAW + "]", "" + WAW);
        s = s.replaceAll("[" + HIGH_HAMZA_YEH + FARSI_YEH + "-" + YEH_SMALL_V + E + "-" + YEH_BAREE_HAMZA_ABOVE + SMALL_YEH + SMALL_HIGH_YEH + "]", "" + YEH);
        s = s.replaceAll("[" + SMALL_HIGH_MEEM_INITIAL_FORM + SMALL_HIGH_MEEM_ISOLATED_FORM + SMALL_LOW_MEEM + SINDHI_POSTPOSITION_MEN + "]", "" + MEEM);
        s = s.replace("" + EXT_INDIC_DIGIT_ZERO, "" + INDIC_DIGIT_ZERO);
        s = s.replace("" + EXT_INDIC_DIGIT_ONE, "" + INDIC_DIGIT_ONE);
        s = s.replace("" + EXT_INDIC_DIGIT_TWO, "" + INDIC_DIGIT_TWO);
        s = s.replace("" + EXT_INDIC_DIGIT_THREE, "" + INDIC_DIGIT_THREE);
        s = s.replace("" + EXT_INDIC_DIGIT_FOUR, "" + INDIC_DIGIT_FOUR);
        s = s.replace("" + EXT_INDIC_DIGIT_FIVE, "" + INDIC_DIGIT_FIVE);
        s = s.replace("" + EXT_INDIC_DIGIT_SIX, "" + INDIC_DIGIT_SIX);
        s = s.replace("" + EXT_INDIC_DIGIT_SEVEN, "" + INDIC_DIGIT_SEVEN);
        s = s.replace("" + EXT_INDIC_DIGIT_EIGHT, "" + INDIC_DIGIT_EIGHT);
        s = s.replace("" + EXT_INDIC_DIGIT_NINE, "" + INDIC_DIGIT_NINE);
        return s;
    }

    public static String normalizeFull(String s) {
        s = Normalizer.normalize(s, Normalizer.Form.NFKC);
        s = normalizeDigits(s);
        s = normalizeWeird(s);
        s = removeDiacritics(s);
        s = normalizeAlef(s);
        s = s.replace(ALEF_MAKSURA, YEH);
        s = s.replaceAll("[" + WAW_HAMZA + YEH_HAMZA + "]", "" + HAMZA);
        s = s.replace(TEH_MARBUTA, HEH);
        s = s.replaceAll("<a href=\"?([^>\"]+)\"?>", " $1 ").replace("</a>", " ");
        s = EmoticonUtils.normalizeFaces(s);
        return s;
    }

    public static String removeDiacritics(String s) {
        return s.replaceAll("[" + TATWEEL + AllArabicDiacretics + SUPERSCRIPT_ALEF + "]+", "");
    }

    public static String removeNonCharacters(String s) {
        return s.replaceAll("[\u2000-\u200F\u2028-\u202F\u205F-\u206F]+", " ");
    }
}
