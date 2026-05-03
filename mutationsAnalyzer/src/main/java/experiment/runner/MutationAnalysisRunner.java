package experiment.runner;

import core.MutationAnalyser;
import core.PropagationAnalyzer;
import experiment.execution.ClasseSimpleExecution;
import experiment.execution.Execution;
import experiment.execution.WithdrawalLimiterExecution;
import experiment.subjects.WithdrawalLimiter;

import java.io.Console;
import java.util.List;
import java.util.Set;

public class MutationAnalysisRunner {
    public static void run(String instrumentedMutantClassName, String instrumentedObserveeClassName) {

        try {
            // 1. Reset state
            MutationAnalyser.getInstance().reset();

            // 2. Load generated mutant
            Object mutant = MutantLoader.load(instrumentedMutantClassName);

            // 3. Create original/reference version
            Object observee = MutantLoader.load(instrumentedObserveeClassName);

            // 4. Run the same execution scenario on both versions
            Execution execution = new WithdrawalLimiterExecution();

            execution.run(mutant);
            execution.run(observee);

            // 5. Analyze propagation
            PropagationAnalyzer analyzer = new PropagationAnalyzer();

            List<Set<String>> infectedSets = analyzer.computeInfectedSets();

            analyzer.printInfectedSets(infectedSets);
            analyzer.analyzeTransitions(infectedSets);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
