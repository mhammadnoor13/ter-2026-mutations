import static org.junit.jupiter.api.Assertions.*;

import core.MutationAnalyser;
import core.PropagationAnalyzer;
import experiment.instrumented.ClasseSimpleMutable;
import experiment.instrumented.ClasseSimpleObservee;
import experiment.instrumented.PropagationPlaygroundMutable;
import experiment.instrumented.PropagationPlaygroundObservee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

public class ClasseSimpleMutableTest {

    private ClasseSimpleMutable instance;
    private ClasseSimpleObservee instanceObservee;

    private PropagationPlaygroundMutable propagationPlaygroundMutable;
    private PropagationPlaygroundObservee propagationPlaygroundObservee;

    @BeforeEach
    void setUp() {
        instance = new ClasseSimpleMutable(10, 1.5);
        instanceObservee = new ClasseSimpleObservee(10, 1.5);

        propagationPlaygroundMutable = new PropagationPlaygroundMutable(0, 0.5);
        propagationPlaygroundObservee = new PropagationPlaygroundObservee(0, 0.5);
        MutationAnalyser.getInstance().reset();
    }

    @Test
    void testAjouterValeur() {
        instance.ajouterValeur(5);
        instanceObservee.ajouterValeur(5);
        instance.ajouterValeur(15);
        instanceObservee.ajouterValeur(15);

        List<Integer> valeurs = instance.getValeurs();
        List<Integer> valeursObservee = instanceObservee.getValeurs();
        assertEquals(2, valeurs.size());
        assertEquals(5, valeurs.get(0));
        assertEquals(15, valeurs.get(1));
    }

    @Test
    void testCalculerScore() {
        instance.ajouterValeur(5);   // en dessous du seuil
        instanceObservee.ajouterValeur(5);
        instance.ajouterValeur(15);  // au-dessus du seuil
        instanceObservee.ajouterValeur(15);
        instance.ajouterValeur(25);  // au-dessus du seuil
        instanceObservee.ajouterValeur(25);

        double score = instance.calculerScore();
        double scoreObservee = instanceObservee.calculerScore();

        MutationAnalyser.getInstance().showSnapshotsInPopup();
        // Seule 15 et 25 > seuil 10, moyenne = (15+25)/2 = 20, scoreFinal = 20*1.5=30
        assertEquals(30.0, score, 0.0001);
    }

    @Test
    void testVerifierDepassement() {
        instance.ajouterValeur(5);   // en dessous du seuil
        instanceObservee.ajouterValeur(5);
        instance.ajouterValeur(15);  // au-dessus du seuil
        instanceObservee.ajouterValeur(15);

        boolean depassement = instance.verifierDepassement();
        boolean depassementObservee = instanceObservee.verifierDepassement();
        MutationAnalyser.getInstance().showSnapshotsInPopup();
        // calculScore() = 15 * 1.5 = 22.5, limite = 10*1.5 = 15 => true
        assertTrue(depassement);
    }

    @Test
    void testPropagationAnalysis() {

        propagationPlaygroundMutable.add(5);
        propagationPlaygroundObservee.add(5);

        propagationPlaygroundMutable.add(15);
        propagationPlaygroundObservee.add(15);

        propagationPlaygroundMutable.computeScore();
        propagationPlaygroundObservee.computeScore();

        PropagationAnalyzer analyzer = new PropagationAnalyzer();
        List<Set<String>> infectedSets = analyzer.computeInfectedSets();

        analyzer.printInfectedSets(infectedSets);
        analyzer.analyzeTransitions(infectedSets);
    }

}
