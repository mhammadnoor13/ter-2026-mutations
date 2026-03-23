package fr.univnantes.asm;

public class RunnerClassOriginal {

    public static void main(String[] args) {
        Target t = new Target();
        int result = t.calcul();
        System.out.println(result);
    }
}