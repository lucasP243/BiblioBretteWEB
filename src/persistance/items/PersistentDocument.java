package persistance.items;

import mediatek2020.items.Document;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.ReservationException;
import mediatek2020.items.RetourException;
import mediatek2020.items.Utilisateur;

public abstract class PersistentDocument implements Document {
	
	private final int id;
	
	private DocumentState currentState;
	
	public PersistentDocument(int id, DocumentState status) {
		this.id = id;
		this.currentState = status;
	}
	
	public int getId() {
		return id;
	}

	@Override
	public synchronized void emprunter(Utilisateur u) 
			throws EmpruntException {
		currentState = currentState.emprunter(this, u);
	}

	@Override
	public synchronized void rendre(Utilisateur u) 
			throws RetourException {
		currentState = currentState.rendre(this, u);
	}

	@Override
	public synchronized void reserver(Utilisateur u) 
			throws ReservationException {
		currentState = currentState.reserver(this, u);
	}
	
	protected String getStatus() {
		return currentState.toString();
	}

	@Override
	public abstract Object[] data();

}
