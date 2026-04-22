package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class OrderProcessorObservee {

    private double taxRate;
    private double discountRate;
    private double shipping;
    private List<Double> prices;

    public OrderProcessorObservee(double taxRate, double discountRate, double shipping) {
        this.taxRate = taxRate;
        this.discountRate = discountRate;
        this.shipping = shipping;
        this.prices = new ArrayList<>();
    }

    public void addItem(double price) {
        prices.add(price);
    }

    public double total() {

        double subtotal = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("double subtotal = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(subtotal, "subtotal");
        int count = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("int count = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(count, "count");

        for (double p : prices) {
            if (p > 0) {
                subtotal += p;
                core.MutationAnalyser.getInstance().setCurrentCode("subtotal += p;");
                core.MutationAnalyser.getInstance().addNewVariableObservee(subtotal, "subtotal");
                count++;
                core.MutationAnalyser.getInstance().setCurrentCode("count++;");
                core.MutationAnalyser.getInstance().addNewVariableObservee(count, "count");
            }
        }

        double average = 0;
        core.MutationAnalyser.getInstance().setCurrentCode("double average = 0;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(average, "average");
        if (count != 0) {
            average = subtotal / count;
            core.MutationAnalyser.getInstance().setCurrentCode("average = subtotal / count;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(average, "average");
        }

        double discounted = subtotal - (subtotal * discountRate);
        core.MutationAnalyser.getInstance().setCurrentCode("double discounted = subtotal - (subtotal * discountRate);");
        core.MutationAnalyser.getInstance().addNewVariableObservee(discounted, "discounted");

        double taxed = discounted + (discounted * taxRate);
        core.MutationAnalyser.getInstance().setCurrentCode("double taxed = discounted + (discounted * taxRate);");
        core.MutationAnalyser.getInstance().addNewVariableObservee(taxed, "taxed");

        double adjusted = taxed;
        core.MutationAnalyser.getInstance().setCurrentCode("double adjusted = taxed;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(adjusted, "adjusted");
        if (adjusted < 0) {
            adjusted = 0;
            core.MutationAnalyser.getInstance().setCurrentCode("adjusted = 0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(adjusted, "adjusted");
        }

        double result = adjusted + shipping;
        core.MutationAnalyser.getInstance().setCurrentCode("double result = adjusted + shipping;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(result, "result");

        double tmp = result * 2;
        core.MutationAnalyser.getInstance().setCurrentCode("double tmp = result * 2;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(tmp, "tmp");
        tmp = result;
        core.MutationAnalyser.getInstance().setCurrentCode("tmp = result;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(tmp, "tmp");

        if (result > 1000) {
            result = result - shipping;
            core.MutationAnalyser.getInstance().setCurrentCode("result = result - shipping;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(result, "result");
        }

        core.MutationAnalyser.getInstance().setCurrentCode("return result;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return result;
    }

    public boolean isLargeOrder() {
        double t = total();
        core.MutationAnalyser.getInstance().setCurrentCode("double t = total();");
        core.MutationAnalyser.getInstance().addNewVariableObservee(t, "t");
        double limit = shipping * 20;
        core.MutationAnalyser.getInstance().setCurrentCode("double limit = shipping * 20;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(limit, "limit");

        boolean large = t > limit;
        core.MutationAnalyser.getInstance().setCurrentCode("boolean large = t > limit;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(large, "large");

        if (limit == 0) {
            large = false;
            core.MutationAnalyser.getInstance().setCurrentCode("large = false;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(large, "large");
        }

        core.MutationAnalyser.getInstance().setCurrentCode("return large;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return large;
    }
}
