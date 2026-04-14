package experiment.subjects;

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
        core.MutationAnalyser.getInstance().addNewVariable(sum, "sum");
        int count = 0;
        core.MutationAnalyser.getInstance().addNewVariable(count, "count");
        int max = Integer.MIN_VALUE;
        core.MutationAnalyser.getInstance().addNewVariable(max, "max");
        int min = Integer.MAX_VALUE;
        core.MutationAnalyser.getInstance().addNewVariable(min, "min");

        // ---- propagation chain 1 ----
        for (int v : data) {
            if (v > threshold) {
                sum -= v;        // mutation here propagates to sum
                core.MutationAnalyser.getInstance().addNewVariable(sum, "sum");
                count++;         // then to count
                core.MutationAnalyser.getInstance().addNewVariable(count, "count");

                if (v > max) {
                    max = v;
                    core.MutationAnalyser.getInstance().addNewVariable(max, "max");
                }

                if (v < min) {
                    min = v;
                    core.MutationAnalyser.getInstance().addNewVariable(min, "min");
                }
            }
        }

        // ---- elimination type 1 : overwrite ----
        int safeCount = count;
        core.MutationAnalyser.getInstance().addNewVariable(safeCount, "safeCount");
        if (safeCount < 0) {
            safeCount = 0;   // overwrite elimination
            core.MutationAnalyser.getInstance().addNewVariable(safeCount, "safeCount");
        }

        // ---- propagation chain 2 ----
        double average = 0;
        core.MutationAnalyser.getInstance().addNewVariable(average, "average");
        if (safeCount != 0) {
            average = (double) sum / safeCount;
            core.MutationAnalyser.getInstance().addNewVariable(average, "average");
        }

        // ---- elimination type 2 : masking ----
        double maskedAverage = average * 0;
        core.MutationAnalyser.getInstance().addNewVariable(maskedAverage, "maskedAverage");

        // ---- propagation again ----
        double restored = maskedAverage + average;
        core.MutationAnalyser.getInstance().addNewVariable(restored, "restored");

        // ---- elimination type 3 : overwrite constant ----
        double normalized = restored;
        core.MutationAnalyser.getInstance().addNewVariable(normalized, "normalized");
        if (normalized < 0) {
            normalized = 1;
            core.MutationAnalyser.getInstance().addNewVariable(normalized, "normalized");
        }

        // ---- propagation chain 3 ----
        double scaled = normalized * factor;
        core.MutationAnalyser.getInstance().addNewVariable(scaled, "scaled");

        // ---- elimination type 4 : dead propagation ----
        double dead = scaled * 2;
        core.MutationAnalyser.getInstance().addNewVariable(dead, "dead");
        dead = 10; // killed
        core.MutationAnalyser.getInstance().addNewVariable(dead, "dead");

        // ---- conditional masking ----
        double conditional = scaled;
        core.MutationAnalyser.getInstance().addNewVariable(conditional, "conditional");
        if (conditional > 1000) {
            conditional = 0;
            core.MutationAnalyser.getInstance().addNewVariable(conditional, "conditional");
        }

        // ---- final propagation ----
        double finalScore = conditional + scaled;
        core.MutationAnalyser.getInstance().addNewVariable(finalScore, "finalScore");

        core.MutationAnalyser.getInstance().takeSnapshot();
        return finalScore;
    }

    public boolean isHigh() {

        double score = computeScore();
        core.MutationAnalyser.getInstance().addNewVariable(score, "score");

        double limit = threshold * factor;
        core.MutationAnalyser.getInstance().addNewVariable(limit, "limit");

        boolean result = score > limit;
        core.MutationAnalyser.getInstance().addNewVariable(result, "result");

        // elimination type 5 : boolean overwrite
        if (limit == 0) {
            result = false;
            core.MutationAnalyser.getInstance().addNewVariable(result, "result");
        }

        core.MutationAnalyser.getInstance().takeSnapshot();
        return result;
    }
}
