#!/bin/bash

# Chemins des projets
PROJECT_ORIGINAL="../ter-2025-archives/Basic_planner"
PROJECT_MUTATED="../ter-2025-archives/Basic_planner_mutated"

# Extraction des parties encadrées par "#######"
extract_section() {
    awk '/^#######/{flag=!flag; next} flag' "$1"
}

# Exécuter les tests et capturer les sorties
echo "🟢 Exécution des tests sur la version ORIGINALE..."
cd "$PROJECT_ORIGINAL" || exit
mvn -DskipPitest -Dtest=MutationTest test > ../../experimentations/test_original_output.txt

echo "🟠 Exécution des tests sur la version MUTÉE..."
cd ../"$PROJECT_MUTATED" || exit
mvn -DskipPitest -Dtest=MutationTest test > ../../experimentations/test_mutated_output.txt


# Extraire uniquement la partie entre ######
extract_section ../../experimentations/test_original_output.txt > ../../experimentations/filtered_original.txt
extract_section ../../experimentations/test_mutated_output.txt > ../../experimentations/filtered_mutated.txt

# Comparaison des outputs
echo "🔍 Comparaison des résultats..."
diff -y --suppress-common-lines ../../experimentations/filtered_original.txt ../../experimentations/filtered_mutated.txt > ../../experimentations/diff_results.txt

# Afficher les différences si elles existent
if [ -s ../diff_results.txt ]; then
    echo "❌ Différences détectées ! (voir diff_results.txt)"
    cat ../diff_results.txt
else
    echo "✅ Aucune différence détectée."
fi
