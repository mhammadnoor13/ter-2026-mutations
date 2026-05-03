package experiment.execution;

public class ShippingCostCalculatorExecution implements Execution{

    @Override
    public void run(Object instance) {

        ExecutionSupport.call(
                instance,
                "calculate",
                new Class<?>[]{double.class, double.class, boolean.class},
                12.0,
                900.0,
                true
        );

        ExecutionSupport.call(
                instance,
                "calculate",
                new Class<?>[]{double.class, double.class, boolean.class},
                2.0,
                40.0,
                false
        );
    }
}
