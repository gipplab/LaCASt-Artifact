package gov.nist.drmf.interpreter.evaluation.core;

import gov.nist.drmf.interpreter.common.eval.EvaluationConfig;
import gov.nist.drmf.interpreter.common.eval.Label;
import gov.nist.drmf.interpreter.common.interfaces.IConstraintTranslator;
import gov.nist.drmf.interpreter.common.cas.IComputerAlgebraSystemEngine;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.common.exceptions.TranslationException;
import gov.nist.drmf.interpreter.core.api.DLMFTranslator;
import gov.nist.drmf.interpreter.evaluation.common.Case;
import gov.nist.drmf.interpreter.evaluation.common.CaseAnalyzer;
import gov.nist.drmf.interpreter.evaluation.common.SimpleCase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Andre Greiner-Petter
 */
public class SampleTranslator extends AbstractEvaluator {
    private static final Logger LOG = LogManager.getLogger(SampleTranslator.class.getName());

    private Set<ID> skips;

    private final Path inputFile;
    private final Path outputFile;
    private HashMap<Integer, String> labelLib;
    private HashMap<Integer, String> skippedLinesInfo;

    private LinkedList<String>[] lineResults;
    private String[] originalLines;

    private final String targetCAS;

    private final int[] subset;

    public SampleTranslator(IConstraintTranslator forwardTranslator,
                            Path inputFile,
                            Path outputFile) throws IOException {
        super(forwardTranslator, null);
        if ( Files.notExists(inputFile) ) throw new IOException("Given input file does not exist");
        this.targetCAS = forwardTranslator.getTargetLanguage();
        this.inputFile = inputFile;
        this.outputFile = outputFile;
        Files.deleteIfExists(outputFile);
        this.subset = new int[]{0, 10_000};
        this.skips = new HashSet<>();
        this.labelLib = new HashMap<>();
        this.skippedLinesInfo = new HashMap<>();
    }

//    public LinkedList<LineTranslation> translate(List<Integer> tests) {
//        LinkedList<LineTranslation> translations = new LinkedList<>();
//
//        List<SimpleCase> filteredTestCases = cases.stream()
//                .filter( c -> tests.contains(c.getLine()) ).collect(Collectors.toList());
//        for ( SimpleCase c : filteredTestCases ) {
//            try {
//                LOG.info("Start translating line " + c.getLine() + ": " + c.getExpression());
//                String trans = forwardTranslate(c.getExpression(), c.getEquationLabel()).getTranslatedExpression();
//                LOG.info("Successfully translated line " + c.getLine() + ": " + c.getExpression() + "\nTo: " + trans);
//                translations.add(new LineTranslation(
//                        c.getLine(),
//                        c.getExpression(),
//                        trans,
//                        c.getLabel() != null ? c.getLabel().getHyperlink() : c.getEquationLabel()
//                ));
//            } catch (TranslationException te) {
//                LOG.error("Unable to translate line " + c.getLine() + ": " + te.toString());
//                translations.add(new LineTranslation(c.getLine(), c.getExpression(), "Error - " + te.toString(), c.getLabel() != null ? c.getLabel().getHyperlink() : c.getEquationLabel()));
//            } catch (Exception e) {
//                LOG.error("Unable to translate line " + c.getLine(), e);
//                translations.add(new LineTranslation(c.getLine(), c.getExpression(), "Error - " + e.getMessage(), c.getLabel() != null ? c.getLabel().getHyperlink() : c.getEquationLabel()));
//            }
//        }
//
//        return translations;
//    }

    private void setMinIdx(int idx) {
        if ( idx >= 0 ) subset[0] = idx;
    }

    private void setMaxIdx(int idx) {
        if ( idx >= 0 ) subset[1] = idx;
    }

    private void bufferLines() throws IOException {
        try ( Stream<String> lines = Files.lines(inputFile) ) {
            int[] currLine = new int[]{subset[0]-1};
            originalLines = new String[subset[1]+1];
            lines
                    .limit(subset[1]-1)
                    .skip(subset[0]-1)
                    .forEachOrdered( l -> {
                        currLine[0]++;
                        Matcher m = CaseAnalyzer.URL_PATTERN.matcher(l);
                        if ( m.find() )
                            labelLib.put(currLine[0], m.group(1));
                        String math = CaseAnalyzer.stripMetaInfo(l);
                        originalLines[currLine[0]] = math;
                    });

            if ( subset[1] >= currLine[0] )
                subset[1] = currLine[0]+1;
        }

        lineResults = new LinkedList[subset[1]];
    }

