package de.forsthaus.example;

import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.ext.AfterCompose;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Label;
import org.zkoss.zul.Timer;
import org.zkoss.zul.Toolbar;
import org.zkoss.zul.Window;

/**
 * http://www.zkoss.org/forum/listComment/15007
 * 
 * with a few modifications.
 */
@SuppressWarnings("serial")
public class AlarmMsgNotifyPanel extends Window implements AfterCompose {

	protected Label titleLabel;
	protected Label msgLabel;
	protected Toolbar titlePanel;

	public void afterCompose() {

		Components.wireVariables(this, this); // auto wire variables
		// Components.addForwards(this, this); // auto forward

		this.setPosition("right,bottom");
		this.doOverlapped();

		final Timer timer = new Timer();
		timer.setParent(this);
		timer.setRepeats(true);
		timer.setDelay(2000);
		timer.addEventListener(Events.ON_TIMER, new EventListener() {
			private int i = 0;

			public void onEvent(Event event) throws Exception {
				setMessageEvent(i++ % 3, "hello" + i);
			}
		});

		timer.setRunning(true);
	}

	public void setMessageEvent(int msgType, String msg) {

		if (msgType == 0) {
			titlePanel.setStyle("background:green;");
			this.setStyle("border:1px solid green;");
		} else if (msgType == 1) {
			titlePanel.setStyle("background:orange;");
			this.setStyle("border:1px solid orange;");
		} else if (msgType == 2) {
			titlePanel.setStyle("background:red;");
			this.setStyle("border:1px solid red;");
		}

		// this.doOverlapped();
		msgLabel.setValue(msg);
		titleLabel.setValue("hello");
		Clients.evalJavaScript("jq(\"@window[id=" + this.getUuid() + "]\").hide().fadeIn(1000);");
	}

}