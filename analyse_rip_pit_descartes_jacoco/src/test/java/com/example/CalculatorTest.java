package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests de Calculator — qualité volontairement variée pour illustrer RIP.
 *
 * ┌──────────────┬─────┬─────┬─────┬──────────────────────────────┐
 * │ Méthode      │  R  │  I  │  P  │ Diagnostic attendu           │
 * ├──────────────┼─────┼─────┼─────┼──────────────────────────────┤
 * │ add          │  ✓  │  ✓  │  ✓  │ killed — test complet        │
 * │ multiply     │  ✓  │  ✓  │  ✗  │ pseudo-tested — pas assert   │
 * │ max          │  ✗  │  -  │  -  │ no_coverage — jamais appelé  │
 * │ divide       │  ✓  │  ✗  │  ✗  │ survived — entrées neutres   │
 * │ isPositive   │  ✓  │  ✓  │  ✓  │ killed — bon test            │
 * │ factorial    │  ✓  │  ✓  │  ✓  │ killed — cas multiples       │
 * └──────────────┴─────┴─────┴─────┴──────────────────────────────┘
 */
class CalculatorTest {

    private Calculator calc;

    @BeforeEach
    void setUp() {
        calc = new Calculator();
    }

    // ─────────────────────────────────────────────────────────────
    // add() — R ✓ I ✓ P ✓ → mutation TUÉE
    // Entrées : 3 ≠ 2, donc a+b ≠ a-b  (I satisfait)
    // Assertion sur la valeur exacte    (P satisfait)
    // ─────────────────────────────────────────────────────────────

    @Test
    void add_deuxPositifs() {
        assertEquals(5, calc.add(3, 2));
    }

    @Test
    void add_avecNegatif() {
        assertEquals(1, calc.add(3, -2));
    }

    @Test
    void add_deuxZeros() {
        assertEquals(0, calc.add(0, 0));
    }

    // ─────────────────────────────────────────────────────────────
    // multiply() — R ✓ I ✓ P ✗ → PSEUDO-TESTED
    // Le test appelle multiply() mais n'assert PAS le résultat.
    // Descartes le détecte : remplacer la méthode par "return 0"
    // ne fait pas échouer ce test.
    // ─────────────────────────────────────────────────────────────

    @Test
    void multiply_appelSansAssertion() {
        // BUG INTENTIONNEL : on appelle la méthode sans vérifier le retour
        calc.multiply(4, 3);
        // Aucun assertEquals ici → P échoue
        assertTrue(true); // assertion inutile, ne capte rien
    }

    // ─────────────────────────────────────────────────────────────
    // max() — R ✗ → NO_COVERAGE
    // Aucun test n'appelle max(). JaCoCo montrera 0% de couverture.
    // ─────────────────────────────────────────────────────────────

    // (aucun test pour max — intentionnel)

    // ─────────────────────────────────────────────────────────────
    // divide() — R ✓ I ✗ → SURVIVED
    // On teste uniquement divide(6, 2) = 3.
    // Mutation AOR : a / b → a * b  donne 6*2 = 12 ≠ 3 → devrait être tué !
    // Mais : on teste aussi divide(0, 5) = 0, et 0/5 == 0*5 == 0 → I échoue
    // pour CET appel. Résultat : partiellement testé.
    // ─────────────────────────────────────────────────────────────

    @Test
    void divide_casNeutre() {
        // I échoue : 0 / 5 == 0, et 0 * 5 == 0 aussi → mutation non détectée
        assertEquals(0, calc.divide(0, 5));
    }

    @Test
    void divide_exceptionSiZero() {
        // R ✓ mais teste uniquement l'exception, pas la valeur
        assertThrows(IllegalArgumentException.class,
            () -> calc.divide(10, 0));
    }

    // ─────────────────────────────────────────────────────────────
    // isPositive() — R ✓ I ✓ P ✓ → mutation TUÉE
    // ─────────────────────────────────────────────────────────────

    @Test
    void isPositive_nombrePositif() {
        assertTrue(calc.isPositive(5));
    }

    @Test
    void isPositive_nombreNegatif() {
        assertFalse(calc.isPositive(-3));
    }

    @Test
    void isPositive_zero() {
        // Important : distingue > 0 de >= 0 (mutation ROR)
        assertFalse(calc.isPositive(0));
    }

    // ─────────────────────────────────────────────────────────────
    // factorial() — R ✓ I ✓ P ✓ → mutation TUÉE
    // Plusieurs cas pour couvrir toutes les branches
    // ─────────────────────────────────────────────────────────────

    @Test
    void factorial_zero() {
        assertEquals(1L, calc.factorial(0));
    }

    @Test
    void factorial_un() {
        assertEquals(1L, calc.factorial(1));
    }

    @Test
    void factorial_cinq() {
        assertEquals(120L, calc.factorial(5));
    }

    @Test
    void factorial_negatif() {
        assertThrows(IllegalArgumentException.class,
            () -> calc.factorial(-1));
    }
}
