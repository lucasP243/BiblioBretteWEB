package persistance.items.state;

import mediatek2020.items.Document;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.ReservationException;
import mediatek2020.items.RetourException;
import mediatek2020.items.Utilisateur;
import persistance.MediathequeData;
import persistance.items.DocumentState;

@SuppressWarnings("unused")
public class BorrowedState implements DocumentState {
	
	private final Utilisateur borrowedBy;

	public BorrowedState(Utilisateur u) {
		this.borrowedBy = u;
	}

	@Override
	public DocumentState rendre(Document d, Utilisateur u) {
		MediathequeData.getInstance().retourner(d, u);
		return new AvailableState();
	}
	
	@Override
	public String toString() {
		return "Emprunté par " + borrowedBy.name();
	}

}
