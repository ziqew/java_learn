package de.forsthaus.webui.util;

import java.io.Serializable;
import java.util.Locale;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Window;

public class HelpWindowCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;

	private Window helpWindow; // autowired
	private Component parent;
	private String locale;

	private Iframe helpMenuIframe;
	private Iframe helpContentIframe;

	private final String helpPagePath = "Http://www.forsthaus.de/zkoss/zksample2/help/pages/";

	public HelpWindowCtrl() {
		super();

	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		if (this.arg.containsKey("parentComponent")) {
			this.parent = (Component) this.arg.get("parentComponent");
		}

		loadHelpPage();

		this.helpWindow.doModal();

	}

	public void onCreate$helpPopup() {
	}

	private void loadHelpPage() {

		final Locale locale = org.zkoss.util.Locales.getCurrent();
		String loc = locale.getCountry().toUpperCase();

		// System.out.println(loc);

		if (loc != "DE") {
			loc = "";
		} else {
			loc = "_" + loc;
		}

		this.helpMenuIframe.setSrc(this.helpPagePath + "/" + "index" + loc + ".html");
		this.helpContentIframe.setSrc("http://www.forsthaus.de");

	}

	public void onClose(Event event) {

	}

}
