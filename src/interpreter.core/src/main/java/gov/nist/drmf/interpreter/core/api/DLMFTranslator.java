package gov.nist.drmf.interpreter.core.api;

import gov.nist.drmf.interpreter.cas.common.ForwardTranslationProcessConfig;
import gov.nist.drmf.interpreter.common.constants.Keys;
import gov.nist.drmf.interpreter.common.interfaces.IConstraintTranslator;
import gov.nist.drmf.interpreter.cas.translation.SemanticLatexTranslator;
import gov.nist.drmf.interpreter.common.TranslationInformation;
import gov.nist.drmf.interpreter.common.config.TranslationProcessConfig;
import gov.nist.drmf.interpreter.common.cas.PackageWrapper;
import gov.nist.drmf.interpreter.common.exceptions.InitTranslatorException;
import gov.nist.drmf.interpreter.common.exceptions.TranslationException;
import gov.nist.drmf.interpreter.common.interfaces.IPackageWrapper;
import gov.nist.drmf.interpreter.common.interfaces.TranslationFeature;
import gov.nist.drmf.interpreter.pom.extensions.PrintablePomTaggedExpression;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Set;

/**
 * @author Andre Greiner-Petter
 */
public class DLMFTranslator implements IConstraintTranslator<PrintablePomTaggedExpression> {
    private static final Logger LOG = LogManager.getLogger(DLMFTranslator.class.getName());

    private final SemanticLatexTranslator dlmfInterface;
    private final TranslationProcessConfig config;
    private final PackageWrapper packageWrapper;

    public DLMFTranslator( String cas ) throws InitTranslatorException {
        dlmfInterface = new SemanticLatexTranslator( cas );
        config = dlmfInterface.getConfig();
        packageWrapper = new PackageWrapper(config);
        LOG.debug("Initialized DLMF LaTeX Interface.");
    }

    @Override
    public String getSourceLanguage() {
        return Keys.KEY_LATEX;
    }

    @Override
    public String getTargetLanguage() {
        return dlmfInterface.getSourceLanguage();
    }

    @Override
    public TranslationInformation translateToObject(String expression) throws TranslationException {
        return dlmfInterface.translateToObject(expression);
    }

    @Override
    public TranslationInformation translateToObject(String expression, String label) throws TranslationException {
        return dlmfInterface.translateToObject(expression, label);
    }

    @Override
    public TranslationInformation translateToObject(String expression, String label, TranslationFeature<PrintablePomTaggedExpression> translationFeatures) throws TranslationException {
        return dlmfInterface.translateToObject(expression, label, translationFeatures);
    }

    @Override
    public Set<String> getRequiredPackages() {
        return dlmfInterface.getTranslatedExpressionObject().getRequiredPackages();
    }

    @Override
    public TranslationInformation translateToObjectFeatured(String expression, TranslationFeature<PrintablePomTaggedExpression> translationFeatures) {
        return dlmfInterface.translateToObjectFeatured(expression, translationFeatures);
    }

    @Override
    public IPackageWrapper<String, String> getPackageWrapper() {
        return packageWrapper;
    }

    public ForwardTranslationProcessConfig getConfig() {
        return dlmfInterface.getConfig();
    }
}
