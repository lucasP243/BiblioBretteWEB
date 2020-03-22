package services;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import mediatek2020.items.Utilisateur;

@WebServlet (urlPatterns="/", loadOnStartup=1)
public class LoadOnInitServlet extends HttpServlet {
	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			Class.forName("persistance.MediathequeData");
		} catch (@SuppressWarnings("unused") ClassNotFoundException e) {
			throw new RuntimeException("Failed to load persistance");
		}
	}

	@SuppressWarnings("unused")
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) 
			throws IOException {
		HttpSession session = req.getSession();
		Utilisateur user = (Utilisateur) session.getAttribute("utilisateur");
        if (user == null) resp.sendRedirect("login");
        else resp.sendRedirect("documents");
	}
}
