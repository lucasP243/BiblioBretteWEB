package services;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mediatek2020.Mediatheque;
import mediatek2020.items.Document;
import mediatek2020.items.RetourException;
import mediatek2020.items.Utilisateur;

@WebServlet({ "/retour" })

public class RetourServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void doGet( HttpServletRequest request, HttpServletResponse response ) throws ServletException, IOException{
		this.getServletContext().getRequestDispatcher( "/WEB-INF/retour.jsp" ).forward( request, response );
	}

	protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        String numDoc = request.getParameter("numero");
        int num = Integer.parseInt(numDoc);
        Document docu = Mediatheque.getInstance().getDocument(num);
        HttpSession session = request.getSession();
        try {
			Mediatheque.getInstance().rendre(docu, (Utilisateur)session.getAttribute("utilisateur"));
		} catch (RetourException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
	}

}
