#!/bin/bash

#mv Mutant_negate_addMeeting/fr/univnantes/basicplanner/impl/fr.univnantes.basicplanner.impl.BasicPlannerImpl.class \
#   Mutant_negate_addMeeting/fr/univnantes/basicplanner/impl/BasicPlannerImpl.class

# Chemins des fichiers
ORIGINAL_CLASS="target/classes/fr/univnantes/basicplanner/impl/BasicPlannerImpl.class"
MUTANT_CLASS="Mutant_negate_addMeeting/fr/univnantes/basicplanner/impl/BasicPlannerImpl.class"

# Vérifier si le mutant existe
if [ ! -f "$MUTANT_CLASS" ]; then
    echo "Erreur : Le fichier mutant n'existe pas !"
    exit 1
fi

# Sauvegarde de la version originale
cp "$ORIGINAL_CLASS" "${ORIGINAL_CLASS}.bak"


# Remplacement par le mutant
cp "$MUTANT_CLASS" "$ORIGINAL_CLASS"

echo "Fichier mutant copié, vérification :"
ls "target/classes/fr/univnantes/basicplanner/impl/"

#mvn verify -X

#mvn compile

# Exécution des tests spécifiques
mvn test -Dtest=*TestAddMeeting -DskipPitest


# Restauration de la version originale
#mv "${ORIGINAL_CLASS}.bak" "$ORIGINAL_CLASS"

#echo "Tests terminés, le bytecode original a été restauré."
