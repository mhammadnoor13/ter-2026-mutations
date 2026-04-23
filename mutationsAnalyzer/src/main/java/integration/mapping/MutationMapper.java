package integration.mapping;

import integration.pit.PitMutation;

public class MutationMapper {

    public static String apply(String line, PitMutation mutation) {

        // ConditionalsBoundary
        if (mutation.mutator.contains("ConditionalsBoundaryMutator")) {
            line = line.replaceAll("(?<![=])>(?!=)", ">=");
            line = line.replaceAll("(?<![=])<(?!=)", "<=");
            return line;
        }

        // Math
        if (mutation.mutator.contains("MathMutator")) {

            if (line.contains("+")) return line.replace("+", "-");
            if (line.contains("-")) return line.replace("-", "+");
        }

        // Negation
        if (mutation.mutator.contains("NegateConditionalsMutator")) {
            if (line.contains("if (")) {
                return line.replace("if (", "if (!(") + ")";
            }
        }

        return line;
    }
}