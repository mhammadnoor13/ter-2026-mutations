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
                            "  \"eliminatedVariable\": \"" + variable + "\",\n" +
                            "  \"previousSet\": \"" + previous + "\",\n" +
                            "  \"currentSet\": \"" + current + "\"\n" +
                            "},\n";

            writer.write(json);
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }}
