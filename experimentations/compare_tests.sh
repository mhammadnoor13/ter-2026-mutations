#!/bin/bash

# Chemins des projets
PROJECT_ORIGINAL="Basic_planner"
PROJECT_MUTATED="Basic_planner_mutated"

# Extraction des parties encadrées par "#######"
extract_section() {
    awk '/^#######/{flag=!flag; next} flag' "$1"
}

# Exécuter les tests et capturer les sorties
echo "🟢 Exécution des tests sur la version ORIGINALE..."
cd "$PROJECT_ORIGINAL" || exit
mvn -DskipPitest -Dtest=MutationTest test > ../test_original_output.txt

echo "🟠 Exécution des tests sur la version MUTÉE..."
cd ../"$PROJECT_MUTATED" || exit
mvn -DskipPitest -Dtest=MutationTest test > ../test_mutated_output.txt


# Extraire uniquement la partie entre ######
extract_section ../test_original_output.txt > ../filtered_original.txt
extract_section ../test_mutated_output.txt > ../filtered_mutated.txt

# Comparaison des outputs
echo "🔍 Comparaison des résultats..."
diff -y --suppress-common-lines ../filtered_original.txt ../filtered_mutated.txt > ../diff_results.txt

# Afficher les différences si elles existent
if [ -s ../diff_results.txt ]; then
    echo "❌ Différences détectées ! (voir diff_results.txt)"
    cat ../diff_results.txt
else
    echo "✅ Aucune différence détectée."
fi