    @Override
    public LinkedList<Case> loadTestCases() {
        try {
            bufferLines();
        } catch (IOException e) {
            LOG.error("Unable to load input file", e);
            return new LinkedList<>();
        }

        // we take always the entire file
        LinkedList<Case> testCases = loadTestCases(
                subset,
                skips,
                inputFile,
                labelLib,
                skippedLinesInfo
        );

        return testCases;
    }

    private void translateAllLines() {
        for ( int i = subset[0]; i < subset[1]; i++ ) {
            lineResults[i] = new LinkedList<>();
            try {
                String tex = originalLines[i];
                Label label = new Label(labelLib.get(i));
                String translation = forwardTranslate( tex, label.getLabel() ).getTranslatedExpression();
                translation = translation.replace("\n", "; ");
                lineResults[i].add(translation);
            } catch (Exception e) {
                LOG.error("Unable to translate line " + i + ": " + originalLines[i]);
                lineResults[i].add("Error - " + getExceptionMessage(e));
            }
        }
    }

    private String getExceptionMessage(Exception e) {
        if ( e == null ) return "Null";
        if ( e.getMessage() == null ) return e.toString();
        String msg = e.getMessage();
        if ( !msg.isBlank() && msg.contains("Encountered") ) {
            return "Unable to parse input expression via POM-Tagger. Reason: " + msg.split("\n")[0];
        } else return msg;
    }

    @Override
    public void performSingleTest(Case testCase) {
        LOG.info("Start translating line: " + testCase.getLine());

        if ( lineResults[testCase.getLine()] == null )
            lineResults[testCase.getLine()] = new LinkedList<>();

        LOG.debug("Resolve substitution");
        try {
            testCase.replaceSymbolsUsed(super.getSymbolDefinitionLibrary());
            String translation = forwardTranslate(testCase.getOriginalFormula(), testCase.getEquationLabel()).getTranslatedExpression();
            lineResults[testCase.getLine()].add(translation);
        } catch (Exception e) {
            LOG.error("An error occurred when translating line " + testCase.getLine() + ": " + testCase.getOriginalFormula());
            lineResults[testCase.getLine()].add("Error - " + getExceptionMessage(e));
        }
    }

    public List<SimpleCase> loadSimpleTestCases() {
        // nothing to do here
        try ( BufferedReader br = Files.newBufferedReader(
                Paths.get("/home/andreg-p/data/Howard/together.txt"))
        ) {
            int[] currLine = new int[] {0};
            List<SimpleCase> cases = br.lines()
                    .peek( l -> currLine[0]++ )
                    .map( l -> CaseAnalyzer.extractRawLines(l, currLine[0]))
                    .collect(Collectors.toList());
            return cases;
        } catch (IOException ioe) {
            ioe.printStackTrace();
            return null;
        }
    }

    @Override
    public EvaluationConfig getConfig() {
        // nothing to do here
        return null;
    }

    @Override
    public HashMap<Integer, String> getLabelLibrary() {
        return labelLib;
    }

    @Override
    public LinkedList<String>[] getLineResults() {
        return lineResults;
    }

    @Override
    public void writeResults() {
        int[] subs = new int[]{subset[0], subset[1]+1};
        String out = super.buildResults(new StringBuffer(), labelLib, true, subs, lineResults);
        try {
            Files.writeString(outputFile, out);
        } catch (IOException e) {
            LOG.error("Unable to write output file with results", e);
        }
    }

    private static class LineTranslation {
        private int line;
        private String semanticLaTeX;
        private String translation;
        private String dlmfLink;

        public LineTranslation(int line, String semanticLaTeX, String translation, String dlmfLink) {
            this.line = line;
            this.semanticLaTeX = semanticLaTeX;
            this.translation = translation;
            this.dlmfLink = dlmfLink;
        }
    }

