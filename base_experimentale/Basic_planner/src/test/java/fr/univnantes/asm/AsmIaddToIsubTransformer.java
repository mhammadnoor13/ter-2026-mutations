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

    public static byte[] transformer(byte[] classBytes, int indexCible) {
        ClassReader reader = new ClassReader(classBytes);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new ClassVisitor(ASM9, writer) {

            // compteur d'instructions déjà vus dans toute la classe
            private int instructionIndex = 0;

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc,
                                             String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                return new MethodVisitor(ASM9, mv) {

                    @Override
                    public void visitInsn(int opcode) {
                        if (opcode == IADD) {
                            System.out.println("IADD trouvé à instructionIndex = " + instructionIndex);
                        }
                        if (opcode == IADD && instructionIndex==indexCible) {
                            super.visitInsn(ISUB);   // on mute seulement l'occurrence ciblée
                        } else {
                            super.visitInsn(opcode);   // les autres restent inchangés
                        }
                        //on passe à l'occurrence suivante
                        instructionIndex++;
                    }

                    @Override
                    public void visitVarInsn(int opcode, int varIndex) {
                        instructionIndex++;
                        super.visitVarInsn(opcode, varIndex);
                    }

                    @Override
                    public void visitIntInsn(int opcode, int operand) {
                        instructionIndex++;
                        super.visitIntInsn(opcode, operand);
                    }

                };
            }
        };

        reader.accept(cv, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    public static void main(String[] args) throws IOException {
        String res = "fr/univnantes/asm/Target.class";

        byte[] original = AsmIaddToIsubTransformer.class
                .getClassLoader()
                .getResourceAsStream(res)
                .readAllBytes();

        int indexCible = 0;
        if (args.length > 0) {
            indexCible = Integer.parseInt(args[0]);
        }

        byte[] mute = transformer(original, indexCible);
        System.out.println("Index ciblé pour la mutation : " + indexCible);

        Path outDir = Path.of("target/asm-mutated/fr/univnantes/asm");
        Files.createDirectories(outDir);

        Path outFile = outDir.resolve("Target.class");
        Files.write(outFile, mute);

        System.out.println("Pas d'erreurs : classe mutée écrite ici : " + outFile.toAbsolutePath());
    }
}