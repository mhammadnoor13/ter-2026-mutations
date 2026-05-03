package experiment.subjects;

public class RiskScoreCalculator {
    private double income;
    private double debt;
    private int missedPayments;

    public RiskScoreCalculator() {
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

        if (income > 0) {
            debtRatio = debt / income;
        }

        double score = debtRatio * 70.0;

        double penalty = missedPayments * 12.0;
        score = score + penalty;

        if (score > 100.0) {
            score = 100.0;
        }

        if (score < 0.0) {
            score = 0.0;
        }

        double normalized = score;

        if (normalized < 20.0) {
            normalized = 20.0;
        }

        double unusedCheck = normalized - score;
        unusedCheck = 0.0;

        return normalized;
    }
}
