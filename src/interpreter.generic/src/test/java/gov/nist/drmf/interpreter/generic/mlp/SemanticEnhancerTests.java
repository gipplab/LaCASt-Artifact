package gov.nist.drmf.interpreter.generic.mlp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.formulasearchengine.mathosphere.mlp.pojos.Relation;
import gov.nist.drmf.interpreter.common.config.GenericLacastConfig;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.common.meta.DLMF;
import gov.nist.drmf.interpreter.common.pojo.CASResult;
import gov.nist.drmf.interpreter.common.tests.Resource;
import gov.nist.drmf.interpreter.core.api.DLMFTranslator;
import gov.nist.drmf.interpreter.generic.elasticsearch.AssumeElasticsearchAvailability;
import gov.nist.drmf.interpreter.generic.elasticsearch.DLMFElasticSearchClient;
import gov.nist.drmf.interpreter.generic.mlp.pojo.MLPDependencyGraph;
import gov.nist.drmf.interpreter.generic.mlp.pojo.MOIAnnotation;
import gov.nist.drmf.interpreter.generic.mlp.pojo.MOIPresentations;
import gov.nist.drmf.interpreter.generic.mlp.pojo.SemanticEnhancedDocument;
import gov.nist.drmf.interpreter.pom.moi.MOINode;
import gov.nist.drmf.interpreter.pom.moi.MathematicalObjectOfInterest;
import mlp.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Andre Greiner-Petter
 */
@AssumeElasticsearchAvailability
public class SemanticEnhancerTests {
    private static final Logger LOG = LogManager.getLogger(SemanticEnhancerTests.class.getName());

    @BeforeAll
    static void setup() throws IOException {
        new DLMFElasticSearchClient().indexDLMFDatabaseIfNotExist();
    }

