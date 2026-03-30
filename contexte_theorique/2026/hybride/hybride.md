# Articles sur les approches hybrides

## 1. MuJava: An Automated Class Mutation System

**Référence**  
Yu-Seung Ma, Jeff Offutt, Yong Rae Kwon, *MuJava: An Automated Class Mutation System*.

**Idée générale**  
Cet article présente **MuJava**, un outil de mutation pour Java orienté programmation objet. L'objectif principal est de rendre le mutation testing plus praticable malgré son coût. Pour cela, les auteurs combinent deux idées : **Mutant Schemata Generation (MSG)** et **bytecode translation**. Leur méthode permet d'éviter de recompiler séparément chaque mutant et d'accélérer fortement l'exécution. Le papier annonce que cette approche est environ **cinq fois plus rapide** qu'une stratégie de compilation séparée des mutants. citeturn203105view0

**Pourquoi c'est une approche hybride**  
MuJava est intéressant pour notre état de l'art parce qu'il n'est ni purement source, ni purement bytecode. Les mutations sont pensées à un niveau proche du **code source et des opérateurs OO**, mais l'exécution repose aussi sur des mécanismes plus bas niveau avec la **bytecode translation**. Cette combinaison en fait une vraie approche **hybride**. citeturn203105view0

**Ce que l'article apporte**  
L'article rappelle que le coût du mutation testing est un problème central, en particulier pour les programmes orientés objet. Il propose une solution pragmatique pour rendre l'analyse plus efficace, tout en supportant des opérateurs adaptés à l'héritage, au polymorphisme, au masquage, à la surcharge et à d'autres fautes spécifiques aux programmes OO. citeturn203105view0

**Choses utiles pour notre projet**  
- Cet article est utile pour justifier qu'une approche **mixte** entre plusieurs niveaux d'analyse est pertinente.
- Il montre qu'on peut garder une logique proche du **source** tout en s'appuyant sur un niveau plus bas pour l'**efficacité**.
- Il est utile surtout pour la partie **architecture/outillage** de notre réflexion, moins pour une analyse fine de la propagation au sens RIPR.
- Il peut servir à montrer qu'un projet sur les mutants survivants ne doit pas seulement chercher à être exact, mais aussi à rester **exécutable à coût raisonnable**. citeturn203105view0

---

## 2. Major: An Efficient and Extensible Tool for Mutation Analysis in a Java Compiler

**Référence**  
René Just, *Major: An Efficient and Extensible Tool for Mutation Analysis in a Java Compiler*.

**Idée générale**  
Cet article présente **Major**, un framework de mutation pour Java composé de deux parties : un **mutateur intégré au compilateur OpenJDK** et un **analyseur de mutation** pour les tests JUnit. Major génère et embarque les mutants pendant la compilation du programme original, ce qui évite les recompilations coûteuses et permet une approche efficace et scalable. Le papier explique aussi que Major supporte à la fois la **weak mutation** et la **strong mutation**. citeturn827820view2turn827820view1

**Pourquoi c'est une approche hybride**  
Major est une bonne référence hybride parce qu'il ne travaille ni seulement sur le source brut, ni seulement après compilation sur le bytecode final. Il intervient **dans le compilateur**, sur un **AST enrichi avec les informations de type**, puis embarque les mutants dans les class files générés. Cette position intermédiaire lui permet de profiter des avantages du source et du bytecode en limitant certaines faiblesses des deux approches. citeturn827820view2

**Ce que l'article apporte**  
Le papier explique très bien l'intérêt d'une approche intégrée au compilateur. Par rapport à la mutation au niveau source, Major évite de produire trop de mutants non compilables grâce aux informations sémantiques disponibles dans l'AST. Par rapport au bytecode, il évite certains problèmes liés au **desugaring** et au fait que certaines mutations de bas niveau ne se mappent plus proprement au code source d'origine. Le papier montre aussi que Major peut réaliser la weak mutation via une phase de pré-passage où il surveille l'**infection d'état**. citeturn827820view0turn827820view1

**Choses utiles pour notre projet**  
- Cet article est très utile pour justifier une approche **entre plusieurs niveaux**, surtout si on veut observer des phénomènes dynamiques tout en gardant un lien propre avec le **code source**.
- Il donne un bon argument contre une vision trop simple du bytecode : certaines transformations du compilateur compliquent l'interprétation des mutants.
- Il est pertinent pour notre projet si on veut garder des traces de propagation compréhensibles par un humain, par exemple en reliant les observations dynamiques à une **méthode** ou à une **ligne source**.
- Le support explicite de la **weak mutation** et de l'**infection d'état** est très proche de notre problématique sur la propagation des mutants survivants. citeturn827820view2turn827820view1

---

## Conclusion rapide sur les approches hybrides

Ces deux articles montrent que les approches hybrides cherchent en général à obtenir un compromis entre :

- la **précision sémantique** et la lisibilité du niveau source ;
- l'**efficacité d'exécution** et l'instrumentation des niveaux plus bas.

Pour notre projet, l'intérêt principal de ces travaux est de montrer qu'il est pertinent de :
- suivre les effets d'un mutant à un niveau proche de l'exécution ;
- mais conserver un lien clair avec le **code source**, la **méthode** et le **comportement observable**.

En pratique, Major semble plus directement utile que MuJava pour notre sujet sur la **propagation des mutants survivants**, car il relie plus clairement weak mutation, infection d'état et compromis entre source et bytecode. citeturn827820view2turn827820view1turn203105view0
