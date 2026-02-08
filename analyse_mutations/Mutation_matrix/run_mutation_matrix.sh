#!/bin/bash

# Script de lancement de l'analyse de la matrice de mutation PIT

set -e

GREEN="\e[32m"
RED="\e[31m"
BLUE="\e[34m"
RESET="\e[0m"

echo -e "${BLUE}=== Lancement analyse de mutation ===${RESET}"

# ----------------
# Vérification paramètre
# ----------------
if [ $# -ne 1 ]; then
  echo -e "${RED}Erreur : chemin du projet Maven requis${RESET}"
  echo "Usage : ./run_mutation_matrix.sh <chemin_projet_maven>"
  exit 1
fi

PROJECT_PATH=$1

if [ ! -d "$PROJECT_PATH" ]; then
  echo -e "${RED}Erreur : projet Maven introuvable${RESET}"
  exit 1
fi

echo -e "${GREEN}REUSSI : Projet Maven détecté : $PROJECT_PATH${RESET}"

# ----------------
# Étape 1 : mvn test
# ----------------
echo -e "${BLUE}→ Exécution de mvn test${RESET}"
cd "$PROJECT_PATH"

if mvn test; then
  echo -e "${GREEN} REUSSI : mvn test terminé avec succès${RESET}"
else
  echo -e "${RED}ECHEC : Échec de mvn test${RESET}"
  exit 1
fi

# ----------------
# Étape 2 : récupération mutations.xml
# ----------------
echo -e "${BLUE}→ Recherche du fichier mutations.xml${RESET}"

MUTATIONS_XML=$(find target/pit-reports -name mutations.xml | head -n 1)

if [ -z "$MUTATIONS_XML" ]; then
  echo -e "${RED}ECHEC : mutations.xml introuvable${RESET}"
  exit 1
fi

echo -e "${GREEN}REUSSI : mutations.xml trouvé : $MUTATIONS_XML${RESET}"

# ----------------
# Étape 3 : retour dans Mutation_matrix
# ----------------
cd - > /dev/null
SCRIPT_DIR=$(dirname "$0")
cd "$SCRIPT_DIR"

cp "$PROJECT_PATH/$MUTATIONS_XML" mutations.xml
echo -e "${GREEN}REUSSI : mutations.xml copié dans Mutation_matrix${RESET}"

# ----------------
# Étape 4 : activation du venv
# ----------------
echo -e "${BLUE}→ Activation de l'environnement virtuel Python${RESET}"

if [ ! -f "venv/bin/activate" ]; then
  echo -e "${RED}ECHEC : venv introuvable${RESET}"
  exit 1
fi

source venv/bin/activate
echo -e "${GREEN}REUSSI : Environnement virtuel activé${RESET}"

# ----------------
# Étape 5 : analyse de la matrice
# ----------------
echo -e "${BLUE}→ Analyse de la matrice de mutation${RESET}"

if python matrix_analysis.py mutations.xml mutations.json; then
  echo -e "${GREEN}REUSSI : Analyse terminée (mutations.json généré)${RESET}"
else
  echo -e "${RED}ECHEC : Échec de l'analyse de la matrice${RESET}"
  deactivate
  exit 1
fi

# ----------------
# Étape 6 : lancement du serveur
# ----------------
echo -e "${BLUE}→ Lancement du serveur de visualisation${RESET}"
echo -e "${GREEN}REUSSI : Serveur disponible (Ctrl+C pour arrêter)${RESET}"

python site.py mutations.xml mutations.json

deactivate
