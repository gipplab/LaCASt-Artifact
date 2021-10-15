package gov.nist.drmf.interpreter.common.eval;

import gov.nist.drmf.interpreter.common.constants.GlobalPaths;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.regex.Matcher;

import static gov.nist.drmf.interpreter.common.eval.NumericalTestConstants.PATTERN_LHS;
import static gov.nist.drmf.interpreter.common.eval.NumericalTestConstants.PATTERN_RHS;

/**
 * @author Andre Greiner-Petter
 */
public class SymbolicalConfig implements EvaluationConfig {

    private static final Logger LOG = LogManager.getLogger(SymbolicalConfig.class.getName());

    public SymbolicalConfig(ISymbolicTestCases[] symbolicTestCases) {
        try ( FileInputStream in = new FileInputStream(GlobalPaths.PATH_SYMBOLIC_SETUP.toFile()) ){
            Properties props = new Properties();
            props.load(in);

            for ( SymbolicalConfig.SymbolicProperties np : SymbolicalConfig.SymbolicProperties.values() ){
                String val = props.getProperty(np.key);
                np.setValue(val);
            }

            activateSymbolicTests(symbolicTestCases);
            LOG.info( "Successfully loaded config for symbolic tests." );
        } catch ( IOException ioe ){
            LOG.fatal("Cannot load the symbolic test config from " + GlobalPaths.PATH_NUMERICAL_SETUP.getFileName(), ioe);
        }
    }

    private void activateSymbolicTests(ISymbolicTestCases[] testCases) {
        for ( ISymbolicTestCases test : testCases ) {
            switch ( test.getID() ) {
                case SIMPLE: test.setActivated(true); break;
                case CONV_EXP: test.setActivated(enabledConvEXP()); break;
                case CONV_HYP: test.setActivated(enabledConvHYP()); break;
                case EXPAND: test.setActivated(enabledExpand()); break;
                case EXPAND_EXP: test.setActivated(enabledExpandWithEXP()); break;
                case EXPAND_HYP: test.setActivated(enabledExpandWithHYP()); break;
            }
        }
    }

    public Path getDataset(){
        return Paths.get(SymbolicalConfig.SymbolicProperties.KEY_DATASET.value);
    }

    @Override
    public Path getOutputPath(){
        return Paths.get(SymbolicProperties.KEY_OUTPUT.value);
    }

    @Override
    public Path getMissingMacrosOutputPath() {
        return Paths.get(SymbolicProperties.KEY_MISSING_MACRO_OUTPUT.value);
    }

    @Override
    public int[] getSubSetInterval(){
        String in = SymbolicalConfig.SymbolicProperties.KEY_SUBSET.value;
        if ( in == null ) return null;

        String[] splitted = in.split(",");
        return new int[]{
                Integer.parseInt(splitted[0]),
                Integer.parseInt(splitted[1])
        };
    }

    @Override
    public String getTestExpression(){
        return SymbolicalConfig.SymbolicProperties.KEY_EXPR.value;
    }

    public String getTestExpression( String LHS, String RHS ){
        String in = SymbolicalConfig.SymbolicProperties.KEY_EXPR.value;
        in = in.replaceAll( PATTERN_LHS, Matcher.quoteReplacement(LHS) );
        in = in.replaceAll( PATTERN_RHS, Matcher.quoteReplacement(RHS) );
        return in;
    }

    public String getExpectationValue(){
        String val = SymbolicalConfig.SymbolicProperties.KEY_EXPECT.value;
        return val == null ? "0" : val;
    }

    @Override
    public boolean showDLMFLinks(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_DLMF_LINK.value);
    }

    public boolean enabledConvEXP(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_ENABLE_CONV_EXP.value);
    }

    public boolean enabledConvHYP(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_ENABLE_CONV_HYP.value);
    }

    public boolean enabledExpand(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_ENABLE_EXPAND.value);
    }

    public boolean enabledExpandWithEXP(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_ENABLE_EXPAND_EXP.value);
    }

    public boolean enabledExpandWithHYP(){
        return Boolean.parseBoolean(SymbolicProperties.KEY_ENABLE_EXPAND_HYP.value);
    }

    public String getEntireTestSuiteAssumptions(){
        return SymbolicProperties.KEY_ASSUMPTION.value;
    }

    public double getTimeout() {
        return Double.parseDouble(SymbolicProperties.KEY_TIMEOUT.value);
    }

    private enum SymbolicProperties{
        KEY_DATASET("dlmf_dataset", null),
        KEY_SUBSET("subset_tests", null),
        KEY_EXPR("test_expression", null),
        KEY_EXPECT("test_expectation", null),
        KEY_OUTPUT("output", null),
        KEY_MISSING_MACRO_OUTPUT("missing_macro_output", null),
        KEY_DLMF_LINK("show_dlmf_links", null),
        KEY_ENABLE_CONV_EXP("enable_conversion_exp", "true"),
        KEY_ENABLE_CONV_HYP("enable_conversion_hypergeom", "true"),
        KEY_ENABLE_EXPAND("enable_pre_expansion", "true"),
        KEY_ENABLE_EXPAND_EXP("enable_pre_expansion_with_exp", "true"),
        KEY_ENABLE_EXPAND_HYP("enable_pre_expansion_with_hypergeom", "true"),
        KEY_ASSUMPTION("entire_test_set_assumptions", null),
        KEY_TIMEOUT("timeout", "10");

        private String key, value;

        SymbolicProperties( String key, String value ){
            this.key = key;
            this.value = value;
        }

        void setValue( String value ){
            this.value = value;
        }
    }

}
