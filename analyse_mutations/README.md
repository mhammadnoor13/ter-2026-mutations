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