package persistance.items.docs;

import persistance.items.DocumentState;
import persistance.items.PersistentDocument;

public class Book extends PersistentDocument {
	
	private String name;
	
	private String author;
	
	private String year;
	
	private String category;

	public Book(int id, DocumentState s, String name, String author, String year, String cat) {
		super(id, s);
		this.name = name;
		this.author = author;
		this.year = year;
		this.category = cat;
	}

	@Override
	public Object[] data() {
		return new Object[] { getId(), name, author, year, category, getStatus() };
	}

}
