package persistance;

import persistance.items.DocumentState;
import persistance.items.PersistentDocument;
import persistance.items.docs.Book;
import persistance.items.docs.CD;
import persistance.items.docs.DVD;
import persistance.items.state.AvailableState;
import persistance.items.state.BorrowedState;

public class DocumentFactory {

	private DocumentFactory() {}

	public static PersistentDocument create(int type, Object ...args) {
		DocumentState s;
		if (args.length > 5) {
			s = new BorrowedState(
					MediathequeData.getInstance().getUser((Integer) args[5]));
		}
		else s = new AvailableState();
		switch (type) {
		case 0:
			return new Book(
					(Integer) args[0], s,
					(String) args[1], 
					(String) args[2], 
					(String) args[3], 
					(String) args[4]
					);
		case 1:
			return new DVD(
					(Integer) args[0], s,
					(String) args[1], 
					(String) args[2], 
					(String) args[3], 
					(String) args[4]
					);
		case 2:
			return new CD(
					(Integer) args[0], s,
					(String) args[1], 
					(String) args[2], 
					(String) args[3], 
					(String) args[4]
					);
			
		default: return null;
		}
	}
}
