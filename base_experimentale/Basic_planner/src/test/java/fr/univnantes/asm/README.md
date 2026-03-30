# Projet ASM

Petit projet d'expérimentation autour de **ASM** pour transformer du bytecode Java.

L'idée générale est simple :
- on part d'une classe Java `Target`
- on repère les instructions `IADD` dans son bytecode
- on peut en remplacer une par `ISUB` pour simuler une mutation
- on ajoute aussi des traces dans le bytecode pour voir la valeur après mutation et la valeur retournée

Le projet permet donc de comparer :
- l'exécution **originale**
- l'exécution **mutée et tracée**
- l'exécution **originale tracée**

---

## Structure des classes

### `Target.java`
C'est la classe de base sur laquelle on travaille.

Elle contient une méthode `calcul()` qui fait plusieurs additions, stocke les résultats dans des variables, puis retourne la somme finale.

C'est cette classe qui sert de support pour observer le bytecode avant et après transformation.

---

### `AsmIaddToIsubTransformer.java`
C'est la classe principale de transformation.

Elle lit le bytecode de `Target`, puis parcourt les instructions avec ASM.

Son rôle :
- repérer les instructions `IADD`
- afficher leur index dans la classe
- remplacer une occurrence précise de `IADD` par `ISUB` si la mutation est activée
- injecter un appel à `TraceLogger.logMutation(int)` juste après la mutation
- injecter un appel à `TraceLogger.logReturn(int)` juste avant le `IRETURN`

Ensuite, elle écrit deux fichiers `.class` dans `target/asm-mutated/fr/univnantes/asm/` :
- `Target.class` : version **mutée et tracée**
- `TargetOriginal.class` : version **originale tracée**

---

### `TraceLogger.java`
Classe utilitaire pour afficher les traces.

Elle contient deux méthodes statiques :
- `logMutation(int value)` : affiche la valeur juste après la mutation
- `logReturn(int value)` : affiche la valeur retournée

Elle sert uniquement à visualiser ce qui se passe pendant l'exécution des classes instrumentées.

---

### `RunOriginalClass.java`
Lance la classe `Target` normale, sans transformation ASM.

Elle permet d'avoir le comportement de référence du programme d'origine.

---

### `RunMutatedTraceClass.java`
Charge dynamiquement la classe mutée générée dans `target/asm-mutated/.../Target.class`, puis exécute sa méthode `calcul()`.

Elle utilise un `ClassLoader` personnalisé pour définir la classe à partir des octets modifiés.

Cette exécution permet de voir :
- l'effet de la mutation
- les traces affichées pendant l'exécution
- le résultat final

---

### `RunOriginalTraceClass.java`
Fait le même principe que `RunMutatedTraceClass`, mais avec la version **originale tracée** contenue dans `TargetOriginal.class`.

Elle sert à comparer le comportement original instrumenté avec le comportement muté instrumenté.

---

## Ce que montre le projet

Ce projet sert surtout à comprendre concrètement :
- comment lire et modifier du bytecode avec ASM
- comment injecter des appels de méthodes dans le bytecode
- comment charger et exécuter une classe transformée dynamiquement
- comment observer la propagation d'une mutation jusqu'à la valeur retournée

---

## Commandes utiles

### 1. Compiler le projet
```bash
mvn -q test-compile
```

---

### 2. Exécuter la classe originale
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.RunOriginalClass
```

Cela exécute `Target` sans transformation.

---

### 3. Générer les classes transformées avec ASM
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.AsmIaddToIsubTransformer
```

Par défaut, la mutation cible l'index `0`.

Pour cibler un autre index :
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.AsmIaddToIsubTransformer -Dexec.args="1"
```

ou par exemple :
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.AsmIaddToIsubTransformer -Dexec.args="2"
```

Cette commande génère :
- `target/asm-mutated/fr/univnantes/asm/Target.class`
- `target/asm-mutated/fr/univnantes/asm/TargetOriginal.class`

---

### 4. Exécuter la version mutée et tracée
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.RunMutatedTraceClass
```

Cette commande charge la classe mutée depuis le dossier généré et affiche :
- les traces de mutation
- les traces de retour
- le résultat final

---

### 5. Exécuter la version originale tracée
```bash
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=fr.univnantes.asm.RunOriginalTraceClass
```

Cette commande permet de comparer avec la version non mutée, mais instrumentée avec les mêmes traces.
