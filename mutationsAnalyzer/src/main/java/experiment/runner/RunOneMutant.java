package experiment.runner;

public class RunOneMutant {
    public static void main(String[] args) {
        MutationAnalysisRunner.run(
                "experiment.instrumented.WithdrawalLimiterMutate_18_8Instrumented",
                "experiment.instrumented.WithdrawalLimiterObservee"
        );
    }
}
