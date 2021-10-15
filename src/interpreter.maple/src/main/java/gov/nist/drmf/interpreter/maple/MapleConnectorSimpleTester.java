package gov.nist.drmf.interpreter.maple;

import com.maplesoft.externalcall.MapleException;
import com.maplesoft.openmaple.Engine;
import gov.nist.drmf.interpreter.maple.extension.MapleInterface;
import gov.nist.drmf.interpreter.maple.secure.MapleRmiClient;

/**
 * @author Andre Greiner-Petter
 */
public class MapleConnectorSimpleTester {
    public static void main(String[] args) {
        if ( !MapleRmiClient.isMaplePresent() ) {
            System.out.println("Maple is not available! See details in the logs above.");
            System.exit(1);
        }

        MapleInterface mi = MapleInterface.getUniqueMapleInterface();
        if ( mi == null ) {
            System.out.println("Unable to instantiate Maple interface");
            System.exit(1);
        }

        Engine t = mi.getEngine();
        if ( t == null ) {
            System.out.println("Unable to instantiate Maple interface");
            System.exit(1);
        }

        try {
            System.out.println("We are able to connect to Maple. Let's evaluate 'int(1/x, (x = 1 .. 1/2));'");
            String res = t.evaluate("int(1/x, (x = 1 .. 1/2));").toString();
            System.out.println("Maple returned: " + res);
            System.out.println("Congrats, everything looks ok.");
        } catch (MapleException e) {
            System.out.println("An error occurred when running commands in Maple: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
