package services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mediatek2020.Mediatheque;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.Utilisateur;

@WebServlet("/emprunter")
public class EmpruntServlet extends HttpServlet {

	@SuppressWarnings("unused")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendRedirect("/BiblioBretteWEB/documents");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		int docid = Integer.parseInt(req.getParameter("docid"));
		try {
			Mediatheque.getInstance().getDocument(docid).emprunter(
					(Utilisateur) req.getSession().getAttribute("utilisateur")
					);
			resp.sendRedirect("/BiblioBretteWEB/documents");
		} catch (@SuppressWarnings("unused") EmpruntException e) {
			this.getServletContext()
			.getRequestDispatcher("/WEB-INF/refuse.jsp")
			.forward(req, resp);
		}
	}
}
