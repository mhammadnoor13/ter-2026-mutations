package fr.univnantes.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.objectweb.asm.Opcodes.*;

public class AsmIaddToIsubTransformer {

    //Transforme une classe (bytecode) en remplaçant toutes les instructions IADD par ISUB
    public static byte[] transformer(byte[] classBytes) {


        ClassReader reader = new ClassReader(classBytes);

        // ClassWriter : reconstruit un nouveau .class
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        //ClassVisitor : intercepte les éléments de la classe pendant la lecture
        ClassVisitor cv = new ClassVisitor(ASM9, writer) {

            /**
             * ASM appelle visitMethod pour chaque méthode de la classe
             * On a accès aux infos de la méthode (nom, descripteur, etc.)
             */
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                //On retourne un MethodVisitor personnalisé qui intercepte les instructions.
                return new MethodVisitor(ASM9, mv) {

                    @Override
                    public void visitInsn(int opcode) {

                        // Si l’instruction est IADD
                        if (opcode == IADD) {
                            // On écrit ISUB à la place
                            super.visitInsn(ISUB);
                        } else {
                            // Sinon on réécrit l’instruction identique (pas de mutation)
                            super.visitInsn(opcode);
                        }
                    }
                };
            }
        };
        reader.accept(cv, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    public static void main(String[] args) throws IOException {

        /**
         * On lit la classe Target compilée depuis le classpath de test
         * Après `mvn test-compile`, Maven place les classes de test compilées dans :
         *   target/test-classes/
         */
        String res = "fr/univnantes/asm/Target.class";

        // On récupère le .class et on lit tous les octets
        byte[] original = AsmIaddToIsubTransformer.class
                .getClassLoader()
                .getResourceAsStream(res)
                .readAllBytes();

        // On applique la transformation ASM : IADD -> ISUB
        byte[] mute = transformer(original);

        /**
         * On écrit la version mutée dans un dossier séparé :
         *   target/asm-mutated/...
         */
        Path outDir = Path.of("target/asm-mutated/fr/univnantes/asm");
        Files.createDirectories(outDir);

        // Chemin complet du fichier .class muté
        Path outFile = outDir.resolve("Target.class");

        // Écriture des octets mutés sur disque
        Files.write(outFile, mute);

        // Messages d'information pour vérifier facilement
        System.out.println("CA A MACRHE : classe mutée écrite ici : " + outFile.toAbsolutePath());
        System.out.println("Avant : IADD (Target.main)");
        System.out.println("Après mutation : IADD -> ISUB (classe générée dans target/asm-mutated)");
    }
}