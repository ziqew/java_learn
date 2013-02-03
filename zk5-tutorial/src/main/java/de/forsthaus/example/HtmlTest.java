package de.forsthaus.example;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Div;
import org.zkoss.zul.Html;
import org.zkoss.zul.Window;

public class HtmlTest extends GenericForwardComposer {

	private static final long serialVersionUID = -7162264370053892629L;

	private Window htmlWindow;
	private Div div;
	private String str;

	public HtmlTest() {
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		String s = "Hello world! Please	<a href='http://www.google.com'>click here</a> to get redirected to google.com!";
		Html html = new Html(s);
		div.appendChild(html);
	}
}
