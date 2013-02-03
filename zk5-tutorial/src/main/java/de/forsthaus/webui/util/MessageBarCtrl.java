package de.forsthaus.webui.util;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.EventQueues;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Center;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;
import org.zkoss.zul.Window;

import de.forsthaus.policy.model.UserImpl;

/**
 * =======================================================================<br>
 * MessageBarController. <br>
 * =======================================================================<br>
 * Works with the EventQueues mechanism of zk 5.x. ALl needed components are
 * created in this class. In the zul-template declare only this controller with
 * 'apply' to a winMessageBar window component.<br>
 * This MessageBarController is for sending and receiving messages from other
 * users.<br>
 * 
 * The message text we do input with a special helper window that is called
 * InputMessageTextBox.java
 * 
 * <pre>
 * < borderlayout >
 *   . . .
 *    < !-- STATUS BAR AREA -- >
 *    < south id="south" border="none" margins="1,0,0,0"
 * 		height="20px" splittable="false" flex="true" >
 * 	      < div id="divSouth" >
 * 
 *          < !-- The MessageBar. Comps are created in the Controller -- >
 *          < window id="winMessageBar" apply="${messageBarCtrl}"
 *                   border="none" width="100%" height="100%" />
 *          < !-- The StatusBar. Comps are created in the Controller -- >
 *          < window id="winStatusBar" apply="${statusBarCtrl}"
 *                   border="none" width="100%" height="100%" />
 *        < /div >
 *    < /south >
 *  < /borderlayout >
 * </pre>
 * 
 * call for the message system:
 * 
 * <pre>
 * EventQueues.lookup(&quot;userNameEventQueue&quot;, EventQueues.APPLICATION, true).publish(new Event(&quot;onChangeSelectedObject&quot;, null, &quot;new Value&quot;));
 * </pre>
 * 
 * 
 * Spring bean declaration:
 * 
 * <pre>
 * < !-- MessageBarCtrl -->
 * < bean id="messageBarCtrl" class="de.forsthaus.webui.util.MessageBarCtrl"
 *    scope="prototype">
 * < /bean>
 * </pre>
 * 
 * since: zk 5.0.0
 * 
 * @author Stephan Gerth
 * 
 */
