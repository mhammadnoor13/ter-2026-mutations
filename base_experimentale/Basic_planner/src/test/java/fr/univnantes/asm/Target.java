package fr.univnantes.asm;

public class Target {

    public int calcul() {
        int x1 = 10;
        int x2 = 3;

        int a = x1 + x2;

        return x1 + a;
    }

    public static void main(String[] args) {
        System.out.println(new Target().calcul());
    }
}