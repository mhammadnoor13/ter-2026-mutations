package fr.univnantes.asm;

public class Target {
    public int add(int a, int b) {
        return a + b;
    }

    public static void main(String[] args) {
        System.out.println(new Target().add(10, 3));
    }
}