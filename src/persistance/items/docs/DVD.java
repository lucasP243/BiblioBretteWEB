package persistance.items.docs;

import persistance.items.DocumentState;
import persistance.items.PersistentDocument;

public class DVD extends PersistentDocument{

	private String name;

	private String realisator;

	private String year;

	private String category;

	public DVD(int id, DocumentState s, String name, String rea, String year, String cat) {
		super(id, s);
		this.name = name;
		this.realisator = rea;
		this.year = year;
		this.category = cat;
	}

	@Override
	public Object[] data() {
		return new Object[] { getId(), name, realisator, year, category, getStatus() };
	}
	
}