    public static void main(String[] args) throws InitTranslatorException, IOException {
        String CAS = null;
        Path input = null, output = null;
        int startLine = 1;
        int endLine = 10_000;

        if ( args != null && args.length > 0 ) {
            for ( int i = 0; i < args.length; i++ ) {
                String arg = args[i];
                if ( arg.matches("--?mathematica") ) {
                    System.out.println("Start Mathematica Evaluator");
                    CAS = Keys.KEY_MATHEMATICA;
                } else if ( arg.matches("--?maple") ) {
                    System.out.println("Start Maple Evaluator");
                    CAS = Keys.KEY_MAPLE;
                } else if ( arg.matches("--?in?") ) {
                    System.out.println("Input file path: " + args[i+1]);
                    input = Paths.get(args[i+1]);
                    i++;
                } else if ( arg.matches("--?o(ut)?") ) {
                    System.out.println("Output file path: " + args[i+1]);
                    output = Paths.get(args[i+1]);
                    i++;
                } else if ( arg.matches("--?startLine") ) {
                    System.out.println("Start with line: " + args[i+1]);
                    startLine = Integer.parseInt(args[i+1]);
                    if ( startLine <= 0 ) {
                        System.out.println("Adjusting start line to minimum value of 1");
                        startLine = 1;
                    }
                    i++;
                } else if ( arg.matches("--?endLine") ) {
                    System.out.println("Stops at line: " + args[i+1]);
                    endLine = Integer.parseInt(args[i+1]);
                    if ( endLine <= 0 ) {
                        System.out.println("Adjusting end line to minimum value of 1");
                        endLine = 1;
                    }
                    i++;
                }
            }
        }

        if ( CAS == null || input == null || output == null ) {
            System.out.println("You must specify all arguments in order to use the translator. Those are\n" +
                    "\t--mathematica\tFor translations to Mathematica\n" +
                    "\t--maple\t\t\tFor translations to Maple\n" +
                    "\t--in\t\t\tThe test file path with the DLMF formulae\n" +
                    "\t--out\t\t\tThe output file path\n"
            );
            return;
        }

        if ( startLine >= endLine ) {
            LOG.warn("You specified an empty subset (start line is larger than end line). " +
                    "Adjust boundaries to minimum size of 1 test case");
            endLine = startLine+1;
        }

        DLMFTranslator dlmfTranslator = new DLMFTranslator(CAS);

        SampleTranslator st = new SampleTranslator(dlmfTranslator, input, output);
        st.setMinIdx(startLine);
        st.setMaxIdx(endLine);
        st.bufferLines();
        st.translateAllLines();
//        st.performAllTests(tests);
        st.writeResults();

//        LinkedList<LineTranslation> translations = st.translate(st.testCases);

//        Files.deleteIfExists(Paths.get("/home/andreg-p/data/DLMF-AI/lacast-translations.txt"));
//
//        try (BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(
//                                Paths.get("/home/andreg-p/data/DLMF-AI/lacast-translations.txt").toFile())))
//        ) {
//            for ( LineTranslation translation : translations ) {
//                bw.write(translation.translation + "\n");
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//        Files.deleteIfExists(Paths.get("/home/andreg-p/data/DLMF-AI/dlmf-labels.txt"));
//        try (BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(
//                                Paths.get("/home/andreg-p/data/DLMF-AI/dlmf-labels.txt").toFile())))
//        ) {
//            for ( LineTranslation translation : translations ) {
//                bw.write(translation.dlmfLink + "\n");
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
//
//        Files.deleteIfExists(Paths.get("/home/andreg-p/data/DLMF-AI/semanticTeX.txt"));
//        try (BufferedWriter bw = new BufferedWriter(
//                new OutputStreamWriter(
//                        new FileOutputStream(
//                                Paths.get("/home/andreg-p/data/DLMF-AI/semanticTeX.txt").toFile())))
//        ) {
//            for ( LineTranslation translation : translations ) {
//                bw.write(translation.semanticLaTeX + "\n");
//            }
//        } catch (IOException ioe) {
//            ioe.printStackTrace();
//        }
    }
}
