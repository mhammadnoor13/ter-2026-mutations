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

    private static MutationAnalyser instance;

    private int mutationCounter = 0;

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
        allVariablesObservee.set(0, (int)(allVariablesObservee.get(0)) + 1);
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



    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);

        System.out.print("Entrez le chemin du fichier .java : ");
        String filePath = scanner.nextLine();

        if (!filePath.endsWith(".java")) {
            System.out.println("Ce n'est pas un fichier .java");
            return;
        }

        File originalFile = new File(filePath);

        if (!originalFile.exists()) {
            System.out.println("Fichier introuvable.");
            return;
        }

        String originalClassName = originalFile.getName().replace(".java", "");

        String mutableClassName = originalClassName + "Mutable";
        String observedClassName = originalClassName + "Observee";

        String mutableFilePath = filePath.replace(".java", "Mutable.java");
        String observedFilePath = filePath.replace(".java", "Observee.java");

        File mutableFile = new File(mutableFilePath);
        File observedFile = new File(observedFilePath);

        Pattern varPattern = Pattern.compile("^\\s*([\\w<>]+)\\s+(\\w+)\\s*=.*;");
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

                    // 🔥 injecter snapshot AVANT le return

                    String indent = line.substring(0, line.indexOf("return"));

                    String snapMutable = indent + "MutationAnalyser.getInstance().takeSnapshot();";
                    String snapObservee = indent + "MutationAnalyser.getInstance().takeSnapshotObservee();";

                    mutableWriter.write(snapMutable);
                    mutableWriter.newLine();

                    observedWriter.write(snapObservee);
                    observedWriter.newLine();
                }

                // écrire la ligne originale modifiée
                mutableWriter.write(mutableLine);
                mutableWriter.newLine();

                observedWriter.write(observedLine);
                observedWriter.newLine();

                // --- DETECTION VARIABLES ---
                Matcher matcher = varPattern.matcher(line);

                if (matcher.find()) {

                    String variableName = matcher.group(2);

                    String probeMutable = "MutationAnalyser.getInstance().addNewVariable("
                            + variableName + ", \"" + variableName + "\");";

                    String probeObservee = "MutationAnalyser.getInstance().addNewVariableObservee("
                            + variableName + ", \"" + variableName + "\");";

                    mutableWriter.write(probeMutable);
                    mutableWriter.newLine();

                    observedWriter.write(probeObservee);
                    observedWriter.newLine();
                }
            }

            System.out.println("Fichier mutable créé : " + mutableFilePath);
            System.out.println("Fichier observé créé : " + observedFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
