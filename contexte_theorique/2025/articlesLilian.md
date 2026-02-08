
# INFORMATIONS GÉNÉRALES

- **Nom de l'article** : PIT a practical mutation testing tool for Java
- **Auteur** : Henry Coles, Thomas Laurent, Anthony Ventresque, Christopher Henard, Mike Papadakis
- **Date de publication** : 01-01-2016 
- **Lien** : [https://scholar.google.com/citations?view_op=view_citation&hl=fr&user=4O3EolUAAAAJ&citation_for_view=4O3EolUAAAAJ:lbI08cpqPnQC](https://scholar.google.com/citations?view_op=view_citation&hl=fr&user=4O3EolUAAAAJ&citation_for_view=4O3EolUAAAAJ:lbI08cpqPnQC)  
- **Analysé par** : Lilian DELHOMMEAU  


# ANALYSE DE L'ARTICLE

Résumé : L'article couvre plusieurs sujets. Tout d'abord il débute par rappeler en quoi il est essentiel de tester les test en informatique, et que pour cela, la notion de "code coverage" est très utile mais pas suffisamment précise pour tester la qualité des tests car elle ne prends en compte que la quantité de code tester et non la qualité des tests. C'est pourquoi il présente ensuite l'analyse de mutation et le test par mutation (qui utilise l'analyse de mutation), 
qu'il présente en détail par la suite, puis il présente l'outil PIT pour le Mutation testing, pour finalement présenter une évaluation préliminaire avant de conclure l'article.


## Analyse : 
L'analyse de mutation créer plusieurs variantes d'un programme appelé mutants. 
Ces mutants sont créés selon des règles syntaxiques simple appelé "mutant operators". (qui transforme les a + b en a - b ou les c < d en c <= d par exemple)
Quand un test arrive à repérè un mutant et à l'exposé, alors on dit que le mutant est "tué", dans le cas contraire qu'il "vit".

## Avantages de PIT:
Supporte Ant et Maven
Scalable
PIT est Opensource, bien intégré avec les outils de développement (offre un plugin Maven), est robuste et maintenu activement, et opère sur la dernière version de Java.
Il est rapide : travail sur le bytecode au lieu du source code, il sélectionne les tests à run contre les mutants et minimise le nombre d'éxécutions de mutant.

## Caractéristiques de PIT:
PIT génère des mutants via des mutations du bytecode ramenant presque le coût des ses opérations à 0 (car la manipulation du bytecode ne coute presque rien).
La représentation en bytecode des mutants est gardé en mémoire (à savoir qu'un seul mutant est retenu en mémoire à la fois).
La génération des mutants se présente en 2 étapes : 
- Un scan du code est effectué, toutes les classes testé sont examiné et des premiers points de mutation (où il pourrait être intéressant de créer des mutants) sont stocké en mémoire sous le nom de MutationIdentifiers (c'est  d'ailleurs la seule chose stockée lors de cette étape).
Les MutationIdentifers contiennent la localisation précise de la mutation et le nom de l'opérateur de mutation utilisé.
- La création de processus JVM enfant pour évaluer chaque mutant en exécutant des tests contre lui. 

Il est également expliqué que la liste des opérateurs mutant était limité notamment sur les opérateur relationnel ( < ne pouvait muté qu'en <= par exemple)
pour limité le nombre de mutants créé
!! à voir ce qu'il en est aujourd'hui dans les nouvelles versions de PIT cependant, car l'article date de 2016 !!

