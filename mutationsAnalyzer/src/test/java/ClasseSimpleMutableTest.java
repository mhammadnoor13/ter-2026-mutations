import static org.junit.jupiter.api.Assertions.*;

import core.MutationAnalyser;
import core.PropagationAnalyzer;

import experiment.subjects.*;
import experiment.subjects.ClasseSimpleMutable;
import experiment.subjects.ClasseSimpleObservee;
import experiment.subjects.InvoiceCalculatorMutable;
import experiment.subjects.InvoiceCalculatorObservee;
import experiment.subjects.OrderProcessorMutable;
import experiment.subjects.OrderProcessorObservee;
import experiment.subjects.PropagationPlaygroundMutable;
import experiment.subjects.PropagationPlaygroundObservee;
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

    @Test
    void testPropagationAnalysisOrderProcessor() {

        OrderProcessorMutable mutable =
                new OrderProcessorMutable(0.2, 0.1, 5);

        OrderProcessorObservee observee =
                new OrderProcessorObservee(0.2, 0.1, 5);

        // populate data
        mutable.addItem(10);
        observee.addItem(10);

        mutable.addItem(20);
        observee.addItem(20);

        mutable.addItem(30);
        observee.addItem(30);

        // run computation
        mutable.total();
        observee.total();

        // propagation analysis
        PropagationAnalyzer analyzer = new PropagationAnalyzer();

        List<Set<String>> infectedSets =
                analyzer.computeInfectedSets();

        analyzer.printInfectedSets(infectedSets);
        analyzer.analyzeTransitions(infectedSets);
    }

    @Test
    void testPropagationAnalysisInvoice() {

        InvoiceCalculatorMutable mutable =
                new InvoiceCalculatorMutable(0.2, 5);

        InvoiceCalculatorObservee observee =
                new InvoiceCalculatorObservee(0.2, 5);

        mutable.add(10);
        observee.add(10);

        mutable.add(20);
        observee.add(20);

        mutable.compute();
        observee.compute();

        PropagationAnalyzer analyzer = new PropagationAnalyzer();

        List<Set<String>> infectedSets =
                analyzer.computeInfectedSets();

        analyzer.printInfectedSets(infectedSets);
        analyzer.analyzeTransitions(infectedSets);
    }
}
