/**
 * Copyright 2010 the original author or authors.
 * 
 * This file is part of Zksample2. http://zksample2.sourceforge.net/
 *
 * Zksample2 is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Zksample2 is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Zksample2.  If not, see <http://www.gnu.org/licenses/gpl.html>.
 */
package de.forsthaus.webui.util.searching;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.trg.search.Filter;

/**
 * EN: This class represents a few <b>types of search operators</b>
 * corresponding to the com.trg.search.Search.java class. <br>
 * from the Hibernate-Generic-DAO framework. <br>
 * <br>
 * The domain model have no corresponding table in a database and has a fixed
 * length of records that should see as the search operators of what to search.
 * It's used only for showing the several operators in a dropDown list. <br>
 * <br>
 * DE: Diese Klasse representiert einige <b>Typen von Suche Operatoren</b> die
 * mit denen der com.trg.search.Search.java Klasse aus dem Hibernate-Generic-DAO
 * Framework korrespondieren. <br>
 * Diese Domain-Model Klasse hat keine Tabelle als Gegenstueck. Es wird
 * lediglich benoetigt um die Such-Operatoren in einer DropDown Liste (Listbox)
 * zur Auswahl anzuzeigen. <br>
 * <br>
 * Int | sign | search operator <br>
 * ------------------------------------------<br>
 * -1 | | no operator (like empty for reset) <br>
 * 0 | = | equals <br>
 * 1 | # | not equal <br>
 * 2 | < | less than <br>
 * 3 | > | greater than <br>
 * 4 | <= | less or equal <br>
 * 5 | >= | greater or equal <br>
 * 7 | ~ | ilike <br>
 *<br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author Stephan Gerth
 * 
 * @changes 05/15/2009: sge Migrating the list models for paging. <br>
 *          07/24/2009: sge changes for clustering.<br>
 * 
 */
public class SearchOperators implements Serializable {

	private static final long serialVersionUID = 1L;

	private transient int searchOperatorId;
	private transient String searchOperatorSign;
	private transient String searchOperatorName;

	/**
	 * default constructor.<br>
	 */
	public SearchOperators() {
	}

	/**
	 * constructor.<br>
	 * 
	 * @param searchOperatorId
	 * @param searchOperatorSign
	 * @param searchOperatorName
	 */
	public SearchOperators(int searchOperatorId, String searchOperatorSign, String searchOperatorName) {
		this.searchOperatorId = searchOperatorId;
		this.searchOperatorSign = searchOperatorSign;
		this.searchOperatorName = searchOperatorName;
	}

	public List<SearchOperators> getAllOperators() {

		List<SearchOperators> result = new ArrayList<SearchOperators>();

		// list position 0
		result.add(new SearchOperators(-1, "", "no operator"));
		// list position 1
		result.add(new SearchOperators(Filter.OP_EQUAL, "=", "equals"));
		// list position 2
		result.add(new SearchOperators(Filter.OP_NOT_EQUAL, "#", "not equal"));
		// list position 3
		result.add(new SearchOperators(Filter.OP_LESS_THAN, "<", "less than"));
		// list position 4
		result.add(new SearchOperators(Filter.OP_GREATER_THAN, ">", "greater than"));
		// list position 5
		result.add(new SearchOperators(Filter.OP_LESS_OR_EQUAL, "<=", "less or equal"));
		// list position 6
		result.add(new SearchOperators(Filter.OP_GREATER_OR_EQUAL, ">=", "greater or equal"));
		// list position 7
		result.add(new SearchOperators(Filter.OP_ILIKE, "~", "ilike"));

		return result;
	}

	@Override
	public int hashCode() {
		return Integer.valueOf(getSearchOperatorId()).hashCode();
	}

	public boolean equals(SearchOperators searchOperators) {
		return getSearchOperatorId() == searchOperators.getSearchOperatorId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof SearchOperators) {
			SearchOperators searchOperators = (SearchOperators) obj;
			return equals(searchOperators);
		}

		return false;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setSearchOperatorId(int searchOperatorId) {
		this.searchOperatorId = searchOperatorId;
	}

	public int getSearchOperatorId() {
		return searchOperatorId;
	}

	public void setSearchOperatorSign(String searchOperatorSign) {
		this.searchOperatorSign = searchOperatorSign;
	}

	public String getSearchOperatorSign() {
		return searchOperatorSign;
	}

	public void setSearchOperatorName(String searchOperatorName) {
		this.searchOperatorName = searchOperatorName;
	}

	public String getSearchOperatorName() {
		return searchOperatorName;
	}

}
