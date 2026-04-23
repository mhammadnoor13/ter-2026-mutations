package integration.pit;

import java.util.List;

public class PitRunner {

    public static void main(String[] args) {

        int counter = 0;
        List<PitMutation> mutations =
                PitParser.parse("mutationsAnalyzer/target/pit-reports/mutations.xml");
        for (PitMutation m : mutations) {
            MutantGenerator.generateMutant(m, counter++);
        }
    }
}