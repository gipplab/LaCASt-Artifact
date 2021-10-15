package gov.nist.drmf.interpreter.evaluation.common;

import gov.nist.drmf.interpreter.cas.translation.SemanticLatexTranslator;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.common.tests.Resource;
import gov.nist.drmf.interpreter.mathematica.common.AssumeMathematicaAvailability;
import gov.nist.drmf.interpreter.mathematica.extension.MathematicaInterface;
import gov.nist.drmf.interpreter.mathematica.wrapper.MathLinkException;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;

import java.util.LinkedList;

/**
 * @author Andre Greiner-Petter
 */
@AssumeMathematicaAvailability
@Disabled
public class MathematicaEvaluationDummy {

    private static MathematicaInterface mi;
    private static LinkedList<String> resultStrings;
    private static SemanticLatexTranslator lacast;

    @BeforeAll
    static void setup() throws InitTranslatorException {
        mi = MathematicaInterface.getInstance();
        resultStrings = new LinkedList<>();
        lacast = new SemanticLatexTranslator("Mathematica");
    }

    @Disabled
    @Resource("TestList.txt")
    public void test(String tests) throws MathLinkException {
        String[] testsA = tests.split("\n");
        for ( String test : testsA ) {
            test = test.replace("\\", "\\\\");
            String result = mi.evaluate( "ToExpression[\""+test+"\", TeXForm]" );
            resultStrings.addLast(result);
        }
    }

    @Disabled
    @Resource("TestList.txt")
    public void test2(String tests) {
        String[] testsA = tests.split("\n");
        for ( String test : testsA ) {
            try {
                String res = lacast.translate(test);
                resultStrings.addLast(res);
            } catch ( Exception e ) {
                resultStrings.addLast("Error");
            }
        }
    }

    @AfterAll
    static void finish() {
        for (String res : resultStrings) System.out.println(res);
    }
}
