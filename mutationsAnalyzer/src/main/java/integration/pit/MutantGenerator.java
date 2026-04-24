package integration.pit;

import integration.mapping.MutationMapper;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.regex.Pattern;

public class MutantGenerator {



    private static final String SUBJECTS_DIR =
            "mutationsAnalyzer/src/main/java/experiment/subjects/";
    private static final String OUTPUT_DIR =
            "mutationsAnalyzer/src/main/java/experiment/mutants/";

    public static void generateMutant(PitMutation mutation, int id) {

        try {

            Path inputPath = Paths.get(SUBJECTS_DIR + mutation.sourceFile);
            List<String> lines = Files.readAllLines(inputPath);

            int index = mutation.lineNumber - 1;

            if (index < 0 || index >= lines.size()) return;

            String line = lines.get(index);

            String mutatedLine = MutationMapper.apply(line, mutation);

            if (mutatedLine.equals(line)) {
                // nothing changed → skip
                return;
            }

            lines.set(index, mutatedLine);

            String originalClassName =
                    mutation.sourceFile.replace(".java", "");

            String newClassName = originalClassName + "Mutate_" + mutation.lineNumber + "_" + id;

            Path outputPath = Paths.get(OUTPUT_DIR, newClassName + ".java");
            for (int i = 0; i < lines.size(); i++) {

                String linee = lines.get(i);

                // 1. Fix package
                if (linee.startsWith("package ")) {
                    linee = "package experiment.mutants;";
                }

                // 2. Rename class everywhere
                linee = linee.replaceAll(
                        "\\b" + Pattern.quote(originalClassName) + "\\b",
                        newClassName
                );

                lines.set(i, linee);
            }

            Files.write(outputPath, lines);

            System.out.println("Generated mutant: " + newClassName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
