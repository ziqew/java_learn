package de.forsthaus.webui.util.test;

import java.io.Serializable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Window;

/**
 * Controller for let the focus jump from textbox to textbox by press the ENTER
 * key.
 * 
 * @see http
 *      ://www.zkoss.org/forum/listComment/20977-Enter-next-Tabindex-component
 * @author Stephan Gerth
 * 
 */
public class TestSamplesMainCtrl extends GenericForwardComposer implements Serializable {

	private static final long serialVersionUID = 1L;

	protected Window windowTestMain; // autowired

	/**
	 * Default constructor.
	 */
	public TestSamplesMainCtrl() {
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);
	}

	public void onClick$btnChangeTextboxFocus(Event event) {

		Executions.createComponents("/WEB-INF/pages/test/enterTab.zul", windowTestMain, null);
	}

	public void onClick$btnSenthil_vertical_menu_css(Event event) {

		Executions.createComponents("/WEB-INF/pages/test/senthil_vertical_menu_css/senthil_vertical_menu_css.zul", windowTestMain, null);
	}

}
