<%@ include file="head.jsp"%>
<body>
	<div>
		<div>
			<h2>Connexion</h2>
		</div>
		<form action="/BiblioBretteWEB/login" method="post">
			<div>
				<p>Veuillez vous connecter pour continuer.</p>
				<div>
				<label for="email">Adresse mail : </label> 
					<input type="text" name="email" id="email"/>

				</div>
				<div>
					<label for="password">Mot de passe : </label> 
					<input type="password" name="mdp" id="mdp" />
				</div>
			</div>
			<div>
				<input type="submit" value="Connexion" />
				<% if (request.getAttribute("autherror") != null) { %>
				<span style="color:red;">Erreur d'authentification</span>
				<% } %>
			</div>
		</form>
	</div>
</body>
</html>