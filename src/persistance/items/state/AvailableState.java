package persistance.items.state;

import mediatek2020.items.Document;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.Utilisateur;
import persistance.MediathequeData;
import persistance.items.DocumentState;

public class AvailableState implements DocumentState {

	@Override
	public DocumentState emprunter(Document d, Utilisateur u) throws EmpruntException {
		if (MediathequeData.getInstance().emprunter(d, u))
			return new BorrowedState(u);
		throw new EmpruntException();
	}

	@Override
	public DocumentState reserver(Document d, Utilisateur u) {
		return new ReservedState(d, u);
	}

	@Override
	public String toString() {
		return "Disponible";
	}

}
