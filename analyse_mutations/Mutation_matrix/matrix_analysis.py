import xml.etree.ElementTree as ET
import json
import sys
import os

# SI BESOIN, DECOMMENTER
# print(os.getcwd()) # Pour obtenir le chemin courant du dossier

# Inclue le fichier Json en sortie
if len(sys.argv) != 3:
    print("Usage: python matrix_analysis.py <xml_file> <json_file>")
    sys.exit(1)

xml_file = sys.argv[1]
json_file = sys.argv[2]

if os.path.exists(json_file):
    # print(f"Error: The file '{json_file}' already exists. Please choose a different name.")
    os.remove(json_file)
    # sys.exit(1)

# Lecture du XML
tree = ET.parse(xml_file)
root = tree.getroot()

# print(root.tag)
# print(root.attrib)
analysis = ["succeedingTests", "coveringTests", "killingTests"] # Les lignes de "cas particuliers" (ce sont des tableaux )

def is_not_empty(value):
    """
    Fonction qui calcule si un tableau est vide
    
    Keyword arguments:
    value -- Le tableau 
    Return: true si le tableau est vide, false sinon
    """
    
    return bool(value) and value != "/"

def determine_rip_status(mutant):
    """
    Détermine les statuts R, I, P pour un mutant donné.
    
    Keyword arguments:
    mutant -- Un mutant 
    Return: Un dictionnaire contenant les résultats de R, I et P
    """
    reach = is_not_empty(mutant.get("coveringTests"))  # Si le test couvre la mutation, elle est atteinte
    infect = is_not_empty(mutant.get("succeedingTests"))  # Si des tests réussissent, la mutation a infecté l'exécution
    propagate = is_not_empty(mutant.get("killingTests"))  # Si un test tue la mutation, elle est propagée

    return {
        # "R": "✔️" if reach else "❌",
        # "I": "✔️" if infect else "❌",
        # "P": "✔️" if propagate else "❌"
        "R": "OK" if reach else "KO",
        "I": "OK" if infect else "KO",
        "P": "OK" if propagate else "KO"
    }

def print_matrix():
    """
    Affiche la matrice de mutation dans le terminal
    """
    for child in root:
        for subchild in child:
            if (subchild.tag in analysis and subchild.text != None):
                print("-> " + subchild.tag + " : ")
                tab = subchild.text.split("|")
                for elt in tab:
                    print(elt)
            else:
                print("-> " + subchild.tag + " : " + (subchild.text if subchild.text else "/"))
        print("\n\n")

def matrixToJSON():
    """
    Transforme une matrice de mutation en JSON
    
    Keyword arguments:
    Return: Un tableau au format JSON
    """
    
    tab = []
    for child in root:
        actual = {}
        for subchild in child:
            if (subchild.tag in analysis and subchild.text != None):
                tests = []
                splitted = subchild.text.split("|")
                for elt in splitted:
                    tests.append(elt)
                actual[subchild.tag] = tests
            else:
                actual[subchild.tag] = subchild.text if subchild.text else "/"

        actual["RIP"] = determine_rip_status(actual)
        tab.append(actual)
    return tab

def size():
    """
    Fonction pour connaître la taille du tableau rendu dans la fonction matrixToJSON
    
    Keyword arguments:
    Return: La taille du tableau
    """
    return len(matrixToJSON())

# Ecriture d'un fichier JSON
with open(json_file, 'w') as outfile:
    json.dump(matrixToJSON(), outfile, indent=4)

print(f"Fichier JSON '{json_file}' généré avec succès.")