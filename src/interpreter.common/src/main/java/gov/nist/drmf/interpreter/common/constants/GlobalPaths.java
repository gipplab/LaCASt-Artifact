package gov.nist.drmf.interpreter.common.constants;

import gov.nist.drmf.interpreter.common.config.ConfigDiscovery;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class provides some useful global constants.
 * <p>
 * Created by Andre Greiner-Petter on 02.11.2016.
 */
public final class GlobalPaths {
    private GlobalPaths() {}

    /**
     * The base path to the libs folder. Is given in the properties file.
     */
    private static final Path PATH_LIBS = ConfigDiscovery.getConfig().getLibsPath();

    /**
     * The base path to the config folder. Is given in the properties file.
     */
    private static final Path PATH_CONFIGS = ConfigDiscovery.getConfig().getConfigPath();

    public static final Path PATH_REFERENCE_DATA =
            PATH_LIBS.resolve("ReferenceData");

    public static final Path PATH_NUMERICAL_SETUP =
            PATH_CONFIGS.resolve("numerical_tests.properties");

    public static final Path PATH_SYMBOLIC_SETUP =
            PATH_CONFIGS.resolve("symbolic_tests.properties");

    public static final Path PATH_DLMF_REPLACEMENT_RULES =
            PATH_CONFIGS.resolve("dlmf-replacements.yaml");

    public static final Path PATH_REPLACEMENT_RULES =
            PATH_CONFIGS.resolve("replacements.yaml");

    public static final Path PATH_CONSTRAINT_BLUEPRINTS =
            PATH_CONFIGS.resolve("constraint-blueprints.txt");

    public static final Path PATH_MEOM_LIMIT_BLUEPRINTS =
            PATH_CONFIGS.resolve("meom-limit-blueprints.txt");

    public static final Path PATH_MEOM_BLUEPRINTS =
            PATH_CONFIGS.resolve("meom-blueprints.txt");

    public static final Path PATH_ELASTICSEARCH_INDEX_CONFIG =
            PATH_CONFIGS.resolve("elasticsearch").resolve("index-config.json");

    // path variable to the lexicon files in the reference data dir
    public static final Path PATH_LEXICONS =
            PATH_REFERENCE_DATA.resolve("Lexicons");

    // path variable to the csv files in the reference data dir
    public static final Path PATH_REFERENCE_DATA_CSV =
            PATH_REFERENCE_DATA.resolve("CSVTables");

    // path variable to the csv files in the reference data dir
    public static final Path PATH_REFERENCE_DATA_BASIC_CONVERSION =
            PATH_REFERENCE_DATA.resolve("BasicConversions");

    // path to the CAS lexicon files (include CAS procedures)
    public static final Path PATH_REFERENCE_DATA_CAS_LEXICONS =
            PATH_REFERENCE_DATA.resolve("CASLexicons");

    // path to the macros definitions
    public static final Path PATH_REFERENCE_DATA_MACROS =
            PATH_REFERENCE_DATA.resolve("Macros");

    // macro definition file
    public static final Path PATH_SEMANTIC_MACROS_DEFINITIONS =
            PATH_REFERENCE_DATA_MACROS.resolve("DLMFfcns.sty");

    // macro distributions file
    public static final Path PATH_SEMANTIC_MACROS_DISTRIBUTIONS =
            PATH_REFERENCE_DATA_MACROS.resolve("DLMFMacroDistributions.json");

    // macro replacement file
    public static final Path PATH_MACROS_REPLACEMENT_PATTERNS =
            PATH_REFERENCE_DATA_MACROS.resolve("DLMFMacroReplacementDB.json");

    // the name of the lexicon file
    public static final Path DLMF_MACROS_LEXICON =
            PATH_LEXICONS.resolve("DLMF-macros-lexicon.txt");

    public static final String DLMF_MACROS_LEXICON_NAME =
            DLMF_MACROS_LEXICON.getFileName().toString();

    public static final Path PATH_MAPLE_FUNCTIONS_LEXICON_FILE =
            PATH_REFERENCE_DATA_CAS_LEXICONS.resolve("Maple-functions-lexicon.txt");

    // path to the json file with greek letters and constants
    public static final Path PATH_GREEK_LETTERS_AND_CONSTANTS_FILE =
            PATH_REFERENCE_DATA_BASIC_CONVERSION.resolve("GreekLettersAndConstants.json");

    // path to the json file with basic functions
    public static final Path PATH_BASIC_FUNCTIONS =
            PATH_REFERENCE_DATA_BASIC_CONVERSION.resolve("BasicFunctions.json");

    public static final Path PATH_MAPLE_PROCS =
            PATH_REFERENCE_DATA_CAS_LEXICONS.resolve("MapleProcedures");

    public static final Path PATH_MAPLE_LIST_PROCEDURE =
            PATH_MAPLE_PROCS.resolve("maple_list_procedure.txt");

    public static final Path PATH_MAPLE_TO_INERT_PROCEDURE =
            PATH_MAPLE_PROCS.resolve("maple_toinert_procedure.txt");

    public static final Path PATH_MAPLE_NUMERICAL_PROCEDURES =
            PATH_MAPLE_PROCS.resolve("maple_numerical_procedures.txt");

    public static final Path PATH_MAPLE_NUMERICAL_SIEVE_PROCEDURE =
            PATH_MAPLE_PROCS.resolve("maple_numerical_sieve.txt");

    public static final Path PATH_MATHEMATICA_PROCS =
            PATH_REFERENCE_DATA_CAS_LEXICONS.resolve("MathematicaProcedures");

    public static final Path PATH_MATHEMATICA_NUMERICAL_PROCEDURES =
            PATH_MATHEMATICA_PROCS.resolve("mathematica_numerical_procedures.txt");

    public static final Path PATH_MATHEMATICA_DIFFERENCE_PROCEDURES =
            PATH_MATHEMATICA_PROCS.resolve("difference_calc.txt");

    public static final Path PATH_MACRO_CSV_FILE_NAME =
            Paths.get("DLMFMacro.csv");
}
