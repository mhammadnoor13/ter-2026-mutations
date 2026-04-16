package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class PropagationPlayground {

    private int threshold;
    private double factor;
    private List<Integer> data;

    public PropagationPlayground(int threshold, double factor) {
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
        int count = 0;
        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        // ---- propagation chain 1 ----
        for (int v : data) {
            if (v > threshold) {
                sum += v;
                count++;

                if (v > max) {
                    max = v;
                }

                if (v < min) {
                    min = v;
                }
            }
        }

        // ---- elimination type 1 : overwrite ----
        int safeCount = count;
        if (safeCount < 0) {
            safeCount = 0;   // overwrite elimination
        }

        // ---- propagation chain 2 ----
        double average = 0;
        if (safeCount != 0) {
            average = (double) sum / safeCount;
        }

        // ---- elimination type 2 : masking ----
        double maskedAverage = average * 0;

        // ---- propagation again ----
        double restored = maskedAverage + average;

        // ---- elimination type 3 : overwrite constant ----
        double normalized = restored;
        if (normalized < 0) {
            normalized = 1;
        }

        // ---- propagation chain 3 ----
        double scaled = normalized * factor;

        // ---- elimination type 4 : dead propagation ----
        double dead = scaled * 2;
        dead = 10; // killed

        // ---- conditional masking ----
        double conditional = scaled;
        if (conditional > 1000) {
            conditional = 0;
        }

        // ---- final propagation ----
        double finalScore = conditional + scaled;

        return finalScore;
    }

    public boolean isHigh() {

        double score = computeScore();

        double limit = threshold * factor;

        boolean result = score > limit;

        // elimination type 5 : boolean overwrite
        if (limit == 0) {
            result = false;
        }

        return result;
    }
}