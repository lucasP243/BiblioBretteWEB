package persistance.items;

import mediatek2020.items.Utilisateur;

public class PersistentUtilisateur implements Utilisateur {
	
	private int id;
	
	private String username;
	
	private String email;
	
	private boolean isAdmin;

	public PersistentUtilisateur(int id, String username, String email, boolean isAdmin) {
		super();
		this.id = id;
		this.username = username;
		this.email = email;
		this.isAdmin = isAdmin;
	}

	@Override
	public String name() {
		return username;
	}

	@Override
	public boolean isBibliothecaire() {
		return isAdmin;
	}

	@Override
	public Object[] data() {
		return new Object[] { id, username, email, isAdmin };
	}

}
