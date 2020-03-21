package persistance.items.docs;

import persistance.items.DocumentState;
import persistance.items.PersistentDocument;

public class CD extends PersistentDocument {

	private String name;
	
	private String releasedBy;
	
	private String year;
	
	private String category;

	public CD(int id, DocumentState s, String name, String by, String year, String cat) {
		super(id, s);
		this.name = name;
		this.releasedBy = by;
		this.year = year;
		this.category = cat;
	}

	@Override
	public Object[] data() {
		return new Object[] { getId(), name, releasedBy, year, category, getStatus() };
	}

}
