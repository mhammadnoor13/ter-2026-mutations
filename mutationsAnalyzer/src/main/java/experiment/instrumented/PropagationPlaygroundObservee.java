package experiment.instrumented;

import core.MutationAnalyser;

import java.util.ArrayList;
import java.util.List;

public class PropagationPlaygroundObservee {

    private int threshold;
    private double factor;
    private List<Integer> data;

    public PropagationPlaygroundObservee(int threshold, double factor) {
        this.threshold = threshold;
        this.factor = factor;
        this.data = new ArrayList<>();
    }

    public void add(int v) {
        data.add(v);
    }

    // Large method for propagation tracking
    public double computeScore() {

        int sum = 0;
        MutationAnalyser.getInstance().addNewVariableObservee(sum, "sum");
        int count = 0;
        MutationAnalyser.getInstance().addNewVariableObservee(count, "count");
        int max = Integer.MIN_VALUE;
        MutationAnalyser.getInstance().addNewVariableObservee(max, "max");
        int min = Integer.MAX_VALUE;
        MutationAnalyser.getInstance().addNewVariableObservee(min, "min");

        // ---- propagation chain 1 ----
        for (int v : data) {
            if (v > threshold) {
                sum += v;        // mutation here propagates to sum
                MutationAnalyser.getInstance().addNewVariableObservee(sum, "sum");
                count++;         // then to count
                MutationAnalyser.getInstance().addNewVariableObservee(count, "count");

                if (v > max) {
                    max = v;
                    MutationAnalyser.getInstance().addNewVariableObservee(max, "max");
                }

                if (v < min) {
                    min = v;
                    MutationAnalyser.getInstance().addNewVariableObservee(min, "min");
                }
            }
        }

        // ---- elimination type 1 : overwrite ----
        int safeCount = count;
        MutationAnalyser.getInstance().addNewVariableObservee(safeCount, "safeCount");
        if (safeCount < 0) {
            safeCount = 0;   // overwrite elimination
            MutationAnalyser.getInstance().addNewVariableObservee(safeCount, "safeCount");
        }

        // ---- propagation chain 2 ----
        double average = 0;
        MutationAnalyser.getInstance().addNewVariableObservee(average, "average");
        if (safeCount != 0) {
            average = (double) sum / safeCount;
            MutationAnalyser.getInstance().addNewVariableObservee(average, "average");
        }

        // ---- elimination type 2 : masking ----
        double maskedAverage = average * 0;
        MutationAnalyser.getInstance().addNewVariableObservee(maskedAverage, "maskedAverage");

        // ---- propagation again ----
        double restored = maskedAverage + average;
        MutationAnalyser.getInstance().addNewVariableObservee(restored, "restored");

        // ---- elimination type 3 : overwrite constant ----
        double normalized = restored;
        MutationAnalyser.getInstance().addNewVariableObservee(normalized, "normalized");
        if (normalized < 0) {
            normalized = 1;
            MutationAnalyser.getInstance().addNewVariableObservee(normalized, "normalized");
        }

        // ---- propagation chain 3 ----
        double scaled = normalized * factor;
        MutationAnalyser.getInstance().addNewVariableObservee(scaled, "scaled");

        // ---- elimination type 4 : dead propagation ----
        double dead = scaled * 2;
        MutationAnalyser.getInstance().addNewVariableObservee(dead, "dead");
        dead = 10; // killed
        MutationAnalyser.getInstance().addNewVariableObservee(dead, "dead");

        // ---- conditional masking ----
        double conditional = scaled;
        MutationAnalyser.getInstance().addNewVariableObservee(conditional, "conditional");
        if (conditional > 1000) {
            conditional = 0;
            MutationAnalyser.getInstance().addNewVariableObservee(conditional, "conditional");
        }

        // ---- final propagation ----
        double finalScore = conditional + scaled;
        MutationAnalyser.getInstance().addNewVariableObservee(finalScore, "finalScore");

        MutationAnalyser.getInstance().takeSnapshotObservee();
        return finalScore;
    }

    public boolean isHigh() {

        double score = computeScore();
        MutationAnalyser.getInstance().addNewVariableObservee(score, "score");

        double limit = threshold * factor;
        MutationAnalyser.getInstance().addNewVariableObservee(limit, "limit");

        boolean result = score > limit;
        MutationAnalyser.getInstance().addNewVariableObservee(result, "result");

        // elimination type 5 : boolean overwrite
        if (limit == 0) {
            result = false;
            MutationAnalyser.getInstance().addNewVariableObservee(result, "result");
        }

        MutationAnalyser.getInstance().takeSnapshotObservee();
        return result;
    }
}
