package core;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class MutationAnalyser {

    private final List<Object> allVariables;
    private final List<String> allVariableNames;
    private final List<List<String>> allSnapshots;

    private final List<Object> allVariablesObservee;
    private final List<String> allVariableNamesObservee;
    private final List<List<String>> allSnapshotsObservee;

    private String currentCode = "";
    private final List<String> snapshotCode = new ArrayList<>();
    private final List<String> snapshotCodeObservee = new ArrayList<>();


    private static MutationAnalyser instance;

    private int mutationCounter = 0;

    private static final String SUBJECTS_DIR =
            "src/main/java/experiment/subjects";

    private static final String INSTRUMENTED_DIR =
            "src/main/java/experiment/instrumented";

    private static final String MUTANTS_DIR =
            "src/main/java/experiment/mutants";

    private MutationAnalyser() {
        this.allVariables = new ArrayList<>();
        this.allVariableNames = new ArrayList<>();
        this.allSnapshots = new ArrayList<>();

        this.allVariablesObservee = new ArrayList<>();
        this.allVariableNamesObservee = new ArrayList<>();
        this.allSnapshotsObservee = new ArrayList<>();
    }

    public static MutationAnalyser getInstance() {
        if (instance == null) {
            instance = new MutationAnalyser();
        }
        return instance;
    }

    public void setCurrentCode(String code) {
        this.currentCode = code.trim();
    }

    public void reset() {
        allVariables.clear();
        allVariableNames.clear();
        allSnapshots.clear();

        allVariablesObservee.clear();
        allVariableNamesObservee.clear();
        allSnapshotsObservee.clear();

        mutationCounter = 0;

        int initialValue = 1;
        allVariables.add(initialValue);
        allVariableNames.add("N°");
        allVariablesObservee.add(initialValue);
        allVariableNamesObservee.add("N°");

    }

    public void addNewVariable(Object obj, String varName) {

        int index = allVariableNames.indexOf(varName);

        if (index != -1) {
            allVariables.set(index, obj);
        } else {
            allVariables.add(obj);
            allVariableNames.add(varName);
        }

        takeSnapshot();
    }

    public void addNewVariableObservee(Object obj, String varName) {

        int index = allVariableNamesObservee.indexOf(varName);

        if (index != -1) {
            allVariablesObservee.set(index, obj);
        } else {
            allVariablesObservee.add(obj);
            allVariableNamesObservee.add(varName);
        }

        takeSnapshotObservee();
    }

    public void addMutation(String mutationName) {

        if (allVariables.contains(mutationName)) return;

        mutationCounter++;
        String varName = "Mutation" + mutationCounter;

        allVariables.add(mutationName);
        allVariableNames.add(varName);
    }

    public void printAllVariableValues() {
        for (int i = 0; i < allVariables.size(); i++) {
            String name = allVariableNames.get(i);
            Object value = allVariables.get(i);
            System.out.println(name + " = " + (value != null ? value.toString() : "null"));
        }
    }

    public void printAllSnapshotsObservee() {

        for (int i = 0; i < allSnapshotsObservee.size(); i++) {
            System.out.println("Snapshot Observé " + (i + 1) + " :");
            List<String> snapshot = allSnapshotsObservee.get(i);

            for (String line : snapshot) {
                System.out.println(line);
            }

            System.out.println("--------------------");
        }
    }

    public void takeSnapshot() {
        List<String> snapshot = new ArrayList<>();

        for (int i = 0; i < allVariables.size(); i++) {
            String name = allVariableNames.get(i);
            Object value = allVariables.get(i);
            snapshot.add(name + " = " + (value != null ? value.toString() : "null"));
        }

        allSnapshots.add(snapshot);
        snapshotCode.add(currentCode);
        allVariables.set(0,(int)(allVariables.get(0)) + 1);
    }

    public void takeSnapshotObservee() {
        List<String> snapshot = new ArrayList<>();

        for (int i = 0; i < allVariablesObservee.size(); i++) {
            String name = allVariableNamesObservee.get(i);
            Object value = allVariablesObservee.get(i);
            snapshot.add(name + " = " + (value != null ? value.toString() : "null"));
        }

        allSnapshotsObservee.add(snapshot);
        snapshotCodeObservee.add(currentCode);
        allVariablesObservee.set(0, (int)(allVariablesObservee.get(0)) + 1);
    }

    public List<String> getSnapshotCode() {
        return snapshotCode;
    }

    // --- Nouvelle méthode ---
    public void printAllSnapshots() {

        for (int i = 0; i < allSnapshots.size(); i++) {
            System.out.println("Snapshot " + (i + 1) + " :");
            List<String> snapshot = allSnapshots.get(i);

            for (String line : snapshot) {
                System.out.println(line);
            }

            System.out.println("--------------------");
        }
    }

    // Snapshots getters (used in Propagation Analyzer)
    public List<List<String>> getSnapshots() {
        return allSnapshots;
    }

    public List<List<String>> getSnapshotsObservee() {
        return allSnapshotsObservee;
    }

    public void showSnapshotsInPopup() {

        if (allSnapshots.isEmpty() || allSnapshotsObservee.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Snapshots insuffisants pour comparaison.");
            return;
        }

        // 🔥 Union des variables
        List<String> allVars = new ArrayList<>();
        for (String v : allVariableNames) if (!allVars.contains(v)) allVars.add(v);
        for (String v : allVariableNamesObservee) if (!allVars.contains(v)) allVars.add(v);

        // Colonnes : var(M) + var(O)
        List<String> columns = new ArrayList<>();
        for (String v : allVars) {
            columns.add(v + " (M)");
            columns.add(v + " (O)");
        }

        String[] columnNames = columns.toArray(new String[0]);

        int rowCount = Math.max(allSnapshots.size(), allSnapshotsObservee.size());
        String[][] data = new String[rowCount][columnNames.length];

        for (int i = 0; i < rowCount; i++) {

            List<String> snapM = i < allSnapshots.size() ? allSnapshots.get(i) : new ArrayList<>();
            List<String> snapO = i < allSnapshotsObservee.size() ? allSnapshotsObservee.get(i) : new ArrayList<>();

            int colIndex = 0;

            for (String var : allVars) {

                String valM = extractValue(snapM, var);
                String valO = extractValue(snapO, var);

                data[i][colIndex++] = valM;
                data[i][colIndex++] = valO;
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);

        // 🎨 Renderer SAFE
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int col) {

                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);

                // On ne compare que les paires (M,O)
                if (col % 2 == 0) {
                    // colonne M → comparer avec O (col+1)
                    if (col + 1 < table.getColumnCount()) {

                        String valM = (String) table.getValueAt(row, col);
                        String valO = (String) table.getValueAt(row, col + 1);

                        if (valM != null && valO != null && !valM.equals(valO)) {
                            c.setBackground(Color.PINK);
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }

                } else {
                    // colonne O → comparer avec M (col-1)
                    if (col - 1 >= 0) {

                        String valO = (String) table.getValueAt(row, col);
                        String valM = (String) table.getValueAt(row, col - 1);

                        if (valM != null && valO != null && !valM.equals(valO)) {
                            c.setBackground(Color.PINK);
                        } else {
                            c.setBackground(Color.WHITE);
                        }
                    }
                }

                return c;
            }
        });

        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(1000, 500));

        JDialog dialog = new JDialog();
        dialog.setTitle("Comparaison Mutable vs Observée");
        dialog.setModal(true);
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true);
    }

    private String extractValue(List<String> snapshot, String variableName) {

        for (String line : snapshot) {
            if (line.startsWith(variableName + " = ")) {
                return line.substring((variableName + " = ").length());
            }
        }
        return "";
    }

    public static void instrumentAllSubjects(){
        File subjects = new File(SUBJECTS_DIR);

        if (!subjects.exists()) {
            System.out.println("Subjects directory not found.");
            return;
        }

        File [] files = subjects.listFiles((dir,name) -> name.endsWith(".java"));

        if (files == null || files.length == 0) {
            System.out.println("No subject files found.");
            return;
        }

        for (File file : files) {
            instrumentFile(file);
        }
    }
    public static void instrumentAllObservees() {
        File subjects = new File(SUBJECTS_DIR);

        if (!subjects.exists()) {
            System.out.println("Subjects directory not found.");
            return;
        }

        File[] files = subjects.listFiles((dir, name) -> name.endsWith(".java"));

        if (files == null || files.length == 0) {
            System.out.println("No subject files found.");
            return;
        }

        for (File file : files) {
            instrumentObserveeFile(file);
        }
    }

    public static void instrumentAllMutants() {
        File mutants = new File(MUTANTS_DIR);

        if (!mutants.exists()) {
            System.out.println("Mutants directory not found.");
            return;
        }

        File[] files = mutants.listFiles((dir, name) -> name.endsWith(".java"));

        if (files == null || files.length == 0) {
            System.out.println("No mutant files found.");
            return;
        }

        for (File file : files) {
            instrumentMutantFile(file);
        }
    }
    public static void instrumentObserveeFile(File originalFile) {
        String originalClassName = originalFile.getName().replace(".java", "");
        String observedClassName = originalClassName + "Observee";

        File observedFile = new File(INSTRUMENTED_DIR, observedClassName + ".java");

        Pattern declarationPattern =
                Pattern.compile("^\\s*(?:final\\s+)?[\\w<>\\[\\]]+\\s+(\\w+)\\s*=.+;");

        Pattern assignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*=\\s*.+;");

        Pattern compoundAssignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*(\\+=|-=|\\*=|/=|%=)\\s*.+;");

        Pattern incrementPattern =
                Pattern.compile("^\\s*(?:\\+\\+|--)?\\s*(\\w+)\\s*(?:\\+\\+|--)\\s*;");

        Pattern returnPattern = Pattern.compile("\\breturn\\b");

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter observedWriter = new BufferedWriter(new FileWriter(observedFile))) {

            String line;

            while ((line = reader.readLine()) != null) {

                if (line.startsWith("package ")) {
                    line = "package experiment.instrumented;";
                }

                String observedLine =
                        line.replaceAll("\\b" + Pattern.quote(originalClassName) + "\\b", observedClassName);

                Matcher returnMatcher = returnPattern.matcher(line);

                if (returnMatcher.find()) {
                    String indent = line.substring(0, line.indexOf("return"));
                    String escapedLine = line.trim().replace("\"", "\\\"");

                    observedWriter.write(indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                            + escapedLine + "\");");
                    observedWriter.newLine();

                    observedWriter.write(indent + "core.MutationAnalyser.getInstance().takeSnapshotObservee();");
                    observedWriter.newLine();
                }

                observedWriter.write(observedLine);
                observedWriter.newLine();

                String variableName = null;

                Matcher declarationMatcher = declarationPattern.matcher(line);
                Matcher assignmentMatcher = assignmentPattern.matcher(line);
                Matcher compoundMatcher = compoundAssignmentPattern.matcher(line);
                Matcher incrementMatcher = incrementPattern.matcher(line);

                if (declarationMatcher.find()) {
                    variableName = declarationMatcher.group(1);
                } else if (compoundMatcher.find()) {
                    variableName = compoundMatcher.group(1);
                } else if (incrementMatcher.find()) {
                    variableName = incrementMatcher.group(1);
                } else if (assignmentMatcher.find()) {
                    variableName = assignmentMatcher.group(1);
                }

                if (variableName != null) {
                    String indent = line.substring(0, line.indexOf(line.trim()));
                    String escapedLine = line.trim().replace("\"", "\\\"");

                    observedWriter.write(indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                            + escapedLine + "\");");
                    observedWriter.newLine();

                    observedWriter.write(indent + "core.MutationAnalyser.getInstance().addNewVariableObservee("
                            + variableName + ", \"" + variableName + "\");");
                    observedWriter.newLine();
                }
            }

            System.out.println("Observee instrumenté créé : " + observedFile.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void instrumentMutantFile(File mutantFile) {

        String originalClassName = mutantFile.getName().replace(".java", "");
        String instrumentedClassName = originalClassName + "Instrumented";

        File instrumentedFile = new File(INSTRUMENTED_DIR, instrumentedClassName + ".java");

        Pattern declarationPattern =
                Pattern.compile("^\\s*(?:final\\s+)?[\\w<>\\[\\]]+\\s+(\\w+)\\s*=.+;");

        Pattern assignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*=\\s*.+;");

        Pattern compoundAssignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*(\\+=|-=|\\*=|/=|%=)\\s*.+;");

        Pattern incrementPattern =
                Pattern.compile("^\\s*(?:\\+\\+|--)?\\s*(\\w+)\\s*(?:\\+\\+|--)\\s*;");

        Pattern returnPattern = Pattern.compile("\\breturn\\b");

        try (BufferedReader reader = new BufferedReader(new FileReader(mutantFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(instrumentedFile))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // package fix
                if (line.startsWith("package ")) {
                    line = "package experiment.instrumented;";
                }

                // rename class and constructor
                String instrumentedLine =
                        line.replaceAll("\\b" + Pattern.quote(originalClassName) + "\\b",
                                instrumentedClassName);

                // --- DETECTION RETURN ---
                Matcher returnMatcher = returnPattern.matcher(line);

                if (returnMatcher.find()) {

                    String indent = line.substring(0, line.indexOf("return"));
                    String escapedLine = line.trim().replace("\"", "\\\"");

                    writer.write(indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                            + escapedLine + "\");");
                    writer.newLine();

                    writer.write(indent + "core.MutationAnalyser.getInstance().takeSnapshot();");
                    writer.newLine();
                }

                // write original modified line
                writer.write(instrumentedLine);
                writer.newLine();

                // --- DETECTION VARIABLES ---
                String variableName = null;

                Matcher declarationMatcher = declarationPattern.matcher(line);
                Matcher assignmentMatcher = assignmentPattern.matcher(line);
                Matcher compoundMatcher = compoundAssignmentPattern.matcher(line);
                Matcher incrementMatcher = incrementPattern.matcher(line);

                if (declarationMatcher.find()) {
                    variableName = declarationMatcher.group(1);
                } else if (compoundMatcher.find()) {
                    variableName = compoundMatcher.group(1);
                } else if (incrementMatcher.find()) {
                    variableName = incrementMatcher.group(1);
                } else if (assignmentMatcher.find()) {
                    variableName = assignmentMatcher.group(1);
                }

                if (variableName != null) {

                    String indent = line.substring(0, line.indexOf(line.trim()));
                    String escapedLine = line.trim().replace("\"", "\\\"");

                    writer.write(indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                            + escapedLine + "\");");
                    writer.newLine();

                    writer.write(indent + "core.MutationAnalyser.getInstance().addNewVariable("
                            + variableName + ", \"" + variableName + "\");");
                    writer.newLine();
                }
            }

            System.out.println("Mutant instrumenté créé : " + instrumentedFile.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void instrumentFile(File originalFile){
        String originalClassName = originalFile.getName().replace(".java", "");

        String mutableClassName = originalClassName + "Mutable";
        String observedClassName = originalClassName + "Observee";


        File mutableFile = new File(INSTRUMENTED_DIR, mutableClassName + ".java");
        File observedFile = new File(INSTRUMENTED_DIR, observedClassName + ".java");

        Pattern declarationPattern =
                Pattern.compile("^\\s*(?:final\\s+)?[\\w<>\\[\\]]+\\s+(\\w+)\\s*=.+;");

        Pattern assignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*=\\s*.+;");

        Pattern compoundAssignmentPattern =
                Pattern.compile("^\\s*(\\w+)\\s*(\\+=|-=|\\*=|/=|%=)\\s*.+;");

        Pattern incrementPattern =
                Pattern.compile("^\\s*(?:\\+\\+|--)?\\s*(\\w+)\\s*(?:\\+\\+|--)\\s*;");

        Pattern returnPattern = Pattern.compile("\\breturn\\b");

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter mutableWriter = new BufferedWriter(new FileWriter(mutableFile));
             BufferedWriter observedWriter = new BufferedWriter(new FileWriter(observedFile))) {

            String line;

            while ((line = reader.readLine()) != null) {

                // Remplacement du nom de classe
                String mutableLine = line.replaceAll("\\b" + originalClassName + "\\b", mutableClassName);
                String observedLine = line.replaceAll("\\b" + originalClassName + "\\b", observedClassName);

                // --- DETECTION RETURN ---
                Matcher returnMatcher = returnPattern.matcher(line);


                if (returnMatcher.find()) {

                    String indent = line.substring(0, line.indexOf("return"));
                    String escapedLine = line.trim().replace("\"", "\\\"");

                    String setCodeMutable =
                            indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                                    + escapedLine + "\");";

                    String snapMutable =
                            indent + "core.MutationAnalyser.getInstance().takeSnapshot();";

                    String setCodeObservee =
                            indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                                    + escapedLine + "\");";

                    String snapObservee =
                            indent + "core.MutationAnalyser.getInstance().takeSnapshotObservee();";

                    mutableWriter.write(setCodeMutable);
                    mutableWriter.newLine();
                    mutableWriter.write(snapMutable);
                    mutableWriter.newLine();

                    observedWriter.write(setCodeObservee);
                    observedWriter.newLine();
                    observedWriter.write(snapObservee);
                    observedWriter.newLine();
                }

                // écrire la ligne originale modifiée
                mutableWriter.write(mutableLine);
                mutableWriter.newLine();

                observedWriter.write(observedLine);
                observedWriter.newLine();

                // --- DETECTION VARIABLES ---


                String variableName = null;

                Matcher declarationMatcher = declarationPattern.matcher(line);
                Matcher assignmentMatcher = assignmentPattern.matcher(line);
                Matcher compoundMatcher = compoundAssignmentPattern.matcher(line);
                Matcher incrementMatcher = incrementPattern.matcher(line);

                if (declarationMatcher.find()) {
                    variableName = declarationMatcher.group(1);
                } else if (compoundMatcher.find()) {
                    variableName = compoundMatcher.group(1);
                } else if (incrementMatcher.find()) {
                    variableName = incrementMatcher.group(1);
                } else if (assignmentMatcher.find()) {
                    variableName = assignmentMatcher.group(1);
                }

                if (variableName != null) {

                    String indent = line.substring(0, line.indexOf(line.trim()));

                    String escapedLine = line.trim().replace("\"", "\\\"");

                    String setCodeMutable =
                            indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                                    + escapedLine + "\");";

                    String probeMutable =
                            indent + "core.MutationAnalyser.getInstance().addNewVariable("
                                    + variableName + ", \"" + variableName + "\");";

                    String setCodeObservee =
                            indent + "core.MutationAnalyser.getInstance().setCurrentCode(\""
                                    + escapedLine + "\");";

                    String probeObservee =
                            indent + "core.MutationAnalyser.getInstance().addNewVariableObservee("
                                    + variableName + ", \"" + variableName + "\");";

                    mutableWriter.write(setCodeMutable);
                    mutableWriter.newLine();
                    mutableWriter.write(probeMutable);
                    mutableWriter.newLine();

                    observedWriter.write(setCodeObservee);
                    observedWriter.newLine();
                    observedWriter.write(probeObservee);
                    observedWriter.newLine();
                }
            }

            System.out.println("Fichier mutable créé : " + mutableFile.getName());
            System.out.println("Fichier observé créé : " + observedFile.getName());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String[] args) {

        instrumentAllObservees();
        instrumentAllMutants();

    }
}
