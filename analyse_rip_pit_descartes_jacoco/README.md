# Analyse de mutation RIP en Java

Projet de démonstration pour l'analyse de mutation selon le modèle **RIP** (Reachability, Infection, Propagation) à l'aide de PITest, Descartes et JaCoCo.

---

## Table des matières

1. [Le modèle RIP](#1-le-modèle-rip)
2. [Prérequis](#2-prérequis)
3. [Structure du projet](#3-structure-du-projet)
4. [Lancer l'analyse](#4-lancer-lanalyse)
5. [Lire les rapports](#5-lire-les-rapports)
6. [Résultats obtenus sur ce projet](#6-résultats-obtenus-sur-ce-projet)
7. [Corriger les défauts détectés](#7-corriger-les-défauts-détectés)
8. [Rôle de chaque outil](#8-rôle-de-chaque-outil)
9. [Bases de recherche](#9-bases-de-recherche)
10. [Références](#10-références)

---

## 1. Le modèle RIP

Le modèle RIP, formalisé par Howden (1982) et popularisé par Offutt & Untch (2001), décompose la détection d'une faute en trois conditions nécessaires et suffisantes :

```
R — Reachability   : le test doit atteindre la ligne mutée
I — Infection      : l'état du programme doit devenir incorrect
P — Propagation    : l'état incorrect doit être observable en sortie
```

Un test ne détecte une mutation que si **R ∧ I ∧ P** sont tous vérifiés simultanément. L'intérêt de cette décomposition est de diagnostiquer précisément *pourquoi* un test échoue à détecter une mutation plutôt que de se contenter d'un score global.

### Les trois échecs possibles

| Condition échouée | Symptôme dans les tests | Cause typique |
|---|---|---|
| **R ✗** | Ligne jamais exécutée | Code mort, branche non couverte, méthode jamais appelée |
| **I ✗** | État identique entre original et mutant | Entrées neutres (`a == 0`, `a == b`), points fixes de l'opérateur |
| **P ✗** | Assertion ne capture pas la sortie | `assertTrue(true)`, vérification de non-null, pas d'`assertEquals` |
| **Aucun** | Toutes conditions vérifiées, test passe quand même | Mutant équivalent (sémantiquement identique au programme original) |

---

## 2. Prérequis

| Outil | Version minimale | Vérification |
|---|---|---|
| Java JDK | 17 | `java -version` |
| Maven | 3.8 | `mvn -version` |
| Connexion internet | — | Téléchargement des dépendances Maven au premier lancement |

Aucune installation manuelle des plugins n'est nécessaire : Maven télécharge automatiquement PITest, Descartes et JaCoCo depuis Maven Central au premier build.

---

## 3. Structure du projet

```
rip-demo/
├── pom.xml                                        ← configuration Maven (PITest + Descartes + JaCoCo)
├── README.md                                      ← ce fichier
├── rapport-rip.html                               ← rapport interactif pré-généré (ouvrir dans un navigateur)
└── src/
    ├── main/java/com/example/
    │   └── Calculator.java                        ← classe cible (6 méthodes)
    └── test/java/com/example/
        └── CalculatorTest.java                    ← tests de qualité volontairement variée
```

### Calculator.java — les 6 méthodes et leur statut RIP attendu

| Méthode | R | I | P | Statut Descartes attendu | Défaut introduit |
|---|---|---|---|---|---|
| `add(int,int)` | ✓ | ✓ | ✓ | `killed` | Aucun — sert de référence |
| `multiply(int,int)` | ✓ | ✓ | ✗ | `pseudo-tested` | Test sans `assertEquals` |
| `max(int,int)` | ✗ | — | — | `no_coverage` | Aucun test n'appelle la méthode |
| `divide(int,int)` | ✓ | ✗ | ✗ | `survived` | Entrée neutre `(0, 5)` |
| `isPositive(int)` | ✓ | ✓ | ✓ | `killed` | Aucun — sert de référence |
| `factorial(int)` | ✓ | ✓ | ✓ | `killed` | Aucun — sert de référence |

Les défauts dans `multiply`, `max` et `divide` sont **intentionnels** pour illustrer les trois cas d'échec RIP distincts.

---

## 4. Lancer l'analyse

### Commande complète (recommandée)

```bash
mvn clean test jacoco:report org.pitest:pitest-maven:mutationCoverage
```

Cette commande effectue en séquence :
1. Nettoyage du build précédent
2. Compilation et exécution des tests JUnit 5
3. Génération du rapport JaCoCo (diagnostic R)
4. Génération du rapport PITest + Descartes (diagnostic I et P)

### Commandes séparées

```bash
# Étape 1 — tests uniquement
mvn clean test

# Étape 2 — couverture JaCoCo (R)
mvn jacoco:report

# Étape 3 — mutations Descartes (I et P)
mvn org.pitest:pitest-maven:mutationCoverage
```

### Accélérer l'analyse sur un grand projet

```bash
# Augmenter le nombre de threads (défaut : 2)
mvn org.pitest:pitest-maven:mutationCoverage -Dthreads=4

# Cibler une seule classe
mvn org.pitest:pitest-maven:mutationCoverage \
    -DtargetClasses=com.example.Calculator \
    -DtargetTests=com.example.CalculatorTest
```

---

## 5. Lire les rapports

### Rapport JaCoCo — diagnostic R

**Chemin :** `target/site/jacoco/index.html`

| Couleur dans JaCoCo | Diagnostic RIP | Action |
|---|---|---|
| Ligne **verte** | R ✓ — test atteint ce code | Passer à I et P |
| Ligne **rouge** | R ✗ — aucun test n'atteint cette ligne | Ajouter un test d'appel en priorité |
| Losange **jaune** | R partiel — branche manquante | Ajouter un test pour la branche non couverte |

> **Règle :** corriger R avant d'analyser I et P. Un mutant sur une ligne rouge ne peut jamais être tué.

### Rapport PITest + Descartes — diagnostic I et P

**Chemin :** `target/pit-reports/index.html`  
**Données brutes :** `target/pit-reports/mutations.json`

Descartes remplace le **corps entier** de chaque méthode par une valeur constante (`return 0`, `return null`, `return false`, etc.) et observe si les tests détectent le changement. Ce principe, appelé *extreme mutation*, permet de diagnostiquer I et P indépendamment.

| Statut Descartes | R | I | P | Signification | Action |
|---|---|---|---|---|---|
| `killed` | ✓ | ✓ | ✓ | Mutation détectée | Aucune |
| `pseudo-tested` | ✓ | ✓ | ✗ | Méthode appelée, sortie non vérifiée | Ajouter `assertEquals` sur la valeur de retour |
| `survived` | ✓ | ✗ | ✗ | Entrées ne créent pas d'état différent | Choisir des entrées non neutres |
| `no_coverage` | ✗ | — | — | Aucun test n'appelle la méthode | Ajouter un test d'appel (JaCoCo confirme) |
| `partially-tested` | ✓ | ~ | ~ | Certains tests tuent, d'autres non | Compléter les tests existants |

### Exemple d'entrée JSON

```json
{
  "method": "multiply(int,int)",
  "classification": "pseudo-tested",
  "tests_killing": [],
  "tests_covering": ["multiply_appelSansAssertion"],
  "line": 21
}
```

Ce résultat indique : R ✓ (couvert par `multiply_appelSansAssertion`), mais P ✗ (aucun test tueur). L'assertion du test n'observe pas la valeur retournée.

---

## 6. Résultats obtenus sur ce projet

### Scores avant corrections

| Outil | Métrique | Valeur |
|---|---|---|
| Descartes | Score de mutation | 57 % (4/7 méthodes tuées) |
| JaCoCo | Couverture ligne | 78 % (18/23 lignes) |
| JaCoCo | Couverture branche | 70 % (14/20 branches) |

> Le seuil configuré dans `pom.xml` est 60 %. Le build **échoue** avec ce score de 57 %.

### Détail par méthode

```
add(int,int)       → killed        R✓ I✓ P✓  — test de référence
multiply(int,int)  → pseudo-tested R✓ I✓ P✗  — calc.multiply(4,3) sans assertEquals
max(int,int)       → no_coverage   R✗ —  —   — aucun test ne l'appelle
divide(int,int)    → survived      R✓ I✗ P✗  — divide(0,5) : 0/5 == 0*5 == 0
isPositive(int)    → killed        R✓ I✓ P✓  — trois valeurs limites testées
factorial(int)     → killed        R✓ I✓ P✓  — cas 0, 1, 5, négatif
```

---

## 7. Corriger les défauts détectés

### Cas 1 — multiply() : P ✗ → ajouter une assertion

```java
// Avant (pseudo-tested) :
calc.multiply(4, 3);
assertTrue(true);           // P ✗ : n'observe pas le résultat

// Après (killed) :
assertEquals(12, calc.multiply(4, 3));  // P ✓
assertEquals( 0, calc.multiply(5,  0));
assertEquals(-6, calc.multiply(-2, 3));
```

**Règle P :** utiliser `assertEquals(valeurAttendue, appelDeMéthode())`. Les assertions de type `assertNotNull`, `assertTrue(x > 0)` ou `assertTrue(true)` sont insuffisantes — elles ne capturent pas les mutations sur la valeur retournée.

### Cas 2 — max() : R ✗ → ajouter des tests d'appel

```java
@Test void max_premierPlusGrand()  { assertEquals(5, calc.max(5, 3)); } // branche a > b
@Test void max_deuxiemePlusGrand() { assertEquals(7, calc.max(2, 7)); } // branche else
@Test void max_egaux()             { assertEquals(4, calc.max(4, 4)); } // distingue > de >=
```

**Règle R :** vérifier JaCoCo en premier. Toute ligne rouge doit recevoir un test avant d'analyser I et P. Le cas `max(4, 4)` est particulièrement important : il distingue l'opérateur `>` de `>=` (mutation ROR).

### Cas 3 — divide() : I ✗ → changer les entrées

```java
// Avant (survived) — entrée neutre :
assertEquals(0, calc.divide(0, 5));   // 0/5 == 0*5 == 0  → I ✗

// Après (killed) — entrées non neutres :
assertEquals(3, calc.divide(6, 2));   // 6/2=3 ≠ 6*2=12   → I ✓
assertEquals(2, calc.divide(7, 3));   // 7/3=2 ≠ 7*3=21   → I ✓
```

**Règle I :** éviter les entrées qui sont des points fixes de la mutation. Pour une mutation AOR `a/b → a*b`, tout appel avec `a == 0` donne le même résultat (0) dans les deux versions. Choisir `a ≠ 0` et `a/b ≠ a*b`.

### Score prévu après corrections

| Méthode | Avant | Après |
|---|---|---|
| `add` | killed | killed |
| `multiply` | pseudo-tested | **killed** (P corrigé) |
| `max` | no_coverage | **killed** (R corrigé) |
| `divide` | survived | **killed** (I corrigé) |
| `isPositive` | killed | killed |
| `factorial` | killed | killed |

Score final : **100 %** (7/7 méthodes tuées)

---

## 8. Rôle de chaque outil

### JaCoCo 0.8.12 — diagnostic R

JaCoCo instrumente le bytecode à l'exécution (via un agent Java) et enregistre quelles lignes et branches ont été parcourues. Il répond à la question : *le test atteint-il le code muté ?*

JaCoCo seul ne dit pas si I ou P sont satisfaits — il fournit uniquement la condition préalable à toute analyse de mutation.

### PITest (PIT) 1.15.3 — moteur de mutation

PITest est le moteur qui génère et injecte les mutants dans le bytecode compilé, puis exécute les tests contre chaque mutant. Sans plugin Descartes, il utilise ses propres opérateurs classiques (AOR, ROR, COR, etc.) et produit un score de mutation standard.

PITest seul ne distingue pas les cas R ✗ / I ✗ / P ✗ : il indique seulement `survived` ou `killed`.

### Descartes 1.3.3 — diagnostic I et P (extreme mutation)

Descartes est un moteur de mutation alternatif pour PITest qui implémente l'*extreme mutation testing* (Niedermayr et al., 2016). Au lieu de modifier un opérateur à la fois, il remplace le **corps entier** d'une méthode par une valeur constante :

```
void    → supprime le corps
null    → return null
0       → return 0
true    → return true
false   → return false
```

Cette approche produit des mutants beaucoup plus forts que les mutations classiques. Si un tel mutant survit, cela signifie qu'aucun test n'observe réellement ce que fait la méthode — ce qui correspond précisément à un échec de I ou P.

Le statut `pseudo-tested` introduit par Descartes n'existe pas dans PITest standard : c'est sa contribution principale pour le diagnostic RIP.

### Synthèse de la complémentarité

```
JaCoCo  →  répond à R  : "le test passe-t-il par ici ?"
PITest  →  génère les mutants et exécute les tests contre eux
Descartes →  répond à I et P : "le test observe-t-il vraiment ce que fait la méthode ?"
```

---

## 9. Bases de recherche

Les résultats et la configuration de ce projet s'appuient sur les travaux suivants.

### Fondements théoriques du modèle RIP

**Howden, W. E. (1982).** *Weak mutation testing and completeness of test sets.*  
IEEE Transactions on Software Engineering, 8(4), 371–379.  
→ Première formalisation des trois conditions R, I, P. Howden introduit la notion de *weak mutation* (vérifier I sans exiger P) par opposition à la *strong mutation* (R ∧ I ∧ P).

**Offutt, A. J., & Untch, R. H. (2001).** *Mutation 2000: Uniting the orthogonal.*  
In Mutation Testing for the New Century (pp. 34–44). Springer.  
→ Synthèse qui unifie les différentes formulations du modèle et établit RIP comme cadre standard pour raisonner sur la détectabilité des mutations.

### Extreme mutation testing et Descartes

**Niedermayr, R., Jürgens, E., & Wagner, S. (2016).** *Will my tests tell me if I break this code?*  
Proceedings of the International Workshop on Continuous Software Evolution and Delivery (CSED), 23–29.  
→ Introduit l'*extreme mutation* : remplacer le corps entier d'une méthode par une constante. Montre qu'un pourcentage significatif de méthodes dans des projets industriels sont *pseudo-testées* (appelées sans que leur sortie soit vérifiée).

**Vera-Pérez, O. L., Danglot, B., Monperrus, M., & Baudry, B. (2018).** *A comprehensive study of pseudo-tested methods.*  
Empirical Software Engineering, 24(3), 1484–1520.  
→ Étude empirique sur 10 projets open source Java. Résultat principal : 25 à 40 % des méthodes couvertes sont pseudo-testées. Fournit la taxonomie `killed / pseudo-tested / partially-tested / no_coverage` implémentée dans Descartes.

**Vera-Pérez, O. L., Monperrus, M., & Baudry, B. (2019).** *Descartes: A PITest engine to detect pseudo-tested methods.*  
Proceedings of ASE 2019 (Tool Demonstrations), 22–25.  
→ Article de présentation de l'outil Descartes. Décrit l'implémentation des opérateurs d'extreme mutation comme plugin PITest et leur interprétation en termes de conditions RIP.

### PITest et test de mutation en pratique

**Coles, H., Laurent, T., Henard, C., Papadakis, M., & Ventresque, A. (2016).** *PIT: A practical mutation testing tool for Java.*  
Proceedings of ISSTA 2016, 449–452.  
→ Article de référence sur PITest. Présente les optimisations (bytecode mutation, test ordering, early exit) qui rendent le test de mutation utilisable en intégration continue.

**Papadakis, M., Kintis, M., Zhang, J., Jia, Y., Le Traon, Y., & Harman, M. (2019).** *Mutation testing advances: An analysis and survey.*  
Advances in Computers, 112, 275–378.  
→ Survey complet sur l'état de l'art du test de mutation. Couvre les opérateurs de mutation, les techniques de réduction du coût, et les outils disponibles pour Java, C, Python.

### JaCoCo et couverture de code

**Aho, P., Suominen, A., Räty, T., Rautiainen, A., & Sakkinen, M. (2009).** *EMMA and JaCoCo for Java Code Coverage Analysis.*  
→ Comparaison des outils de couverture. JaCoCo est retenu pour sa précision sur les branches et sa compatibilité avec les agents Java modernes.

---

## 10. Références

```
[1] Howden (1982)         — IEEE TSE 8(4) : formalisation de RIP
[2] Offutt & Untch (2001) — Mutation 2000 : unification du modèle
[3] Niedermayr et al. (2016) — CSED : extreme mutation testing
[4] Vera-Pérez et al. (2018) — EMSE 24(3) : étude des pseudo-tested methods
[5] Vera-Pérez et al. (2019) — ASE : outil Descartes
[6] Coles et al. (2016)   — ISSTA : PITest en pratique
[7] Papadakis et al. (2019) — Advances in Computers : survey mutation testing
```

### Liens officiels des outils

- PITest : https://pitest.org
- Descartes : https://github.com/STAMP-project/pitest-descartes
- JaCoCo : https://www.jacoco.org/jacoco
- JUnit 5 : https://junit.org/junit5

---

## Licence

Projet de démonstration pédagogique — libre d'utilisation et de modification.
