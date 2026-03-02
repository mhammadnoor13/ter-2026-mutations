package fr.univnantes.asm;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class RunMutatedTarget {

    /**
     * Une classe est normalement chargée par un ClassLoader à partir du classpath
     * Ici on veut charger une classe depuis un tableau d’octets (byte[]) qui contient le contenu brut di fichier .class
     */
    static class ByteArrayLoader extends ClassLoader {

        public Class<?> define(String name, byte[] bytes) {
            // Enregistre la classe dans ce classloader
            // Les paramètres 0 et bytes.length indiquent qu’on prend tout le tableau
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {

        Path p = Path.of("target/asm-mutated/fr/univnantes/asm/Target.class");
        byte[] mutatedBytes = Files.readAllBytes(p);

        // charger une classe depuis des bytes
        ByteArrayLoader loader = new ByteArrayLoader();

        // On charge la classe "fr.univnantes.asm.Target" depuis mutatedBytes
        Class<?> mutatedTarget = loader.define("fr.univnantes.asm.Target", mutatedBytes);

        // Création d’une instance de la classe chargée.
        Object instance = mutatedTarget.getDeclaredConstructor().newInstance();

        // On récupère la méthode add
        Method add = mutatedTarget.getMethod("add", int.class, int.class);

        Object result = add.invoke(instance, 10, 3);

        System.out.println(result); // attendu : 7 (si la mutation a eu lie)u
    }
}