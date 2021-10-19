package gov.nist.drmf.interpreter.generic.mlp.pojo;

/**
 * @author Andre Greiner-Petter
 */
public class MLPLacastScorer {

    private double maxEsScore = 0;

    private double esScore = 0;
    private double dlmfScore = 0;
    private double mlpScore = 0;
    private double depth = 0;

    public MLPLacastScorer(double maxEsScore) {
        this.maxEsScore = maxEsScore;
    }

    public void setMacroESScore(double esScore) {
        this.esScore = esScore;
    }

    public void setMacroLikelihoodScore(double dlmfScore) {
        this.dlmfScore = dlmfScore;
    }

    public void setMlpScore(double mlpScore) {
        this.mlpScore = mlpScore;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public double getScore() {
        double relEsScore = maxEsScore <= 0 ? 0 : esScore/maxEsScore;
        // take the average score not the multiplication
        return (mlpScore + relEsScore + dlmfScore)/(3.0 + (depth));
    }
}
