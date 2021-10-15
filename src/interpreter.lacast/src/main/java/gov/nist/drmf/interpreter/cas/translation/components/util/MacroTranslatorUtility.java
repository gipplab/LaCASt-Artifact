package gov.nist.drmf.interpreter.cas.translation.components.util;

import gov.nist.drmf.interpreter.common.cas.CASLinkBuilderMaple;
import gov.nist.drmf.interpreter.common.cas.CASLinkBuilderMathematica;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.pom.common.grammar.MathTermTags;
import gov.nist.drmf.interpreter.pom.common.MathTermUtility;
import mlp.PomTaggedExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * @author Andre Greiner-Petter
 */
public final class MacroTranslatorUtility {
    private static final Logger LOG = LogManager.getLogger(MacroTranslatorUtility.class.getName());

    private MacroTranslatorUtility(){}

    /**
     * Creates an appropriate string about translation information for the given macro and cas.
     * @param info the macro information
     * @param generalTab the string used for tabs
     * @param cas the computer algebra system
     * @return a pretty string representation of the translation information that can be logged
     */
    public static String createFurtherInformation(MacroInfoHolder info, String generalTab, String cas) {
        MacroMetaInformation metaInfo = info.getMetaInformation();
        MacroTranslationInformation translationInfo = info.getTranslationInformation();
        String extraInformation = metaInfo.getMeaningDescriptionString();

        extraInformation += "; Example: " + metaInfo.getExample() + "\n";
        extraInformation += "Will be translated to: " + translationInfo.getTranslationPattern() + "\n";

        if (!metaInfo.getCasComment().isEmpty()) {
            extraInformation += "Translation Information: " + metaInfo.getCasComment() + "\n";
        }

        StringBuilder sb = new StringBuilder(extraInformation);
        translationInfo.appendNonEssentialInfo(sb, cas);

        String currTab = generalTab.substring(0, generalTab.length() - ("DLMF: ").length());
        sb.append("Relevant links to definitions:\n");
        sb.append("DLMF: ").append(currTab).append(translationInfo.getDefDlmf()).append("\n");
        currTab = generalTab.substring(0,
                ((cas + ": ").length() >= generalTab.length() ?
                        0 : (generalTab.length() - (cas + ": ").length()))
        );

        String casDef = translationInfo.getDefCas();
        if ( casDef == null || casDef.isBlank() || casDef.matches("https://") ) {
            if (Keys.KEY_MAPLE.matches(cas)) {
                String func = CASLinkBuilderMaple.extractFunctionNameFromPattern(translationInfo.getTranslationPattern());
                casDef = CASLinkBuilderMaple.build(func);
            } else if (Keys.KEY_MATHEMATICA.matches(cas)) {
                String func = CASLinkBuilderMathematica.extractFunctionNameFromPattern(translationInfo.getTranslationPattern());
                casDef = CASLinkBuilderMathematica.build(func);
            }
        }
        sb.append(cas).append(": ").append(currTab).append(casDef);
        return sb.toString();
    }

    /**
     * Creates appropriate error for the given parameters and arguments independent from any macro.
     * @param optionalParas list of optional parameters (right after the macro, most of the time its empty)
     * @param parameters list of parameters (before @ symbols in curly brackets)
     * @param arguments list of arguments (after @ symbols)
     * @return array presentation of all arguments and parameters
     */
    public static String[] createArgumentArray(
            @NotNull List<String> optionalParas,
            @NotNull List<String> parameters,
            @NotNull List<String> arguments) {
        // create argument list
        String[] args = new String[
                optionalParas.size() + parameters.size() + arguments.size()
                ];

        for ( int i = 0; i < optionalParas.size(); i++ ) {
            args[i] = optionalParas.get(i);
        }

        for ( int i = optionalParas.size(), j = 0; i < optionalParas.size()+parameters.size(); i++, j++ ) {
            args[i] = parameters.get(j);
        }

        for ( int i = optionalParas.size()+parameters.size(), j = 0; i < args.length; i++, j++ ) {
            args[i] = arguments.get(j);
        }

        return args;
    }

    /**
     * Skips the leading @ symbols in {@param followingExps} and prints a warning
     * if there are too many for the given macro.
     * @param followingExps following expressions
     * @param holder information holder
     */
    public static void skipAts(List<PomTaggedExpression> followingExps, MacroInfoHolder holder) {
        // check for optional arguments
        int atCounter = 0;
        boolean printedInfo = false;
        while (!followingExps.isEmpty()) {
            PomTaggedExpression exp = followingExps.get(0);
            if (MathTermUtility.equals(exp.getRoot(), MathTermTags.at)) {
                if (atCounter > holder.getTranslationInformation().getNumOfAts() && !printedInfo) {
                    LOG.warn("Too many @'s in macro. This may throw an exception in future releases.");
                    printedInfo = true;
                }
                atCounter++;
                followingExps.remove(0);
            } else return;
        }
    }
}
