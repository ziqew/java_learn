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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.calendar.Calendars;
import org.zkoss.calendar.event.CalendarsEvent;
import org.zkoss.calendar.impl.SimpleCalendarModel;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Div;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.West;
import org.zkoss.zul.Window;

import de.forsthaus.backend.model.MyCalendarEvent;
import de.forsthaus.backend.model.SecUser;
import de.forsthaus.backend.service.MyCalendarEventService;
import de.forsthaus.policy.model.UserImpl;
import de.forsthaus.webui.calendar.model.CalendarDateFormatter;
import de.forsthaus.webui.calendar.model.MySimpleCalendarEvent;
import de.forsthaus.webui.util.GFCBaseCtrl;
import de.forsthaus.webui.util.ZksampleMessageUtils;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * Main controller for the calendar module.<br>
 * <br>
 * zul-file: /WEB-INF/pages/calendar/calendar.zul.<br>
 * <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * <br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class CalendarMainCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(CalendarMainCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowCalendar; // autowired

	protected Borderlayout borderLayout_calendar; // autowired
	protected Div divCenter; // autowired
	protected Calendars cal; // autowired

	protected Button btn_Show1Day; // autowired
	protected Button btn_Show5Days; // autowired
	protected Button btn_ShowWeek; // autowired
	protected Button btn_Show2Weeks; // autowired
	protected Button btn_ShowMonth; // autowired

	private String btnOriginColor = "color: black; font-weight: normal;";
	private String btnPressedColor = "color: red; font-weight: bold;";

	private SimpleCalendarModel calModel;

	// ServiceDAOs / Domain Classes
	MyCalendarEventService calendarEventService;

	/**
	 * default constructor.<br>
	 */
	public CalendarMainCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		setCal(cal);
		cal.setHflex("true");
		cal.setWeekOfYear(true);
		// cal.setEventRender(render)
		cal.setDateFormatter(new CalendarDateFormatter());

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);

		init();

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
	public void onCreate$windowCalendar(Event event) throws Exception {
		doFitSize();
	}

	public void init() {

		// cal.addTimeZone("Mexico", "GMT-6");
		// this.cal.addTimeZone("Germany", "GMT+1");
		// this.cal.setTimeZone("Germany=GMT+1");
		cal.setMold("default");
		cal.setFirstDayOfWeek("monday");
		cal.setDays(7);
		cal.setCurrentDate(new Date());

		btn_Show1Day.setStyle(btnOriginColor);
		btn_Show5Days.setStyle(btnOriginColor);
		btn_ShowWeek.setStyle(btnPressedColor);
		btn_ShowWeek.focus();
		btn_Show2Weeks.setStyle(btnOriginColor);
		btn_ShowMonth.setStyle(btnOriginColor);
	}

	/**
	 * If the onEventCreate event is called. <br>
	 * Loads the create event window.
	 * 
	 * @param event
	 */
	public void onEventCreate$cal(CalendarsEvent event) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("calendarController", this);
		map.put("calendarsEvent", event);

		try {
			Executions.createComponents("/WEB-INF/pages/calendar/cal_createEvent.zul", windowCalendar, map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * If the onEventUpdate event is called. <br>
	 * Handles the resizing or movement of an calendarEvent. Saves the new data
	 * to db.
	 * 
	 * @param event
	 */
	public void onEventUpdate$cal(CalendarsEvent evt) {

		// SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy/MM/d");
		// sdf1.setTimeZone(cal.getDefaultTimeZone());

		int left = evt.getX();
		int top = evt.getY();
		if (top + 100 > evt.getDesktopHeight())
			top = evt.getDesktopHeight() - 100;
		if (left + 330 > evt.getDesktopWidth())
			left = evt.getDesktopWidth() - 330;

		SimpleCalendarModel m = (SimpleCalendarModel) cal.getModel();
		MySimpleCalendarEvent sce = (MySimpleCalendarEvent) evt.getCalendarEvent();
		sce.setBeginDate(evt.getBeginDate());
		sce.setEndDate(evt.getEndDate());
		// update the model
		// m.update(sce); <-- if activated, later an error occurs

		// prepare the backend Bean
		MyCalendarEvent calEvt = getCalendarEventService().getNewCalendarEvent();
		calEvt.setId(sce.getId());
		calEvt.setSecUser(sce.getUser());
		calEvt.setVersion(sce.getVersion());
		calEvt.setTitle(sce.getTitle());
		calEvt.setContent(sce.getContent());
		calEvt.setBeginDate(sce.getBeginDate());
		calEvt.setEndDate(sce.getEndDate());
		calEvt.setHeaderColor(sce.getHeaderColor());
		calEvt.setContentColor(sce.getContentColor());
		// Save the calendar event to database
		try {
			getCalendarEventService().saveOrUpdate(calEvt);
		} catch (Exception e) {
			e.printStackTrace();
		}

		try {
			synchronizeModel();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * If the onEventEdit event is called. <br>
	 * Loads the edit event window.
	 * 
	 * @param event
	 */
	public void onEventEdit$cal(CalendarsEvent event) {

		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("calendarController", this);
		map.put("calendarsEvent", event);

		try {
			Executions.createComponents("/WEB-INF/pages/calendar/cal_editEvent.zul", windowCalendar, map);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * When the "help" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		doHelp(event);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws ParseException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException, ParseException {
		doFitSize();
	}

	/**
	 * when the "print calendar" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Calendar_PrintCalendar(Event event) throws InterruptedException {
		ZksampleMessageUtils.doShowNotImplementedMessage();
	}

	/**
	 * when the "resize to full screen" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnFullScreen(Event event) throws InterruptedException {
		doViewInFullScreen(event);
	}

	/**
	 * when the "previous" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Previous(Event event) throws InterruptedException {
		doShowPrevious(event);
	}

	/**
	 * when the "next" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Next(Event event) throws InterruptedException {
		doShowNext(event);
	}

	/**
	 * when the "show 1 Day" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Show1Day(Event event) throws InterruptedException {
		doShow1Day(event);
	}

	/**
	 * when the "show 5 Day" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Show5Days(Event event) throws InterruptedException {
		doShow5Days(event);
	}

	/**
	 * when the "show week" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_ShowWeek(Event event) throws InterruptedException {
		doShowWeek(event);
	}

	/**
	 * when the "show 2 weeks" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_Show2Weeks(Event event) throws InterruptedException {
		doShow2Weeks(event);
	}

	/**
	 * when the "show month" button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btn_ShowMonth(Event event) throws InterruptedException {
		doShowMonth(event);
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Changes the view for full width screen mode by collapsing the west
	 * borderlayout are, means the menu.
	 * 
	 * @param event
	 */
	public void doViewInFullScreen(Event event) {

		final Borderlayout bl = ((Borderlayout) Path.getComponent("/outerIndexWindow/borderlayoutMain"));
		final West west = bl.getWest();

		if (west != null) {
			try {
				if (west.isOpen()) {
					west.setOpen(false);
				} else
					west.setOpen(true);
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}

	/**
	 * Loads the PREVIOUS data for the given view mode.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShowPrevious(Event event) throws InterruptedException {
		this.cal.previousPage();

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Loads the NEXT data for the given view mode.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShowNext(Event event) throws InterruptedException {
		this.cal.nextPage();

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the view for 1 day view.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShow1Day(Event event) throws InterruptedException {
		this.cal.setMold("default");

		btn_Show1Day.setStyle(btnPressedColor);
		btn_Show5Days.setStyle(btnOriginColor);
		btn_ShowWeek.setStyle(btnOriginColor);
		btn_Show2Weeks.setStyle(btnOriginColor);
		btn_ShowMonth.setStyle(btnOriginColor);

		this.cal.setDays(1);

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the view for 5 days view.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShow5Days(Event event) throws InterruptedException {
		this.cal.setMold("default");

		btn_Show1Day.setStyle(btnOriginColor);
		btn_Show5Days.setStyle(btnPressedColor);
		btn_ShowWeek.setStyle(btnOriginColor);
		btn_Show2Weeks.setStyle(btnOriginColor);
		btn_ShowMonth.setStyle(btnOriginColor);

		this.cal.setFirstDayOfWeek("monday");
		this.cal.setDays(5);

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the view for 1 week view.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShowWeek(Event event) throws InterruptedException {
		this.cal.setMold("default");

		btn_Show1Day.setStyle(btnOriginColor);
		btn_Show5Days.setStyle(btnOriginColor);
		btn_ShowWeek.setStyle(btnPressedColor);
		btn_Show2Weeks.setStyle(btnOriginColor);
		btn_ShowMonth.setStyle(btnOriginColor);

		this.cal.setFirstDayOfWeek("monday");
		this.cal.setDays(7);

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the view for 2 weeks view.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShow2Weeks(Event event) throws InterruptedException {
		this.cal.setMold("default");

		btn_Show1Day.setStyle(btnOriginColor);
		btn_Show5Days.setStyle(btnOriginColor);
		btn_ShowWeek.setStyle(btnOriginColor);
		btn_Show2Weeks.setStyle(btnPressedColor);
		btn_ShowMonth.setStyle(btnOriginColor);

		this.cal.setFirstDayOfWeek("monday");
		this.cal.setDays(14);

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Changes the view for month view.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void doShowMonth(Event event) throws InterruptedException {
		this.cal.setMold("month");

		btn_Show1Day.setStyle(btnOriginColor);
		btn_Show5Days.setStyle(btnOriginColor);
		btn_ShowWeek.setStyle(btnOriginColor);
		btn_Show2Weeks.setStyle(btnOriginColor);
		btn_ShowMonth.setStyle(btnPressedColor);

		try {
			synchronizeModel();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Opens the help screen for the current module.
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	private void doHelp(Event event) throws InterruptedException {

		ZksampleMessageUtils.doShowNotImplementedMessage();

		// we stop the propagation of the event, because zk will call ALL events
		// with the same name in the namespace and 'btnHelp' is a standard
		// button in this application and can often appears.
		// Events.getRealOrigin((ForwardEvent) event).stopPropagation();
		event.stopPropagation();
	}

	public void synchronizeModel() throws ParseException {

		SimpleCalendarModel cm = null;
		MySimpleCalendarEvent sce = null;
		Date beginDate = cal.getBeginDate();
		Date endDate = cal.getEndDate();

		// first, delete old stuff
		cm = (SimpleCalendarModel) cal.getModel();

		if (cm != null) {
			cm.clear();
		}

		final SecUser user = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getSecUser();
		// List<MyCalendarEvent> list =
		// getCalendarEventService().getAllCalendarEvents(user.getId());
		List<MyCalendarEvent> list = getCalendarEventService().getCalendarEventsFromToDate(beginDate, endDate, user.getId());

		cm = new SimpleCalendarModel();

		for (MyCalendarEvent myCalendarEvent : list) {
			sce = new MySimpleCalendarEvent();
			sce.setId(myCalendarEvent.getId());
			sce.setUser(myCalendarEvent.getSecUser());
			sce.setVersion(myCalendarEvent.getVersion());
			sce.setBeginDate(myCalendarEvent.getBeginDate());
			sce.setContent(myCalendarEvent.getContent());
			sce.setContentColor(myCalendarEvent.getContentColor());
			sce.setEndDate(myCalendarEvent.getEndDate());
			sce.setHeaderColor(myCalendarEvent.getHeaderColor());
			sce.setLocked(myCalendarEvent.isLocked());
			sce.setTitle(myCalendarEvent.getTitle());

			cm.add(sce);
		}
		setCalModel(cm);

		cal.setModel(cm);
		cal.invalidate();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++++++ Helpers ++++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Recalculates the container size for this controller and resize them.
	 * 
	 * Calculate how many rows have been place in the listbox. Get the
	 * currentDesktopHeight from a hidden Intbox from the index.zul that are
	 * filled by onClientInfo() in the indexCtroller.
	 * 
	 * @throws ParseException
	 */
	public void doFitSize() throws ParseException {
		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 0;
		final int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		final int maxListBoxHeight = height - specialSize - 119;
		// setCountRows((int) Math.round((maxListBoxHeight) / 17.7));
		this.borderLayout_calendar.setHeight(String.valueOf(maxListBoxHeight) + "px");

		synchronizeModel();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setCalModel(SimpleCalendarModel calModel) {
		this.calModel = calModel;
	}

	public SimpleCalendarModel getCalModel() {
		return calModel;
	}

	public Calendars getCal() {
		return cal;
	}

	public void setCal(Calendars cal) {
		this.cal = cal;
	}

	public MyCalendarEventService getCalendarEventService() {
		return calendarEventService;
	}

	public void setCalendarEventService(MyCalendarEventService calendarEventService) {
		this.calendarEventService = calendarEventService;
	}

}
