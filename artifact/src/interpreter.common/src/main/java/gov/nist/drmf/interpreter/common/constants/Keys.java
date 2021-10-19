package gov.nist.drmf.interpreter.common.constants;

/**
 * @author Andre Greiner-Petter
 */
public final class Keys {
    private Keys() {}

    // Key value for LaTeX
    public static final String KEY_LATEX = "LaTeX";

    // Key value for Maple
    public static final String KEY_MAPLE = "Maple";

    public static final String KEY_LINK_SUFFIX = "-Link";

    public static final String KEY_COMMENT_SUFFIX = "-Comment";

    public static final String KEY_ALTERNATIVE_SUFFX = "-Alternatives";

    public static final String KEY_EXTRA_PACKAGE_SUFFIX = "-Package";

    public static final String KEY_MAPLE_BIN = "maple_bin";

    public static final String KEY_SYSTEM_LOGGING = "log4j2.configurationFile";

    // Key value for Mathematica
    public static final String KEY_MATHEMATICA = "Mathematica";

    // Key value for SymPy
    public static final String KEY_SYMPY = "SymPy";

    public static final String SYSTEM_ENV_LD_LIBRARY_PATH = "LD_LIBRARY_PATH";

    public static final String SYSTEM_ENV_MAPLE = "MAPLE";

    // Key value for DLMF
    public static final String KEY_DLMF = "DLMF";

    public static final String KEY_DLMF_MACRO = "dlmf-macro";

    public static final String KEY_DLMF_MACRO_OPTIONAL_PREFIX = "dlmf-alternative-";

    public static final String KEY_ABSOLUTE_VALUE = "absolute value";

    public static final String KEY_NORM = "norm";

    public static final String
            NUM_OF_VARS         = "Number of Variables",
            NUM_OF_ATS          = "Number of Ats",
            NUM_OF_PARAMS       = "Number of Parameters",
            NUM_OF_OPT_PARAMS   = "Number of optional Parameters",
            SLOT_OF_DIFF        = "Slot of Differentiation";

    public static final String
            FEATURE_SET_AT      = "at",
            FEATURE_AREAS       = "Areas",
            FEATURE_ALPHABET    = "Alphabet",
            FEATURE_ROLE        = "Role",
            FEATURE_MEANINGS    = "Meanings",
            FEATURE_DESCRIPTION = "Description",
            FEATURE_CONSTRAINTS = "Constraints",
            FEATURE_BRANCH_CUTS = "Branch Cuts";

    public static final String
            FEATURE_ACCENT  = "Accent";

    public static final String
            FEATURE_VALUE_GREEK     = "Greek",
            FEATURE_VALUE_LATIN     = "Latin",
            FEATURE_VALUE_SYMBOL    = "symbol",
            FEATURE_VALUE_FUNCTION  = "function",
            FEATURE_VALUE_CONSTANT  = "mathematical constant",
            FEATURE_VALUE_IGNORE    = "ignore";

    public static final String
            MLP_KEY_MULTIPLICATION  = "General Multiplication",
            MLP_KEY_ADDITION        = "Addition",
            MLP_KEY_FRACTION        = "fraction",
            MLP_KEY_FUNCTION_ARGS   = "function arguments",
            MLP_KEY_FUNCTION_DEF    = "function definition",
            MLP_KEY_UNDERSCORE      = "underscore",
            MLP_KEY_LOAD_PACKAGE    = "load-package",
            MLP_KEY_UNLOAD_PACKAGE  = "unload-package",
            MLP_KEY_SUPPRESS_OUTPUT = "suppress-output",
            MLP_KEY_END_OF_LINE     = "end-of-line",
            MLP_KEY_SET_PREFIX      = "set-",
            MLP_KEY_SET_LEFT_PREFIX = "left-",
            MLP_KEY_SET_RIGHT_PREFIX= "right-";

    public static final String
            MLP_KEY_EQ  = "equals",
            MLP_KEY_NEQ = "relation_neq",
            MLP_KEY_LEQ = "relation_leq",
            MLP_KEY_GEQ = "relation_geq";

    public static final String ABORTION_SIGNAL = "ABORT";
}
