- Prise en main projet TER-2025-mutations

documents/

Ce dossier contient l’ensemble du travail théorique et bibliographique du projet. On y trouve des articles scientifiques, des notes de lecture et des documents de synthèse liés au test par mutation, au modèle RIPR et aux travaux de Vera-Perez.

code/

Ce dossier regroupe toutes les expérimentations techniques menées dans le cadre du projet.

code/Basic_planner/

Ce dossier correspond au programme de référence non muté. Il sert de base de comparaison pour l’ensemble des expériences. Les tests unitaires y sont exécutés afin d’observer le comportement normal du programme.

code/Basic_planner_mutated/

Ce dossier contient une version mutée du programme qui est utilisée pour comparer son comportement avec celui de la version originale. L’objectif est d’identifier des différences de comportement entre les deux versions afin de détecter l’état Infection du modèle RIPR.

code/Mutation_matrix/

Ce dossier regroupe les éléments liés à l’analyse de mutation avec PIT (ex : l’export de la matrice de mutation). Il permet d’identifier quels tests couvrent quelles mutations (Reachability) et quels mutants sont tués ou survivent.

code/ASM/

Ce dossier correspond à la piste expérimentale principale du projet. Il contient un prototype d’instrumentation du bytecode Java à l’aide de la bibliothèque ASM. L’objectif est de tracer finement l’état interne du programme (variables, champs, valeurs de retour) afin de comparer les exécutions du programme original et du programme muté, et ainsi détecter l’état Infection.

code/Btrace/

Ce dossier contient une tentative d’utilisation de l’outil BTrace pour observer dynamiquement l’exécution du programme. Cette piste a été explorée mais s’est révélée insuffisante, notamment en raison de l’impossibilité de tracer les variables locales. Il s’agit d’un résultat négatif documenté.

code/compare_tests.sh

Ce script shell est utilisé pour automatiser l’exécution des tests sur les versions originale et mutée du programme et comparer leurs sorties.

Fichiers de résultats (*.txt)

Les fichiers tels que test_original_output.txt, test_mutated_output.txt, diff_results.txt ou filtered_*.txt correspondent à des traces expérimentales (sorties de tests, comparaisons, différences observées).
