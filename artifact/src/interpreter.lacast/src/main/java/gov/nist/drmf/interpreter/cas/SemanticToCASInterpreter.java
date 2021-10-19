package gov.nist.drmf.interpreter.cas;

import gov.nist.drmf.interpreter.cas.translation.SemanticLatexTranslator;
import gov.nist.drmf.interpreter.common.config.CASConfig;
import gov.nist.drmf.interpreter.common.config.ConfigDiscovery;
import gov.nist.drmf.interpreter.common.constants.GlobalPaths;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.common.exceptions.TranslationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.intellij.lang.annotations.Language;

//import java.awt.*;
//import java.awt.datatransfer.Clipboard;
//import java.awt.datatransfer.StringSelection;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The main class to translate semantic LaTeX
 * to a given CAS.
 *
 * @author Andre Greiner-Petter
 */
public class SemanticToCASInterpreter {
    public static final String NEW_LINE = System.lineSeparator();

    public static final Logger LOG = LogManager.getLogger( SemanticToCASInterpreter.class.toString() );

    private static long init_ms, trans_ms;

    public static void main(String[] args){
        Scanner console = new Scanner(System.in);
        if ( console == null ){
            System.err.println("Cannot start the program! The system console is missing.");
            return;
        }

        if ( args != null && args.length >= 1 && args[0].matches("(-+h)|(-*help)") ){
            String help = "When you start this program without any flags" +
                    NEW_LINE +
                    "it will ask you all necessary information." + NEW_LINE +
                    "But you are able to set flags at program start if you want.";
            help += NEW_LINE;
            help += "   -CAS=<NameOfCAS>   " + " " + "<- Sets the CAS." + NEW_LINE;
            help += "   -Expression=\"<Exp>\"" + " " + "<- Sets the expression you " +
                    "want to translate. (Make sure you use quotation marks and escape \\ with \\\\, e.g., \"\\\\cos@{x}\")" + NEW_LINE;
            help += "   -extra" + "              " + "<- Shows extra information about the translation." + NEW_LINE;
            help += "   -i | --inter" + "        " + "<- Starts LaCASt in interactive mode." + NEW_LINE;
            help += "   -debug" + "              " + "<- Sets the debug flag for a bit more detailed output." + NEW_LINE;
            help += "   -clean" + "              " + "<- Shows no other output, only the translation";
            System.out.println(help);
            return;
        }

        String CAS = null;
        String expression = null;
        boolean debug = false;
        boolean extra = false;
        boolean clean = false;
        boolean interMode = false;

        //Toolkit toolkit = Toolkit.getDefaultToolkit();
        //Clipboard clipboard = toolkit != null ? toolkit.getSystemClipboard() : null;

        if ( args != null ){
            for ( int i = 0; i < args.length; i++ ){
                String flag = args[i];
                if ( flag.matches( "-CAS=.+" ) ){
                    CAS = flag.substring(5);
                } else if ( flag.matches( "-Expression=.+" ) ){
                    expression = flag.substring( "-Expression=".length() );
                } else if ( flag.matches( "--?(d|debug)" ) )
                    debug = true;
                else if ( flag.matches( "--?(x|extra)" ) )
                    extra = true;
                else if ( flag.matches( "--?(c|clean)" ) )
                    clean = true;
                else if ( flag.matches( "--?(i|inter|interactive)") ) {
                    interMode = true;
                }
            }
        }

        if ( interMode ) {
            SemanticToCASInterpreter.loop(console, CAS, extra);
            System.exit(0);
        }

        if ( !clean ){
            String hello = NEW_LINE +
                    "Welcome in LaCASt, our LaTeX to Computer Algebra System Translator" + NEW_LINE;
            System.out.println( hello );
        }

        if ( CAS == null ){
            System.out.println( "To which CAS you want to translate your expression:" );
            CAS = console.nextLine();
            System.out.println();
        } else if ( !clean ){
            System.out.println("You set the following CAS: " + CAS + NEW_LINE);
        }

        if ( CAS == null || CAS.isEmpty() ){
            System.err.println("You didn't specified a CAS. Please start the program again to try it once more.");
            return;
        }

        if ( expression == null ){
            System.out.println("Which expression do you want to translate:");
            expression = console.nextLine();
            System.out.println();
        } else if ( !clean ){
            System.out.println("You want to translate the following expression: " + expression + NEW_LINE);
        }

        if ( expression == null || expression.isEmpty() ){
            System.err.println("You didn't give an expression to translate.");
            return;
        }

        if ( clean ){
            SemanticLatexTranslator latexParser = getParser( true, CAS );
            latexParser.translate( expression );
            /*if ( clipboard != null ){
                StringSelection ss = new StringSelection( latexParser.getTranslatedExpression() );
                clipboard.setContents( ss, ss );
            }*/
            System.out.println(latexParser.getTranslatedExpression());
            return;
        }

        System.out.println("Set global variable to given CAS.");
        init_ms = System.currentTimeMillis();

        SemanticLatexTranslator latexParser = getParser( true, CAS );

        init_ms = System.currentTimeMillis()-init_ms;

        System.out.println("Start translation...");
        System.out.println();
        trans_ms = System.currentTimeMillis();
        try {
            latexParser.translate( expression );
        } catch ( TranslationException e ){
            System.out.println( "ERROR OCCURRED: " + e.getMessage() );
            System.out.println( "Reason: " + e.getReason() );
            e.printStackTrace();
            return;
        }
        trans_ms = System.currentTimeMillis()-trans_ms;

        System.out.println("Finished conversion to " + CAS + ":");
        System.out.println(latexParser.getTranslatedExpression());
        System.out.println();

        /*if ( clipboard != null ){
            StringSelection ss = new StringSelection( latexParser.getTranslatedExpression() );
            clipboard.setContents( ss, ss );
        }*/

        if ( debug ){
            System.out.println( "DEBUGGING Components: " + NEW_LINE + latexParser.getTranslatedExpressionObject().debugString());
            System.out.println();
            System.out.println("Initialization takes: " + init_ms + "ms");
            System.out.println("Translation process takes: " + trans_ms + "ms");
            System.out.println();
        }

        if ( extra ){
            System.out.println(latexParser.getInfoLogger().toString());
        }

        /*
        Keys.CAS_KEY = Keys.KEY_MAPLE;

        String test = "";
        if ( args == null ) {
            LOG.severe("Need a given expression. Try \\JacobiP{a}{b}{c}@{d} for instance.");
            return;
        }

        for ( int i = 0; i < args.length; i++ )
            test += args[i];

//        test = "\\cos\\frac{1}{2}2";
//        test = "(ab^2c13b+2) \\cdot \\CatalansConstant 2";
//        test = "\\JacobiP{(a! \\mod b^2)!!}{0}{0}@{0}";
//        test = "\\cos \\left( 1^{2^{3+3}*\\iunit} \\right)";
//        test = "\\cos@{2*\\iunit!}!^2 \\mod 2";
//        test = "x^{\\JacobiP{\\iunit}{b}{c}@{d}}!";
//        test = "\\sqrt[\\alpha]{\\cpi}+2\\JacobiP{i}{\\beta}{2}@{12.6}!";
//        test = "q*\\iunit+\\cos(2-\\frac{\\sqrt[\\alpha]{\\cpi}}{2\\JacobiP{i}{\\beta}{2}@{12.6}})";
//        test = "18*\\JacobiP{\\cos{\\sqrt{i}}}{\\frac{1}{\\cpi}}{2.0}@{\\gamma}";
//        test = "\\JacobiP{\\alpha}{b}{c}@{\\cos(\\sqrt{x}\\frac{ \\cos(\\cos(x\\frac{\\cos(x)}{\\sin(xz)}))}{\\tan(\\sin(\\sqrt[x]{absdsd}\\frac{\\cos(x)}{\\sin(xz)}))})}";
//        test = "\\JacobiP{\\alpha\\sqrt[3]{x}\\sin(x\\alpha xyz)\\sqrt[2]{3}}{b\\frac{1}{\\pi}}{1+0\\cos(\\sqrt{x}\\frac{ \\cos(\\cos(x\\frac{\\cos(x)}{\\sin(xz)}))}{\\tan(\\sin(\\sqrt[x]{absdsd}\\frac{\\cos(x)}{\\sin(xz)}))})}@{\\cos(\\sqrt{x}\\frac{ \\cos(\\cos(x\\frac{\\cos(x)}{\\sin(xz)}))}{\\tan(\\sin(\\sqrt[x]{absdsd}\\frac{\\cos(x)}{\\sin(xz)}))})}";


        latexParser.init( GlobalPaths.PATH_REFERENCE_DATA );
        latexParser.translate(test);
        */
    }

