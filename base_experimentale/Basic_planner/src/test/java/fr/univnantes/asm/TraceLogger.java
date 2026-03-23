package fr.univnantes.asm;

public class TraceLogger {

    public static void logMutation(int value) {
        System.out.println("[TRACE] valeur après mutation = " + value);
    }

    public static void logReturn(int value) {
        System.out.println("[TRACE] valeur retournée = " + value);
    }
}