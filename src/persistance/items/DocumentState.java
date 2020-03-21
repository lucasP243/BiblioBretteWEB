package persistance.items;

import mediatek2020.items.Document;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.ReservationException;
import mediatek2020.items.RetourException;
import mediatek2020.items.Utilisateur;

@SuppressWarnings("unused")
public interface DocumentState {

	default DocumentState emprunter(Document d, Utilisateur u) 
			throws EmpruntException {
		throw new EmpruntException();
	}
	
	default DocumentState rendre(Document d, Utilisateur u) 
			throws RetourException {
		throw new RetourException();
	}
	
	default DocumentState reserver(Document d, Utilisateur u) 
			throws ReservationException {
		throw new ReservationException();
	}
	
	@Override String toString();
}
