package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class OrderProcessor {

    private double taxRate;
    private double discountRate;
    private double shipping;
    private List<Double> prices;

    public OrderProcessor(double taxRate, double discountRate, double shipping) {
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
        int count = 0;

        for (double p : prices) {
            if (p > 0) {
                subtotal += p;
                count++;
            }
        }

        double average = 0;
        if (count != 0) {
            average = subtotal / count;
        }

        double discounted = subtotal - (subtotal * discountRate);

        double taxed = discounted + (discounted * taxRate);

        double adjusted = taxed;
        if (adjusted < 0) {
            adjusted = 0;
        }

        double result = adjusted + shipping;

        double tmp = result * 2;
        tmp = result;

        if (result > 1000) {
            result = result - shipping;
        }

        return result;
    }

    public boolean isLargeOrder() {
        double t = total();
        double limit = shipping * 20;

        boolean large = t > limit;

        if (limit == 0) {
            large = false;
        }

        return large;
    }
}