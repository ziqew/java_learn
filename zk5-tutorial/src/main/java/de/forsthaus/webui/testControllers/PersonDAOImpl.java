package de.forsthaus.webui.testControllers;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

/**
 * EN: DAO method implementation for the person bean.<br>
 * DE: DAO Methoden Implementierung fuer den person bean.<br>
 * 
 * @author sgerth sge(at)forsthaus(dot)de
 * 
 */
public class PersonDAOImpl implements PersonDAO {

	public PersonDAOImpl() {
	}

	@Override
	public List<Person> getAllPersons() {
		List<Person> result = new ArrayList<Person>();

		Person person;
		person = new Person(1, "Hans", "Möller", "Hamburg", "Hauptstrasse 23", "034568234", "3786487634");
		result.add(person);
		person = new Person(2, "Werner", "Meier", "Freiburg", "Hauptstrasse 6", "74837468234", "45653786487634");
		result.add(person);
		person = new Person(3, "Lutz", "Hafflinger", "Berlin", "An der Siegessäule 234", "2347468234", "6123786487634");
		result.add(person);
		person = new Person(4, "Bill", "Gates", "New York", "50, street 1234", "2347468234", "111213786487634");
		result.add(person);
		person = new Person(5, "Shawn", "Cassidy", "New Orleans", "23, baker street 343", "1247468234", "1323786487634");
		result.add(person);
		person = new Person(6, "Hans", "Müller", "Hamburg", "Hauptstrasse 23", "034568234", "2223786487634");
		result.add(person);
		person = new Person(7, "Uli", "Huber", "Mannheim", "In den Weihern 12", "034568234", "1113786487634");
		result.add(person);
		person = new Person(8, "Stephan", "Schneider", "Olvenstedt", "Hasselbachplatz 44", "034568234", "1233786487634");
		result.add(person);
		person = new Person(9, "Volker", "Schindler", "Frankfurt", "Am breiten Weg 21", "034568234", "2133786487634");
		result.add(person);
		person = new Person(10, "Rüdiger", "Krombach", "München", "Werner-Siemens-Ring 45", "034568234", "1563786487634");
		result.add(person);
		person = new Person(11, "Felix", "Haudrauf", "Bischoffingen", "Hauptstrasse 81", "034568234", "1873786487634");
		result.add(person);
		person = new Person(12, "Björn", "Weissglut", "Stuttgart", "Ambrosiusstrasse 34", "034568234", "1673786487634");
		result.add(person);
		person = new Person(13, "Klaus", "Ritter", "Ober Ursel", "Stoffelweg 66", "034568234", "4443786487634");
		result.add(person);
		person = new Person(14, "Michael", "Urbräu", "Unter Ursel", "Neben den Gleisen 34", "034568234", "4553786487634");
		result.add(person);
		person = new Person(15, "Helmut", "Riegeler", "Hügelheim", "Am Bach 12", "034568234", "663786487634");
		result.add(person);
		person = new Person(16, "Xaver", "Ganther", "Stendal", "Hauptstrasse 45", "034568234", "773786487634");
		result.add(person);
		person = new Person(17, "Hans", "Meier", "Hamburg", "Hauptstrasse 23", "034568234", "883786487634");
		result.add(person);
		person = new Person(18, "Werner", "Meier", "Freiburg", "Hauptstrasse 6", "74837468234", "993786487634");
		result.add(person);
		person = new Person(19, "Lutz", "Ritter", "Berlin", "An der Siegessäule 234", "2347468234", "7773786487634");
		result.add(person);
		person = new Person(20, "Bill", "Gates", "New York", "50, street 1234", "2347468234", "443786487634");
		result.add(person);
		person = new Person(21, "Shawn", "Cassidy", "New Orleans", "23, baker street 343", "1247468234", "5553786487634");
		result.add(person);
		person = new Person(22, "Hans", "Meier", "Stendal", "Hauptstrasse 23", "034568234", "4323786487634");
		result.add(person);
		person = new Person(23, "Uli", "Haudrauf", "Mannheim", "In den Weihern 12", "034568234", "1763786487634");
		result.add(person);
		person = new Person(24, "Stephan", "Schneider", "Olvenstedt", "Hasselbachplatz 44", "034568234", "7243786487634");
		result.add(person);
		person = new Person(25, "Volker", "Schindler", "Hügelheim", "Am breiten Weg 21", "034568234", "3763786487634");
		result.add(person);
		person = new Person(26, "Rüdiger", "Krombach", "München", "Werner-Siemens-Ring 45", "034568234", "17233786487634");
		result.add(person);
		person = new Person(27, "Felix", "Haudrauf", "Bischoffingen", "Hauptstrasse 81", "034568234", "3453786487634");
		result.add(person);
		person = new Person(28, "Björn", "Cassidy", "Stuttgart", "Ambrosiusstrasse 34", "034568234", "5643786487634");
		result.add(person);
		person = new Person(29, "Klaus", "Ritter", "Ober Ursel", "Stoffelweg 66", "034568234", "5673786487634");
		result.add(person);
		person = new Person(30, "Michael", "Riegeler", "Mannheim", "Neben den Gleisen 34", "034568234", "8883786487634");
		result.add(person);
		person = new Person(31, "Helmut", "Riegeler", "München", "Am Bach 12", "034568234", "9993786487634");
		result.add(person);
		person = new Person(32, "Xaver", "Ganther", "Stendal", "Hauptstrasse 45", "034568234", "9873786487634");
		result.add(person);
		person = new Person(33, "Hans", "Miller", "Hamburg", "Hauptstrasse 23", "034568234", "3453786487634");
		result.add(person);
		person = new Person(34, "Werner", "Meier", "Freiburg", "Hauptstrasse 6", "74837468234", "2953786487634");
		result.add(person);
		person = new Person(35, "Lutz", "Hafflinger", "Berlin", "An der Siegessäule 234", "2347468234", "5433786487634");
		result.add(person);
		person = new Person(36, "Bill", "Gates", "New York", "50, street 1234", "2347468234", "9993786487634");
		result.add(person);
		person = new Person(37, "Shawn", "Cassidy", "New Orleans", "23, baker street 343", "1247468234", "9123786487634");
		result.add(person);
		person = new Person(38, "Hans", "Riegeler", "Hamburg", "Hauptstrasse 23", "034568234", "3873786487634");
		result.add(person);
		person = new Person(39, "Uli", "Urbräu", "Mannheim", "In den Weihern 12", "034568234", "9153786487634");
		result.add(person);
		person = new Person(40, "Stephan", "Ritter", "Olvenstedt", "Hasselbachplatz 44", "034568234", "1093786487634");
		result.add(person);
		person = new Person(41, "Volker", "Haudrauf", "Frankfurt", "Am breiten Weg 21", "034568234", "1043786487634");
		result.add(person);
		person = new Person(42, "Rüdiger", "Krombach", "Hügelheim", "Werner-Siemens-Ring 45", "034568234", "1023786487634");
		result.add(person);
		person = new Person(43, "Felix", "Haudrauf", "Bischoffingen", "Hauptstrasse 81", "034568234", "1003786487634");
		result.add(person);
		person = new Person(44, "Björn", "Weissglut", "Stuttgart", "Ambrosiusstrasse 34", "034568234", "1083786487634");
		result.add(person);
		person = new Person(45, "Klaus", "Ritter", "Ober Ursel", "Stoffelweg 66", "034568234", "2346487634");
		result.add(person);
		person = new Person(46, "Michael", "Urbräu", "Bischoffingen", "Neben den Gleisen 34", "034568234", "8763786487634");
		result.add(person);
		person = new Person(47, "Helmut", "Riegeler", "Hamburg", "Am Bach 12", "034568234", "34653786487634");
		result.add(person);

		return result;
	}

	@Override
	public List<Person> getPersonsByLastName(String lastName) {

		List<Person> allList = new ArrayList<Person>();
		List<Person> result = new ArrayList<Person>();

		allList = getAllPersons();

		if (StringUtils.isEmpty(lastName) && StringUtils.isBlank(lastName)) {
			result = allList;
			return result;
		}

		int i = 0;
		for (Person person : allList) {
			if (((Person) allList.get(i)).getLastName().equalsIgnoreCase(lastName)) {
				result.add(person);

			}
			i = i + 1;
		}

		return result;
	}

	@Override
	public Person getFirstPersonInList() {
		Person person = null;

		List<Person> list = getAllPersons();

		if (list.size() > 0) {
			person = (Person) list.get(0);
		}

		return person;
	}

}
