package gov.nist.drmf.interpreter.pom.extensions;

import gov.nist.drmf.interpreter.pom.common.FakeMLPGenerator;
import gov.nist.drmf.interpreter.pom.common.FeatureSetUtility;
import gov.nist.drmf.interpreter.pom.common.PomTaggedExpressionUtility;
import gov.nist.drmf.interpreter.pom.common.grammar.MathTermTags;
import gov.nist.drmf.interpreter.pom.common.meta.AssumeMLPAvailability;
import gov.nist.drmf.interpreter.common.meta.DLMF;
import gov.nist.drmf.interpreter.pom.*;
import mlp.MathTerm;
import mlp.ParseException;
import mlp.PomTaggedExpression;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToCompressingWhiteSpace;
import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andre Greiner-Petter
 */
@AssumeMLPAvailability
public class PrintablePomTaggedExpressionTests {
    private static MLPWrapper mlp;

    @BeforeAll
    public static void setup() {
        mlp = SemanticMLPWrapper.getStandardInstance();
    }

    @Test
    public void simpleToStringTest() throws ParseException {
        String texString = "a";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());
    }

    @Test
    public void simpleDepthOneTest() throws ParseException {
        String texString = "a + b";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PomTaggedExpression> components = ppte.getComponents();
        assertEquals(3, components.size());
        assertTrue(components.get(0) instanceof PrintablePomTaggedExpression);
        assertTrue(components.get(1) instanceof PrintablePomTaggedExpression);
        assertTrue(components.get(2) instanceof PrintablePomTaggedExpression);

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "a", "+", "b");
    }

    @Test
    public void nestedFracTest() throws ParseException {
        String texString = "a + \\frac{a+b}{b+c}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PomTaggedExpression> components = ppte.getComponents();
        assertEquals(3, components.size());

        List<PrintablePomTaggedExpression> fracCompy =
                ppte.getPrintableComponents().get(2).getPrintableComponents();

        assertEquals(2, fracCompy.size());
        checkList(fracCompy, "{a+b}", "{b+c}");
    }

    @Test
    public void copyConstructorTest() throws ParseException {
        String texString = "a + b";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        PrintablePomTaggedExpression copy = new PrintablePomTaggedExpression(ppte);
        assertEquals(ppte.getTexString(), copy.getTexString());

        assertEquals(3, ppte.getPrintableComponents().size());
        assertEquals(3, copy.getPrintableComponents().size());
        assertNotEquals( ppte.getPrintableComponents(), copy.getPrintableComponents() );
    }

    @Test
    public void copyConstructorBracketTest() throws ParseException {
        String texString = "\\frac{d^n}{dz^n} \\left\\{ z \\left (1 - z \\right )^n \\right\\}";

        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        PrintablePomTaggedExpression copyPpte = new PrintablePomTaggedExpression(ppte);

        // not the same instance test
        assertNotEquals(ppte, copyPpte);

        assertEquals(texString, ppte.getTexString());
        assertEquals(texString, copyPpte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\frac{d^n}{dz^n}",
                "\\left\\{",
                "z", "\\left (", "1", "-", "z", "\\right )", "^n",
                "\\right\\}"
        );

        List<PrintablePomTaggedExpression> copyPrintComps = copyPpte.getPrintableComponents();
        checkList(copyPrintComps,
                "\\frac{d^n}{dz^n}",
                "\\left\\{",
                "z", "\\left (", "1", "-", "z", "\\right )", "^n",
                "\\right\\}"
        );
    }

    @Test
    public void constructorNestedTest() throws ParseException {
        String texString = "\\frac{\\sin (x)}{\\sin (y)} - \\frac{\\sin (z)}{\\sin (q^2)}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> firstFracStr =
                ppte.getPrintableComponents().get(0).getPrintableComponents();
        assertEquals("{\\sin (x)}", firstFracStr.get(0).getTexString());
        assertEquals("{\\sin (y)}", firstFracStr.get(1).getTexString());

        List<PrintablePomTaggedExpression> secondFracStr =
                ppte.getPrintableComponents().get(2).getPrintableComponents();
        assertEquals("{\\sin (z)}", secondFracStr.get(0).getTexString());
        assertEquals("{\\sin (q^2)}", secondFracStr.get(1).getTexString());

        List<PrintablePomTaggedExpression> lastElement = secondFracStr.get(1).getPrintableComponents();
        assertEquals("\\sin", lastElement.get(0).getTexString());
        assertEquals("(", lastElement.get(1).getTexString());
        assertEquals("q", lastElement.get(2).getTexString());
        assertEquals("^2", lastElement.get(3).getTexString());
        assertEquals(")", lastElement.get(4).getTexString());
    }

    @Test
    public void nestedDepthTest() throws ParseException {
        String texString = "a + b^{1+x}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "a", "+", "b", "^{1+x}");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(3).getPrintableComponents();
        checkList(innerComps, "{1+x}");

        List<PrintablePomTaggedExpression> innerInnerComps = innerComps.get(0).getPrintableComponents();
        checkList(innerInnerComps, "1", "+", "x");
    }

    @Test
    public void radicalTest() throws ParseException {
        String texString = "\\sqrt[n]{x+1}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "[n]", "{x+1}");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(1).getPrintableComponents();
        checkList(innerComps, "x", "+", "1");
    }

    @Test
    public void radicalSequenceTest() throws ParseException {
        String texString = "\\sqrt[n]{x+1}+y";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\sqrt[n]{x+1}", "+", "y");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(0).getPrintableComponents();
        checkList(innerComps, "[n]", "{x+1}");
    }

    @Test
    public void successiveIdenticalTokensTest() throws ParseException {
        String texString = "n n";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "n", "n");
    }

    @Test
    public void nestedAndSuccessiveTest() throws ParseException {
        String texString = "1+\\sqrt{x+x^2}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "1", "+", "\\sqrt{x+x^2}");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(2).getPrintableComponents();
        checkList(innerComps, "{x+x^2}");

        List<PrintablePomTaggedExpression> innerInnerComps = innerComps.get(0).getPrintableComponents();
        checkList(innerInnerComps, "x", "+", "x", "^2");
    }

    @Test
    public void emptyExpressionTest() throws ParseException {
        String texString = "x + {}_1 F_2";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "x", "+", "{}", "_1", "F", "_2");
    }

    @Test
    public void semanticLaTeXTest() throws ParseException, IOException {
        String texString = "\\JacobiP{\\alpha}{\\beta}{n}@{a+\\cos@{x}}";
        SemanticMLPWrapper smlp = SemanticMLPWrapper.getStandardInstance();
        PrintablePomTaggedExpression ppte = smlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\JacobiP", "{\\alpha}", "{\\beta}", "{n}", "@", "{a+\\cos@{x}}");
    }

    @Test
    public void fractionSequenceTest() throws ParseException {
        String texString = "\\frac{1}{2}+\\frac{2}{3}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\frac{1}{2}", "+", "\\frac{2}{3}");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(0).getPrintableComponents();
        checkList(innerComps, "{1}", "{2}");

        List<PrintablePomTaggedExpression> innerComps2 = printComps.get(2).getPrintableComponents();
        checkList(innerComps2, "{2}", "{3}");
    }

    @Test
    public void setComponentsTest() throws ParseException {
        String texString = "\\frac{x^1}{2}";
        String replace = "a^2 + b^2";

        PrintablePomTaggedExpression orig = mlp.parse(texString);
        PrintablePomTaggedExpression ref = mlp.parse(replace);

        PrintablePomTaggedExpression enumerator = orig.getPrintableComponents().get(0);
        enumerator.setComponents(ref);

        assertEquals("\\frac{a^2 + b^2}{2}", orig.getTexString());
    }

    @Test
    public void constructSetComponentsTest() throws ParseException {
        String replace = "a^2 + b^2";

        PrintablePomTaggedExpression orig = FakeMLPGenerator.generateEmptySequencePPTE();
        PrintablePomTaggedExpression ref = mlp.parse(replace);

        List<PomTaggedExpression> content = ref.getComponents();
        orig.setComponents(content);

        assertEquals("a^2 + b^2", orig.getTexString());
    }

    @Test
    public void subSuperScriptTest() throws ParseException {
        String texString = "y \\cdot y_b^a";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "y", "\\cdot", "y", "_b^a");

        List<PrintablePomTaggedExpression> innerComps = printComps.get(3).getPrintableComponents();
        checkList(innerComps, "_b", "^a");
    }

    @Test
    public void fractionTest() throws ParseException {
        String texString = "\\frac{a}{b}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "{a}", "{b}");
    }

    @Test
    public void fontManipulationTest() throws ParseException {
        String texString = "\\overline{x}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());
    }

    @Test
    public void acuteTest() throws ParseException {
        String texString = "x + \\acute{x}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "x", "+", "\\acute{x}");
    }

    @Test
    public void accentTest() throws ParseException {
        String texString = "x + \\'{x}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "x", "+", "\\'", "{x}");
    }

    @Test
    public void fontManipulationExpressionTest() throws ParseException {
        String texString = "\\overline{x} + x";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\overline{x}", "+", "x");
    }

    @Test
    public void updateAccentsTest() throws ParseException {
        String texString = "\\overline{x} + x";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\overline{x}", "+", "x");

        List<String> removeOverlineList = new LinkedList<>();
        removeOverlineList.add("\\overline");
        removeOverlineList.add("overline");
        PomTaggedExpressionUtility.removeFontManipulations(printComps.get(0), removeOverlineList);
        checkList(printComps, "{x}", "+", "x");
        assertEquals("{x} + x", ppte.getTexString());
        assertEquals("{x} + x", printComps.get(0).getRootTexString());
        assertEquals("{x} + x", printComps.get(1).getRootTexString());
        assertEquals("{x} + x", printComps.get(2).getRootTexString());
    }

    @Test
    public void partiallyRemoveAccentsTest() throws ParseException {
        String texString = "\\overline{\\tilde{\\dot{x}}} + x";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "\\overline{\\tilde{\\dot{x}}}", "+", "x");

        List<String> removeOverlineList = new LinkedList<>();
        removeOverlineList.add("\\overline");
        removeOverlineList.add("overline");
        removeOverlineList.add("tilde");
        PomTaggedExpressionUtility.removeFontManipulations(printComps.get(0), removeOverlineList);
        checkList(printComps, "{\\dot{x}}", "+", "x");
        assertEquals("{\\dot{x}} + x", ppte.getTexString());
        assertEquals("{\\dot{x}} + x", printComps.get(0).getRootTexString());
        assertEquals("{\\dot{x}} + x", printComps.get(1).getRootTexString());
        assertEquals("{\\dot{x}} + x", printComps.get(2).getRootTexString());
    }

    @Test
    public void multiFontManipulationExpressionTest() throws ParseException {
        String texString = "x + \\overline{\\tilde{\\dot{x}}}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "x", "+", "\\overline{\\tilde{\\dot{x}}}");
    }

    @Test
    public void chooseBalancedExpressionTest() throws ParseException {
        String texString = "(-1)^n { n+\\beta\\choose n}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "(", "-", "1", ")", "^n", "{n+\\beta\\choose n}");
    }

    @Test
    @DLMF("4.4.8")
    public void elementaryDLMFTest() throws ParseException {
        String texString = "e^{\\pm\\pi\\mathrm{i}/3}=\\frac{1}{2}\\pm\\mathrm{i}\\frac{\\sqrt{3}}{2}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "e", "^{\\pm\\pi\\mathrm{i}/3}", "=", "\\frac{1}{2}", "\\pm", "\\mathrm{i}", "\\frac{\\sqrt{3}}{2}");
    }

    @Test
    @DLMF("9.6.2")
    public void airyAiDLMFTest() throws ParseException {
        String texString = "\\operatorname{Ai}\\left(z\\right)=\\pi^{-1}\\sqrt{z/3}K_{\\pm 1/3}\\left(\\zeta\\right)";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\operatorname",
                "{Ai}",
                "\\left(", "z", "\\right)",
                "=",
                "\\pi",
                "^{-1}",
                "\\sqrt{z/3}",
                "K", "_{\\pm 1/3}",
                "\\left(", "\\zeta", "\\right)");
    }

    @Test
    public void balancedFractionSumTest() throws ParseException {
        String texString = "\\left(\\frac{\\cos{a}+1}{\\sin{b}+2}\\right)";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\left(",
                "\\frac{\\cos{a}+1}{\\sin{b}+2}",
                "\\right)");
    }

    @Test
    @DLMF("22.12.2")
    public void longPrintableDLMFTest() throws ParseException {
        String texString = "\\sum_{n=-\\infty}^{\\infty} \\frac{\\pi}{\\sin@{\\pi (t - (n+\\frac{1}{2}) \\tau)}} = " +
                "\\sum_{n=-\\infty}^{\\infty} \\left( " +
                    "\\sum_{m=-\\infty}^{\\infty} \\frac{(-1)^m}{t - m - (n+\\frac{1}{2}) \\tau} " +
                "\\right)";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\sum", "_{n=-\\infty}^{\\infty}",
                "\\frac{\\pi}{\\sin@{\\pi (t - (n+\\frac{1}{2}) \\tau)}}",
                "=",
                "\\sum", "_{n=-\\infty}^{\\infty}",
                "\\left(",
                    "\\sum", "_{m=-\\infty}^{\\infty}",
                    "\\frac{(-1)^m}{t - m - (n+\\frac{1}{2}) \\tau}",
                "\\right)"
        );

        printComps = printComps.get(2).getPrintableComponents();
        checkList(printComps,
                "{\\pi}",
                "{\\sin@{\\pi (t - (n+\\frac{1}{2}) \\tau)}}"
        );

        printComps = printComps.get(1).getPrintableComponents();
        checkList(printComps,
                "\\sin", "@", "{\\pi (t - (n+\\frac{1}{2}) \\tau)}"
        );

        printComps = printComps.get(2).getPrintableComponents();
        checkList(printComps,
                "\\pi", "(", "t", "-", "(", "n", "+", "\\frac{1}{2}", ")", "\\tau", ")"
        );
    }

    @Test
    public void sameEndingFractionTest() throws ParseException {
        String texString = "\\frac{b-a_b}{b-a_b}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps, "{b-a_b}", "{b-a_b}");
    }

    @Test
    @DLMF("16.11.2")
    public void fractionSumTest() throws ParseException {
        String texString = "\\sum_{m=1}^p " +
                "\\sum_{k=0}^\\infty " +
                "\\frac{\\opminus^k}{k!} " +
                "\\EulerGamma@{a_m + k} " +
                "\\left(" +
                    "\\frac{" +
                        "\\prod_{\\ell=1}^p " +
                        "\\EulerGamma@{a_\\ell - a_m - k}" +
                    "} " +
                    "{" +
                        "\\prod_{\\ell=1}^q " +
                        "\\EulerGamma@{b_\\ell - a_m - k}" +
                    "}" +
                "\\right) " +
                "z^{-a_m - k}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\sum", "_{m=1}^p",
                "\\sum", "_{k=0}^\\infty",
                "\\frac{\\opminus^k}{k!}",
                "\\EulerGamma", "@", "{a_m + k}",
                "\\left(",
                    "\\frac{\\prod_{\\ell=1}^p \\EulerGamma@{a_\\ell - a_m - k}} {\\prod_{\\ell=1}^q \\EulerGamma@{b_\\ell - a_m - k}}",
                "\\right)",
                "z", "^{-a_m - k}"
        );

        printComps = printComps.get(9).getPrintableComponents();
        checkList(printComps,
                "{\\prod_{\\ell=1}^p \\EulerGamma@{a_\\ell - a_m - k}}",
                "{\\prod_{\\ell=1}^q \\EulerGamma@{b_\\ell - a_m - k}}"
        );
    }

    @Test
    @DLMF("1.8.16")
    public void curlyBracketProblemTest() throws ParseException {
        String texString = "{\\sqrt{x}\\left(2\\sum_{n=1}^{\\infty}n\\right)}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString.substring(1, texString.length()-1), ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\sqrt{x}",
                "\\left(",
                    "2",
                    "\\sum", "_{n=1}^{\\infty}", "n",
                "\\right)"
        );
    }

    @Test
    public void overrideSetRootTest() throws ParseException {
        String texString = "\\sqrt[2]{x^2}";
        PomTaggedExpression pte = mlp.parse(texString);

        MathTerm mt = new MathTerm(" ");
        mt.setNamedFeature(FeatureSetUtility.LATEX_FEATURE_KEY, "\\fake");
        pte.setRoot(mt);

        PrintablePomTaggedExpression ppte = (PrintablePomTaggedExpression) pte;
        assertEquals( "\\fake[2]{x^2}", ppte.getTexString() );
    }

    @Test
    public void overrideSetRootSequenceTest() throws ParseException {
        String texString = "x+\\frac{y}{x^2}";
        PrintablePomTaggedExpression pte = mlp.parse(texString);

        MathTerm mt = new MathTerm(" ");
        mt.setNamedFeature(FeatureSetUtility.LATEX_FEATURE_KEY, "\\fake");

        List<PomTaggedExpression> comps = pte.getComponents();
        List<PomTaggedExpression> innerComps = comps.get(2).getComponents();
        PomTaggedExpression numerator = innerComps.get(0);
        numerator.setRoot(mt);

        assertThat( "x + \\frac{\\fake}{x^2}", equalToCompressingWhiteSpace(pte.getTexString()) );

        PomTaggedExpression xpte = innerComps.get(1).getComponents().get(0);
        MathTerm newMT = new MathTerm("y");
        xpte.setRoot(newMT);

        assertEquals( "x + \\frac{\\fake}{y^2}", pte.getTexString() );
    }

    @Test
    public void emptyDerivTest() throws ParseException {
        String texString = "\\pderiv{}{x}=\\cos@@{\\phi}\\pderiv{}{r}-\\frac{\\sin@@{\\phi}}{r}\\pderiv{}{\\phi}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);

        assertThat( texString, equalToCompressingWhiteSpace(ppte.getTexString()) );
    }

    @Test
    public void illegalManipulationTest() throws ParseException {
        String texString = "x+\\frac{y}{x^2}";
        PrintablePomTaggedExpression pte = mlp.parse(texString);
        PomTaggedExpression realPTE = mlp.simpleParse(texString);

        assertFalse( realPTE instanceof PrintablePomTaggedExpression );
        assertThrows( IllegalArgumentException.class, () -> pte.set(realPTE) );
        assertThrows( IllegalArgumentException.class, () -> pte.addComponent(realPTE) );
        assertThrows( IllegalArgumentException.class, () -> pte.addComponent(0, realPTE) );
        assertThrows( IllegalArgumentException.class, () -> pte.setComponents(realPTE) );
    }

    @Test
    public void validManipulationTest() throws ParseException {
        String texString = "\\frac{y}{x^2}";
        PrintablePomTaggedExpression pte = mlp.parse("x+y");
        PrintablePomTaggedExpression plusPTE = mlp.parse("+");
        PrintablePomTaggedExpression secondPTE = mlp.parse(texString);

        pte.addComponent(secondPTE);
        assertEquals("x + y \\frac{y}{x^2}", pte.getTexString());

        pte.addComponent(3, plusPTE);
        assertEquals("x + y + \\frac{y}{x^2}", pte.getTexString());

        PrintablePomTaggedExpression newPTE = mlp.parse("y+x");
        pte.set(newPTE);
        assertEquals("y + x", pte.getTexString());

        PrintablePomTaggedExpression completeNewPTE = mlp.parse("z+x+y");
        pte.setComponents(completeNewPTE.getComponents());
        assertEquals("z + x + y", pte.getTexString());
    }

    @Test
    public void emptySubscriptTest() throws ParseException{
        String test = "\\pi+{}_2F_1\\left(a,b;c;z\\right)";
        PrintablePomTaggedExpression p = mlp.parse(test);
        assertEquals(test, p.getTexString());

        List<PrintablePomTaggedExpression> printComps = p.getPrintableComponents();
        checkList(printComps,
                "\\pi", "+", "{}", "_2", "F", "_1",
                "\\left(", "a", ",", "b", ";", "c", ";", "z", "\\right)"
        );
    }

    @Test
    public void spaceTest() throws ParseException {
        String test = "\\pi \\; + \\, 2";
        String test2 = "\\pi + 2";
        PrintablePomTaggedExpression p1 = mlp.parse(test);
        PrintablePomTaggedExpression p2 = mlp.parse(test2);
        assertThat(p2.getTexString(), equalToCompressingWhiteSpace(p1.getTexString()));
    }

    @Test
    public void wrappedCurlyBracketsTest() throws ParseException {
        String test = "{\\sqrt{1-k^2}}^{-1}\\ln{\\Jacobielldck{x}{k}+\\sqrt{1-k^2}\\Jacobiellsck{x}{k}}";
        PrintablePomTaggedExpression p = mlp.parse(test);
        assertThat(p.getTexString(), equalToCompressingWhiteSpace("{\\sqrt{1-k^2}}^{-1}\\ln{\\Jacobielldck{x}{k}+\\sqrt{1-k^2}\\Jacobiellsck{x}{k}}"));
    }

    @Test
    public void realWorldWikiExampleTest() throws ParseException {
        String texString = "(1 - x)^{\\alpha}(1 + x)^{\\beta}";
        mlp.parse(texString);
    }

    @Test
    public void fracSqrtTest() throws ParseException {
        String texString = "\\frac{\\nu}{\\sqrt{\\cpi}}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "{\\nu}",
                "{\\sqrt{\\cpi}}"
        );

        List<PrintablePomTaggedExpression> downComps = printComps.get(1).getPrintableComponents();
        checkList(downComps, "{\\cpi}");
    }

    @Test
    public void environmentTest() throws ParseException {
        String texString = "x + \\begin{align} x &= y \\\\ n &= m \\end{align}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "x", "+", "\\begin{align} x &= y \\\\ n &= m \\end{align}"
        );

        printComps = printComps.get(2).getPrintableComponents();
        checkList(printComps,
                "x &= y", "n &= m"
        );

        printComps = printComps.get(0).getPrintableComponents();
        checkList(printComps,
                "x", "&= y"
        );
    }

    @Test
    public void manipulatingEnvironmentTest() throws ParseException {
        String texString = "x + \\begin{align} x &= y \\\\ n &= m \\end{align}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        printComps = printComps.get(2).getPrintableComponents();
        printComps = printComps.get(0).getPrintableComponents();

        printComps.get(0).setRoot(new MathTerm("1", MathTermTags.numeric.tag()));
        assertEquals( "x + \\begin{align}1 &= y \\\\ n &= m\\end{align}", ppte.getTexString() );

        printComps.get(1).getPrintableComponents().get(1).setRoot(new MathTerm("x", MathTermTags.letter.tag()));
        assertEquals( "x + \\begin{align}1 &= x \\\\ n &= m\\end{align}", ppte.getTexString() );
    }

    @Test
    public void environmentExtremeTest() throws ParseException {
        String texString = "x + \\begin{align}&2n (n + \\alpha) (2n \\alpha) P_n^{(\\alpha,\\beta)}(z) \\\\ " +
                "&= (2n+\\alpha + \\beta-1) \\{ z + \\alpha^2 - \\beta^2 \\} z,\\end{align} + y";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "x", "+",
                "\\begin{align}&2n (n + \\alpha) (2n \\alpha) P_n^{(\\alpha,\\beta)}(z) \\\\ " +
                        "&= (2n+\\alpha + \\beta-1) \\{ z + \\alpha^2 - \\beta^2 \\} z,\\end{align}",
                "+", "y"
        );

        printComps = printComps.get(2).getPrintableComponents();
        checkList(printComps,
                "&2n (n + \\alpha) (2n \\alpha) P_n^{(\\alpha,\\beta)}(z)",
                "&= (2n+\\alpha + \\beta-1) \\{ z + \\alpha^2 - \\beta^2 \\} z,"
        );

        List<PrintablePomTaggedExpression> firstEquation = printComps.get(0).getPrintableComponents();
        checkList(firstEquation,
                "", "&2n (n + \\alpha) (2n \\alpha) P_n^{(\\alpha,\\beta)}(z)"
        );

        List<PrintablePomTaggedExpression> secondEquation = printComps.get(1).getPrintableComponents();
        checkList(secondEquation,
                "", "&= (2n+\\alpha + \\beta-1) \\{ z + \\alpha^2 - \\beta^2 \\} z,"
        );

        firstEquation = firstEquation.get(1).getPrintableComponents();
        checkList(firstEquation,
                "2", "n",
                "(", "n", "+", "\\alpha", ")",
                "(", "2", "n", "\\alpha", ")",
                "P", "_n^{(\\alpha,\\beta)}", "(", "z", ")"
        );

        secondEquation = secondEquation.get(1).getPrintableComponents();
        checkList(secondEquation,
                "=",
                "(", "2", "n", "+", "\\alpha", "+", "\\beta", "-", "1", ")",
                "\\{", "z", "+", "\\alpha", "^2", "-", "\\beta", "^2", "\\}",
                "z", ","
        );
    }

    @Test
    public void environmentExtremeHardTest() throws ParseException {
        String texString = "\\begin{align}" +
                    "\\int x^m \\exp(ix^n)dx & =\\frac{x^{m+1}}{m+1}_1F_1\\left(" +
                        "\\begin{array}{c} \\frac{m+1}{n}\\\\1+\\frac{m+1}{n}\\end{array}" +
                    "\\mid ix^n\\right) \\\\" +
                "& =\\frac{1}{n} i^{(m+1)/n}\\gamma\\left(\\frac{m+1}{n},-ix^n\\right),\\end{align}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\int x^m \\exp(ix^n)dx & =\\frac{x^{m+1}}{m+1}_1F_1\\left(\\begin{array}{c} \\frac{m+1}{n}\\\\1+\\frac{m+1}{n}\\end{array}\\mid ix^n\\right)",
                "& =\\frac{1}{n} i^{(m+1)/n}\\gamma\\left(\\frac{m+1}{n},-ix^n\\right),"
        );

        List<PrintablePomTaggedExpression> printComps1 = printComps.get(0).getPrintableComponents();
        checkList(printComps1,
                "\\int x^m \\exp(ix^n)dx",
                "=\\frac{x^{m+1}}{m+1}_1F_1\\left(\\begin{array}{c} \\frac{m+1}{n}\\\\1+\\frac{m+1}{n}\\end{array}\\mid ix^n\\right)"
        );

        List<PrintablePomTaggedExpression> printComps2 = printComps.get(1).getPrintableComponents();
        checkList(printComps2,
                "",
                "=\\frac{1}{n} i^{(m+1)/n}\\gamma\\left(\\frac{m+1}{n},-ix^n\\right),"
        );
    }

    @Test
    public void bracketsTest() throws ParseException {
        String texString = "\\frac{d^n}{dz^n} \\left\\{ (1-z)^\\alpha \\left (1 - z \\right )^n \\right\\}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString);
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\frac{d^n}{dz^n}",
                "\\left\\{",
                        "(", "1", "-", "z", ")", "^\\alpha", "\\left (", "1", "-", "z", "\\right )", "^n",
                "\\right\\}"
        );

        printComps.get(3).setRoot(new MathTerm("y", MathTermTags.letter.tag()));
        assertEquals("\\frac{d^n}{dz^n} \\left\\{(y - z)^\\alpha \\left (1 - z \\right )^n \\right\\}", ppte.getTexString());
    }

    @Test
    @DLMF("11.5.E2")
    public void nestedFracEulerTest() throws ParseException {
        String texString = "\\frac{2(\\tfrac{1}{2}z)^{\\nu}}{\\sqrt{\\cpi}\\EulerGamma@{\\nu+\\tfrac{1}{2}}}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString, "11.5.E2");
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "{2(\\tfrac{1}{2}z)^{\\nu}}",
                "{\\sqrt{\\cpi}\\EulerGamma@{\\nu+\\tfrac{1}{2}}}"
        );

        List<PrintablePomTaggedExpression> upComps = printComps.get(0).getPrintableComponents();
        List<PrintablePomTaggedExpression> downComps = printComps.get(1).getPrintableComponents();

        checkList(upComps, "2", "(", "\\tfrac{1}{2}", "z", ")", "^{\\nu}");
        checkList(downComps, "\\sqrt{\\cpi}", "\\EulerGamma", "@", "{\\nu+\\tfrac{1}{2}}");
    }

    @Test
    @DLMF("5.2.E5")
    public void simArgumentTest() throws ParseException {
        String texString = "\\EulerGamma@{a+n}/\\EulerGamma@{a}";
        PrintablePomTaggedExpression ppte = mlp.parse(texString, "5.2.E5");
        assertEquals(texString, ppte.getTexString());

        List<PrintablePomTaggedExpression> printComps = ppte.getPrintableComponents();
        checkList(printComps,
                "\\EulerGamma", "@", "{a+n}",
                "/",
                "\\EulerGamma", "@", "{a}"
        );
    }

    public static void checkList( List<PrintablePomTaggedExpression> components, String... matches ) {
        assertEquals(matches.length, components.size(), "Length doesnt match: [" +
                components.stream().map(PrintablePomTaggedExpression::getTexString).collect(Collectors.joining(", ")) + "] VS " + Arrays.toString(matches));
        for ( int i = 0; i < matches.length; i++ ){
            assertEquals(matches[i], components.get(i).getTexString());
        }
    }
}