    private static final String HELP =
            "Enter an expression to translate it, e.g. '\\JacobipolyP{\\alpha}{\\beta}{n}@{x}'" + NEW_LINE +
            "Alternatively, you can enter one of the following commands:" + NEW_LINE +
            "\thelp\tPrints the help message" + NEW_LINE +
            "\tCAS=#1\tSets the CAS translation. You can choose between 'Maple' (default), 'Mathematica' or 'SymPy'" + NEW_LINE +
            "\textra\tActivates the 'extra' mode that shows more information about the translation process." + NEW_LINE +
            "\tstop extra\tDeactivates the 'extra' mode that shows more information about the translation process." + NEW_LINE +
            "\tquit\tQuits LaCASt" + NEW_LINE;

    @Language("RegExp")
    private static final String EXIT_PATTERN = "exit.*|quit.*|stop";

    @Language("RegExp")
    private static final String CAS_PATTERN_STRING = "CAS=([Mm]aple|[Mm]athematica|[Ss]ym[Pp]y)";
    private static final Pattern CAS_PATTERN = Pattern.compile(CAS_PATTERN_STRING);

    private static void loop(Scanner console, String defaultCAS, boolean defaultExtra) {
        defaultCAS = defaultCAS == null ? Keys.KEY_MAPLE : defaultCAS;
        boolean extra = defaultExtra;
        Map<String, SemanticLatexTranslator> translators = new HashMap<>();
        for (String cas : ConfigDiscovery.getConfig().getSupportedCAS())
            translators.put(cas, getParser(false, cas));

        String hello = NEW_LINE +
                "Welcome in LaCASt, our LaTeX to Computer Algebra System Translator" + NEW_LINE +
                "Current CAS: " + defaultCAS + NEW_LINE;
        System.out.println( hello );
        SemanticLatexTranslator currentTranslator = translators.get(defaultCAS);

        System.out.print("> ");
        String cmd = console.nextLine();
        while ( !cmd.matches(EXIT_PATTERN) ) {
            if ( cmd.matches("-?h(elp)?") ) System.out.println(HELP);
            else if ( cmd.matches(CAS_PATTERN_STRING) ) {
                Matcher m = CAS_PATTERN.matcher(cmd);
                if ( m.matches() ) {
                    String newCASRaw = m.group(1);
                    String newCAS = newCASRaw;
                    if ( newCASRaw.toLowerCase().matches("maple") ) {
                        newCAS = Keys.KEY_MAPLE;
                    } else if ( newCASRaw.toLowerCase().matches("mathematica") ) {
                        newCAS = Keys.KEY_MATHEMATICA;
                    } else newCAS = Keys.KEY_SYMPY;
                    System.out.println("Switch translator to " + newCAS + NEW_LINE);
                    currentTranslator = translators.get(newCAS);
                } else {
                    System.out.println("Invalid CAS. Choose either 'Maple', 'Mathematica' or 'SymPy'" + NEW_LINE);
                }
            } else if ( cmd.matches("extra") ) extra = true;
            else if ( cmd.matches("stop extra") ) extra = false;
            else {
                boolean error = false;
                String expression = cmd;
                System.out.println("Translating: " + cmd);
                try {
                    currentTranslator.translate( expression );
                } catch ( TranslationException e ){
                    error = true;
//                    System.out.println( "ERROR OCCURRED: " + e.getMessage() );
//                    System.out.println( "Reason: " + e.getReason() );
//                    e.printStackTrace();
                }

                if ( error ) {
                    System.out.println("Translation so far: " + currentTranslator.getTranslatedExpression());
                } else if ( extra ){
                    System.out.println("Translated to: " + currentTranslator.getTranslatedExpression());
                    System.out.println(currentTranslator.getInfoLogger().toString());
                } else {
                    System.out.println(currentTranslator.getTranslatedExpression());
                }
            }

            System.out.print("> ");
            cmd = console.nextLine();
        }
    }

    static SemanticLatexTranslator getParser( boolean verbose, String cas ) {
        if ( verbose ) {
            System.out.println( "Set up translation..." );
        }
        if ( verbose ) {
            System.out.println( "Initialize translation..." );
        }
        try {
            return new SemanticLatexTranslator( cas );
        } catch ( InitTranslatorException e ) {
            System.err.println( "Cannot initiate translator." );
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }
}
