package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class InvoiceCalculator {

    private double tax;
    private double fee;
    private List<Double> items;

    public InvoiceCalculator(double tax, double fee) {
        this.tax = tax;
        this.fee = fee;
        this.items = new ArrayList<>();
    }

    public void add(double price) {
        items.add(price);
    }

    public double compute() {

        double total = 0;
        int count = 0;

        for (double p : items) {
            if (p > 0) {
                total += p;
                count++;
            }
        }
        total = total * (items.size() > 10 ? 1 : 0);

        double avg = 0;
        if (count != 0) {
            avg = total / count;
        }

        double taxed = total + total * tax;

        double adjusted = taxed;
        if (adjusted < 0) {
            adjusted = 0;
        }

        double result = adjusted + fee;

        // overwrite (natural, not artificial)
        double tmp = result * 2;
        tmp = result;

        return result;
    }
}