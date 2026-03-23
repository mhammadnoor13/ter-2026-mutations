package fr.univnantes.asm;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class RunMutatedTraceClass {

    static class ByteArrayLoader extends ClassLoader {

        public ByteArrayLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {
        Path p = Path.of("target/asm-mutated/fr/univnantes/asm/Target.class");
        byte[] mutatedBytes = Files.readAllBytes(p);

        // On récupère le classloader normal du programme
        ClassLoader parentLoader = RunMutatedTraceClass.class.getClassLoader();

        // On force le chargement de TraceLogger avec ce loader
        Class.forName("fr.univnantes.asm.TraceLogger", true, parentLoader);

        // On crée notre loader custom avec le bon parent
        ByteArrayLoader loader = new ByteArrayLoader(parentLoader);

        Class<?> mutatedTarget = loader.define("fr.univnantes.asm.Target", mutatedBytes);

        Object instance = mutatedTarget.getDeclaredConstructor().newInstance();
        Method calcul = mutatedTarget.getMethod("calcul");

        Object result = calcul.invoke(instance);
        System.out.println(result);
    }
}