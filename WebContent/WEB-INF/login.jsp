<%@ include file="head.jsp"%>
<body>
	<div>
		<div>
			<h2>Connexion</h2>
		</div>
		<form method="post">
			<div>
				<p>Veuillez vous connecter pour continuer.</p>
				<div>
					<label for="login">Login : </label> 
					<input type="text" name="login" id="login"/>

				</div>
				<div>
					<label for="password">Mot de passe : </label> 
					<input type="password" name="mdp" id="mdp" />

				</div>
			</div>
			<div>
				<input type="submit" value="Connexion" />
			</div>
		</form>
	</div>
</body>
</html>