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

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import org.apache.commons.lang.StringUtils;
import org.zkoss.calendar.api.DateFormatter;
import org.zkoss.calendar.impl.SimpleDateFormatter;
import org.zkoss.util.resource.Labels;

import de.forsthaus.webui.util.ZksampleDateFormat;

/**
 * Implementation of the DateFormatter class for the calender component. Used
 * for displaying different date formats in the several views and areas of the
 * calendar.
 * 
 * The format of the dates are stored in the i3-label.properties file.<br>
 * 
 * THIS CLASS IS IN WORK !!!
 * 
 * @author Stephan Gerth
 * 
 */
public class CalendarDateFormatter extends SimpleDateFormatter implements DateFormatter, Serializable {

	private static final long serialVersionUID = 1L;

	private String _ppFormat = "EEE, MMM/d";
	private SimpleDateFormat _df, _wf, _tf, _pf;

	/**
	 * This is for the day/week view, means mold="default" .<br>
	 * EN: Mo 09/12 | Dayshort month/day<br>
	 * DE: Mo 12.09 | Tageskuerzel Tag.Monat<br>
	 */
	@Override
	public String getCaptionByDate(Date date, Locale locale, TimeZone timezone) {

		String sDate = ZksampleDateFormat.getDayMonthFormater().format(date);
		String day = null;

		if (date.getDay() == 0) {
			day = Labels.getLabel("common.dayname.sunday.2");
		} else if (date.getDay() == 1) {
			day = Labels.getLabel("common.dayname.monday.2");
		} else if (date.getDay() == 2) {
			day = Labels.getLabel("common.dayname.tuesday.2");
		} else if (date.getDay() == 3) {
			day = Labels.getLabel("common.dayname.wednesday.2");
		} else if (date.getDay() == 4) {
			day = Labels.getLabel("common.dayname.thursday.2");
		} else if (date.getDay() == 5) {
			day = Labels.getLabel("common.dayname.friday.2");
		} else if (date.getDay() == 6) {
			day = Labels.getLabel("common.dayname.saturday.2");
		}

		day = StringUtils.capitalize(day.toLowerCase());
		String result = day + " " + sDate;

		return result;
	}

	/**
	 * This is for the month view, means mold="month" .<br>
	 * EN: on every first of month the 3 digits month name is shown.<br>
	 * DE: an jedem ersten des Monats wird der Monatsname (3-stellig) angezeigt.<br>
	 */
	@Override
	public String getCaptionByDateOfMonth(Date date, Locale locale, TimeZone timezone) {

		String s = ZksampleDateFormat.getDayNumberFormater().format(date);

		if (date.getDate() == 1) {
			return ZksampleDateFormat.getMonth3DigitsFormater().format(date) + " " + s;
		} else
			return s;
	}

	/**
	 * This is for showing the Daynames in the month view, means mold="month" .<br>
	 * EN: on top of every column the 2 digits day name is shown.<br>
	 * DE: oberhalb jeder Spalte wird der Tagesnamen (2-stellig) angezeigt.<br>
	 */
	@Override
	public String getCaptionByDayOfWeek(Date date, Locale locale, TimeZone timezone) {

		String day = null;

		if (date.getDay() == 0) {
			day = Labels.getLabel("common.dayname.sunday.3");
		} else if (date.getDay() == 1) {
			day = Labels.getLabel("common.dayname.monday.3");
		} else if (date.getDay() == 2) {
			day = Labels.getLabel("common.dayname.tuesday.3");
		} else if (date.getDay() == 3) {
			day = Labels.getLabel("common.dayname.wednesday.3");
		} else if (date.getDay() == 4) {
			day = Labels.getLabel("common.dayname.thursday.3");
		} else if (date.getDay() == 5) {
			day = Labels.getLabel("common.dayname.friday.3");
		} else if (date.getDay() == 6) {
			day = Labels.getLabel("common.dayname.saturday.3");
		}

		day = StringUtils.capitalize(day.toLowerCase());

		return day;
	}

	/**
	 * Returns the caption of the popup title.<br>
	 * EN: ? <br>
	 * DE: ? <br>
	 */
	@Override
	public String getCaptionByPopup(Date date, Locale locale, TimeZone timezone) {
		if (_pf == null) {
			_pf = new SimpleDateFormat(_ppFormat, locale);
		}
		_pf.setTimeZone(timezone);
		return _pf.format(date);
	}

	/**
	 * This is the time that is shown on top of an event.<br>
	 * EN: From - To time on top of the event.<br>
	 * DE: von - bis Zeitangabe oben beim Termin.<br>
	 */
	@Override
	public String getCaptionByTimeOfDay(Date date, Locale locale, TimeZone timezone) {

		String s = ZksampleDateFormat.getTimeFormater().format(date);
		return s;
	}

	/**
	 * Returns the number of week of the year in month mold .<br>
	 * EN: .<br>
	 * DE: .<br>
	 */
	@Override
	public String getCaptionByWeekOfYear(Date date, Locale locale, TimeZone timezone) {
		Calendar cal = Calendar.getInstance(timezone, locale);
		cal.setTime(date);
		return String.valueOf(cal.get(Calendar.WEEK_OF_YEAR));
	}
}
