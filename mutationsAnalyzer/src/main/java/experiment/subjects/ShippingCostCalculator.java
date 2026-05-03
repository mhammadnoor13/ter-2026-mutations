package experiment.subjects;

public class ShippingCostCalculator {
    private double basePrice;
    private double maxShippingCost;
    private double minimumCost;

    public ShippingCostCalculator() {
        this.basePrice = 4.0;
        this.maxShippingCost = 50.0;
        this.minimumCost = 0.0;
    }

    public double calculate(double weight, double distance, boolean express) {

        double weightCost = weight * 1.8;
        double distanceCost = distance * 0.05;

        double cost = basePrice + weightCost + distanceCost;

        if (express) {
            cost = cost * 1.5;
        }

        double fuelAdjustment = distanceCost - weightCost;

        if (fuelAdjustment < 0) {
            fuelAdjustment = 0;
        }

        cost = cost + fuelAdjustment;

        if (cost > maxShippingCost) {
            cost = maxShippingCost;
        }

        if (cost < minimumCost) {
            cost = minimumCost;
        }

        double temporary = cost * 3;
        temporary = cost;

        return cost;
    }
}
