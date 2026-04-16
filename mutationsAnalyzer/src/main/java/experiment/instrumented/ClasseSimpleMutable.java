package experiment.subjects;

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
        core.MutationAnalyser.getInstance().setCurrentCode("int somme = 0;                 // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(somme, "somme");
        int compteur = 0;              // variable interne
        core.MutationAnalyser.getInstance().setCurrentCode("int compteur = 0;              // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(compteur, "compteur");

        for (int v : valeurs) {
            if (v > seuil) {
                somme += v;
                core.MutationAnalyser.getInstance().setCurrentCode("somme += v;");
                core.MutationAnalyser.getInstance().addNewVariable(somme, "somme");
                compteur++;
                core.MutationAnalyser.getInstance().setCurrentCode("compteur++;");
                core.MutationAnalyser.getInstance().addNewVariable(compteur, "compteur");
            }
        }

        if (compteur == 0) {
            core.MutationAnalyser.getInstance().setCurrentCode("return 0;");
            core.MutationAnalyser.getInstance().takeSnapshot();
            return 0;
        }

        double moyenne = (double) somme / compteur;  // variable interne
        core.MutationAnalyser.getInstance().setCurrentCode("double moyenne = (double) somme / compteur;  // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(moyenne, "moyenne");
        double scoreFinal = moyenne * coefficient;   // variable interne
        core.MutationAnalyser.getInstance().setCurrentCode("double scoreFinal = moyenne * coefficient;   // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(scoreFinal, "scoreFinal");

        core.MutationAnalyser.getInstance().setCurrentCode("return scoreFinal;");
        core.MutationAnalyser.getInstance().takeSnapshot();
        return scoreFinal;
    }

    // Autre méthode avec logique interne
    public boolean verifierDepassement() {

        double score = calculerScore();  // variable interne
        core.MutationAnalyser.getInstance().setCurrentCode("double score = calculerScore();  // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(score, "score");
        double limite = seuil * coefficient; // variable interne
        core.MutationAnalyser.getInstance().setCurrentCode("double limite = seuil * coefficient; // variable interne");
        core.MutationAnalyser.getInstance().addNewVariable(limite, "limite");

        core.MutationAnalyser.getInstance().setCurrentCode("return score > limite;");
        core.MutationAnalyser.getInstance().takeSnapshot();
        return score > limite;
    }

    public List<Integer> getValeurs() {
        core.MutationAnalyser.getInstance().setCurrentCode("return valeurs;");
        core.MutationAnalyser.getInstance().takeSnapshot();
        return valeurs;
    }
}
