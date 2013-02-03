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
package de.forsthaus.webui.calendar;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.MyCalendarEvent;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.MyCalendarEventService;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.calendar.model.MySimpleCalendarEvent;
import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Controller for the calendar create event module.<br>
 * <br>
 * zul-file: /WEB-INF/pages/calendar/cal_createEvent.zul.<br>
 * <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * 
 * @author bbruhns
 * @author sgerth
 */
public class CalendarCreateEventCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CalendarCreateEventCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window createEventWindow; // autowired

	protected Datebox ppbegin; // autowired
	protected Datebox ppend; // autowired
	protected Checkbox ppallDay; // autowired
	protected Listbox ppbt; // autowired
	protected Listbox ppet; // autowired
	protected Combobox ppcolor; // autowired
	protected Textbox txtb_title; // autowired
	protected Textbox ppcnt; // autowired
	protected Checkbox pplocked; // autowired

	protected Button btnOK; // autowired
	protected Button btnCancel; // autowired

	private CalendarMainCtrl calendarCtrl;
	private CalendarsEvent newEvent;

	// ServiceDAOs / Domain Classes
	MyCalendarEventService calendarEventService;

	/**
	 * default constructor.<br>
	 */
	public CalendarCreateEventCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Get the overhanded MainController.<br>
		 * 2. Get the selected event.<br>
		 */
		if (arg.containsKey("calendarController")) {
			setCalendarCtrl((CalendarMainCtrl) arg.get("calendarController"));
		}
		if (arg.containsKey("calendarsEvent")) {
			setNewEvent(((CalendarsEvent) arg.get("calendarsEvent")));
		} else
			setNewEvent(null);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$createEventWindow(Event event) {
		List dateTime = new LinkedList();

		// Calendar calendar = Calendar.getInstance();
		Calendar calendar = Calendar.getInstance(org.zkoss.util.Locales.getCurrent());
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);

		for (int i = 0; i < 48; i++) {
			dateTime.add(sdf.format(calendar.getTime()));
			calendar.add(Calendar.MINUTE, 30);
		}
		ppbt.setModel(new ListModelList(dateTime));
		ppet.setModel(new ListModelList(dateTime));

		CalendarsEvent evt = getNewEvent();

		int left = evt.getX();
		int top = evt.getY();
		if (top + 245 > evt.getDesktopHeight())
			top = evt.getDesktopHeight() - 245;
		if (left + 410 > evt.getDesktopWidth())
			left = evt.getDesktopWidth() - 410;
		createEventWindow.setLeft(left + "px");
		createEventWindow.setTop(top + "px");
		SimpleDateFormat create_sdf = new SimpleDateFormat("HH:mm");
		create_sdf.setTimeZone(getCalendarCtrl().getCal().getDefaultTimeZone());

		// Calendar calendar =
		// Calendar.getInstance(org.zkoss.util.Locales.getCurrent());
		String[] times = create_sdf.format(evt.getBeginDate()).split(":");
		int hours = Integer.parseInt(times[0]) * 2;
		int mins = Integer.parseInt(times[1]);
		int bdTimeSum = hours + mins;
		if (mins >= 30)
			hours++;
		ppbt.setSelectedIndex(hours);
		times = create_sdf.format(evt.getEndDate()).split(":");
		hours = Integer.parseInt(times[0]) * 2;
		mins = Integer.parseInt(times[1]);
		int edTimeSum = hours + mins;
		if (mins >= 30)
			hours++;
		ppet.setSelectedIndex(hours);
		boolean isAllday = (bdTimeSum + edTimeSum) == 0;
		ppbegin.setTimeZone(getCalendarCtrl().getCal().getDefaultTimeZone());
		ppbegin.setValue(evt.getBeginDate());
		ppend.setTimeZone(getCalendarCtrl().getCal().getDefaultTimeZone());
		ppend.setValue(evt.getEndDate());
		ppallDay.setChecked(isAllday);
		pplocked.setChecked(false);
		ppbt.setVisible(!isAllday);
		ppet.setVisible(!isAllday);

		createEventWindow.setVisible(true);

		createEventWindow.setAttribute("calendars", getCalendarCtrl().getCal());
		createEventWindow.setAttribute("calevent", evt);
		evt.stopClearGhost();
	}

	/**
	 * on closing this window.
	 * 
	 * @param event
	 */
	public void onClose$createEventWindow(Event event) {

		getNewEvent().clearGhost();

		event.stopPropagation();

		createEventWindow.onClose();
	}

	/**
	 * If the button 'OK' is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnOK(Event event) {
		doSave(event);
	}

	/**
	 * If the button 'Cancel' is clicked.
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		doCancel(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Saves the data of an CalendarEvent and closes the window.
	 * 
	 * @param event
	 */
	public void doSave(Event event) {
		Calendars cals = getCalendarCtrl().getCal();

		MySimpleCalendarEvent ce = new MySimpleCalendarEvent();
		Calendar cal = Calendar.getInstance(cals.getDefaultTimeZone());
		Date beginDate = ppbegin.getValue();
		Date endDate = ppend.getValue();

		beginDate.setSeconds(0);
		endDate.setSeconds(0);
		if (!ppallDay.isChecked()) {
			String[] times = ppbt.getSelectedItem().getLabel().split(":");

			cal.setTime(beginDate);
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			beginDate = cal.getTime();
			times = ppet.getSelectedItem().getLabel().split(":");
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, Integer.parseInt(times[0]));
			cal.set(Calendar.MINUTE, Integer.parseInt(times[1]));
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			endDate = cal.getTime();
		}

		if (!beginDate.before(endDate)) {
			alert("The end date cannot be before/equal than begin date!");
			return;
		}

		String[] colors = ((String) ppcolor.getSelectedItem().getValue()).split(",");
		ce.setHeaderColor(colors[0]);
		ce.setContentColor(colors[1]);
		ce.setBeginDate(beginDate);
		ce.setEndDate(endDate);
		ce.setTitle(txtb_title.getValue());
		ce.setContent(ppcnt.getValue());
		ce.setLocked(pplocked.isChecked());
		getCalendarCtrl().getCalModel().add(ce);

		ppcnt.setRawValue("");
		ppbt.setSelectedIndex(0);
		ppet.setSelectedIndex(0);

		final SecUser user = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();

		// prepare the backend Bean
		MyCalendarEvent calEvt = getCalendarEventService().getNewCalendarEvent();
		calEvt.setSecUser(user);
		calEvt.setTitle(ce.getTitle());
		calEvt.setContent(ce.getContent());
		calEvt.setBeginDate(ce.getBeginDate());
		calEvt.setEndDate(ce.getEndDate());
		calEvt.setHeaderColor(colors[0]);
		calEvt.setContentColor(ce.getContentColor());

		// Save the calendar event to database
		try {
			getCalendarEventService().saveOrUpdate(calEvt);
			syncModel();
		} catch (Exception e) {
			e.printStackTrace();
		}

		createEventWindow.onClose();

	}

	/**
	 * Cancels the creation of an CalendarEvent and closes the window.
	 * 
	 * @param event
	 */
	public void doCancel(Event event) {
		// logger.debug(event.toString());

		ppcnt.setRawValue("");
		ppbt.setSelectedIndex(0);
		ppet.setSelectedIndex(0);

		((CalendarsEvent) getNewEvent()).clearGhost();
		createEventWindow.onClose();
	}

	/**
	 * Calls the method for synchronizing the view with the data that are stored
	 * in the db.
	 * 
	 * @throws ParseException
	 */
	private void syncModel() throws ParseException {
		getCalendarCtrl().synchronizeModel();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public CalendarMainCtrl getCalendarCtrl() {
		return calendarCtrl;
	}

	public void setCalendarCtrl(CalendarMainCtrl calendarCtrl) {
		this.calendarCtrl = calendarCtrl;
	}

	public void setNewEvent(CalendarsEvent newEvent) {
		this.newEvent = newEvent;
	}

	public CalendarsEvent getNewEvent() {
		return newEvent;
	}

	public MyCalendarEventService getCalendarEventService() {
		return calendarEventService;
	}

	public void setCalendarEventService(MyCalendarEventService calendarEventService) {
		this.calendarEventService = calendarEventService;
	}
}
