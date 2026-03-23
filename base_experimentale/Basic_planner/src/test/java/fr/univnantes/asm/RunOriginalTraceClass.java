package fr.univnantes.asm;

import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;

public class RunOriginalTraceClass {

    static class ByteArrayLoader extends ClassLoader {
        public ByteArrayLoader(ClassLoader parent) {
            super(parent);
        }

        public Class<?> define(String name, byte[] bytes) {
            return defineClass(name, bytes, 0, bytes.length);
        }
    }

    public static void main(String[] args) throws Exception {

        Path p = Path.of("target/asm-mutated/fr/univnantes/asm/TargetOriginal.class");
        byte[] bytes = Files.readAllBytes(p);

        ClassLoader parent = RunOriginalTraceClass.class.getClassLoader();
        Class.forName("fr.univnantes.asm.TraceLogger", true, parent);

        ByteArrayLoader loader = new ByteArrayLoader(parent);

        Class<?> clazz = loader.define("fr.univnantes.asm.Target", bytes);

        Object instance = clazz.getDeclaredConstructor().newInstance();
        Method calcul = clazz.getMethod("calcul");

        Object result = calcul.invoke(instance);
        System.out.println(result);
    }
}