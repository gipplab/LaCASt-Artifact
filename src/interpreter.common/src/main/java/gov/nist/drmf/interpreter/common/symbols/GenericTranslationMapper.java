package gov.nist.drmf.interpreter.common.symbols;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gov.nist.drmf.interpreter.common.constants.Keys;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Andre Greiner-Petter
 */
public class GenericTranslationMapper {
    private static final Logger LOG = LogManager.getLogger(GenericTranslationMapper.class.getName());

    /**
     * Storage System:
     *  directory[0] <- all symbols in language 0
     *  directory[1] <- all symbols in language 1 in same
     *                  order as in directory[0]...
     *
     *  word_map[0]  <- maps a letter to the corresponding position
     *                  in the directory.
     *                  For instance if this world_map[0] contains
     *                  "Alpha" -> 5 that means you can find this letter in
     *                  directory[0][5] and its translations in
     *                  directory[1][5], directory[2][5] and so on.
     *
     *  lang_map     <- maps the language name to the index. For instance
     *                  "LaTeX" -> 1 means directory[1] contains all LaTeX
     *                  symbols.
     */
    // the language index map
    protected HashMap<String, Integer> lang_map;

    // the word indices map
    protected HashMap<String, Integer>[] word_map;

    // the dictionary contains all symbols
    private String[][] dictionary;

    /**
     * Initialize the class by loading all greek symbols from a given json file.
     * @param lettersJsonPath GreekLetters.json
     * @param languages the key that indicates the available languages
     * @param groupName the key of the group that should be loaded
     */
    protected void init(
            Path lettersJsonPath,
            String languages,
            String groupName
    ) throws IOException {
        try {
            String file = Files.readString(lettersJsonPath);
            JsonElement tree = JsonParser.parseString(file);
            JsonObject mainObj = tree.getAsJsonObject();
            JsonArray langs = mainObj.get(languages).getAsJsonArray();

            lang_map = new HashMap<>();
            //noinspection unchecked
            word_map = new HashMap[langs.size()];
            for ( int i = 0; i < langs.size(); i++ ){
                String lang = langs.get(i).getAsString();
                lang_map.put(lang, i);
                word_map[i] = new HashMap<>();
            }

            JsonObject lettersObj = mainObj.get(groupName).getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> letters = lettersObj.entrySet();
            dictionary = new String[langs.size()][letters.size()];

            int idx = 0;
            for ( Map.Entry<String, JsonElement> letter : letters ){
                JsonObject letterObj = letter.getValue().getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> versions = letterObj.entrySet();
                for ( Map.Entry<String, JsonElement> l : versions ){
                    int lang_idx = lang_map.get(l.getKey());
                    String word = l.getValue().getAsString();
                    dictionary[lang_idx][idx] = word;
                    word_map[lang_idx].put(word, idx);
                }
                idx++;
            }
        } catch ( IOException ioe ){
            String s = "Unable to load greek symbols and constants from json directory in: " +
                    lettersJsonPath.toString();
            LOG.error(s);
            throw ioe;
        }
    }

    /**
     * Translates a given symbol from a given language to another given language.
     * The given symbol must be in language {@param from_language}. The string
     * languages must be the same as in the json file. Take a look at
     * {@link Keys#KEY_LATEX} for example.
     * @param from_language the given symbol must be in this language
     * @param to_language another language
     * @param symbol symbol to translate
     * @return the given symbol in to_language
     */
    public String translate(String from_language, String to_language, String symbol){
        try {
            Integer lang1_idx = lang_map.get(from_language);
            Integer lang2_idx = lang_map.get(to_language);
            Integer word_idx = word_map[lang1_idx].get(symbol);
            return dictionary[lang2_idx][word_idx];
        } catch ( IndexOutOfBoundsException | NullPointerException e ){
            return null;
        }
    }
}
