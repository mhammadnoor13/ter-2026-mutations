package fr.univnantes.asm;

public class Target {

    public int calcul() {
        int x1 = 10;
        int x2 = 3;
        int y1 = 8;
        int y2 = 2;
        int z1 = 4;
        int z2 = 1;

        int a = x1 + x2;
        int b = y1 + y2;
        int c = z1 + z2;

        return a + b + c;
    }

    public static void main(String[] args) {
        System.out.println(new Target().calcul());
    }
}