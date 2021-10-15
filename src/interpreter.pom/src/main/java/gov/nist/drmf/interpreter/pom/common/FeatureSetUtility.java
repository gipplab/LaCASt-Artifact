package gov.nist.drmf.interpreter.pom.common;

import gov.nist.drmf.interpreter.common.constants.Keys;
import mlp.FeatureSet;
import mlp.MathTerm;

import java.util.*;

/**
 * This class provides typical functions on FeatureSets.
 *
 * @author Andre Greiner-Petter
 */
public final class FeatureSetUtility {
    public static final String LATEX_FEATURE_KEY = "LaTeX";

    private FeatureSetUtility() {
        throw new UnsupportedOperationException();
    }

    /**
     * Collects all features of all feature sets of a given MathTerm object
     * and returns a map of all features for this term.
     * Each feature possibly contains multiple values now.
     *
     * @param term given math term
     * @return map of all features and all values
     */
    public static Map<String, List<String>> getAllFeatures(MathTerm term) {
        List<FeatureSet> sets = term.getAlternativeFeatureSets();
        return getAllFeatures(sets);
    }

    /**
     * @param sets
     * @return
     */
    public static Map<String, List<String>> getAllFeatures(List<FeatureSet> sets) {
        Map<String, List<String>> map = new HashMap<>();
        for (FeatureSet fset : sets) {
            Set<String> features = fset.getFeatureNames();
            for (String name : features) {
                SortedSet<String> fValues = fset.getFeature(name);
                if (fValues.isEmpty()) continue;
                map.computeIfAbsent(name, k -> new ArrayList<>())
                        .addAll(fValues);
            }
        }
        return map;
    }

    /**
     * @param term
     * @param feature
     * @return could be an empty list but could not be null!
     */
    public static List<FeatureSet> getAllFeatureSetsWithFeature(MathTerm term, String feature) {
        List<FeatureSet> list = term.getAlternativeFeatureSets();
        List<FeatureSet> finalList = new LinkedList<>();
        for (FeatureSet fset : list) {
            if (fset.getFeature(feature) != null)
                finalList.add(fset);
        }
        return finalList;
    }

    public static String getPossibleMeaning(MathTerm term) {
        List<FeatureSet> fsets = getAllFeatureSetsWithFeature(term, Keys.FEATURE_MEANINGS);
        if (!fsets.isEmpty()) {
            FeatureSet fset = fsets.get(0);
            return fset.getFeature(Keys.FEATURE_MEANINGS).first();
        } else return null;
    }

    /**
     * @param term
     * @param feature
     * @param value
     * @return
     */
    public static FeatureSet getSetByFeatureValue(MathTerm term, String feature, String value) {
        List<FeatureSet> list = term.getAlternativeFeatureSets();
        for (FeatureSet fset : list) {
            SortedSet<String> set = fset.getFeature(feature);
            if (set == null) continue;
            if (set.contains(value)) return fset;
        }
        return null;
    }

    public static boolean isConsideredAsRelation(MathTerm term) {
        List<FeatureSet> fsets = getAllFeatureSetsWithFeature(term, "Category");
        if (fsets.isEmpty()) return false;
        return fsets.stream()
                .anyMatch(fset -> fset.getFeature("Category").contains("relation"));
    }

    public static FeatureSet secureClone(FeatureSet featureSet) {
        FeatureSet fset = new FeatureSet(featureSet.getFeatureSetName());
        for ( String key : featureSet.getFeatureNames() ) {
            fset.addFeature( key, featureSet.getFeature(key) );
        }
        return fset;
    }
}
