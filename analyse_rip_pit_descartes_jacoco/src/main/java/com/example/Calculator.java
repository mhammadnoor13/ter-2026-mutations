package com.example;

 // Calculatrice simple — sert de cible pour l'analyse de mutation RIP.

public class Calculator {

    public int add(int a, int b) {
        return a + b;
    }

    public int multiply(int a, int b) {
        return a * b;
    }

    public int max(int a, int b) {
        if (a > b) {
            return a;
        }
        return b;
    }

    public int divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("Division par zéro");
        }
        return a / b;
    }

    public boolean isPositive(int n) {
        return n > 0;
    }

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
