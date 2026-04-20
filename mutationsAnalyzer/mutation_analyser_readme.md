# TER-2026-mutations --- Mutation Analyser

Par Baptiste Girard

## Description

Cet outil permet de transformer un fichier `.java` en une version
**mutable**, afin de faciliter l'injection et le suivi de **mutations
dans le code source**.\
Il fournit également des mécanismes d'observation permettant d'analyser
le comportement des mutants pendant l'exécution des tests.

## Utilisation

### Génération d'une classe prête à être mutée

La classe `core.MutationAnalyser` possède une méthode `main` exécutable qui
demande en entrée une classe `.java`.\
Cette exécution génère deux nouvelles classes :

-   une **classe mutable**, prête à recevoir des mutations,
-   une **classe d'analyse**, qui est une copie de la classe originale
    enrichie de **sondes d'observation**.

### Injection de mutations dans le code source

Pour injecter une mutation :

1.  Ouvrir le fichier `.java` généré dont le nom se termine par
    **`Mutable`**.
2.  Modifier le code aux emplacements où vous souhaitez introduire une
    mutation.
3.  Avant chaque mutant, ajouter l'appel suivant :

``` java
core.MutationAnalyser.getInstance().addMutation("nom_de_la_mutation");
```

Cet appel permet d'enregistrer et d'identifier la mutation appliquée.

### Exécution et analyse avec les tests

Dans vos tests **JUnit**, vous pouvez utiliser `core.MutationAnalyser` pour
observer l'évolution des mutants durant l'exécution.

Pour afficher les snapshots d'analyse, ajoutez la ligne suivante **avant
vos assertions** :

``` java
core.MutationAnalyser.getInstance().showSnapshotsInPopup();
```

### Conseil

Il est recommandé de réinitialiser `core.MutationAnalyser` avant chaque test
afin d'éviter toute interférence entre les cas de test :

``` java
core.MutationAnalyser.getInstance().reset();
```

Cette instruction peut être placée dans la méthode `setUp()` de vos
tests.
