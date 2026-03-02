# TER-2025-mutations

Par Cyril ALEXANDRE, Maël ANGOT, Lilian DELHOMMEAU, Quentin TEGNY et Rémy RIOLÉ

## Description

Projet de recherche du parcours ALMA du master informatique.
Projet portant sur l'analyse de mutations en test logiciel.

## Les codes présents

- `Mutation_matrix` : le code de l'analyse de matrice de mutation. Langage : Python

## Utilisation

Pour utiliser le script `matrix_analysis.py`, il suffit de lancer la commande suivante :

```bash
python matrix_analysis.py <xml-file> <json-output>
```

Exemple :

```bash
python matrix_analysis.py ./mutations.xml ./mutations.json
```

Lancer le serveur :

```bash
python site.py mutations.xml mutations.json 
```


Script run_mutation_matrix.sh pour un projet passer en paramètre:

* lance les tests et l’analyse de mutation avec PIT 
* extrait le fichier mutations.xml 
* génère la matrice de mutation
* démarre le serveur pour visualiser le site

exemple : 
```bash
bash  run_mutation_matrix.sh ../../base_experimentale/Basic_planner/
```