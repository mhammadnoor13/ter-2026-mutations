package experiment.instrumented;

public class ShippingCostCalculatorObservee {
    private double basePrice;
    private double maxShippingCost;
    private double minimumCost;

    public ShippingCostCalculatorObservee() {
        this.basePrice = 4.0;
        this.maxShippingCost = 50.0;
        this.minimumCost = 0.0;
    }

    public double calculate(double weight, double distance, boolean express) {

        double weightCost = weight * 1.8;
        core.MutationAnalyser.getInstance().setCurrentCode("double weightCost = weight * 1.8;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(weightCost, "weightCost");
        double distanceCost = distance * 0.05;
        core.MutationAnalyser.getInstance().setCurrentCode("double distanceCost = distance * 0.05;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(distanceCost, "distanceCost");

        double cost = basePrice + weightCost + distanceCost;
        core.MutationAnalyser.getInstance().setCurrentCode("double cost = basePrice + weightCost + distanceCost;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(cost, "cost");

        if (express) {
            cost = cost * 1.5;
            core.MutationAnalyser.getInstance().setCurrentCode("cost = cost * 1.5;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(cost, "cost");
        }

        double fuelAdjustment = distanceCost - weightCost;
        core.MutationAnalyser.getInstance().setCurrentCode("double fuelAdjustment = distanceCost - weightCost;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(fuelAdjustment, "fuelAdjustment");

        if (fuelAdjustment < 0) {
            fuelAdjustment = 0;
            core.MutationAnalyser.getInstance().setCurrentCode("fuelAdjustment = 0;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(fuelAdjustment, "fuelAdjustment");
        }

        cost = cost + fuelAdjustment;
        core.MutationAnalyser.getInstance().setCurrentCode("cost = cost + fuelAdjustment;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(cost, "cost");

        if (cost > maxShippingCost) {
            cost = maxShippingCost;
            core.MutationAnalyser.getInstance().setCurrentCode("cost = maxShippingCost;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(cost, "cost");
        }

        if (cost < minimumCost) {
            cost = minimumCost;
            core.MutationAnalyser.getInstance().setCurrentCode("cost = minimumCost;");
            core.MutationAnalyser.getInstance().addNewVariableObservee(cost, "cost");
        }

        double temporary = cost * 3;
        core.MutationAnalyser.getInstance().setCurrentCode("double temporary = cost * 3;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(temporary, "temporary");
        temporary = cost;
        core.MutationAnalyser.getInstance().setCurrentCode("temporary = cost;");
        core.MutationAnalyser.getInstance().addNewVariableObservee(temporary, "temporary");

        core.MutationAnalyser.getInstance().setCurrentCode("return cost;");
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return cost;
    }
}
