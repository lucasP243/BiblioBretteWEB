package persistance;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
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

	private static final PreparedStatement[] GET_ALL_DOCS;

	private static final PreparedStatement GET_USER_BYID;

	private static final PreparedStatement GET_USER_BYLOGIN;

	private static final PreparedStatement[] GET_CAT_ID_BYNAME;

	private static final PreparedStatement[] WRITE_DOC;

	private static final PreparedStatement[] WRITE_CAT;

	private static final PreparedStatement WRITE_BORROW;

	private static final PreparedStatement WRITE_RETURN;

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

		// Initialisation des PreparedStatements
		try {
			GET_ALL_DOCS = new PreparedStatement[] {
					instance.db.prepareStatement("SELECT BOOK.id, BOOK.name, BOOK.author, BOOK.publicationyear, G.name, U.id FROM BOOK INNER JOIN LITERARYGENRE G ON BOOK.genre = G.id LEFT JOIN BORROWLOG B ON BOOK.id = B.docid AND B.returnedon IS NULL LEFT JOIN BIBLIOUSER U ON B.userid = U.id"), 
					instance.db.prepareStatement("SELECT DVD.id, DVD.name, DVD.realisator, DVD.releaseyear, G.name, U.id FROM DVD INNER JOIN LITERARYGENRE G ON DVD.genre = G.id LEFT JOIN BORROWLOG B ON DVD.id = B.docid AND B.returnedon IS NULL LEFT JOIN BIBLIOUSER U ON B.userid = U.id"), 
					instance.db.prepareStatement("SELECT CD.id, CD.name, CD.releasedby, CD.releaseyear, G.name, U.id FROM CD INNER JOIN LITERARYGENRE G ON CD.genre = G.id LEFT JOIN BORROWLOG B ON CD.id = B.docid AND B.returnedon IS NULL LEFT JOIN BIBLIOUSER U ON B.userid = U.id") 
			};
			GET_USER_BYID = instance.db.prepareStatement("SELECT * FROM BIBLIOUSER WHERE id = ?");
			GET_USER_BYLOGIN = instance.db.prepareStatement("SELECT * FROM BIBLIOUSER WHERE email = ? AND password = ?");
			GET_CAT_ID_BYNAME = new PreparedStatement[] {
					instance.db.prepareStatement("SELECT id FROM LITERARYGENRE WHERE name = ?"),
					instance.db.prepareStatement("SELECT id FROM CINEMATICGENRE WHERE name = ?"),
					instance.db.prepareStatement("SELECT id FROM MUSICALGENRE WHERE name = ?")
			};
			WRITE_DOC = new PreparedStatement[] {
					instance.db.prepareStatement("INSERT INTO BOOK (name, author, publicationyear, genre) VALUES (?, ?, ?, ?) RETURNING id"),
					instance.db.prepareStatement("INSERT INTO DVD (name, realisator, releaseyear, genre) VALUES (?, ?, ?, ?) RETURNING id"),
					instance.db.prepareStatement("INSERT INTO CD (name, releasedby, releaseyear, genre) VALUES (?, ?, ?, ?) RETURNING id"),
			};
			WRITE_CAT = new PreparedStatement[] {
					instance.db.prepareStatement("INSERT INTO LITERARYGENRE (name) VALUES (?) RETURNING id"),
					instance.db.prepareStatement("INSERT INTO CINEMATICGENRE (name) VALUES (?) RETURNING id"),
					instance.db.prepareStatement("INSERT INTO MUSICALGENRE (name) VALUES (?) RETURNING id"),
			};
			WRITE_BORROW = instance.db.prepareStatement("INSERT INTO BORROWLOG (userid, docid, borrowedOn) VALUES (?, ?, ?)");
			WRITE_RETURN = instance.db.prepareStatement("UPDATE BORROWLOG SET returnedon = ? WHERE docid = ? AND userid = ? AND returnedon IS NULL");
		} catch (SQLException e) {
			throw new RuntimeException("Failed to init SQL statements", e);
		}
		
		// Une fois les requête précompilées on charge les documents
		instance.chargerDocuments();
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

	private void chargerDocuments() {
		try {
			for (int i = 0; i < GET_ALL_DOCS.length; i++) {
				GET_ALL_DOCS[i].execute();
				ResultSet res = GET_ALL_DOCS[i].getResultSet();
				while (res.next()) {
					docs.put(res.getInt(1),
							DocumentFactory.create(
									i,
									res.getInt(1),		// id
									res.getString(2),	// name
									res.getString(3),	// author || realisator
									res.getString(4),	// year
									res.getString(5),	// category
									res.getInt(6) 		// borrower
									));
				}
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

	@Override
	public List<Document> tousLesDocuments() {
		List<Document> allDocs = new LinkedList<>();
		for (Map.Entry<Integer, Document> e : docs.entrySet()) {
			allDocs.add(e.getValue());
		}
		Collections.reverse(allDocs);
		return allDocs;
	}

	@Override
	public Document getDocument(int numDocument) {
		return docs.get(numDocument);
	}

	public Utilisateur getUser(int id) {
		Utilisateur u;
		if ((u = users.get(id)) != null) {
			return u;
		}

		try {
			GET_USER_BYID.setInt(1, id);
			GET_USER_BYID.execute();
			ResultSet res = GET_USER_BYID.getResultSet();
			if (res.next()) {
				u = new PersistentUtilisateur(
						res.getInt(1),
						res.getString(2),
						res.getString(4),
						res.getBoolean(5)
						);
				users.put(id, u);
				return u;
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
		return null;
	}

	@Override
	public Utilisateur getUser(String login, String password) {
		try {
			GET_USER_BYLOGIN.setString(1, login);
			GET_USER_BYLOGIN.setString(2, digest(password));
			GET_USER_BYLOGIN.execute();
			ResultSet res = GET_USER_BYLOGIN.getResultSet();
			if (res.next()) {
				Utilisateur u = new PersistentUtilisateur(
						res.getInt(1),
						res.getString(2),
						res.getString(4),
						res.getBoolean(5)
						);
				if (users.get(u.data()[0]) == null)
					users.put((Integer) u.data()[0], u);
				return users.get(u.data()[0]);
			}
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
		return null;
	}

	private static String digest(String password) {
		StringBuilder s = new StringBuilder();
		for (byte b : hash.digest(password.getBytes(StandardCharsets.UTF_8))) {
			s.append(String.format("%02x", b));
		}
		return s.toString();
	}

	@Override
	public void nouveauDocument(int type, Object... args) {
		Integer catid = null;
		try {
			GET_CAT_ID_BYNAME[type].setString(1, (String) args[3]);
			GET_CAT_ID_BYNAME[type].execute();
			ResultSet res = GET_CAT_ID_BYNAME[type].getResultSet();
			if (res.next()) {
				catid = res.getInt("id");
			}
			else {
				WRITE_CAT[type].setString(1, (String) args[3]);
				WRITE_CAT[type].execute();
				res = WRITE_CAT[type].getResultSet();
				res.next();
				catid = res.getInt("id");
			}
			WRITE_DOC[type].setString(1, (String) args[0]);
			WRITE_DOC[type].setString(2, (String) args[1]);
			WRITE_DOC[type].setString(3, (String) args[2]);
			WRITE_DOC[type].setInt(4, catid);
			WRITE_DOC[type].execute();
			res = WRITE_DOC[type].getResultSet();
			res.next();
			int docid = res.getInt("id");
			docs.put(docid, DocumentFactory.create(type, docid, args[0], args[1], args[2], args[3], 0));

		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

	public boolean emprunter(Document d, Utilisateur u) {
		try {
			WRITE_BORROW.setInt(1, (int) u.data()[0]);
			WRITE_BORROW.setInt(2, (int) d.data()[0]);
			WRITE_BORROW.setDate(3, new java.sql.Date(new java.util.Date().getTime()));
			WRITE_BORROW.executeUpdate();
			return true;
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
			return false;
		}
	}

	public void retourner(Document d, Utilisateur u) {
		try {
			WRITE_RETURN.setDate(1, new java.sql.Date(new java.util.Date().getTime()));
			WRITE_RETURN.setInt(2, (int) d.data()[0]);
			WRITE_RETURN.setInt(3, (int) u.data()[0]);
			WRITE_RETURN.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Failed to execute query : " + e.getMessage());
		}
	}

}
