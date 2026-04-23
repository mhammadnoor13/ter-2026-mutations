# Integration Layer (PIT-based Mutant Generation)

This package provides a lightweight integration with PIT to generate source-level mutants for propagation analysis.

> 🇫🇷 Une version française est disponible ci-dessous.

---

## Overview

Instead of rebuilding PIT, this module **leverages PIT outputs** (`mutations.xml`) and reconstructs a subset of mutants at the source level.
These mutants are then used by the rest of the system for **instrumentation and propagation analysis**.

---

## Components

* **PitParser**
  Parses PIT XML reports and extracts relevant mutations (typically *SURVIVED*).

* **PitMutation**
  Data model representing a mutation (class, line, mutator, description).

* **MutantGenerator**
  Recreates mutated `.java` classes by applying approximated transformations and ensuring:

    * unique class names
    * correct package (`experiment.instrumented`)
    * consistency between file and class name

* **PitRunner**
  Entry point that:

    * reads PIT results
    * generates corresponding mutant classes

---

## Mapping Strategy

Mutations are approximated at the source level using simple mappings (e.g., boundary changes, arithmetic changes).
This is **not a full reproduction of PIT**, but a controlled approximation sufficient for analysis.

---

## Purpose

This layer bridges:

PIT (mutation generation) → Generated source mutants → Propagation Analyzer

The goal is **not to replace PIT**, but to enable deeper analysis of *why mutants survive*.

---

---

# 🇫🇷 Version française

## Vue d’ensemble

Ce module propose une intégration légère avec PIT pour générer des mutants au niveau source à partir du fichier `mutations.xml`.

L’objectif n’est pas de reproduire PIT, mais de **reconstruire certains mutants** afin de permettre l’analyse de propagation.

---

## Composants

* **PitParser**
  Analyse le fichier XML de PIT et extrait les mutations pertinentes (*SURVIVED*).

* **PitMutation**
  Représente une mutation (classe, ligne, mutateur, description).

* **MutantGenerator**
  Génère des classes mutées en :

    * appliquant une transformation approximative
    * garantissant des noms uniques
    * corrigeant le package (`experiment.instrumented`)
    * assurant la cohérence entre le nom du fichier et celui de la classe

* **PitRunner**
  Point d’entrée qui :

    * lit les résultats de PIT
    * génère les classes mutées correspondantes

---

## Stratégie de mapping

Les mutations sont approximées au niveau source (conditions, opérations, etc.).
Ce n’est pas une reproduction exacte de PIT, mais une approximation contrôlée suffisante pour l’analyse.

---

## Objectif

Ce module sert de pont entre :

PIT → Mutants → Analyse de propagation

Le but est d’expliquer **pourquoi certains mutants survivent**, et non de remplacer PIT.
