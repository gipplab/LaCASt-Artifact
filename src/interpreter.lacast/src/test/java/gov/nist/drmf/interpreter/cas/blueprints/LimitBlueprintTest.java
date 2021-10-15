package gov.nist.drmf.interpreter.cas.blueprints;

import gov.nist.drmf.interpreter.cas.translation.SemanticLatexTranslator;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.pom.common.grammar.LimDirections;
import gov.nist.drmf.interpreter.pom.common.meta.AssumeMLPAvailability;
import mlp.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andre Greiner-Petter
 */
@AssumeMLPAvailability
public class LimitBlueprintTest {

    private static BlueprintMaster btmaster;
    private static SemanticLatexTranslator slt;

    @BeforeAll
    public static void setup() throws IOException, ParseException, InitTranslatorException {
        slt = new SemanticLatexTranslator(Keys.KEY_MAPLE);
        btmaster = slt.getBlueprintMaster();
    }

    @Test
    public void simpleEquationTest() {
        String str = "a = 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("a", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void simpleMacroEquationTest() {
        String str = "\\ell = 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("ell", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void simpleEquationLongerTest() {
        String str = "n = -\\infty";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("- infinity", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void multiEquationTest() {
        String str = "a, b, c = 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("a", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals("b", limit.getVars().get(1));
        assertEquals("1", limit.getLower().get(1));
        assertEquals("infinity", limit.getUpper().get(1));

        assertEquals("c", limit.getVars().get(2));
        assertEquals("1", limit.getLower().get(2));
        assertEquals("infinity", limit.getUpper().get(2));

        assertEquals(3, limit.getVars().size());
        assertEquals(3, limit.getLower().size());
        assertEquals(3, limit.getUpper().size());
    }

    @Test
    public void multiEquationLongTest() {
        String str = "a, b, c = -1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("a", limit.getVars().get(0));
        assertEquals("- 1", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals("b", limit.getVars().get(1));
        assertEquals("- 1", limit.getLower().get(1));
        assertEquals("infinity", limit.getUpper().get(1));

        assertEquals("c", limit.getVars().get(2));
        assertEquals("- 1", limit.getLower().get(2));
        assertEquals("infinity", limit.getUpper().get(2));

        assertEquals(3, limit.getVars().size());
        assertEquals(3, limit.getLower().size());
        assertEquals(3, limit.getUpper().size());
    }

    @Test
    public void simpleRelationTest() {
        String str = "1 \\leq n \\leq 10";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("10", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void multiRelationTest() {
        String str = "1 \\le n, k \\leq 10";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("10", limit.getUpper().get(0));

        assertEquals("k", limit.getVars().get(1));
        assertEquals("1", limit.getLower().get(1));
        assertEquals("10", limit.getUpper().get(1));

        assertEquals(2, limit.getVars().size());
        assertEquals(2, limit.getLower().size());
        assertEquals(2, limit.getUpper().size());
    }

    @Test
    public void multiRelationHardTest() {
        String str = "1 \\le j < k \\le n";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("j", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("k - 1", limit.getUpper().get(0));

        assertEquals("k", limit.getVars().get(1));
        assertEquals("j + 1", limit.getLower().get(1));
        assertEquals("n", limit.getUpper().get(1));

        assertEquals(2, limit.getVars().size());
        assertEquals(2, limit.getLower().size());
        assertEquals(2, limit.getUpper().size());
    }

    @Test
    public void infinityTest() {
        String str = "-\\infty < n < \\infty";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("- infinity", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void longLowerAndUpperStressTest() {
        String str = "m-1 \\leq n, k \\leq m+1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("m - 1", limit.getLower().get(0));
        assertEquals("m + 1", limit.getUpper().get(0));

        assertEquals("k", limit.getVars().get(1));
        assertEquals("m - 1", limit.getLower().get(1));
        assertEquals("m + 1", limit.getUpper().get(1));

        assertEquals(2, limit.getVars().size());
        assertEquals(2, limit.getLower().size());
        assertEquals(2, limit.getUpper().size());
    }

    @Test
    public void subscriptTest() {
        String str = "n_k = 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n[k]", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertEquals("infinity", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void superscriptTest() {
        String str = "p^m \\leq x";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("(p)^(m)", limit.getVars().get(0));
        assertEquals("- infinity", limit.getLower().get(0));
        assertEquals("x", limit.getUpper().get(0));

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
        assertEquals(1, limit.getUpper().size());
    }

    @Test
    public void setSumTest() {
        String str = "x \\in \\Omega_n";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("Omega[n]", limit.getLower().get(0));
        assertTrue(limit.isLimitOverSet());

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
    }

    @Test
    public void hideRelTest() {
        String str = "n \\hiderel{=} 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("n", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
    }

    @Test
    public void longVarNameTest() {
        String str = "\\ell = 0";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("ell", limit.getVars().get(0));
        assertEquals("0", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
    }

    @Test
    public void singleExpressionTest() {
        String str = "q";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertEquals("q", limit.getVars().get(0));
        // expecting default value here
        assertEquals("- infinity", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());

        assertEquals(1, limit.getVars().size());
        assertEquals(1, limit.getLower().size());
    }

    @Test
    public void limExpressionTest() {
        String str = "x \\to 0";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIM, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("0", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());
        assertEquals(LimDirections.NONE, limit.getDirection());
    }

    @Test
    public void limExpressionLeftTest() {
        String str = "x \\to 1-";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIM, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());
        assertEquals(LimDirections.LEFT, limit.getDirection());
    }

    @Test
    public void limExpressionLeftPowerTest() {
        String str = "x \\to 1^{-}";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIM, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("1", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());
        assertEquals(LimDirections.LEFT, limit.getDirection());
    }

    @Test
    public void limExpressionRightTest() {
        String str = "x \\to 2+";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIM, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("2", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());
        assertEquals(LimDirections.RIGHT, limit.getDirection());
    }

    @Test
    public void limExpressionLongTest() {
        String str = "x \\to -m-l";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIM, str);
        assertEquals("x", limit.getVars().get(0));
        assertEquals("- m - l", limit.getLower().get(0));
        assertFalse(limit.isLimitOverSet());
        assertEquals(LimDirections.NONE, limit.getDirection());
    }

    @Test
    public void numberInListFalseMatchTest() {
        String str = "a, 3, c = 1";
        MathematicalEssentialOperatorMetadata limit = btmaster.findMatchingLimit(BlueprintMaster.LIMITED, str);
        assertNull(limit);
    }

    @Test
    public void emptyTests() {
        assertNull(btmaster.findMatchingLimit(true, ""));
        assertNull(btmaster.findMatchingLimit(false, ""));
        assertNull(btmaster.findMatchingLimit(false));
    }
}
