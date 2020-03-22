package persistance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import mediatek2020.Mediatheque;
import mediatek2020.PersistentMediatheque;
import mediatek2020.items.Document;
import mediatek2020.items.Utilisateur;
import persistance.items.PersistentUtilisateur;

public class MediathequeData implements PersistentMediatheque {

	private static final String[] GET_ALL_DOCS = new String[] {
			"SELECT BOOK.id, BOOK.name, BOOK.author, BOOK.publicationYear, G.name AS \"genre\" "
					+ "FROM BOOK INNER JOIN LITERARYGENRE G ON BOOK.genre = G.id;",

					"SELECT DVD.id, DVD.name, DVD.realisator, DVD.releaseYear, G.name AS \"genre\" "
							+ "FROM DVD INNER JOIN CINEMATICGENRE G ON DVD.genre = G.id;",

							"SELECT CD.id, CD.name, CD.releasedBy, CD.releaseYear, G.name AS \"genre\" "
									+ "FROM CD INNER JOIN MUSICALGENRE G ON CD.genre = G.id;" };

	private static final String GET_USER_ID = 
			"SELECT * FROM BIBLIOUSER WHERE id = ?";

	private static final String GET_USER_LOGIN = 
			"SELECT * FROM BIBLIOUSER WHERE email = ? AND password = ?";

	private static MessageDigest hash;

	private static MediathequeData instance;

	private Map<Integer, Document> docs;

	private Map<Integer, Utilisateur> users;

	static {

		try {
			hash = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}

		try {
			Class.forName("org.postgresql.Driver");
			System.out.println("Driver PostgreSQL chargé avec succès");
		} catch (ClassNotFoundException e) {
			throw new RuntimeException("Impossible de charger le Driver PostgreSQL", e);
		}

		final String url = "jdbc:postgresql://localhost/bibliobrettedb";
		final Properties p = new Properties();
		p.setProperty("user", "biblioadmin");
		p.setProperty("password", "admin");

		Connection c;
		try {
			c = DriverManager.getConnection(url, p);
			System.out.println("Connecté à BiblioBretteDB");
		} catch (SQLException e) {
			throw new RuntimeException("Impossible de se connecter à BiblioBretteDB", e);
		}

		Mediatheque.getInstance().setData(instance = new MediathequeData(c));
	}

	public static MediathequeData getInstance() {
		return instance;
	}

	private final Connection db;

	private MediathequeData(Connection connection) {
		this.db = connection;
		this.docs = new Hashtable<>();
		this.users = new Hashtable<>();
	}

	@Override
	public List<Document> tousLesDocuments() {
		List<Document> allDocs = new LinkedList<>();
		try {
			Statement query = db.createStatement();
			for (int i = 0; i < GET_ALL_DOCS.length; i++) {
				query.execute(GET_ALL_DOCS[i]);
				ResultSet res = query.getResultSet();
				while (res.next()) {
					allDocs.add(DocumentFactory.create(
							i,
							res.getInt(1),		// id
							res.getString(2),	// name
							res.getString(3),	// author || realisator
							res.getString(4),	// year
							res.getString(5)	// category
							));
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		} finally {
			for (Document d : allDocs) {
				docs.put((Integer) d.data()[0], d);
			}
		}
		return allDocs;

	}

	public Utilisateur getUser(int id) {
		Utilisateur u;
		if ((u = users.get(id)) != null) {
			return u;
		}

		try {
			PreparedStatement s = db.prepareStatement(GET_USER_ID);
			s.setInt(1, id);
			s.execute();
			ResultSet res = s.getResultSet();
			res.next();
			u = new PersistentUtilisateur(
					res.getInt(1),
					res.getString(2),
					res.getString(4),
					res.getBoolean(5)
					);
			users.put(id, u);
			return u;
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
		return null;
	}

	@Override
	public Utilisateur getUser(String login, String password) {
		try {
			PreparedStatement s = db.prepareStatement(GET_USER_LOGIN);
			s.setString(1, login);
			s.setString(2, digest(password));
			s.execute();
			ResultSet res = s.getResultSet();
			res.next();
			Utilisateur u = new PersistentUtilisateur(
					res.getInt(1),
					res.getString(2),
					res.getString(4),
					res.getBoolean(5)
					);
			users.put((Integer) u.data()[0], u);
			return u;
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
			return null;
		}
	}

	private static String digest(String password) {
		StringBuilder s = new StringBuilder();
		for (byte b : hash.digest(password.getBytes(StandardCharsets.UTF_8))) {
			s.append(String.format("%02x", b));
		}
		return s.toString();
	}

	@Override
	public Document getDocument(int numDocument) {
		if (docs.get(numDocument) == null) {
			tousLesDocuments();
		}
		return docs.get(numDocument);
	}

	@Override
	public void nouveauDocument(int type, Object... args) {
		Document d = DocumentFactory.create(type, args, null);
		docs.put((Integer) args[0], d);
		String[] doctypes = new String[] {
				"BOOK (id, name, author, publicationYear, genre)", 
				"DVD (id, name, realisator, releaseYear, genre)", 
				"CD (id, name, releasedBy, releaseYear, genre)"
		};
		String[] cats = new String[] { 
				"LITERARYGENRE", "CINEMATICGENRE", "MUSICALGENRE" 
		};
		Integer catid = null;
		try {
			PreparedStatement s = db.prepareStatement(
					"SELECT id FROM "+cats[type]+" WHERE name = ?"
					);
			s.setString(1, (String) args[4]);
			s.execute();
			ResultSet res = s.getResultSet();
			if (res.next()) {
				catid = res.getInt("id");
			}
			else {
				s = db.prepareStatement(
						"INSERT INTO "+cats[type]+" (name) VALUES ?"
						);
				s.setString(1, (String) args[4]);
				s.executeUpdate();
				s = db.prepareStatement(
						"SELECT id FROM "+cats[type]+" WHERE name = ?"
						);
				s.setString(1, (String) args[4]);
				s.execute();
				res = s.getResultSet();
				res.next();
				catid = res.getInt("id");
			}
			assert catid != null;
			s = db.prepareStatement(
					"INSERT INTO "+doctypes[type]+" VALUES (?, ?, ?, ?, ?)"
					);
			s.setInt(1, (int) args[0]);
			s.setString(2, (String) args[1]);
			s.setString(3, (String) args[2]);
			s.setString(4, (String) args[3]);
			s.setInt(5, catid);
			s.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

	public void emprunter(Document d, Utilisateur u) {
		try {
			PreparedStatement s = db.prepareStatement(
					"INSERT INTO BORROWLOG (userid, docid, borrowedOn) VALUES (?, ?, ?)");
			s.setInt(1, (int) u.data()[0]);
			s.setInt(2, (int) d.data()[0]);
			s.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			s.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

	public void retourner(Document d, Utilisateur u) {
		try {
			PreparedStatement s = db.prepareStatement(
					"UPDATE BORROWLOG SET returnedon = ? WHERE docid = ? AND userid = ? AND returnedon = null"
					);
			s.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
			s.setInt(2, (int) d.data()[0]);
			s.setInt(3, (int) u.data()[0]);
			s.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

}
