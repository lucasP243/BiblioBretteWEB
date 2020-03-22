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


@WebServlet({ "/documents" })
public class DocumentServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		HttpSession session = request.getSession();
		Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
		System.out.println(user);
        if (user == null) {
            response.sendRedirect("/PROJET_JAVAEE/login");
            return;
        }
        request.setAttribute("liste_documents",  Mediatheque.getInstance().tousLesDocuments());
		this.getServletContext().getRequestDispatcher( "/WEB-INF/documents.jsp" ).forward( request, response );
	}

}
