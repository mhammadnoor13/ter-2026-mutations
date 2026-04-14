package core;

import java.util.*;

public class PropagationAnalyzer {


    public List<Set<String>> computeInfectedSets() {
        MutationAnalyser mutationAnalyser = MutationAnalyser.getInstance();

        List<List<String>> snapShotsM = mutationAnalyser.getSnapshots();
        List<List<String>> snapShotsO = mutationAnalyser.getSnapshotsObservee();

        List<Set<String>> infectedSets = new ArrayList<>();

        int maxSnapshots = Math.min(snapShotsO.size(), snapShotsM.size());

        for (int i = 0; i < maxSnapshots; i++) {

            List<String> snapM = i < snapShotsM.size() ? snapShotsM.get(i) : new ArrayList<>();
            List<String> snapO = i < snapShotsO.size() ? snapShotsO.get(i) : new ArrayList<>();

            Map<String, String> mapM = parseSnapshot(snapM);
            Map<String, String> mapO = parseSnapshot(snapO);

            Set<String> infected = new HashSet<>();

            Set<String> allVars = new HashSet<>();
            allVars.addAll(mapM.keySet());
            allVars.addAll(mapO.keySet());

            for (String var: allVars) {
                String valM = mapM.getOrDefault(var, "");
                String valO = mapO.getOrDefault(var, "");

                if (!valM.equals(valO)){
                    infected.add(var);
                }
            }
            infectedSets.add(infected);
        }

        return infectedSets;
    }

    public void analyzeTransitions(List<Set<String>> infectedSets) {

        Set<String> previous = new HashSet<>();

        for (int i = 0; i < infectedSets.size(); i++) {

            Set<String> current = infectedSets.get(i);

            System.out.println("---- Snapshot " + (i + 1) + " ----");

            // union of variables
            Set<String> all = new HashSet<>();
            all.addAll(previous);
            all.addAll(current);

            for (String var : all) {

                boolean before = previous.contains(var);
                boolean after = current.contains(var);

                if (!before && after) {
                    System.out.println(var + " → PROPAGATION");
                }
                else if (before && !after) {
                    System.out.println(var + " → ELIMINATION");

                    EliminationLogger.getInstance().logElimination(
                            i+1,
                            var,
                            previous,
                            current
                    );
                }
                else if (before && after) {
                    System.out.println(var + " → PERSISTENCE");
                }
            }

            previous = current;
        }
    }

    private Map<String, String> parseSnapshot(List<String> snapshot){
        Map<String, String> map = new HashMap<>();

        for (String line : snapshot) {
            if (line.contains(" = ")) {
                String[] parts = line.split(" = ", 2);
                map.put(parts[0].trim(), parts[1].trim());
            }
        }

        return map;
    }

    public void printInfectedSets(List<Set<String>> sets) {
        for (int i = 0; i < sets.size(); i++) {
            System.out.println("Snapshot " + (i + 1) + " → " + sets.get(i));
        }
    }

}
