# Mutation Propagation Analysis — Project Structure

This README is written in **two languages**.
If you want the **French** version, please scroll down.

---

# English

## Overview

This project is organized into two main parts:

* **core**: contains the analysis logic
* **experiment**: contains classes used for experimentation

This separation helps keep the analysis code clean and allows running experiments independently.

---

## Structure

```
core/
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

Ce projet est organisé en deux parties principales :

* **core** : contient la logique d’analyse
* **experiment** : contient les classes utilisées pour les expériences

Cette séparation permet de garder le code propre et d’organiser les expériences.

---

## Structure

```
core/
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

Ces fichiers sont générés automatiquement.

---

### logs

Contient les résultats des expériences :

* logs d’élimination
* observations
* données pour l’analyse
