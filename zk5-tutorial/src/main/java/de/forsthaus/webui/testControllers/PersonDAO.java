package de.forsthaus.webui.testControllers;

import java.util.List;

/**
 * DAO methods for the person bean.<br>
 * 
 * @author sgerth sge(at)forsthaus(dot)de
 * 
 */
public interface PersonDAO {

	/**
	 * EN: Gets back a list off all persons.<br>
	 * DE: Gibt eine Liste aller Personen Objekte zurueck.<br>
	 * 
	 * @return List of Persons / Liste von Personen
	 */
	public List<Person> getAllPersons();

	/**
	 * EN: Gets back a list off all persons by their LastName.<br>
	 * DE: Gibt eine Liste aller Personen zurueck deren Nachname mit dem
	 * Parameter uebereinstimmt.<br>
	 * 
	 * @param lastName
	 *            LastName for searching
	 * 
	 * @return List of Persons / Liste von Personen
	 */
	public List<Person> getPersonsByLastName(String lastName);

	/**
	 * EN: Gets back the first persons in the list.<br>
	 * DE: Gibt das erste Personen Objekt in der Liste zurueck.<br>
	 * 
	 * @return Person obj / Person obj
	 */
	public Person getFirstPersonInList();

}
