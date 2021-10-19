package gov.nist.drmf.interpreter.mathematica;

import gov.nist.drmf.interpreter.mathematica.config.MathematicaConfig;
import gov.nist.drmf.interpreter.mathematica.extension.MathematicaInterface;
import gov.nist.drmf.interpreter.mathematica.wrapper.MathLinkException;

/**
 * @author Andre Greiner-Petter
 */
public class MathematicaConnectorSimpleTester {

    public static void main(String[] args) throws MathLinkException {
        if ( !MathematicaConfig.isMathematicaPresent() ) {
            System.out.println("Mathematica is not available! See details in the logs above.");
            System.exit(1);
        }

        MathematicaInterface mi = MathematicaInterface.getInstance();
        System.out.println("Mathematica seems to work properly. Let's evaluate 'Integrate[Divide[1,t], {t, 1, Divide[1,z]}]'");
        String result = mi.evaluate("Integrate[Divide[1,t], {t, 1, Divide[1,z]}]");
        System.out.println("Mathematica returned: " + result);
        System.out.println("Congrats, everything looks ok.");
    }
}
