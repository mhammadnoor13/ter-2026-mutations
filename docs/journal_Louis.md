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

- Lire le code source PIT 
- Identifier la partie mutateurs

*Architecture simplifié de PIT :*

1. Analyse des classes
2. Génération des mutants (via ASM)
3. Création d’une nouvelle version bytecode
4. Sélection des tests pertinents
5. Exécution isolée
6. Rapport

*Flux simplifié d'un mutant :*

GregorMutationEngine -> Mutater -> ASM ClassVisitor -> Injection mutation

(Gregor est le moteur principal de mutation)

- On observe de nombreux mutateurs comme ConditionalsBoundaryMutator, MathMutator ou VoidMethodCallMutator. Ils implémentent tous MethodMutatorFactory.
- PIT ne modifie pas le code source, il modifie les instructions bytecode via ASM. Chaque mutateur retourne un MethodVisitor personnalisé via l'import org.objectweb.asm.
- PIT calcule d’abord quelles lignes sont couvertes par quels tests. Ensuite pour chaque mutant, il ne lance que les tests qui couvrent cette zone avec CoverageDatabase.java


*Observation de GregorMutationEngine et de MathMutator*


* GregorMutationEngine

  - deux variables : mutationOperators: l’ensemble des mutateurs activés ET methodFilter: un filtre pour dire quelles méthodes sont candidates à mutation
  - permet de fabriquer des mutateurs, méthode create permet de retourner un GregorMutater
  - "Orchestre" et configure mais il n'y a pas de bytecode dans cette classe (pas d'utilisation d'ASM)


    GregorMutater :
    classe qui implement Mutater
    methode findMutation : parcourir la classe et lister tous les mutants possibles sans forcément en appliquer un.
    methode getMutation : produire les octets .class mutés pour un mutant donné

* MathMutator

  - Implémente MethodMutatorFactory 
  - Methode create : donne un MathMethodVisitor ASM (c’est la mutation en action)
    - Implémente la classe MathMethodVisitor :
       - classe héritée de AbstractInsnMutator
       - regarder les opcodes
       - si opcode ∈ mutations possibles, alors créer un mutant
       - si ce mutant est sélectionné, écrire l’opcode remplacé dans le flux ASM
  

        Exemple : 
        MUTATIONS.put(Opcodes.IADD, new InsnSubstitution(Opcodes.ISUB,
        "Replaced integer addition with subtraction"));

        si PIT rencontre IADD (addition int) ,il peut créer un mutant qui remplace par ISUB


        - AbstractInsnMutator 
            -> ASM appelle visitInsn pour les instructions JVM sans opérandes (IADD, ISUB, RETURN…).
            -> Création du mutant = registerMutation(...)
            -> Décision d’appliquer ou pas = shouldMutate(newId)


        - InsnSubstitution 
            -> C’est la mutation “physique” du bytecode. on applique la mutation


Résumé de l'étude des classes : PIT liste d’abord les mutants en visitant le bytecode avec ASM (mode collecte), puis génère le bytecode d’un mutant ciblé en réappliquant la visite avec un ClassWriter. La décision d’appliquer la mutation se fait via MutationContext.shouldMutate, et l’injection réelle se fait dans les MethodVisitor (ex: AbstractInsnMutator + InsnSubstitution).


- Rôle de MutatingClassVisitor

  - C’est un ClassVisitor ASM qui sert à :
     - enregistrer des infos de classe
     - décider quelles méthodes seront mutées (via filter)
     - construire une chaîne de MethodVisitor : d’abord les mutateurs (ex: MathMutator) puis des “décorateurs” PIT (line tracker, instruction tracker, block tracker)

C’est l'orchestrateur ASM pour une classe.

Pipeline PIT :
1. GregorMutationEngine fournit la liste des mutateurs + filtre et crée GregorMutater
2. GregorMutater.findMutations visite la classe avec ASM + MutatingClassVisitor et enregistre tous les mutants (registerMutation)
3. GregorMutater.getMutation(id) revisite la classe et quand shouldMutate(id) est vrai, le mutateur applique la transformation ASM (ex: InsnSubstitution)

- étude de MethodMutationContext.java
  - construire des MutationIdentifier uniques
  - produire les MutationDetails (fichier, description, ligne, bloc)
  - déléguer au ClassContext la collecte et la sélection (shouldMutate) 
