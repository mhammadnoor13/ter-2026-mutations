package core;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class EliminationLogger {

    private static final Path LOG_FILE = Paths.get(
            System.getProperty("user.dir"),
            "src",
            "main",
            "java",
            "experiment",
            "logs",
            "eliminations.json"
    );
    private static EliminationLogger instance;

    private final List<MutantLog> mutantLogs = new ArrayList<>();
    private MutantLog currentMutantLog;

    public static EliminationLogger getInstance() {
        if (instance == null) {
            instance = new EliminationLogger();
        }
        return instance;
    }

    public void startReport() {
        mutantLogs.clear();
        currentMutantLog = null;

        File logFile = LOG_FILE.toFile();
        if (logFile.exists()) {
            logFile.delete();
        }
    }


    public void startMutant(String mutantClass, String observeeClass) {
        String originalClass = extractOriginalClass(mutantClass);

        currentMutantLog = new MutantLog(
                mutantClass,
                observeeClass,
                originalClass
        );

        mutantLogs.add(currentMutantLog);
    }
    public void logElimination(
            int snapshot,
            String code,
            String variable,
            Set<String> previous,
            Set<String> current
    ) {

        if (currentMutantLog == null) {
            currentMutantLog = new MutantLog("unknown", "unknown", "unknown");
            mutantLogs.add(currentMutantLog);
        }

        currentMutantLog.eliminations.add(
                new EliminationEvent(
                        snapshot,
                        code,
                        variable,
                        previous,
                        current
                )
        );
    }

    public void writeReport() {
        try {
            File logFile = LOG_FILE.toFile();
            logFile.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(logFile, false);

            writer.write("{\n");
            writer.write("  \"mutants\": [\n");

            for (int i = 0; i < mutantLogs.size(); i++) {
                MutantLog mutantLog = mutantLogs.get(i);

                writer.write("    {\n");
                writer.write("      \"mutantClass\": \"" + escape(mutantLog.mutantClass) + "\",\n");
                writer.write("      \"observeeClass\": \"" + escape(mutantLog.observeeClass) + "\",\n");
                writer.write("      \"originalClass\": \"" + escape(mutantLog.originalClass) + "\",\n");
                writer.write("      \"eliminations\": [\n");

                for (int j = 0; j < mutantLog.eliminations.size(); j++) {
                    EliminationEvent event = mutantLog.eliminations.get(j);

                    writer.write("        {\n");
                    writer.write("          \"snapshot\": " + event.snapshot + ",\n");
                    writer.write("          \"code\": \"" + escape(event.code) + "\",\n");
                    writer.write("          \"eliminatedVariable\": \"" + escape(event.variable) + "\",\n");
                    writer.write("          \"previousSet\": " + toJsonArray(event.previous) + ",\n");
                    writer.write("          \"currentSet\": " + toJsonArray(event.current) + "\n");
                    writer.write("        }");

                    if (j < mutantLog.eliminations.size() - 1) {
                        writer.write(",");
                    }

                    writer.write("\n");
                }

                writer.write("      ]\n");
                writer.write("    }");

                if (i < mutantLogs.size() - 1) {
                    writer.write(",");
                }

                writer.write("\n");
            }

            writer.write("  ]\n");
            writer.write("}\n");

            writer.close();

            System.out.println("Elimination report written to: " + LOG_FILE.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private String toJsonArray(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int i = 0;
        for (String s : set) {
            sb.append("\"").append(escape(s)).append("\"");

            if (i < set.size() - 1) {
                sb.append(", ");
            }

            i++;
        }

        sb.append("]");
        return sb.toString();
    }

    private String escape(String value) {
        if (value == null) return "";

        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");
    }

    private String extractOriginalClass(String mutantClass) {
        String simpleName = mutantClass.substring(mutantClass.lastIndexOf(".") + 1);

        int index = simpleName.indexOf("Mutate_");

        if (index == -1) {
            return simpleName;
        }

        return simpleName.substring(0, index);
    }

    private static class MutantLog {
        String mutantClass;
        String observeeClass;
        String originalClass;
        List<EliminationEvent> eliminations = new ArrayList<>();

        MutantLog(String mutantClass, String observeeClass, String originalClass) {
            this.mutantClass = mutantClass;
            this.observeeClass = observeeClass;
            this.originalClass = originalClass;
        }
    }

    private static class EliminationEvent {
        int snapshot;
        String code;
        String variable;
        Set<String> previous;
        Set<String> current;

        EliminationEvent(
                int snapshot,
                String code,
                String variable,
                Set<String> previous,
                Set<String> current
        ) {
            this.snapshot = snapshot;
            this.code = code;
            this.variable = variable;
            this.previous = previous;
            this.current = current;
        }
    }
}
