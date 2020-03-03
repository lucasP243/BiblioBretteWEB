-- file script-bibliobrette-db.sql

------------------------------
-- BiblioBretteWeb DATABASE --
------------------------------
-- by Lucas Pinard
-- for POSTGRESQL

CREATE TABLE BIBLIOUSER (
	id			SERIAL PRIMARY KEY,
	username	VARCHAR(20) NOT NULL,
	password	VARCHAR(64) NOT NULL,
	email		VARCHAR(256) UNIQUE NOT NULL,
	isAdmin		BOOLEAN
);

CREATE SEQUENCE DOC_SERIAL;

CREATE TABLE DOCUMENT (
	id INTEGER PRIMARY KEY
);

CREATE TABLE BORROWLOG (
	userID		INTEGER REFERENCES BIBLIOUSER (id) ,
	docID		INTEGER REFERENCES DOCUMENT (id),
	borrowedOn	TIMESTAMP NOT NULL,
	returnedOn	TIMESTAMP, -- is null if the document hasn't been returned yet
	
	CONSTRAINT PK_BORROWLOG PRIMARY KEY (userID, docID, borrowedOn)
);

CREATE TABLE LITERARYGENRE (
	id		SERIAL PRIMARY KEY,
	name 	VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE CINEMATICGENRE (
	id		SERIAL PRIMARY KEY,
	name 	VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE MUSICALGENRE (
	id		SERIAL PRIMARY KEY,
	name	VARCHAR(20) NOT NULL UNIQUE
);

CREATE TABLE BOOK (
	id				INTEGER PRIMARY KEY REFERENCES DOCUMENT (id) DEFAULT nextval('DOC_SERIAL'),
	name 			VARCHAR(50),
	author			VARCHAR(50),
	publicationYear	VARCHAR(4),
	genre			INTEGER REFERENCES LITERARYGENRE (id)
);

CREATE TABLE DVD (
	id				INTEGER PRIMARY KEY REFERENCES DOCUMENT (id) DEFAULT nextval('DOC_SERIAL'),
	name 			VARCHAR(50),
	realisator		VARCHAR(50),
	releaseYear		VARCHAR(4),
	genre			INTEGER REFERENCES CINEMATICGENRE (id)
);

CREATE TABLE CD (
	id				INTEGER PRIMARY KEY REFERENCES DOCUMENT (id) DEFAULT nextval('DOC_SERIAL'),
	name 			VARCHAR(50),
	releasedBy		VARCHAR(50),
	releaseYear		VARCHAR(4),
	genre			INTEGER REFERENCES MUSICALGENRE (id)
);

CREATE OR REPLACE FUNCTION TGPROC_DOC() RETURNS trigger AS $tgproc$
BEGIN
	INSERT INTO DOCUMENT
	VALUES (NEW.id);
	RETURN NEW;
END;
$tgproc$ LANGUAGE plpgsql;

CREATE TRIGGER TG_DOC BEFORE INSERT ON BOOK
FOR EACH ROW EXECUTE PROCEDURE TGPROC_DOC();

CREATE TRIGGER TG_DOC BEFORE INSERT ON DVD
FOR EACH ROW EXECUTE PROCEDURE TGPROC_DOC();

CREATE TRIGGER TG_DOC BEFORE INSERT ON CD
FOR EACH ROW EXECUTE PROCEDURE TGPROC_DOC();

INSERT INTO BIBLIOUSER (username, password, email, isAdmin)
VALUES -- Passwords are hashed using SHA256 hashing algorithm
	('admin', /*admin*/'8c6976e5b5410415bde908bd4dee15dfb167a9c873fc4bb8a81f6f2ab448a918', 'admin@fakemail.com', TRUE),
	('user1', /*J2EEforever*/'ae1f74648fc59b06a0d2a1c2cf7fee864c027f986eb2637058cd84931e60c114', 'user1@fakemail.com', FALSE),
	('user2', /*MrBretteLeMeilleur*/'45663ce8518043f903366e50ac683fe7cf2c298bf116effb5348f828b8a74ae3', 'user2@fakemail.com', FALSE);

INSERT INTO LITERARYGENRE (name) VALUES
	('Biographie'),
	('Polar'),
	('Fantastique'),
	('Épistolaire'),
	('Poésie');

INSERT INTO CINEMATICGENRE (name) VALUES
	('Drame'),
	('Action'),
	('Documentaire'),
	('Horreur');

INSERT INTO MUSICALGENRE (name) VALUES
	('Rock');

WITH val (n, c, y, g) AS (
	VALUES
		('Les Confessions', 'Jean-Jacques Rousseau', '1813', 'Biographie'),
		('Les Confessions', 'Jean-Jacques Rousseau', '1813', 'Biographie'),
		('Les Confessions', 'Jean-Jacques Rousseau', '1813', 'Biographie'),
		('Les Confessions', 'Jean-Jacques Rousseau', '1813', 'Biographie'),
		('Les Confessions', 'Jean-Jacques Rousseau', '1813', 'Biographie'),
		('Les mots', 'Jean-Paul Sartre', '1964', 'Biographie'),
		('Les mots', 'Jean-Paul Sartre', '1964', 'Biographie'),
		('Les mots', 'Jean-Paul Sartre', '1964', 'Biographie'),
		('Le Journal d''Anne Frank', 'Anne Frank', '1947', 'Biographie'),
		('Le Journal d''Anne Frank', 'Anne Frank', '1947', 'Biographie'),
		('Le Journal d''Anne Frank', 'Anne Frank', '1947', 'Biographie'),
		('Le Journal d''Anne Frank', 'Anne Frank', '1947', 'Biographie'),
		('Un long chemin vers la liberté', 'Jean Guiloineau', '1996', 'Biographie'),
		('Un long chemin vers la liberté', 'Jean Guiloineau', '1996', 'Biographie'),
		
		('Le Signal', 'Maxime Chattam', '2020', 'Polar'),
		('Le Signal', 'Maxime Chattam', '2020', 'Polar'),
		('Le Signal', 'Maxime Chattam', '2020', 'Polar'),
		('Crimes et Châtiment', 'Fiodor Dovstoïevski', '2019', 'Polar'),
		('Crimes et Châtiment', 'Fiodor Dovstoïevski', '2019', 'Polar'),
		('Crimes et Châtiment', 'Fiodor Dovstoïevski', '2019', 'Polar'),
		
		('Frankenstein ou le Prométhée moderne', 'Mary Shelley', '1818', 'Fantastique'),
		('Frankenstein ou le Prométhée moderne', 'Mary Shelley', '1818', 'Fantastique'),
		('Frankenstein ou le Prométhée moderne', 'Mary Shelley', '1818', 'Fantastique'),
		('Frankenstein ou le Prométhée moderne', 'Mary Shelley', '1818', 'Fantastique'),
		('La Comédie des ténèbres', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Comédie des ténèbres', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Comédie des ténèbres', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Peau de chagrin', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Peau de chagrin', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Peau de chagrin', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Peau de chagrin', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La Peau de chagrin', 'Honoré de Balzac', '1831', 'Fantastique'),
		('La morte amoureuse', 'Théophile Gautier', '1836', 'Fantastique'),
		('La morte amoureuse', 'Théophile Gautier', '1836', 'Fantastique'),
		('La morte amoureuse', 'Théophile Gautier', '1836', 'Fantastique'),
		
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Mémoires de deux jeunes mariées', 'Honoré de Balzac', '1842', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Lettres Persanes', 'Montesquieu', '1721', 'Épistolaire'),
		('Papa-Longues-Jambes', 'Jean Webster', '1912', 'Épistolaire'),
		('Papa-Longues-Jambes', 'Jean Webster', '1912', 'Épistolaire'),
		('Papa-Longues-Jambes', 'Jean Webster', '1912', 'Épistolaire'),
		('Papa-Longues-Jambes', 'Jean Webster', '1912', 'Épistolaire'),
		
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Contemplations', 'Victor Hugo', '1856', 'Poésie'),
		('Les Fleurs du Mal', 'Charles Beaudelaire', '1857', 'Poésie'),
		('Les Fleurs du Mal', 'Charles Beaudelaire', '1857', 'Poésie'),
		('Les Fleurs du Mal', 'Charles Beaudelaire', '1857', 'Poésie'),
		('Les Fleurs du Mal', 'Charles Beaudelaire', '1857', 'Poésie'),
		('Les Fleurs du Mal', 'Charles Beaudelaire', '1857', 'Poésie'),
		('Les yeux d''Elsa', 'Louis Aragon', '1942', 'Poésie'),
		('Les yeux d''Elsa', 'Louis Aragon', '1942', 'Poésie'),
		('Les yeux d''Elsa', 'Louis Aragon', '1942', 'Poésie')
)
INSERT INTO BOOK (name, author, publicationYear, genre)
SELECT val.n, val.c, val.y, LITERARYGENRE.id
FROM val LEFT JOIN LITERARYGENRE
ON val.g = LITERARYGENRE.name;
		
WITH val (n, c, y, g) AS (
	VALUES
		('Forrest Gump', 'Robert Zemeckis', '1994', 'Drame'),
		('Forrest Gump', 'Robert Zemeckis', '1994', 'Drame'),
		('Forrest Gump', 'Robert Zemeckis', '1994', 'Drame'),
		('Forrest Gump', 'Robert Zemeckis', '1994', 'Drame'),
		('Forrest Gump', 'Robert Zemeckis', '1994', 'Drame'),
		('Le Parrain', 'Francis Ford Coppola', '1972', 'Drame'),
		('Le Parrain', 'Francis Ford Coppola', '1972', 'Drame'),
		('Le Parrain', 'Francis Ford Coppola', '1972', 'Drame'),
		('Le Parrain', 'Francis Ford Coppola', '1972', 'Drame'),
		('Les Évadés', 'Frank Darabont', '1995', 'Drame'),
		('Les Évadés', 'Frank Darabont', '1995', 'Drame'),
		('Gran Torino', 'Clint Eastwood', '2009', 'Drame'),
		('Gran Torino', 'Clint Eastwood', '2009', 'Drame'),
		('Gran Torino', 'Clint Eastwood', '2009', 'Drame'),
		
		('The Dark Knight', 'Christopher Nolan', '2008', 'Action'),
		('The Dark Knight', 'Christopher Nolan', '2008', 'Action'),
		('The Dark Knight', 'Christopher Nolan', '2008', 'Action'),
		('Avengers: Infinity War', 'Joe Russo', '2018', 'Action'),
		('Avengers: Infinity War', 'Joe Russo', '2018', 'Action'),
		('Avengers: Infinity War', 'Joe Russo', '2018', 'Action'),
		('Avengers: Infinity War', 'Joe Russo', '2018', 'Action'),
		('Avengers: Infinity War', 'Joe Russo', '2018', 'Action'),
		('Indiana Jones et la Dernière Croisade', 'Steven SpielBerg', '1989', 'Action'),
		('Indiana Jones et la Dernière Croisade', 'Steven SpielBerg', '1989', 'Action'),
		('Indiana Jones et la Dernière Croisade', 'Steven SpielBerg', '1989', 'Action'),
		('Indiana Jones et la Dernière Croisade', 'Steven SpielBerg', '1989', 'Action'),
		('Matrix', 'Lana Wachowski', '1999', 'Action'),
		('Matrix', 'Lana Wachowski', '1999', 'Action'),
		('Matrix', 'Lana Wachowski', '1999', 'Action'),
		('Matrix', 'Lana Wachowski', '1999', 'Action'),
		('Ready Player One', 'Steven SpielBerg', '2018', 'Action'),
		('Ready Player One', 'Steven SpielBerg', '2018', 'Action'),
		
		('Et je choisis de vivre', 'Damien Boyer', '2019', 'Documentaire'),
		('Et je choisis de vivre', 'Damien Boyer', '2019', 'Documentaire'),
		('Et je choisis de vivre', 'Damien Boyer', '2019', 'Documentaire'),
		('Sugar Man', 'Malik Bendjelloul', '2012', 'Documentaire'),
		('Sugar Man', 'Malik Bendjelloul', '2012', 'Documentaire'),
		
		('Shining', 'Stanley Kubrick', '1980', 'Horreur'),
		('Shining', 'Stanley Kubrick', '1980', 'Horreur'),
		('Shining', 'Stanley Kubrick', '1980', 'Horreur'),
		('Shining', 'Stanley Kubrick', '1980', 'Horreur'),
		('Shining', 'Stanley Kubrick', '1980', 'Horreur'),
		('Conjuring: Les dossiers Warren', 'James Wan', '2013', 'Horreur'),
		('Conjuring: Les dossiers Warren', 'James Wan', '2013', 'Horreur'),
		('The Thing', 'John Carpenter', '1982', 'Horreur'),
		('The Thing', 'John Carpenter', '1982', 'Horreur'),
		('The Thing', 'John Carpenter', '1982', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Saw', 'James Wan', '2004', 'Horreur'),
		('Scream', 'Wes Craven', '1997', 'Horreur'),
		('Scream', 'Wes Craven', '1997', 'Horreur'),
		('Scream', 'Wes Craven', '1997', 'Horreur'),
		('Scream', 'Wes Craven', '1997', 'Horreur')
)
INSERT INTO DVD (name, realisator, releaseYear, genre)
SELECT val.n, val.c, val.y, CINEMATICGENRE.id
FROM val LEFT JOIN CINEMATICGENRE
ON val.g = CINEMATICGENRE.name;

WITH val (n, c, y, g) AS (
	VALUES
		('Sheer Heart Attack', 'Queen', '1974', 'Rock'),
		('Back in Black', 'ACDC', '1980', 'Rock')
)
INSERT INTO CD (name, releasedBy, releaseYear, genre)
SELECT val.n, val.c, val.y, MUSICALGENRE.id
FROM val LEFT JOIN MUSICALGENRE
ON val.g = MUSICALGENRE.name;

-- EOF script-bibliobrette-db.sql