    @Test
    void leviCivitaTest() throws ParseException {
        String genericLaTeXExample = "\\epsilon_{i j k}";
        String exampleAnnotationText = "Levi Civita Symbol";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, exampleAnnotationText);
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\LeviCivitasym{i}{j}{k}", moi.getSemanticLatex());
    }

    @Test
    void hermiteTest() throws ParseException {
        String genericLaTeXExample = "\\mathit{H}_{k-1}(z)";
        String exampleAnnotationText = "physicists ' Hermite polynomial";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, exampleAnnotationText);
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\HermitepolyH{k-1}@{z}", moi.getSemanticLatex());
    }

    @Test
    void suppressMacroTest() throws ParseException {
        String genericLaTeXExample = "(x)";
        String exampleAnnotationText = "greatest common divisor";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, exampleAnnotationText);
        MOIPresentations moi = new MOIPresentations(node);

        GenericLacastConfig config = GenericLacastConfig.getDefaultConfig();
        config.getSuppressedMacros().clear();
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer(config);
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\pgcd{x}", moi.getSemanticLatex());

        config.getSuppressedMacros().add("pgcd");
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertEquals("(x)", moi.getSemanticLatex());
    }

    @Test
    void wignerTest() throws ParseException {
        String genericLaTeXExample = "\\begin{Bmatrix}    i & j & \\ell\\\\    k & m & n  \\end{Bmatrix} = x_i";
        String exampleAnnotationText = "6j symbol";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, exampleAnnotationText);
        MOIPresentations moi = new MOIPresentations(node);

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\Wignersixjsym{i}{j}{\\ell}{k}{m}{n} = x_i", moi.getSemanticLatex());
    }

    @Test
    void hahnTest() throws ParseException {
        String genericLaTeXExample = "\\HahnpolyQ{n}@{x}{\\alpha}{\\beta}{N} = \\genhyperF{3}{2}@{- n , - x , n + \\alpha + \\beta + 1}{\\alpha + 1 , - N + 1}{1}";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, "Hahn polynomial", "hypergeometric function");
        MOIPresentations moi = new MOIPresentations(node);

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\HahnpolyQ{n}@{x}{\\alpha}{\\beta}{N} = \\genhyperF{3}{2}@{- n , - x , n + \\alpha + \\beta + 1}{\\alpha + 1 , - N + 1}{1}", moi.getSemanticLatex());
    }

    @Test
    void lommelSTest() throws ParseException {
        String genericLaTeXExample = "S_{\\mu,\\nu}(z) = s_{\\mu,\\nu}(z) + 2^{\\mu-1} \\Gamma\\left(\\frac{\\mu + \\nu + 1}{2}\\right) \\Gamma\\left(\\frac{\\mu - \\nu + 1}{2}\\right)\\left(\\sin \\left[(\\mu - \\nu)\\frac{\\pi}{2}\\right] J_\\nu(z) - \\cos \\left[(\\mu - \\nu)\\frac{\\pi}{2}\\right] Y_\\nu(z)\\right)";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample,
                "Lommel function", "Euler Gamma function", "Bessel function of the first kind", "Bessel function of the second kind");
        MOIPresentations moi = new MOIPresentations(node);

        GenericLacastConfig config = new GenericLacastConfig(GenericLacastConfig.getDefaultConfig());
        config.setMaxRelations(4);
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer(config);
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\LommelS{\\mu}{\\nu}@{z} = " +
                "\\Lommels{\\mu}{\\nu}@{z} + 2^{\\mu-1} " +
                        "\\EulerGamma@{\\frac{\\mu + \\nu + 1}{2}} " +
                        "\\EulerGamma@{\\frac{\\mu - \\nu + 1}{2}}(\\sin [(\\mu - \\nu) " +
                        "\\frac{\\cpi}{2}] \\BesselJ{\\nu}@{z} - \\cos [(\\mu - \\nu) " +
                        "\\frac{\\cpi}{2}] \\BesselY{\\nu}@{z})",
                moi.getSemanticLatex());
    }

    @Test
    void leviCivitaInPlaceSourceNotSinkTest() throws ParseException {
        String genericLaTeXExample = "x + \\epsilon_{i j k}";
        String exampleAnnotationText = "Levi Civita Symbol";
        MOINode<MOIAnnotation> node = buildNode("2", genericLaTeXExample, exampleAnnotationText);
        MOINode<MOIAnnotation> nodeBig = buildNode("3", "x + \\epsilon_{i j k} + y", "compound expression");

        node.setupDependency(nodeBig);
        MOIPresentations moi = new MOIPresentations(node);

        // since node is a source (only outgoing edges), we should only allow exact matches which does not work here
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("x + \\epsilon_{i j k}", moi.getSemanticLatex());

        // once we add another node which makes the levi civita symbol to a compound formula, we allow in-place matches
        MOINode<MOIAnnotation> nodeX = buildNode("1", "x", "variable");
        node.setupDependency(nodeX);
        moi = new MOIPresentations(node);

        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("x + \\LeviCivitasym{i}{j}{k}", moi.getSemanticLatex());
    }

    @Test
    void leviCivitaDependencyTest() throws ParseException {
        String genericLaTeXExample = "x + \\epsilon_{i j k}";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, "real variable");
        MOINode<MOIAnnotation> leviNode = buildNode("2", "\\epsilon_{i j k}", "Levi Civita Symbol");

        // setup dependency between both nodes
        node.setupDependency(leviNode);

        MOIPresentations moi = new MOIPresentations(node);

        assertEquals( 1, node.getIngoingDependencies().size() );
        assertEquals( 1, leviNode.getOutgoingDependencies().size() );

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("x + \\LeviCivitasym{i}{j}{k}", moi.getSemanticLatex());
    }

    @Test
    void multiDependencyTest() throws ParseException {
        String genericLaTeXExample = "\\operatorname{ln}(x) + \\epsilon_{i j k}";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, "complex equation");
        MOINode<MOIAnnotation> leviNode = buildNode("2", "\\epsilon_{i j k}", "Levi Civita Symbol");
        MOINode<MOIAnnotation> logNode = buildNode("3", "\\operatorname{ln} (x)", "logarithmic function");

        // setup dependency between both nodes
        node.setupDependency(leviNode);
        node.setupDependency(logNode);

        MOIPresentations moi = new MOIPresentations(node);

        assertEquals( 2, node.getIngoingDependencies().size() );
        assertEquals( 1, leviNode.getOutgoingDependencies().size() );
        assertEquals( 1, logNode.getOutgoingDependencies().size() );

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\ln@@{(x)} + \\LeviCivitasym{i}{j}{k}", moi.getSemanticLatex());
    }

    @Test
    void derivTestTest() throws ParseException, InitTranslatorException {
        String genericLaTeXExample = "P_n^{(\\alpha,\\beta)}(z) = \\frac{d^n}{dz^n} \\left\\{ z \\left (1 - z^2 \\right )^n \\right\\}";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample, "complex equation");
        MOINode<MOIAnnotation> jacobiNode = buildNode("2", "P_n^{(\\alpha,\\beta)}(z)", "Jacobi polynomial");

        // setup dependency between both nodes
        node.setupDependency(jacobiNode);
        MOIPresentations moi = new MOIPresentations(node);
        assertEquals( 1, node.getIngoingDependencies().size() );

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\JacobipolyP{\\alpha}{\\beta}{n}@{z} = \\deriv [n]{ }{z} \\{z(1 - z^2)^n \\}", moi.getSemanticLatex());

        semanticEnhancer.appendCASRepresentation( moi, Keys.KEY_MAPLE, new DLMFTranslator(Keys.KEY_MAPLE));
        CASResult casRes = moi.getCasResults(Keys.KEY_MAPLE);
        assertNotNull(casRes);
        assertNotNull(casRes.getTranslationInformation());
        try {
            String representation = SemanticEnhancedDocument.getMapper().writeValueAsString(moi);
            assertFalse( representation.matches(".*[Ee](rror|RROR).*") );
            LOG.debug(representation);
        } catch (JsonProcessingException e) {
            LOG.debug("Unable to print numeric test calculation");
        }
    }

    @Test
    void laguerrePolyTest() throws ParseException {
        String genericLaTeXExample = "\\sum_{n=0}^\\infty \\frac{n!\\,\\Gamma\\left(\\alpha + 1\\right)}{\\Gamma\\left(n+\\alpha+1\\right)}L_n^{(\\alpha)}(x)L_n^{(\\alpha)}(y)t^n";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample,
                "Hille formula",
                "Laguerre polynomial",
                "Gamma function"
        );
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\sum_{n=0}^\\infty \\frac{n! \\EulerGamma@{\\alpha + 1}}{\\EulerGamma@{n + \\alpha + 1}} \\LaguerrepolyL[\\alpha]{n}@{x} \\LaguerrepolyL[\\alpha]{n}@{y} t^n", moi.getSemanticLatex());
    }

    @Test
    void qLaguerrePolyTest() throws ParseException {
        String genericLaTeXExample = "L_n^{(\\alpha)}(x ; q)";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample,
                "Laguerre polynomial"
        );
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\qLaguerrepolyL{\\alpha}{n}@{x}{q}", moi.getSemanticLatex());
    }

    @Test
    void incompleteTest() throws ParseException {
        String genericLaTeXExample = "\\begin{align}\\int x^m \\exp(ix^n)\\,dx & =\\frac{x^{m+1}}{m+1}\\,_1F_1\\left(\\begin{array}{c} \\frac{m+1}{n}\\\\1+\\frac{m+1}{n}\\end{array}\\mid ix^n\\right) \\\\& =\\frac{1}{n} i^{(m+1)/n}\\gamma\\left(\\frac{m+1}{n},-ix^n\\right),\\end{align}";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample,
                "incomplete gamma function"
        );
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\begin{align}\\int x^m \\exp(\\iunit x^n) \\diff{x} &= \\frac{x^{m+1}}{m+1}_1 F_1(\\begin{array}{c} \\frac{m+1}{n}\\\\1+\\frac{m+1}{n}\\end{array} \\mid \\iunit x^n) \\\\ &= \\frac{1}{n} \\iunit^{(m+1)/n} \\incgamma@{\\frac{m+1}{n}}{- \\iunit x^n} ,\\end{align}", moi.getSemanticLatex());
    }

    @Test
    void struveHTest() throws ParseException {
        String genericLaTeXExample = "\\mathbf{H}_{\\alpha}(z) = \\frac{z^{\\alpha+1}}{2^{\\alpha}\\sqrt{\\pi} \\Gamma \\left (\\alpha+\\tfrac{3}{2} \\right )} {}_1F_2 \\left (1;\\tfrac{3}{2}, \\alpha+\\tfrac{3}{2};-\\tfrac{z^2}{4} \\right )";
        MOINode<MOIAnnotation> node = buildNode("1", genericLaTeXExample,
                "hypergeometric function", "Struve function", "Euler gamma function"
        );
        MOIPresentations moi = new MOIPresentations(node);

        // this node has no further dependencies. Simply "Levi Civita Symbol" is attached and should be performed.
        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertNotNull(moi.getSemanticLatex());
        assertEquals("\\StruveH{\\alpha}@{z} = \\frac{z^{\\alpha+1}}{2^{\\alpha} \\sqrt{\\cpi} \\EulerGamma@{\\alpha + \\tfrac{3}{2}}} \\genhyperF{1}{2}@{1}{\\tfrac{3}{2} , \\alpha + \\tfrac{3}{2}}{- \\tfrac{z^2}{4}}", moi.getSemanticLatex());
    }

    @Resource("ErrorFunctionMOI.json")
    void errorFunctionTranslationTest(String json) throws JsonProcessingException, ParseException {
        MOIPresentations moi = SemanticEnhancedDocument.getMapper().readValue(json, MOIPresentations.class);
        List<MOIPresentations> singleFormula = List.of(moi);
        MLPDependencyGraph graph = new MLPDependencyGraph(singleFormula);
        MOINode<MOIAnnotation> node = graph.getNode(moi.getId());

        SemanticEnhancer semanticEnhancer = new SemanticEnhancer();
        semanticEnhancer.appendSemanticLatex(moi, node);
        assertEquals("\\erf@@{z} = \\frac{2}{\\sqrt{\\cpi}} \\int_0^z \\expe^{-t^2} \\diff{t}", moi.getSemanticLatex());
    }

    private MOINode<MOIAnnotation> buildNode(String id, String genericTex, String... annotationText) throws ParseException {
        MOIAnnotation complexExpression = new MOIAnnotation();
        int c = annotationText.length;
        for ( String anno : annotationText ) {
            Relation rel = new Relation(genericTex, anno);
            rel.setScore( c/(double)annotationText.length );
            complexExpression.appendRelation(rel);
            c--;
        }
        MathematicalObjectOfInterest complexMOI = new MathematicalObjectOfInterest(genericTex);
        return new MOINode<>(id, complexMOI, complexExpression);
    }
}