- étude de ClassContexte.java
  -  contexte global de la classe pendant la visite ASM :
     - ClassInfo classInfo : infos de la classe (nom interne, access, super…)
     - String sourceFile : fichier source (pour le rapport)
     - Optional<MutationIdentifier> target : le mutant ciblé 
     - List<MutationDetails> mutations : tous les mutants collectés pendant la visite
     - ConcreteBlockCounter blockCounter : tracking des blocs (CFG-ish)


1. MutatingClassVisitor visite chaque méthode, construit MethodMutationContext, et empile les MethodVisitor des mutateurs.
2. Chaque mutateur (ex MathMutator) intercepte des opcodes via AbstractInsnMutator.visitInsn.
3. Quand un opcode est mutable, le mutateur appelle MethodMutationContext.registerMutation, qui :
   - fabrique un MutationIdentifier = (Location, instructionIndex, mutatorId)
   - construit un MutationDetails (fichier, ligne, bloc, description)
   - et l’ajoute au ClassContext.
4. L’application réelle dépend de ClassContext.shouldMutate :
   - si target est vide → collecte seulement 
   - si target = id → applique uniquement le mutant qui “match” cet id (via matches)



*Implémentation d'un exemple*

- Comprendre comment modifier du bytecode avec ASM
- Reproduire un mutateur simple similaire à ceux de PIT
- Générer et exécuter un mutant sans modifier la classe originale

-> Lecture d’une classe compilée (Target.class) sous forme de tableau d’octets.

-> Utilisation de ASM (ClassReader, ClassVisitor, MethodVisitor) pour parcourir le bytecode.

-> Interception des instructions JVM via visitInsn(int opcode).

-> Remplacement de l’instruction IADD par ISUB (mutation arithmétique simple).

-> Génération d’un nouveau fichier .class muté dans un dossier séparé (target/asm-mutated).

-> Chargement dynamique de cette classe mutée via un ClassLoader personnalisé.

-> Invocation de la méthode modifiée par réflexion pour vérifier le comportement.

- Résultat observé

Classe originale : 10 + 3 = 13

Classe mutée : 10 - 3 = 7

La mutation au niveau bytecode est donc effectivement prise en compte à l’exécution.

- Lien avec PIT

Cette expérimentation reproduit le principe fondamental de PIT :

-> Les mutations sont réalisées au niveau bytecode, pas au niveau source.

-> Le .class original n’est jamais modifié.

-> Un mutant est une version alternative temporaire du programme.

-> Le mutant est chargé dynamiquement et exécuté de manière isolée.

Ce prototype correspond à une version simplifiée du fonctionnement interne de PIT, sans la gestion des identifiants de mutation ni la sélection des tests.


*Visualisation du P*

Aujourd’hui, j’ai commencé l’étude de la phase P (Propagation) du modèle RIPR en travaillant sur un seul opérateur mutant : IADD -> ISUB. J’ai gardé mon prototype ASM capable de muter une instruction précise par index, puis j’ai ajouté un arrangement du bytecode pour insérer des traces d’exécution.

J’ai créé une classe TraceLogger et j’ai modifié le transformer ASM pour tracer :
- la valeur produite juste après l’instruction mutée
- la valeur retournée par la méthode

L’objectif est de comparer ce qui se passe localement au point de mutation et ce qui est finalement observable à la sortie. Cela permet de voir si l’infection introduite par le mutant se propage réellement jusqu’au résultat final.

Cette étape est utile pour visualiser la propagation car :
- si la valeur mutée et la valeur retournée sont toutes les deux différentes, alors la propagation a réussi 
- si la valeur mutée change mais pas la valeur retournée, alors la propagation échoue 
- si rien ne change au point muté, alors il n’y a pas d’infection observable

J’ai donc maintenant un premier arangement simple mais fonctionnelle pour observer la phase P au niveau bytecode.


Trois classes :
- RunOriginalClass.java : version normale
- RunOriginalTraceClass.java : version normale avec trace 
- RunMutatedTraceClass.java : version mutée avec traces

Les traces sont ajoutées au bytecode en injectant des appels à TraceLogger avec ASM.

au point de mutation :
on remplace IADD par ISUB, puis on fait DUP + logMutation
au retour :
on intercepte IRETURN, puis on fait DUP + logReturn + IRETURN

DUP est essentiel car il permet de logger une valeur sans la faire disparaître de la pile, donc sans casser l’exécution normale.

exemple :

    iadd
devient

    isub
    dup
    invokestatic TraceLogger.logMutation(int)