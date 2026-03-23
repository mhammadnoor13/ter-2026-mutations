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

    public static byte[] transformer(byte[] classBytes, int indexCible, boolean mutate) {
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
                        //cas mutation
                        if (opcode == IADD) {
                            System.out.println("IADD trouvé à instructionIndex = " + instructionIndex);

                            if (mutate && instructionIndex == indexCible) {

                                // on remplace IADD par ISUB
                                super.visitInsn(ISUB);

                                // on duplique le résultat pour le logger
                                super.visitInsn(DUP);

                                // appel TraceLogger.logMutation(int)
                                super.visitMethodInsn(
                                        INVOKESTATIC,
                                        "fr/univnantes/asm/TraceLogger",
                                        "logMutation",
                                        "(I)V",
                                        false
                                );
                            } else {
                                super.visitInsn(IADD);
                            }
                            instructionIndex++;
                            return;
                        }
                        //on passe à l'occurrence suivante

                        // cas retour
                        if (opcode == IRETURN) {
                            super.visitInsn(DUP); // duplique la valeur sur la pile
                            super.visitMethodInsn(
                                    INVOKESTATIC,
                                    "fr/univnantes/asm/TraceLogger",
                                    "logReturn",
                                    "(I)V",
                                    false
                            );
                            super.visitInsn(IRETURN);
                            instructionIndex++;
                            return;
                        }

                        super.visitInsn(opcode);
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

        // version muté tracée
        byte[] mutatedTrace = transformer(original, indexCible, true);
        System.out.println("Index ciblé pour la mutation : " + indexCible);

        // version originale tracée
        byte[] originalTrace = transformer(original, indexCible, false);

        Path outDir = Path.of("target/asm-mutated/fr/univnantes/asm");
        Files.createDirectories(outDir);

        // mutant traces
        Path mutatedFile = outDir.resolve("Target.class");
        Files.write(mutatedFile, mutatedTrace);

        // original traces
        Path originalFile = outDir.resolve("TargetOriginal.class");
        Files.write(originalFile, originalTrace);

        System.out.println("Mutant tracé : " + mutatedFile.toAbsolutePath());
        System.out.println("Original tracé : " + originalFile.toAbsolutePath());
    }
}