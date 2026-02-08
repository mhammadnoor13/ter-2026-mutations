02 février 2026


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


- Exécution des tests sur la version originale

    - mvn test sur Basic_planner

22 tests ont été lancés, sans aucune erreur ni échec.
Le plugin PIT génére une analyse de mutation complète. PIT a produit 207 mutants, 127 ont été tués, pour un score de mutation global de 61 % et une force de test estimée à 82 %. 
Ces résultats montrent que même si la couverture est correcte plusieurs mutants survivent et ils seraient intéressants pour une analyse plus précise via le modèle RIPR.

- Exécution des tests sur la version mutée

    - mvn test sur Basic_planner_mutated

Première tentative a échoué lors de la compilation car il y avait une incohérence entre le nom de la classe publique (BasicPlannerImpl) et le nom du fichier source (BasicPlannerImplMutated.java).
Après correction, la compilation s’est déroulée correctement, mais l’exécution des tests a donner plusieurs échecs :

-> des tests liés à la récupération du rendez-vous courant (testSeeCurrentMeetingCurrently)

-> des tests portant sur le comportement muté d’une méthode spécifique (MutationTest)

-> des tests de gestion des conflits lors de l’ajout de réunions (TestAddMeeting)

Ces échecs indiquent que la mutation introduite modifie effectivement le comportement du programme de manière observable par les oracles de test. Les mutants correspondants sont donc tués (Propagation et de Revealability).

- script compare_tests.sh
Permet de comparer automatiquement les sorties des tests entre la version originale et la version mutée.

Le script détecte des différences explicites dans les traces d’exécution, notamment sur l’évolution de la variable interne j :

version originale : j = 0, retour (true, 0)

version mutée : j = -1, retour (false, -1)

Ces différences confirment que la mutation provoque une infection de l’état interne du programme (valeurs observables).
La comparaison original / muté est donc validée comme utile pour l’analyse du RIPR en sutrtout pour l’identification de l’état Infect.

- Objectifs

Objectif du ter de 2025 -> explorer la faisabilité d’une analyse du test par mutation à l’aide de PIT et du modèle RIPR. 
Identification des limites des outils existants (en particulier état Infect) / exploration de pistes techniques permettant de comparer le comportement d’un programme original et muté

Objectif du ter de 2026 -> structurer et exploiter les résultats de2025 afin d’aider le testeur avec les mutants survivants. 
Analyser des cas ciblés de mutants selon le modèle RIPR / identifier précisément pourquoi ils survivent (R, I ou P) / essayer d'améliorer la méthodo des tests unitaires

- Idée de workflow :

1. Lancer PIT
2. Identifier un mutant survivant
3. Identifier les tests qui le couvrent
4. Exécuter ces tests sur original et muté
5. Observer :
    - sortie
    - état interne minimal
6. Classer le problème selon RIPR
7. Proposer une amélioration de test

- Observation d’une mutation

Lors de la prise en main du projet, j’ai observé une mutation appliquée dans le code du Basic Planner sur une variable interne j.
Dans la version originale, la variable est incrémentée selon l’instruction j=j+1. Dans la version mutée, cette instruction est remplacée par j=j*2+1

Cette mutation correspond à une modification locale et volontaire du comportement du programme afin de simuler une erreur réaliste de développement. Elle n’entraîne pas nécessairement un plantage du programme mais peut produire des valeurs différentes selon les données d’entrée.

L’observation des exécutions montre que cette mutation peut dans certains cas modifier l’état interne du programme (le I infection), et dans d’autres cas rester invisible pour les tests existants. 
On observe donc l’importance du modèle RIPR pour analyser précisément pourquoi certains mutants sont détectés et d’autres non.


- Prise en main de la matrice de mutation

Lancement de l’app Web de visualisation de la matrice de mutation 

L’interface Web affiche, pour chaque mutant :

1. la classe mutée
2. la méthode concernée
3. la ligne de code affectée
4. le type de mutateur appliqué
5. le nombre total de mutations associées

Chaque mutant correspond à une entrée distincte dans la matrice, représentant une modification précise du programme.

Pour chaque mutant, l’outil indique explicitement l’état du modèle RIP :

     - Reachability: si la ligne mutée est atteinte par au moins un test

     - Infectio: si la mutation provoque une modification de l’état interne du programme

     - Propagation : si cette modification se propage jusqu’à un oracle de test
Ces informations permettent d’analyser finement pourquoi un mutant est tué ou survit

Analyse des tests :

     - les tests qui atteignent la ligne mutée (partie "Tests couverts")

     - les tests qui passent malgré la mutation (partie "Tests passés")

     - les tests qui détectent la mutation et tuent le mutant (partie "Tests qui ont tués des mutants")


Apport pour le projet de recherche

Le fait de pouvoir voir de cette facon la matrice facilite la compréhension du comportement des tests face aux mutants.
La matrice permet d’identifier des faiblesses dans certains tests surtout au niveau de l’oracle.
Cette app permet de comprendre l’intérêt du modèle RIPR pour analyser et améliorer la qualité des suites de tests dans le cadre du test par mutation.


        
08 février 2026

Architecture du git avec des dossier pertinent pour essayer d'avoir une bonne base du git de recherche.