public class MessageBarCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window winMessageBar; // autowired

	// Indicator column for message buttons
	private Column statusBarMessageIndicator;
	private Toolbarbutton btnOpenMsg;
	private Toolbarbutton btnSendMsg;

	private Window msgWindow = null;
	private String msg = "";
	private String userName;

	/**
	 * Default constructor.
	 */
	public MessageBarCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		// !!! not used at time. Here we can get the User for filtering the
		// incoming
		// messages.
		try {
			userName = ((UserImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Listener for incoming messages ( scope=APPLICATION )
		EventQueues.lookup("testEventQueue", EventQueues.APPLICATION, true).subscribe(new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				final String msg = (String) event.getData();

				// Check if empty, than do not show incoming message
				if (StringUtils.isEmpty(msg)) {
					return;
				}

				setMsg(msg);

				if (msgWindow == null) {

					/**
					 * If you whish to popup the incoming message than uncomment
					 * these next 3 lines.
					 */
					// START Popup
					getMsgWindow();
					((Textbox) getMsgWindow().getFellow("tb")).setValue(getMsg());
					MessageBarCtrl.this.btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
					// END Popup

					/**
					 * If you whish to inform smartly of incoming message than
					 * uncomment the next line for only a blinking Icon.
					 */
					// MessageBarCtrl.this.btnOpenMsg.setImage("/images/icons/incoming_message1_16x16.gif");
				} else {
					Textbox t = ((Textbox) getMsgWindow().getFellow("tb"));
					t.setValue(getMsg());
					// SCROLL the text to the last Message.
					Clients.evalJavaScript("scrollTextbox('" + t.getUuid() + "')");
					// int stPos = t.getText().length();
					// t.setSelectionRange(stPos, getMsg().length() - 1);
				}
			}
		});

	}

	/**
	 * Automatically called method from zk.
	 * 
	 * @param event
	 */
	public void onCreate$winMessageBar(Event event) {

		final Grid grid = new Grid();
		grid.setHeight("100%");
		grid.setWidth("50px");
		grid.setParent(this.winMessageBar);

		final Columns columns = new Columns();
		columns.setSizable(false);
		columns.setParent(grid);

		// Column for the Message buttons
		this.statusBarMessageIndicator = new Column();
		this.statusBarMessageIndicator.setWidth("50px");
		this.statusBarMessageIndicator.setStyle("background-color: #D6DCDE; padding: 0px");
		this.statusBarMessageIndicator.setParent(columns);
		Div div = new Div();
		div.setStyle("padding: 1px;");
		div.setParent(statusBarMessageIndicator);

		// open message button
		this.btnOpenMsg = new Toolbarbutton();
		this.btnOpenMsg.setId("btnOpenMsg");
		this.btnOpenMsg.setWidth("20px");
		this.btnOpenMsg.setHeight("20px");
		this.btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
		this.btnOpenMsg.setTooltiptext(Labels.getLabel("common.Message.Open"));
		this.btnOpenMsg.setParent(div);
		this.btnOpenMsg.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				// 1. Reset to normal image
				btnOpenMsg.setImage("/images/icons/message2_16x16.gif");
				// 2. open the message window
				Window win = getMsgWindow();
				Textbox t = (Textbox) win.getFellow("tb");
				t.setText(getMsg());
				// TODO scroll to latest message
				// Clients.scrollIntoView(t);

			}
		});

		// send message button
		this.btnSendMsg = new Toolbarbutton();
		this.btnSendMsg.setWidth("20px");
		this.btnSendMsg.setHeight("20px");
		this.btnSendMsg.setImage("/images/icons/message1_16x16.gif");
		this.btnSendMsg.setTooltiptext(Labels.getLabel("common.Message.Send"));
		this.btnSendMsg.setParent(div);
		this.btnSendMsg.addEventListener("onClick", new EventListener() {

			@Override
			public void onEvent(Event event) throws Exception {
				// open a box for inserting the message
				Window win = (Window) Path.getComponent("/outerIndexWindow");
				final String str = InputMessageTextBox.show(win);
				EventQueues.lookup("testEventQueue", EventQueues.APPLICATION, true).publish(new Event("onTestEventQueue", null, str));
			}
		});

	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// ++++++++++++++++ Setter/Getter ++++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	public void setMsg(String msg) {
		this.msg = this.msg + "\n" + msg;
		// this.msg = this.msg + "\n" +
		// "_____________________________________________________" + "\n";
	}

	public String getMsg() {
		return msg;
	}

	public void setMsgWindow(Window msgWindow) {
		this.msgWindow = msgWindow;
	}

	/**
	 * Get the message window. <br>
	 * Thanks to Lesstra for adding the borderlayout for nicer looking by
	 * resizing this window.
	 * 
	 * @return
	 */
	public Window getMsgWindow() {

		int msgHeight = 260;

		// if null, create the MessageWindow
		if (msgWindow == null) {
			msgWindow = new Window();
			msgWindow.setId("msgWindow");
			msgWindow.setTitle("Messages");
			msgWindow.setSizable(true);
			msgWindow.setClosable(true);
			msgWindow.setWidth("400px");
			msgWindow.setHeight(String.valueOf(msgHeight) + "px");
			msgWindow.setParent(winMessageBar);
			msgWindow.setStyle("padding: 3px");
			msgWindow.addEventListener("onClose", new EventListener() {

				@Override
				public void onEvent(Event event) throws Exception {
					msgWindow.detach();
					msgWindow = null;
				}
			});

			// ------ added borderlayout --------
			Borderlayout blayout = new Borderlayout();

			Center center = new Center();
			center.setFlex(true);
			center.setBorder("none");
			center.setAutoscroll(true);

			Textbox tb = new Textbox();
			tb.setId("tb");
			tb.setMultiline(true);
			tb.setReadonly(true);
			// tb.setRows(17);
			// tb.setWidth("98%");

			tb.setParent(center);
			center.setParent(blayout);
			blayout.setParent(msgWindow);

			/**
			 * set the bottom of the msgWindow, so that we can see and reach the
			 * messageBar buttons.
			 */
			final int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
			final int h = height - msgHeight + 30;
			msgWindow.setLeft("3px");
			msgWindow.setTop(String.valueOf(h) + "px");

			msgWindow.doOverlapped();

		}

		return msgWindow;
	}
}
