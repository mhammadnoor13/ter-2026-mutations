package experiment.execution;

public class RiskScoreCalculatorExecution implements Execution{

    @Override
    public void run(Object instance) {

        ExecutionSupport.call(
                instance,
                "setFinancialProfile",
                new Class<?>[]{double.class, double.class, int.class},
                2000.0,
                5000.0,
                4
        );

        ExecutionSupport.call(
                instance,
                "computeRiskScore",
                new Class<?>[]{}
        );

        ExecutionSupport.call(
                instance,
                "setFinancialProfile",
                new Class<?>[]{double.class, double.class, int.class},
                4000.0,
                300.0,
                0
        );

        ExecutionSupport.call(
                instance,
                "computeRiskScore",
                new Class<?>[]{}
        );
    }
}
