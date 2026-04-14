package experiment.instrumented;

import core.MutationAnalyser;

import java.util.ArrayList;
import java.util.List;

public class ClasseSimpleMutable {

    private int seuil;
    private double coefficient;
    private List<Integer> valeurs;

    public ClasseSimpleMutable(int seuil, double coefficient) {
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
        MutationAnalyser.getInstance().addNewVariable(somme, "somme");
        int compteur = 0;              // variable interne
        MutationAnalyser.getInstance().addNewVariable(compteur, "compteur");

        for (int v : valeurs) {
            if (v > seuil) {
                somme -= v;
                MutationAnalyser.getInstance().addNewVariable(somme, "somme");
                compteur++;
                MutationAnalyser.getInstance().addNewVariable(compteur, "compteur");
            }
        }

        if (compteur == 0) {
            MutationAnalyser.getInstance().takeSnapshot();
            return 0;
        }

        double moyenne = (double) somme / compteur;  // variable interne
        MutationAnalyser.getInstance().addNewVariable(moyenne, "moyenne");
        double scoreFinal = moyenne * coefficient;   // variable interne
        MutationAnalyser.getInstance().addNewVariable(scoreFinal, "scoreFinal");

        MutationAnalyser.getInstance().takeSnapshot();
        return scoreFinal;
    }

    // Autre méthode avec logique interne
    public boolean verifierDepassement() {

        double score = calculerScore();  // variable interne
        MutationAnalyser.getInstance().addNewVariable(score, "score");
        double limite = seuil * coefficient; // variable interne
        MutationAnalyser.getInstance().addNewVariable(limite, "limite");

        MutationAnalyser.getInstance().takeSnapshot();
        return score > limite;
    }

    public List<Integer> getValeurs() {
        MutationAnalyser.getInstance().takeSnapshot();
        return valeurs;
    }
}
