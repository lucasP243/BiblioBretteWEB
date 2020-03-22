package services;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mediatek2020.Mediatheque;
import mediatek2020.items.Utilisateur;

/**
 * Servlet implementation class AjoutServlet
 */
@WebServlet("/ajout")
public class AjoutServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public AjoutServlet() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
        if (user == null || !user.isBibliothecaire()) {
            response.sendRedirect("login");
            return;
        }
		this.getServletContext().getRequestDispatcher( "/WEB-INF/ajout.jsp" ).forward( request, response );
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Mediatheque.getInstance().nouveauDocument(
				Integer.parseInt(req.getParameter("type")),
				req.getParameter("titre"),
				req.getParameter("auteur"),
				req.getParameter("annee"),
				req.getParameter("categorie")
				);
		resp.sendRedirect("ajout");
	}

}
