package integration.report;


import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


public class HtmlReportGenerator {

    private static final Path JSON_FILE = Paths.get(
            System.getProperty("user.dir"),
            "src",
            "main",
            "java",
            "experiment",
            "logs",
            "eliminations.json"
    );

    private static final Path REPORT_DIR = Paths.get(
            System.getProperty("user.dir"),
            "target",
            "propagation-report"
    );

    private static final Path HTML_FILE = REPORT_DIR.resolve("index.html");

    public static void generate() {
        try {
            if (!Files.exists(JSON_FILE)) {
                System.out.println("No elimination JSON file found: " + JSON_FILE);
                return;
            }

            String json = Files.readString(JSON_FILE);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            Files.createDirectories(REPORT_DIR);

            try (FileWriter writer = new FileWriter(HTML_FILE.toFile())) {
                writer.write(buildHtml(root));
            }

            System.out.println("HTML propagation report generated:");
            System.out.println(HTML_FILE.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildHtml(JsonObject root) {
        JsonArray mutants = root.getAsJsonArray("mutants");

        int mutantCount = mutants.size();
        int eliminationCount = 0;

        Map<String, Integer> variableFrequency = new HashMap<>();
        Map<String, Integer> codeFrequency = new HashMap<>();

        StringBuilder details = new StringBuilder();

        for (JsonElement mutantElement : mutants) {
            JsonObject mutant = mutantElement.getAsJsonObject();

            String mutantClass = getString(mutant, "mutantClass");
            String observeeClass = getString(mutant, "observeeClass");
            String originalClass = getString(mutant, "originalClass");

            JsonArray eliminations = mutant.getAsJsonArray("eliminations");
            eliminationCount += eliminations.size();

            details.append("<section class=\"mutant-card\">");
            details.append("<h2>").append(escapeHtml(originalClass)).append("</h2>");
            details.append("<p><strong>Mutant:</strong> <code>")
                    .append(escapeHtml(mutantClass))
                    .append("</code></p>");
            details.append("<p><strong>Observee:</strong> <code>")
                    .append(escapeHtml(observeeClass))
                    .append("</code></p>");
            details.append("<p><strong>Eliminations:</strong> ")
                    .append(eliminations.size())
                    .append("</p>");

            if (eliminations.size() == 0) {
                details.append("<p class=\"empty\">No elimination detected for this mutant.</p>");
            } else {
                details.append("<table>");
                details.append("<thead>");
                details.append("<tr>");
                details.append("<th>Snapshot</th>");
                details.append("<th>Code line</th>");
                details.append("<th>Eliminated variable</th>");
                details.append("<th>Previous infected set</th>");
                details.append("<th>Current infected set</th>");
                details.append("</tr>");
                details.append("</thead>");
                details.append("<tbody>");

                for (JsonElement eventElement : eliminations) {
                    JsonObject event = eventElement.getAsJsonObject();

                    int snapshot = event.get("snapshot").getAsInt();
                    String code = getString(event, "code");
                    String variable = getString(event, "eliminatedVariable");
                    String previousSet = jsonArrayToText(event.getAsJsonArray("previousSet"));
                    String currentSet = jsonArrayToText(event.getAsJsonArray("currentSet"));

                    variableFrequency.put(variable, variableFrequency.getOrDefault(variable, 0) + 1);
                    codeFrequency.put(code, codeFrequency.getOrDefault(code, 0) + 1);

                    details.append("<tr>");
                    details.append("<td>").append(snapshot).append("</td>");
                    details.append("<td><code>").append(escapeHtml(code)).append("</code></td>");
                    details.append("<td><strong>").append(escapeHtml(variable)).append("</strong></td>");
                    details.append("<td>").append(escapeHtml(previousSet)).append("</td>");
                    details.append("<td>").append(escapeHtml(currentSet)).append("</td>");
                    details.append("</tr>");
                }

                details.append("</tbody>");
                details.append("</table>");
            }

            details.append("</section>");
        }

        String topVariables = buildFrequencyList(variableFrequency);
        String topCodeLines = buildFrequencyList(codeFrequency);

        return """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Propagation Analysis Report</title>
                    <style>
                        body {
                            font-family: Arial, sans-serif;
                            margin: 40px;
                            background: #f4f6f8;
                            color: #1f2937;
                        }

                        h1 {
                            margin-bottom: 10px;
                        }

                        .subtitle {
                            color: #6b7280;
                            margin-bottom: 30px;
                        }

                        .summary-grid {
                            display: grid;
                            grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                            gap: 16px;
                            margin-bottom: 30px;
                        }

                        .summary-card {
                            background: white;
                            padding: 18px;
                            border-radius: 10px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                        }

                        .summary-card h3 {
                            margin-top: 0;
                            font-size: 16px;
                            color: #374151;
                        }

                        .summary-number {
                            font-size: 32px;
                            font-weight: bold;
                        }

                        .mutant-card {
                            background: white;
                            padding: 22px;
                            border-radius: 10px;
                            margin-bottom: 24px;
                            box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                        }

                        table {
                            width: 100%%;
                            border-collapse: collapse;
                            margin-top: 16px;
                        }

                        th, td {
                            border: 1px solid #e5e7eb;
                            padding: 10px;
                            text-align: left;
                            vertical-align: top;
                        }

                        th {
                            background: #f3f4f6;
                        }

                        code {
                            background: #f3f4f6;
                            padding: 2px 5px;
                            border-radius: 4px;
                        }

                        .empty {
                            color: #6b7280;
                            font-style: italic;
                        }

                        ul {
                            padding-left: 20px;
                        }

                        li {
                            margin-bottom: 6px;
                        }
                        .nav {
                            margin-bottom: 24px;
                        }
                        .nav a {
                            color: #2563eb;
                            text-decoration: none;
                            font-weight: bold;
                        }
                    </style>
                </head>
                <body>
                    <h1>Propagation Analysis Report</h1>
                    <p class="subtitle">
                        This report summarizes elimination events detected during propagation analysis.
                    </p>
                    <div class="nav">
                        <a href="statistics.html">View statistics report -> </a>
                    </div>

                    <div class="summary-grid">
                        <div class="summary-card">
                            <h3>Analyzed mutants</h3>
                            <div class="summary-number">%d</div>
                        </div>

                        <div class="summary-card">
                            <h3>Total eliminations</h3>
                            <div class="summary-number">%d</div>
                        </div>

                        <div class="summary-card">
                            <h3>Frequently eliminated variables</h3>
                            %s
                        </div>

                        <div class="summary-card">
                            <h3>Frequent eliminating code lines</h3>
                            %s
                        </div>
                    </div>

                    %s
                </body>
                </html>
                """.formatted(
                mutantCount,
                eliminationCount,
                topVariables,
                topCodeLines,
                details.toString()
        );
    }

    private static String buildFrequencyList(Map<String, Integer> frequencies) {
        if (frequencies.isEmpty()) {
            return "<p class=\"empty\">No data available.</p>";
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencies.entrySet());

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");

        int limit = Math.min(5, entries.size());

        for (int i = 0; i < limit; i++) {
            Map.Entry<String, Integer> entry = entries.get(i);

            sb.append("<li><code>")
                    .append(escapeHtml(entry.getKey()))
                    .append("</code> — ")
                    .append(entry.getValue())
                    .append("</li>");
        }

        sb.append("</ul>");
        return sb.toString();
    }

    private static String jsonArrayToText(JsonArray array) {
        if (array == null || array.size() == 0) {
            return "[]";
        }

        List<String> values = new ArrayList<>();

        for (JsonElement element : array) {
            values.add(element.getAsString());
        }

        return values.toString();
    }

    private static String getString(JsonObject object, String key) {
        if (!object.has(key) || object.get(key).isJsonNull()) {
            return "";
        }

        return object.get(key).getAsString();
    }

    private static String escapeHtml(String value) {
        if (value == null) {
            return "";
        }

        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public static void generateStatisticsReport() {
        try {
            if (!Files.exists(JSON_FILE)) {
                System.out.println("No elimination JSON file found: " + JSON_FILE);
                return;
            }

            String json = Files.readString(JSON_FILE);
            JsonObject root = JsonParser.parseString(json).getAsJsonObject();

            Files.createDirectories(REPORT_DIR);

            Path statisticsFile = REPORT_DIR.resolve("statistics.html");

            try (FileWriter writer = new FileWriter(statisticsFile.toFile())) {
                writer.write(buildStatisticsHtml(root));
            }

            System.out.println("HTML statistics report generated:");
            System.out.println(statisticsFile.toAbsolutePath());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String buildStatisticsHtml(JsonObject root) {
        JsonArray mutants = root.getAsJsonArray("mutants");

        Map<String, Integer> byCodeLine = new HashMap<>();
        Map<String, Integer> byVariable = new HashMap<>();
        Map<String, Integer> byMutant = new HashMap<>();
        Map<String, Integer> byOriginalClass = new HashMap<>();

        int totalEliminations = 0;

        for (JsonElement mutantElement : mutants) {
            JsonObject mutant = mutantElement.getAsJsonObject();

            String mutantClass = getString(mutant, "mutantClass");
            String originalClass = getString(mutant, "originalClass");

            JsonArray eliminations = mutant.getAsJsonArray("eliminations");

            int mutantEliminationCount = eliminations.size();

            totalEliminations += mutantEliminationCount;

            byMutant.put(mutantClass, mutantEliminationCount);
            byOriginalClass.put(
                    originalClass,
                    byOriginalClass.getOrDefault(originalClass, 0) + mutantEliminationCount
            );

            for (JsonElement eventElement : eliminations) {
                JsonObject event = eventElement.getAsJsonObject();

                String code = getString(event, "code");
                String variable = getString(event, "eliminatedVariable");

                byCodeLine.put(code, byCodeLine.getOrDefault(code, 0) + 1);
                byVariable.put(variable, byVariable.getOrDefault(variable, 0) + 1);
            }
        }

        String codeLineTable = buildFrequencyTable(
                "Code line",
                byCodeLine
        );

        String variableTable = buildFrequencyTable(
                "Variable",
                byVariable
        );

        String mutantTable = buildFrequencyTable(
                "Mutant",
                byMutant
        );

        String classTable = buildFrequencyTable(
                "Original class",
                byOriginalClass
        );

        return """
            <!DOCTYPE html>
            <html lang="en" dir="ltr">
            <head>
                <meta charset="UTF-8">
                <title>Propagation Statistics Report</title>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 40px;
                        background: #f4f6f8;
                        color: #1f2937;
                    }

                    h1 {
                        margin-bottom: 8px;
                    }

                    .subtitle {
                        color: #6b7280;
                        margin-bottom: 30px;
                    }

                    .nav {
                        margin-bottom: 24px;
                    }

                    .nav a {
                        color: #2563eb;
                        text-decoration: none;
                        font-weight: bold;
                    }

                    .summary-grid {
                        display: grid;
                        grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));
                        gap: 16px;
                        margin-bottom: 30px;
                    }

                    .summary-card {
                        background: white;
                        padding: 18px;
                        border-radius: 10px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    }

                    .summary-card h3 {
                        margin-top: 0;
                        font-size: 16px;
                        color: #374151;
                    }

                    .summary-number {
                        font-size: 32px;
                        font-weight: bold;
                    }

                    .section-card {
                        background: white;
                        padding: 22px;
                        border-radius: 10px;
                        margin-bottom: 24px;
                        box-shadow: 0 2px 8px rgba(0,0,0,0.08);
                    }

                    table {
                        width: 100%%;
                        border-collapse: collapse;
                        margin-top: 16px;
                    }

                    th, td {
                        border: 1px solid #e5e7eb;
                        padding: 10px;
                        text-align: left;
                        vertical-align: top;
                    }

                    th {
                        background: #f3f4f6;
                    }

                    code {
                        background: #f3f4f6;
                        padding: 2px 5px;
                        border-radius: 4px;
                    }

                    .count {
                        font-weight: bold;
                    }
                </style>
            </head>
            <body>
                <h1>Propagation Statistics Report</h1>
                <p class="subtitle">
                    Aggregated statistics extracted from elimination events.
                </p>

                <div class="nav">
                    <a href="index.html">← Back to detailed propagation report</a>
                </div>

                <div class="summary-grid">
                    <div class="summary-card">
                        <h3>Analyzed mutants</h3>
                        <div class="summary-number">%d</div>
                    </div>

                    <div class="summary-card">
                        <h3>Total eliminations</h3>
                        <div class="summary-number">%d</div>
                    </div>

                    <div class="summary-card">
                        <h3>Original classes</h3>
                        <div class="summary-number">%d</div>
                    </div>
                </div>

                <div class="section-card">
                    <h2>Eliminations by code line</h2>
                    <p>
                        Code lines that most frequently eliminate infected variables.
                    </p>
                    %s
                </div>

                <div class="section-card">
                    <h2>Eliminations by variable</h2>
                    <p>
                        Variables most frequently removed from infected sets.
                    </p>
                    %s
                </div>

                <div class="section-card">
                    <h2>Eliminations by mutant</h2>
                    <p>
                        Mutants that generated the highest number of elimination events.
                    </p>
                    %s
                </div>

                <div class="section-card">
                    <h2>Eliminations by original class</h2>
                    <p>
                        Original classes where propagation failures occurred most often.
                    </p>
                    %s
                </div>
            </body>
            </html>
            """.formatted(
            mutants.size(),
            totalEliminations,
            byOriginalClass.size(),
            codeLineTable,
            variableTable,
            mutantTable,
            classTable
    );}

    private static String buildFrequencyTable(String label, Map<String, Integer> frequencies) {
        if (frequencies.isEmpty()) {
            return "<p>No data available.</p>";
        }

        List<Map.Entry<String, Integer>> entries = new ArrayList<>(frequencies.entrySet());

        entries.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        StringBuilder sb = new StringBuilder();

        sb.append("<table>");
        sb.append("<thead>");
        sb.append("<tr>");
        sb.append("<th>").append(escapeHtml(label)).append("</th>");
        sb.append("<th>Eliminations</th>");
        sb.append("</tr>");
        sb.append("</thead>");
        sb.append("<tbody>");

        for (Map.Entry<String, Integer> entry : entries) {
            sb.append("<tr>");
            sb.append("<td><code>")
                    .append(escapeHtml(entry.getKey()))
                    .append("</code></td>");
            sb.append("<td class=\"count\">")
                    .append(entry.getValue())
                    .append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody>");
        sb.append("</table>");

        return sb.toString();
    }



}