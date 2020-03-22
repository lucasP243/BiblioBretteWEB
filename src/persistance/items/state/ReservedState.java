package persistance.items.state;

import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;

import mediatek2020.items.Document;
import mediatek2020.items.EmpruntException;
import mediatek2020.items.RetourException;
import mediatek2020.items.Utilisateur;
import persistance.MediathequeData;
import persistance.items.DocumentState;

@SuppressWarnings("unused")
public class ReservedState implements DocumentState {
	
	private class ReservedStateTimer extends TimerTask {

		@Override
		public void run() {
			try {
				reserved.rendre(reservedBy);
			} catch (RetourException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private static final long EXPIRATION = Duration.ofHours(2).toMillis();
	
	private final Document reserved;

	private final Utilisateur reservedBy;
	
	private final ReservedStateTimer expiration;
	
	public ReservedState(Document d, Utilisateur u) {
		this.reserved = d;
		this.reservedBy = u;
		new Timer().schedule(expiration = new ReservedStateTimer(), EXPIRATION);
	}
	
	@Override
	public DocumentState emprunter(Document d, Utilisateur u)
			throws EmpruntException {
		if (reservedBy.equals(u)) {
			MediathequeData.getInstance().emprunter(d, u);
			return new BorrowedState(u);
		}
		
		throw new EmpruntException();
	}
	
	@Override
	public DocumentState rendre(Document d, Utilisateur u) 
			throws RetourException {
		if (reservedBy.equals(u)) {
			expiration.cancel();
			return new BorrowedState(u);
		}
		
		throw new RetourException();
	}
	
	@Override
	public String toString() {
		return "Réservé par " + reservedBy.name();
	}
}
