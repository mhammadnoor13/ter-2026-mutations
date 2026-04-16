package core;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class EliminationLogger {

    private static String LOG_FILE = "mutationsAnalyzer/src/main/java/experiment/logs/eliminations.json";

    private static EliminationLogger instance;

    public static EliminationLogger getInstance() {
        if (instance == null) {
            instance = new EliminationLogger();
        }
        return instance;
    }

    public void logElimination(
            int snapshot,
            String code,
            String variable,
            Set<String> previous,
            Set<String> current
    ) {
        try {

            File logFile = new File(LOG_FILE);

            logFile.getParentFile().mkdirs();

            FileWriter writer = new FileWriter(logFile, true);

            String json =
                    "{\n" +
                            "  \"snapshot\": " + snapshot + ",\n" +
                            "  \"code\": \"" + code + "\",\n" +
                            "  \"eliminatedVariable\": \"" + variable + "\",\n" +
                            "  \"previousSet\": " + toJsonArray(previous) + ",\n" +
                            "  \"currentSet\": " + toJsonArray(current) + "\n" +
                            "},\n";

            writer.write(json);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private String toJsonArray(Set<String> set) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");

        int i = 0;
        for (String s : set) {
            sb.append("\"").append(s).append("\"");
            if (i < set.size() - 1) {
                sb.append(", ");
            }
            i++;
        }

        sb.append("]");
        return sb.toString();
    }
}
