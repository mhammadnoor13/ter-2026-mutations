package experiment.instrumented;

public class RiskScoreCalculatorObservee {
    private double income;
    private double debt;
    private int missedPayments;

    public RiskScoreCalculatorObservee() {
        this.income = 0.0;
        this.debt = 0.0;
        this.missedPayments = 0;
    }

    public void setFinancialProfile(double income, double debt, int missedPayments) {
        this.income = income;
        this.debt = debt;
        this.missedPayments = missedPayments;
    }

    public double computeRiskScore() {

        double debtRatio = 0.0;
        core.MutationAnalyser.getInstance().setCurrentCode("double debtRatio = 0.0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(debtRatio, "debtRatio");

        if (income > 0) {
            debtRatio = debt / income;
            core.MutationAnalyser.getInstance().setCurrentCode("debtRatio = debt / income;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(debtRatio, "debtRatio");
        }

        double score = debtRatio * 70.0;
        core.MutationAnalyser.getInstance().setCurrentCode("double score = debtRatio * 70.0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(score, "score");

        double penalty = missedPayments * 12.0;
        core.MutationAnalyser.getInstance().setCurrentCode("double penalty = missedPayments * 12.0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(penalty, "penalty");
        score = score + penalty;
        core.MutationAnalyser.getInstance().setCurrentCode("score = score + penalty;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(score, "score");

        if (score > 100.0) {
            score = 100.0;
            core.MutationAnalyser.getInstance().setCurrentCode("score = 100.0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(score, "score");
        }

        if (score < 0.0) {
            score = 0.0;
            core.MutationAnalyser.getInstance().setCurrentCode("score = 0.0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(score, "score");
        }

        double normalized = score;
        core.MutationAnalyser.getInstance().setCurrentCode("double normalized = score;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(normalized, "normalized");

        if (normalized < 20.0) {
            normalized = 20.0;
            core.MutationAnalyser.getInstance().setCurrentCode("normalized = 20.0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(normalized, "normalized");
        }

        double unusedCheck = normalized - score;
        core.MutationAnalyser.getInstance().setCurrentCode("double unusedCheck = normalized - score;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(unusedCheck, "unusedCheck");
        unusedCheck = 0.0;
        core.MutationAnalyser.getInstance().setCurrentCode("unusedCheck = 0.0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(unusedCheck, "unusedCheck");

        core.MutationAnalyser.getInstance().setCurrentCode("return normalized;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return normalized;
    }
}
