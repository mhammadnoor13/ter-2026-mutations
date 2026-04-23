# Mutation Propagation Analysis — Project Structure

This README is written in **two languages**.
If you want the **French** version, please scroll down.

---

# English

## Overview

This project is organized into three main parts:

* **core**: contains the analysis logic
* **experiment**: contains classes used for experimentation
* **integration**: connects external tools (PIT) with the analysis pipeline

This separation helps keep the analysis code clean and allows running experiments independently.

---

## Structure

```
core/
experiment/
integration/
 ├── mapping/
 └── pit/
experiment/
 ├── subjects/
 ├── instrumented/
 └── logs/
```

---

## core

The **core** package contains the main components of the project:

* `MutationAnalyser` — responsible for instrumentation and snapshots
* `PropagationAnalyzer` — responsible for propagation and elimination analysis

This package contains only reusable analysis logic.

---

## integration

The **integration** package connects external tools with the system.

### pit

Provides a lightweight integration with PIT to generate mutants from `mutations.xml`.

Contains:

* `PitParser` — extracts mutations from PIT reports
* `PitMutation` — represents mutation metadata
* `MutantGenerator` — generates mutated classes (source-level approximation)
* `PitRunner` — entry point to generate mutants

This module does **not replace PIT**, but enables using its results for propagation analysis.
### mapping

Contains the logic used to translate PIT mutation types into source-level code changes.

For example:

- conditional boundary changes (`>` → `>=`)
- arithmetic changes (`+` → `-`)
- conditional negation

This mapping is an approximation and does not cover all PIT mutators.


---

## experiment

The **experiment** package is used to run experiments and observe propagation behavior.

It contains three folders:

### subjects

Contains the original classes used for experiments.

These classes are:

* manually written
* used as input for instrumentation
* used to test propagation behavior

---

### instrumented

Contains the generated classes after instrumentation.

These include:

* Mutable version
* Observee version
* Generated mutants (from PIT integration)

These files are automatically generated and should not be manually edited.

---

### logs

Contains experiment outputs.

Examples:

* elimination logs
* propagation observations
* dataset for analysis

---

# Français

## Vue générale

Ce projet est organisé en trois parties principales :

* **core** : contient la logique d’analyse
* **experiment** : contient les classes utilisées pour les expériences
* **integration** : permet de connecter des outils externes (comme PIT)

Cette séparation permet de garder le code propre et d’organiser les expériences.

---

## Structure

```
core/
experiment/
integration/
 ├── mapping/
 └── pit/
experiment/
 ├── subjects/
 ├── instrumented/
 └── logs/
```

---

## core

Le package **core** contient :

* `MutationAnalyser` — instrumentation et snapshots
* `PropagationAnalyzer` — analyse de propagation

Ce package contient uniquement la logique principale.

---

## integration

Le package **integration** permet d’intégrer des outils externes.

### pit

Fournit une intégration légère avec PIT pour générer des mutants à partir de `mutations.xml`.

Contient :

* `PitParser` — extraction des mutations depuis le XML
* `PitMutation` — représentation des mutations
* `MutantGenerator` — génération de classes mutées (approximation au niveau source)
* `PitRunner` — point d’entrée pour générer les mutants

Ce module **ne remplace pas PIT**, mais permet d’exploiter ses résultats pour l’analyse de propagation.
### mapping

Contient la logique utilisée pour traduire les types de mutations PIT en modifications au niveau source.

Par exemple :

- changement de bornes conditionnelles (`>` → `>=`)
- changement d’opérations arithmétiques (`+` → `-`)
- négation de conditions

Ce mapping est une approximation et ne couvre pas tous les mutateurs PIT.

---

## experiment

Le package **experiment** est utilisé pour les expériences.

Il contient trois dossiers.

### subjects

Contient les classes originales utilisées pour les tests.

Ces classes sont :

* écrites manuellement
* utilisées pour l’instrumentation
* utilisées pour analyser la propagation

---

### instrumented

Contient les classes générées après instrumentation :

* version Mutable
* version Observee
* mutants générés via PIT

Ces fichiers sont générés automatiquement.

---

### logs

Contient les résultats des expériences :

* logs d’élimination
* observations
* données pour l’analyse
