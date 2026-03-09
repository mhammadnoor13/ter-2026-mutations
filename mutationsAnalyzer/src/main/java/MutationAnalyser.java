import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public class MutationAnalyser {

    private final List<Object> allVariables;
    private final List<String> allVariableNames;
    private final List<List<String>> allSnapshots;

    private static MutationAnalyser instance;

    private int mutationCounter = 0;

    private MutationAnalyser() {
        this.allVariables = new ArrayList<>();
        this.allVariableNames = new ArrayList<>();
        this.allSnapshots = new ArrayList<>();
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

        mutationCounter = 0;

        int initialValue = 1;
        allVariables.add(initialValue);
        allVariableNames.add("N°");

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

    public void addMutation(String mutationName) {

        if (allVariables.contains(mutationName)) return;

        mutationCounter++;
        String varName = "Mutation" + mutationCounter;

        allVariables.add(mutationName);
        allVariableNames.add(varName);

        takeSnapshot();
    }

    public void printAllVariableValues() {
        for (int i = 0; i < allVariables.size(); i++) {
            String name = allVariableNames.get(i);
            Object value = allVariables.get(i);
            System.out.println(name + " = " + (value != null ? value.toString() : "null"));
        }
    }

    // --- Nouvelle méthode ---
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

        if (allSnapshots.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Aucun snapshot disponible.");
            return;
        }

        // Colonnes = noms des variables
        String[] columnNames = allVariableNames.toArray(new String[0]);

        // Données du tableau
        String[][] data = new String[allSnapshots.size()][columnNames.length];

        for (int i = 0; i < allSnapshots.size(); i++) {
            List<String> snapshot = allSnapshots.get(i);

            for (int j = 0; j < columnNames.length; j++) {

                String variableName = columnNames[j];
                String valueFound = "";

                for (String line : snapshot) {
                    if (line.startsWith(variableName + " = ")) {
                        valueFound = line.substring((variableName + " = ").length());
                        break;
                    }
                }

                data[i][j] = valueFound;
            }
        }

        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(800, 400));

        // ---- Utilisation d'un JDialog modal pour bloquer ----
        JDialog dialog = new JDialog();
        dialog.setTitle("Evolution des variables");
        dialog.setModal(true); // ⚡ bloque l'exécution tant que la fenêtre n'est pas fermée
        dialog.add(scrollPane);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setVisible(true); // ici le code reste bloqué jusqu'à fermeture
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
        String newClassName = originalClassName + "Mutable";

        String newFilePath = filePath.replace(".java", "Mutable.java");
        File newFile = new File(newFilePath);

        Pattern pattern = Pattern.compile("^\\s*([\\w<>]+)\\s+(\\w+)\\s*=.*;");

        try (BufferedReader reader = new BufferedReader(new FileReader(originalFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(newFile))) {

            String line;

            while ((line = reader.readLine()) != null) {

                line = line.replaceAll("\\b" + originalClassName + "\\b", newClassName);

                writer.write(line);
                writer.newLine();

                Matcher matcher = pattern.matcher(line);

                if (matcher.find()) {

                    String variableName = matcher.group(2);

                    writer.write("MutationAnalyser.getInstance().addNewVariable("
                            + variableName + ", \"" + variableName + "\");");
                    writer.newLine();
                }
            }

            System.out.println("Fichier modifié créé : " + newFilePath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
