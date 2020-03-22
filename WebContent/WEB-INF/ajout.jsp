<%@ include file="head.jsp"%>
<body>
    
    <div>
    	<a href="/BiblioBretteWEB/documents">Retour</a>
        <form method="POST">
            <h1>Ajout d'un livre</h1>
            <select name=type>
            	<option value="0">Livre</option>
            	<option value="1">DVD</option>
            	<option value="2">CD</option>
            </select>
            <input type="text" name="titre" placeholder="Titre" />
            <input type="text" name="auteur" placeholder="Auteur" />
            <input type="text" name="annee" placeholder="Annee"/>
            <input type="text" name="categorie" placeholder="Catégorie"/>
            <input type="submit" value="Ajouter" />
        </form>
    </div>
</body>
</html>