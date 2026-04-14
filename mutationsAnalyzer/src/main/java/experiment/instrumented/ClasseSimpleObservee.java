package experiment.instrumented;

import core.MutationAnalyser;

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
        MutationAnalyser.getInstance().addNewVariableObservee(somme, "somme");
        int compteur = 0;              // variable interne
        MutationAnalyser.getInstance().addNewVariableObservee(compteur, "compteur");

        for (int v : valeurs) {
            if (v > seuil) {
                somme += v;
                MutationAnalyser.getInstance().addNewVariableObservee(somme, "somme");
                compteur++;
                MutationAnalyser.getInstance().addNewVariableObservee(compteur, "compteur");
            }
        }

        if (compteur == 0) {
            MutationAnalyser.getInstance().takeSnapshotObservee();
            return 0;
        }

        double moyenne = (double) somme / compteur;  // variable interne
        MutationAnalyser.getInstance().addNewVariableObservee(moyenne, "moyenne");
        double scoreFinal = moyenne * coefficient;   // variable interne
        MutationAnalyser.getInstance().addNewVariableObservee(scoreFinal, "scoreFinal");

        MutationAnalyser.getInstance().takeSnapshotObservee();
        return scoreFinal;
    }

    // Autre méthode avec logique interne
    public boolean verifierDepassement() {

        double score = calculerScore();  // variable interne
        MutationAnalyser.getInstance().addNewVariableObservee(score, "score");
        double limite = seuil * coefficient; // variable interne
        MutationAnalyser.getInstance().addNewVariableObservee(limite, "limite");

        MutationAnalyser.getInstance().takeSnapshotObservee();
        return score > limite;
    }

    public List<Integer> getValeurs() {
        MutationAnalyser.getInstance().takeSnapshotObservee();
        return valeurs;
    }
}
