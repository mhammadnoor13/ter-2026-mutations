package com.example;

/**
 * Calculatrice simple — sert de cible pour l'analyse de mutation RIP.
 *
 * Méthodes intentionnellement variées pour illustrer les trois cas :
 *   - add()     → test correct  : R ✓ I ✓ P ✓  (mutation tuée)
 *   - multiply() → pas d'assertion : R ✓ I ✓ P ✗  (pseudo-tested)
 *   - max()     → non appelé    : R ✗             (no_coverage)
 *   - divide()  → entrées neutres: R ✓ I ✗         (partiellement testé)
 */
public class Calculator {

    /** Addition de deux entiers. */
    public int add(int a, int b) {
        return a + b;
    }

    /** Multiplication de deux entiers. */
    public int multiply(int a, int b) {
        return a * b;
    }

    /** Maximum de deux entiers. */
    public int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    /** Division entière — lève une exception si diviseur nul. */
    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division par zéro");
        }
        return a / b;
    }

    /** Vérifie si un nombre est positif (strictement > 0). */
    public boolean isPositive(int n) {
        return n > 0;
    }

    /** Calcule la factorielle (n >= 0). */
    public long factorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Entrée négative");
        }
        if (n == 0) {
            return 1L;
        }
        long result = 1L;
        for (int i = 1; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}
