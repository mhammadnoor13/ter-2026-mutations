# Bytecode — articles retenus

## 1. Efficient Mutation Analysis by Propagating and Partitioning Infected Execution States
**Auteurs** : René Just, Michael D. Ernst, Gordon Fraser  
**Conférence** : ISSTA 2014

### Idée générale
Cet article cherche à rendre l’analyse mutationnelle plus efficace.
L’idée centrale est de ne pas exécuter inutilement tous les tests sur tous les mutants.
Pour cela, les auteurs observent ce qu’il se passe pendant l’exécution du programme non muté instrumenté.

### Ce que les auteurs proposent
Ils s’appuient sur trois étapes importantes :
- **Reachability** : le test atteint la mutation.
- **Infection** : la mutation produit une valeur différente de l’original.
- **Propagation** : cette différence se propage jusqu’à un état plus large, potentiellement observable.

L’article propose trois optimisations :
1. **Détecter les mutants test-equivalent pour un test donné** si l’exécution ne produit même pas d’infection.
2. **Observer la propagation** dans les expressions composées, car une infection locale peut disparaître avant de devenir visible.
3. **Partitionner les mutants** qui produisent le même état infecté, pour éviter des exécutions redondantes.

### Résultat principal
Les auteurs implémentent ces idées dans **Major** et montrent une réduction moyenne du temps d’analyse d’environ **40 %** sur 14 projets open source.

### Ce qu’il faut retenir
Cet article est surtout important parce qu’il formalise une logique très proche de notre sujet :
- la mutation ne suffit pas,
- il faut vérifier si elle infecte,
- puis si cette infection se propage réellement.

### Choses utiles pour notre projet
- Il donne une base théorique solide pour distinguer **couverture**, **infection** et **propagation**.
- Il justifie l’idée d’**instrumenter l’exécution** au lieu de regarder seulement le résultat final kill/survive.
- Il montre qu’une mutation peut produire une différence locale sans que cette différence ne sorte vraiment de l’expression ou de la méthode.
- Pour notre projet, cela soutient directement l’idée de mettre une **trace juste après la mutation** puis une autre **au niveau du retour de méthode**.
- Il peut aussi servir pour expliquer pourquoi certains mutants survivants ne sont pas forcément équivalents : ils peuvent être couverts, parfois même infecter localement, mais ne pas propager assez loin.

---

## 2. Ripples of a Mutation — An Empirical Study of Propagation Effects in Mutation Testing
**Auteurs** : Hang Du, Vijay Krishna Palepu, James A. Jones  
**Conférence** : ICSE 2024

### Idée générale
Cet article étudie empiriquement la manière dont une mutation produit des effets pendant l’exécution.
Les auteurs ne cherchent pas seulement à savoir si un mutant est tué ou survivant.
Ils veulent comprendre le chemin complet entre la mutation et l’échec éventuel du test.

### Ce que les auteurs observent
Pour chaque exécution mutée, ils regardent :
- si la mutation provoque une **infection d’état**,
- si cette infection **se propage** jusqu’à la fin de l’exécution du test,
- si cette propagation est **révélée** par un échec de test.

L’étude est menée à grande échelle sur plus de **1,1 million d’exécutions mutées** et plus de **43 000 mutations**.

### Résultats principaux
Les auteurs montrent que :
- une fois qu’il y a infection, la propagation est souvent observée,
- mais une propagation observée ne mène pas toujours à un échec de test,
- une part importante des mutants survivants pourrait être tuée si les tests observaient mieux les états propagés.

Ils montrent aussi que le comportement dépend du **mutateur** : certains opérateurs infectent facilement mais se révèlent peu, d’autres ont un impact beaucoup plus fort sur l’échec final.

### Ce qu’il faut retenir
Cet article est très utile parce qu’il regarde directement les **mutants survivants** sous l’angle de la propagation.
Il montre qu’un mutant peut survivre non pas parce qu’il ne change rien, mais parce que la différence produite n’est pas assez observée par le test.

### Choses utiles pour notre projet
- Il renforce l’idée que le statut **survived** ne suffit pas pour comprendre un mutant.
- Il montre qu’il faut distinguer plusieurs cas :
  - pas d’infection,
  - infection sans propagation,
  - propagation sans révélation.
- Il soutient très fortement l’ajout d’un indicateur du type **revealability**.
- Il justifie une instrumentation fine pour suivre la différence de comportement entre le point de mutation et la sortie de la méthode.
- Il peut servir à motiver une future extension du projet vers l’ajout automatique d’assertions ou de meilleurs oracles.

---

## Conclusion rapide sur les deux articles
Les deux articles sont complémentaires :
- **Just et al. (2014)** expliquent **comment exploiter infection et propagation** pour mieux analyser les mutants et réduire le coût de l’analyse.
- **Du et al. (2024)** montrent **empiriquement** ce que deviennent réellement les mutants pendant l’exécution, en particulier les survivants.

Ensemble, ils donnent une base très solide pour notre travail au niveau **bytecode** :
- instrumenter le programme,
- observer ce qui se passe juste après la mutation,
- vérifier si la différence se propage jusqu’à la sortie,
- puis mieux comprendre pourquoi un mutant survit.
