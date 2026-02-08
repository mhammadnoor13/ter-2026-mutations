from flask import Flask, jsonify, render_template
from matrix_analysis import matrixToJSON, size

app = Flask(__name__)

@app.route('/matrix')
def show_matrix():
    """Avec Flask, affiche le JSON entier sorti de la fonction matrixToJSON"""
    data = matrixToJSON()
    return jsonify(data)

@app.route('/')
def matrix_html():
    """Avec Flask, fait l'affichage sur un template HTML"""
    data = matrixToJSON()
    length = size()
    return render_template('matrix.html', data=data, size=length)

# Démarrage de l'application
if __name__ == '__main__':
    app.run(debug=True)