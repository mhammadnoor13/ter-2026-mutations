package experiment.subjects;

import java.util.ArrayList;
import java.util.List;

public class ClasseSimpleObservee {

    private int seuil;
    private double coefficient;
    private List<Integer> valeurs;

    public ClasseSimpleObservee(int seuil, double coefficient) {
        this.seuil = seuil;
        this.coefficient = coefficient;
        this.valeurs = new ArrayList<>();
    }

    public void ajouterValeur(int valeur) {
        valeurs.add(valeur);
    }

    // Méthode plus complexe avec variables internes
    public double calculerScore() {

        int somme = 0;                 // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(somme, "somme");
        int compteur = 0;              // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(compteur, "compteur");

        for (int v : valeurs) {
            if (v > seuil) {
                somme += v;
                core.MutationAnalyser.getInstance().addNewVariableObservee(somme, "somme");
                compteur++;
                core.MutationAnalyser.getInstance().addNewVariableObservee(compteur, "compteur");
            }
        }

        if (compteur == 0) {
            core.MutationAnalyser.getInstance().takeSnapshotObservee();
            return 0;
        }

        double moyenne = (double) somme / compteur;  // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(moyenne, "moyenne");
        double scoreFinal = moyenne * coefficient;   // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(scoreFinal, "scoreFinal");

        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return scoreFinal;
    }

    // Autre méthode avec logique interne
    public boolean verifierDepassement() {

        double score = calculerScore();  // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(score, "score");
        double limite = seuil * coefficient; // variable interne
        core.MutationAnalyser.getInstance().addNewVariableObservee(limite, "limite");

        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return score > limite;
    }

    public List<Integer> getValeurs() {
        core.MutationAnalyser.getInstance().takeSnapshotObservee();
        return valeurs;
    }
}
