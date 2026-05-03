package integration.pipeline;

import core.EliminationLogger;
import core.MutationAnalyser;
import experiment.runner.MutationAnalysisRunner;
import integration.pit.MutantGenerator;
import integration.pit.PitMutation;
import integration.pit.PitParser;
import integration.report.HtmlReportGenerator;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class FullPipelineRunner {

    private static final String INSTRUMENTED_DIR =
            "src/main/java/experiment/instrumented";

    private static final String INSTRUMENTED_PACKAGE =
            "experiment.instrumented";

    public static void main(String[] args) {

        if (args.length == 0) {
            printUsage();
            return;
        }

        String mode = args[0];

        switch (mode) {
            case "generate":
                generate();
                break;

            case "analyze":
                analyze();
                break;

            default:
                System.out.println("Unknown mode: " + mode);
                printUsage();
        }
    }

    private static void generate() {

        System.out.println("=== GENERATION PHASE ===");

        Path mutationsXml = Paths.get(
                System.getProperty("user.dir"),
                "target",
                "pit-reports",
                "mutations.xml"
        );

        System.out.println("Reading PIT report: " + mutationsXml);

        List<PitMutation> mutations = PitParser.parse(mutationsXml.toString());

        int counter = 0;
        int generated = 0;

        for (PitMutation mutation : mutations) {

            // Keep only survived mutants for propagation analysis
            if (!"SURVIVED".equals(mutation.status)) {
                continue;
            }

            String generatedClass =
                    MutantGenerator.generateMutant(mutation, counter);

            if (generatedClass != null) {
                generated++;
            }

            counter++;
        }

        System.out.println("Generated source mutants: " + generated);

        System.out.println("Instrumenting observee classes...");
        MutationAnalyser.instrumentAllObservees();

        System.out.println("Instrumenting mutant classes...");
        MutationAnalyser.instrumentAllMutants();

        System.out.println();
        System.out.println("Generation phase finished.");
        System.out.println("Now run:");
        System.out.println("mvn compile");
        System.out.println("Then run:");
        System.out.println("FullPipelineRunner analyze");
    }

    private static void analyze() {

        EliminationLogger.getInstance().startReport();

        System.out.println("=== ANALYSIS PHASE ===");

        Path instrumentedDir = Paths.get(
                System.getProperty("user.dir"),
                INSTRUMENTED_DIR
        );

        System.out.println("Scanning instrumented directory: " + instrumentedDir);

        File dir = instrumentedDir.toFile();

        if (!dir.exists()) {
            System.out.println("Instrumented directory not found.");
            return;
        }

        File[] mutantFiles = dir.listFiles((d, name) ->
                name.endsWith("Instrumented.java")
                        && name.contains("Mutate_")
        );

        if (mutantFiles == null || mutantFiles.length == 0) {
            System.out.println("No instrumented mutant files found.");
            return;
        }

        int analyzed = 0;

        for (File mutantFile : mutantFiles) {

            String mutantSimpleName =
                    mutantFile.getName().replace(".java", "");

            String baseClassName =
                    extractBaseClassName(mutantSimpleName);

            String observeeClassName =
                    INSTRUMENTED_PACKAGE + "." + baseClassName + "Observee";

            String mutantClassName =
                    INSTRUMENTED_PACKAGE + "." + mutantSimpleName;

            System.out.println();
            System.out.println("Analyzing mutant:");
            System.out.println("  Mutant  : " + mutantClassName);
            System.out.println("  Observee: " + observeeClassName);

            EliminationLogger.getInstance().startMutant(
                    mutantClassName,
                    observeeClassName
            );

            MutationAnalysisRunner.run(
                    mutantClassName,
                    observeeClassName
            );

            analyzed++;
        }
        EliminationLogger.getInstance().writeReport();
        HtmlReportGenerator.generate();
        HtmlReportGenerator.generateStatisticsReport();

        System.out.println();
        System.out.println("Analysis finished. Mutants analyzed: " + analyzed);
    }

    private static String extractBaseClassName(String mutantSimpleName) {

        int index = mutantSimpleName.indexOf("Mutate_");

        if (index == -1) {
            throw new IllegalArgumentException(
                    "Cannot extract base class name from: " + mutantSimpleName
            );
        }

        return mutantSimpleName.substring(0, index);
    }

    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("  FullPipelineRunner generate");
        System.out.println("  FullPipelineRunner analyze");
    }
}