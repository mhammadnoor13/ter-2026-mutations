import java.util.ArrayList;
import java.util.List;

public class ClasseSimple {

    private int seuil;
    private double coefficient;
    private List<Integer> valeurs;

    public ClasseSimple(int seuil, double coefficient) {
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
        int compteur = 0;              // variable interne

        for (int v : valeurs) {
            if (v > seuil) {
                somme += v;
                compteur++;
            }
        }

        if (compteur == 0) {
            return 0;
        }

        double moyenne = (double) somme / compteur;  // variable interne
        double scoreFinal = moyenne * coefficient;   // variable interne

        return scoreFinal;
    }

    // Autre méthode avec logique interne
    public boolean verifierDepassement() {

        double score = calculerScore();  // variable interne
        double limite = seuil * coefficient; // variable interne

        return score > limite;
    }

    public List<Integer> getValeurs() {
        return valeurs;
    }
}
