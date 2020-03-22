<%@ include file="head.jsp"%>
<%@ page
	import="mediatek2020.Mediatheque, mediatek2020.items.*, java.util.List"%>
<body>
	<%
		Utilisateur user = (Utilisateur) request.getAttribute("user");
		List<Document> catalogue = (List<Document>) request.getAttribute("liste_documents");
	%>
	<a href="/BiblioBretteWEB/login">D�connexion</a>
	<% if (user.isBibliothecaire()) { %>
		<a href="/BiblioBretteWEB/ajout">Ajouter un document</a>
	<% } %>
	<div>
		<div>
			<h2>Nos documents</h2>
		</div>
		<div>
			<span>Voici l'ensemble des documents poss�d�s par la m�diath�que.</span>
				<table>
					<thead>
						<tr>
							<th>ID</th>
							<th>Titre</th>
							<th>Auteur/R�alisateur/Compositeur</th>
							<th>Ann�e</th>
							<th>Cat�gorie</th>
							<th style="width: 25%">Action</th>
						</tr>
					</thead>
					<%
						for (Document d : catalogue) {
					%>
					<tr>
						<td><%=d.data()[0]%></td>
						<td><%=d.data()[1]%></td>
						<td><%=d.data()[2]%></td>
						<td><%=d.data()[3]%></td>
						<td><%=d.data()[4]%></td>
					<td>
						<!-- SI DISPONIBLE -->
						<% if ("Disponible".equals(d.data()[5])){ %>
						<form action="reserver" method="post">
							<input type="text" name="docid" id="docid" value="<%=d.data()[0]%>" style="display:none;"/>
							<input type="submit" value="Reserver" />
						</form>
						<form action="emprunter" method="post">
							<input type="text" name="docid" id="docid" value="<%=d.data()[0]%>" style="display:none;"/>
							<input type="submit" value="Emprunter" />
						</form>
						
						<!-- SI RESERVE PAR UTILISATEUR -->
						<% } else if (("R�serv� par "+user.name()).equals(d.data()[5])) { %>
						<form action="emprunter" method="post">
							<input type="text" name="docid" id="docid" value="<%=d.data()[0]%>" style="display:none;"/>
							<input type="submit" value="Emprunter" />
						</form>
						<form action="retourner" method="post">
							<input type="text" name="docid" id="docid" value="<%=d.data()[0]%>" style="display:none;"/>
							<input type="submit" value="Retourner" />
						</form>
						
						<!-- SI EMPRUNTE PAR UTILISATEUR -->
						<% } else if (("Emprunt� par "+user.name()).equals(d.data()[5])) { %>
						<form action="retourner" method="post">
							<input type="text" name="docid" id="docid" value="<%=d.data()[0]%>" style="display:none;"/>
							<input type="submit" value="Retourner" />
						</form>
						
						<!-- SI INDISPONIBLE -->
						<% } else { %>Indisponible.<% } %>
					</td>
				</tr>
				<%
					} // END FOR
				%>
			</table>
		</div>
	</div>
</body>
</html>