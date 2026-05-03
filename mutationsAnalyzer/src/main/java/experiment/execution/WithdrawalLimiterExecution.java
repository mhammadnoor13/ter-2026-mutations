package experiment.execution;

public class WithdrawalLimiterExecution implements Execution{

    @Override
    public void run(Object instance) {

        ExecutionSupport.call(
                instance,
                "deposit",
                new Class<?>[]{double.class},
                1000.0
        );

        ExecutionSupport.call(
                instance,
                "withdraw",
                new Class<?>[]{double.class, boolean.class},
                900.0,
                false
        );

        ExecutionSupport.call(
                instance,
                "withdraw",
                new Class<?>[]{double.class, boolean.class},
                80.0,
                true
        );

        ExecutionSupport.call(
                instance,
                "getBalance",
                new Class<?>[]{}
        );
    }
}
