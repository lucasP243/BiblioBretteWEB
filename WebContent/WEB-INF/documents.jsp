<%@ include file="head.jsp"%>
<%@ page import="mediatek2020.Mediatheque, mediatek2020.items.*, java.util.List" %>
<body>
	<div>
		<div>
			<h2>Nos documents</h2>
		</div>
		<div>
			<div>
				Voici l'ensemble des documents possédés par la médiathèque.
			</div>
			<% List<Document> catalogue = (List<Document>) request.getAttribute("liste_documents");%>
			<div>
				<table>
				<thead>
					<td>Nom du livre</td>
					<td>Auteur</td>
					<td>Nombre de pages</td>
					<td style="width: 25%">Action</td>
				</thead>
				<% for (Document d: catalogue) { %>
					<% if (d.toString().equals("Livre")) { %>
						<% Object[] aff = d.data(); %>
						<tr>
							<td><%= aff[2] %></td>
							<td><%= aff[3] %></td>
							<td><%= aff[4] %></td>
							<td>
								<% if (aff[1] == null) { %>
									<form method="POST" action="../abonne/emprunt">
										<input type="hidden" name="id_doc" value="<%= aff[0] %>" />
										<input type="submit" value="Emprunter" />
									</form>
								<% } else { %>
									Emprunté
								<% } %>
							</td>
						</tr>
					<% } %>
				<% } %>
			</table>
			</div>
		</div>
	</div>
</body>
</html>