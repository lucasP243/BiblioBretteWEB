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

@WebServlet({ "/login" })
public class LoginServlet extends HttpServlet {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		this.getServletContext().getRequestDispatcher( "/WEB-INF/login.jsp" ).forward( request, response );
	}

	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
		HttpSession session = request.getSession();
		String login = request.getParameter("login");
		String mdp = request.getParameter("mdp");

		Utilisateur user = Mediatheque.getInstance().getUser(login, mdp);
		if (user == null)
			response.getWriter().println("<span style=\"color:red;\">Erreur d'identification</span>");

		else {
			session.setAttribute("utilisateur", user);
			if (user.isBibliothecaire()) {
				response.sendRedirect("www.google.com");
			}
			else {
				response.sendRedirect("www.google.com");
			}
		}


	}

}
