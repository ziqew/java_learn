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
package de.forsthaus.webui.calendar.model;

import java.util.Date;

import org.zkoss.calendar.api.CalendarEvent;

import de.forsthaus.backend.model.SecUser;

/**
 * DB oriented Implementation of the calendarEvent for rendering the events
 * data.
 * 
 * @author sge 
 * 
 */
public class MySimpleCalendarEvent implements CalendarEvent {

	// The id from the DB
	private long _id;

	// The logged in user
	private SecUser _user;

	// Hibernate versioning field
	private int _version;

	private String _headerColor = "";
	private String _contentColor = "";
	private String _content = "";
	private String _title = "";
	private Date _beginDate;
	private Date _endDate;
	private boolean _locked;

	// ####################################################
	// ############ Zksample2 db properties ###############
	// ####################################################

	public long getId() {
		return _id;
	}

	public void setId(long id) {
		_id = id;
	}

	public void setUser(SecUser _user) {
		this._user = _user;
	}

	public SecUser getUser() {
		return _user;
	}

	public void setVersion(int _version) {
		this._version = _version;
	}

	public int getVersion() {
		return _version;
	}

	// ####################################################
	// ################## ZK properties ###################
	// ####################################################

	public Date getBeginDate() {
		return _beginDate;
	}

	public void setBeginDate(Date beginDate) {
		_beginDate = beginDate;
	}

	public Date getEndDate() {
		return _endDate;
	}

	public void setEndDate(Date endDate) {
		_endDate = endDate;
	}

	public String getContent() {
		return _content;
	}

	public void setContent(String content) {
		_content = content;
	}

	public String getTitle() {
		return _title;
	}

	public void setTitle(String title) {
		_title = title;
	}

	public String getContentColor() {
		return _contentColor;
	}

	public void setContentColor(String ccolor) {
		_contentColor = ccolor;
	}

	public String getHeaderColor() {
		return _headerColor;
	}

	public void setHeaderColor(String hcolor) {
		_headerColor = hcolor;
	}

	public String getZclass() {
		return "z-calevent";
	}

	public boolean isLocked() {
		return _locked;
	}

	public void setLocked(boolean locked) {
		_locked = locked;
	}

}
