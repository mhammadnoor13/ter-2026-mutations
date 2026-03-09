import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

public class ClasseSimpleMutableTest {

    private ClasseSimpleMutable instance;

    @BeforeEach
    void setUp() {
        instance = new ClasseSimpleMutable(10, 1.5);
        MutationAnalyser.getInstance().reset();
    }

    @Test
    void testAjouterValeur() {
        instance.ajouterValeur(5);
        instance.ajouterValeur(15);

        List<Integer> valeurs = instance.getValeurs();
        assertEquals(2, valeurs.size());
        assertEquals(5, valeurs.get(0));
        assertEquals(15, valeurs.get(1));
    }

    @Test
    void testCalculerScore() {
        instance.ajouterValeur(5);   // en dessous du seuil
        instance.ajouterValeur(15);  // au-dessus du seuil
        instance.ajouterValeur(25);  // au-dessus du seuil

        double score = instance.calculerScore();

        MutationAnalyser.getInstance().showSnapshotsInPopup();
        // Seule 15 et 25 > seuil 10, moyenne = (15+25)/2 = 20, scoreFinal = 20*1.5=30
        assertEquals(30.0, score, 0.0001);
    }

    @Test
    void testVerifierDepassement() {
        instance.ajouterValeur(5);   // en dessous du seuil
        instance.ajouterValeur(15);  // au-dessus du seuil

        boolean depassement = instance.verifierDepassement();
        MutationAnalyser.getInstance().showSnapshotsInPopup();
        // calculScore() = 15 * 1.5 = 22.5, limite = 10*1.5 = 15 => true
        assertTrue(depassement);
    }

}
