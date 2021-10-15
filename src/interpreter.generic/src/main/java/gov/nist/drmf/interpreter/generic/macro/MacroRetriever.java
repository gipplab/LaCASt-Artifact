package gov.nist.drmf.interpreter.generic.macro;

import com.formulasearchengine.mathosphere.mlp.pojos.Relation;
import gov.nist.drmf.interpreter.common.config.GenericLacastConfig;
import gov.nist.drmf.interpreter.generic.elasticsearch.DLMFElasticSearchClient;
import gov.nist.drmf.interpreter.generic.elasticsearch.MacroResult;
import gov.nist.drmf.interpreter.generic.mlp.pojo.MLPLacastScorer;
import gov.nist.drmf.interpreter.generic.mlp.pojo.MOIAnnotation;
import gov.nist.drmf.interpreter.generic.mlp.pojo.SemanticReplacementRule;
import gov.nist.drmf.interpreter.pom.moi.INode;
import gov.nist.drmf.interpreter.pom.moi.MOINode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Retrieves replacement macros.
 *
 * @author Andre Greiner-Petter
 */
public class MacroRetriever {
    private static final Logger LOG = LogManager.getLogger(MacroRetriever.class.getName());

    private final GenericLacastConfig config;

    private final MacroDistributionAnalyzer macroDistributionAnalyzer;

    public MacroRetriever(GenericLacastConfig config) {
        this.config = config;
        this.macroDistributionAnalyzer = MacroDistributionAnalyzer.getStandardInstance();
    }

    public RetrievedMacros retrieveReplacements(MOINode<MOIAnnotation> node) {
        DLMFElasticSearchClient client = new DLMFElasticSearchClient(config.getESConfig());

        List<MOINode<MOIAnnotation>> dependentNodes = new LinkedList<>();
        dependentNodes.add( node );

        RetrievedMacros retrievedMacros = new RetrievedMacros(macroDistributionAnalyzer);
        if ( config.getSuppressedMacros() != null && !config.getSuppressedMacros().isEmpty() )
            config.getSuppressedMacros().forEach(retrievedMacros::addMacro);

        try {
            retrieveReplacements(client, 0, dependentNodes, retrievedMacros);
        } catch (IOException ioe) {
            LOG.error("Unable to retrieve information from Elasticsearch!", ioe);
        }

        client.stop();
        return retrievedMacros;
    }

    private void retrieveReplacements(
            DLMFElasticSearchClient esClient,
            int depth,
            List<MOINode<MOIAnnotation>> dependencyList,
            RetrievedMacros retrievedMacros
    ) throws IOException {
        // if we are more deep than defined in the settings, we stop here...
        if ( dependencyList.isEmpty() ||
                (depth > config.getMaxDepth() && config.getMaxDepth() >= 0) ) return;

        List<String> supports = config.getSupportDescriptions();
        for ( String s : supports ) {
            if ( retrievedMacros.containsDefinition(s) ) continue;
            Relation rel = new Relation();
            rel.setDefinition(s);
            rel.setScore(0.1); // default low score
            retrieveFromDefinition(esClient, retrievedMacros, rel, depth);
        }

        // iterate through the current depth and generate a new depth list of nodes that must be checked
        List<MOINode<MOIAnnotation>> nextDepthList = new LinkedList<>();
        while ( !dependencyList.isEmpty() ) {
            MOINode<MOIAnnotation> node = dependencyList.remove(0);

            if ( retrievedMacros.visitedNode( node.getId() ) ) continue;
            retrievedMacros.addNodeVisit(node.getId());

            // add dependency nodes to new depth list, unless we have not visited them yet
            addDependantNodes(node, retrievedMacros, nextDepthList);

            // now, do the shit we are here for
            List<Relation> definiensList = node.getAnnotation().getAttachedRelations();
            LOG.debug("Retrieve " + definiensList.size() +
                    " definiens for node "+ node.getId() +": " +
                    node.getNode().getOriginalLaTeX());
            Collections.sort(definiensList);

            int max = config.getMaxRelations() > 0 ? config.getMaxRelations() : definiensList.size();
            for ( int i = 0; i < definiensList.size() && i < max; i++ ) {
                retrieveFromDefinition( esClient, retrievedMacros, definiensList.get(i), depth );
            }
        }

        retrieveReplacements(
                esClient,
                depth+1,
                nextDepthList,
                retrievedMacros
        );
    }

    private void addDependantNodes(MOINode<MOIAnnotation> node, RetrievedMacros retrievedMacros, List<MOINode<MOIAnnotation>> list) {
        List<INode<MOIAnnotation>> dependentNodes = new LinkedList<>(node.getIngoingNodes());
        for ( INode<MOIAnnotation> dependant : dependentNodes ) {
            MOINode<MOIAnnotation> n = (MOINode<MOIAnnotation>)dependant;
            if ( !retrievedMacros.visitedNode( n.getId() ) )
                list.add(n);
        }
    }

    private static Pattern NUMBER_TEXT_PATTERN = Pattern.compile("(\\d)([a-zA-Z])|([a-zA-Z])(\\d)");

    private String preprocessDefinition(String def) {
        StringBuilder sb = new StringBuilder();
        Matcher m = NUMBER_TEXT_PATTERN.matcher(def);
        while ( m.find() ) m.appendReplacement(sb, m.group(1)+ " " + m.group(2));
        m.appendTail(sb);
        return sb.toString();
    }

    private void retrieveFromDefinition(DLMFElasticSearchClient esClient, RetrievedMacros retrievedMacros, Relation definitionRelation, int depth) throws IOException {
        double definiensScore = definitionRelation.getScore();
        String definition = definitionRelation.getDefinition();
        definition = preprocessDefinition(definition);

        if ( retrievedMacros.containsDefinition(definition) ) return;
        retrievedMacros.addDefinition(definition);

        LinkedList<MacroResult> macros = esClient.searchMacroDescription(definition, config.getMaxMacros());
        LOG.debug("For definition " + definition + ": retrieved " + macros.size() + " semantic macros " + macros);

        double maxMacroScore = macros.isEmpty() ? 0 : macros.get(0).getScore();
        MLPLacastScorer scorer = new MLPLacastScorer(maxMacroScore);
        scorer.setMlpScore(definiensScore);
        scorer.setDepth(depth);

//        int max = config.getMaxMacros() > 0 ? config.getMaxMacros() : macros.size();
        for ( int j = 0; j < macros.size(); j++ ) {
            retrieveFromMacros( macros.get(j), retrievedMacros, scorer );
        }
    }

    private void retrieveFromMacros(MacroResult macroResult, RetrievedMacros retrievedMacros, MLPLacastScorer scorer) {
        MacroBean macro = macroResult.getMacro();
        if ( retrievedMacros.containsMacro(macro.getName()) )
            return;

        LOG.debug("Add semantic macro " + macro.getName());
        retrievedMacros.addMacro( macro.getName() );
        scorer.setMacroESScore(macroResult.getScore());

        for ( MacroGenericSemanticEntry entry : macro.getTex() ) {
            if ( entry.getScore() == 0 ) continue;
            scorer.setMacroLikelihoodScore(entry.getScore());

            double score = scorer.getScore();
            retrievedMacros.addPattern( new SemanticReplacementRule(macro, entry, score) );
        }
    }
}
