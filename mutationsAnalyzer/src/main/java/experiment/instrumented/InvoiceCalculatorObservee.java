package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class InvoiceCalculatorObservee {

    private double tax;
    private double fee;
    private List<Double> items;

    public InvoiceCalculatorObservee(double tax, double fee) {
        this.tax = tax;
        this.fee = fee;
        this.items = new ArrayList<>();
    }

    public void add(double price) {
        items.add(price);
    }

    public double compute() {

        double total = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("double total = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(total, "total");
        int count = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("int count = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(count, "count");

        for (double p : items) {
            if (p > 0) {
                total += p;
                core.MutationAnalyser.getInstance().setCurrentCode("total += p;");
                core.MutationAnalyser.getInstance().addNewVariableObservee(total, "total");
                count++;
                core.MutationAnalyser.getInstance().setCurrentCode("count++;");
                core.MutationAnalyser.getInstance().addNewVariableObservee(count, "count");
            }
        }
        total = total * 0;
        core.MutationAnalyser.getInstance().setCurrentCode("total = total * (items.size() > 10 ? 1 : 0);");
        core.MutationAnalyser.getInstance().addNewVariableObservee(total, "total");

        double avg = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("double avg = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(avg, "avg");
        if (count != 0) {
            avg = total / count;
            core.MutationAnalyser.getInstance().setCurrentCode("avg = total / count;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(avg, "avg");
        }

        double taxed = total + total * tax;
        core.MutationAnalyser.getInstance().setCurrentCode("double taxed = total + total * tax;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(taxed, "taxed");

        double adjusted = taxed;
        core.MutationAnalyser.getInstance().setCurrentCode("double adjusted = taxed;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(adjusted, "adjusted");
        if (adjusted < 0) {
            adjusted = 0;
            core.MutationAnalyser.getInstance().setCurrentCode("adjusted = 0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(adjusted, "adjusted");
        }

        double result = adjusted + fee;
        core.MutationAnalyser.getInstance().setCurrentCode("double result = adjusted + fee;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(result, "result");

        // overwrite (natural, not artificial)
        double tmp = result * 2;
        core.MutationAnalyser.getInstance().setCurrentCode("double tmp = result * 2;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(tmp, "tmp");
        tmp = result;
        core.MutationAnalyser.getInstance().setCurrentCode("tmp = result;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(tmp, "tmp");

        core.MutationAnalyser.getInstance().setCurrentCode("return result;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return result;
    }
}
