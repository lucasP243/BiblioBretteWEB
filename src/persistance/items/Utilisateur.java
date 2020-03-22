package persistance.items;

public class Utilisateur implements mediatek2020.items.Utilisateur {
	
	private int id;
	
	private String username;
	
	private String email;
	
	private boolean isBiblio;
	
	

	public Utilisateur(int id, String username, String email, boolean isBiblio) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.isBiblio = isBiblio;
	}

	@Override
	public String name() {
		return email;
	}

	@Override
	public boolean isBibliothecaire() {
		return isBiblio;
	}

	@Override
	public Object[] data() {
		return new Object[] { id, username, email, isBiblio };
	}

}
