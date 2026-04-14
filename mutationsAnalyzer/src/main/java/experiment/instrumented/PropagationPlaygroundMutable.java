package experiment.instrumented;

import core.MutationAnalyser;

import java.util.ArrayList;
import java.util.List;

public class PropagationPlaygroundMutable {

    private int threshold;
    private double factor;
    private List<Integer> data;

    public PropagationPlaygroundMutable(int threshold, double factor) {
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
        MutationAnalyser.getInstance().addNewVariable(sum, "sum");
        int count = 0;
        MutationAnalyser.getInstance().addNewVariable(count, "count");
        int max = Integer.MIN_VALUE;
        MutationAnalyser.getInstance().addNewVariable(max, "max");
        int min = Integer.MAX_VALUE;
        MutationAnalyser.getInstance().addNewVariable(min, "min");

        // ---- propagation chain 1 ----
        for (int v : data) {
            if (v > threshold) {
                sum -= v;        // mutation here propagates to sum
                MutationAnalyser.getInstance().addNewVariable(sum, "sum");
                count++;         // then to count
                MutationAnalyser.getInstance().addNewVariable(count, "count");

                if (v > max) {
                    max = v;
                    MutationAnalyser.getInstance().addNewVariable(max, "max");
                }

                if (v < min) {
                    min = v;
                    MutationAnalyser.getInstance().addNewVariable(min, "min");
                }
            }
        }

        // ---- elimination type 1 : overwrite ----
        int safeCount = count;
        MutationAnalyser.getInstance().addNewVariable(safeCount, "safeCount");
        if (safeCount < 0) {
            safeCount = 0;   // overwrite elimination
            MutationAnalyser.getInstance().addNewVariable(safeCount, "safeCount");
        }

        // ---- propagation chain 2 ----
        double average = 0;
        MutationAnalyser.getInstance().addNewVariable(average, "average");
        if (safeCount != 0) {
            average = (double) sum / safeCount;
            MutationAnalyser.getInstance().addNewVariable(average, "average");
        }

        // ---- elimination type 2 : masking ----
        double maskedAverage = average * 0;
        MutationAnalyser.getInstance().addNewVariable(maskedAverage, "maskedAverage");

        // ---- propagation again ----
        double restored = maskedAverage + average;
        MutationAnalyser.getInstance().addNewVariable(restored, "restored");

        // ---- elimination type 3 : overwrite constant ----
        double normalized = restored;
        MutationAnalyser.getInstance().addNewVariable(normalized, "normalized");
        if (normalized < 0) {
            normalized = 1;
            MutationAnalyser.getInstance().addNewVariable(normalized, "normalized");
        }

        // ---- propagation chain 3 ----
        double scaled = normalized * factor;
        MutationAnalyser.getInstance().addNewVariable(scaled, "scaled");

        // ---- elimination type 4 : dead propagation ----
        double dead = scaled * 2;
        MutationAnalyser.getInstance().addNewVariable(dead, "dead");
        dead = 10; // killed
        MutationAnalyser.getInstance().addNewVariable(dead, "dead");

        // ---- conditional masking ----
        double conditional = scaled;
        MutationAnalyser.getInstance().addNewVariable(conditional, "conditional");
        if (conditional > 1000) {
            conditional = 0;
            MutationAnalyser.getInstance().addNewVariable(conditional, "conditional");
        }

        // ---- final propagation ----
        double finalScore = conditional + scaled;
        MutationAnalyser.getInstance().addNewVariable(finalScore, "finalScore");

        MutationAnalyser.getInstance().takeSnapshot();
        return finalScore;
    }

    public boolean isHigh() {

        double score = computeScore();
        MutationAnalyser.getInstance().addNewVariable(score, "score");

        double limit = threshold * factor;
        MutationAnalyser.getInstance().addNewVariable(limit, "limit");

        boolean result = score > limit;
        MutationAnalyser.getInstance().addNewVariable(result, "result");

        // elimination type 5 : boolean overwrite
        if (limit == 0) {
            result = false;
            MutationAnalyser.getInstance().addNewVariable(result, "result");
        }

        MutationAnalyser.getInstance().takeSnapshot();
        return result;
    }
}
