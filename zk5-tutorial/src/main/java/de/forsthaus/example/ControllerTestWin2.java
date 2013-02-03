package de.forsthaus.example;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.Label;
import org.zkoss.zul.Window;

import de.forsthaus.util.ZkossComponentTreeUtil;

/** Controller class for test.zul page
 * 
 *  http://www.zkoss.org/forum/listComment/13874
 *  
 *  */

public class ControllerTestWin2 extends GenericForwardComposer {
	// private ZKCommonUtils zkCommonUtils = new ZKCommonUtils();
	// Auto-wire components
	private Window testWin;
	private Label currDateLabel;

	/** Controller constructor */
	public ControllerTestWin2() {
		System.out.println("================= Checking Order of execution ==================");
		System.out.println("this is Constructor");
	}// end contructor

	@Override
	public void doAfterCompose(Component win) throws Exception {
		super.doAfterCompose(win);
		System.out.println("This is doAfterCompose()");
	}// end method

	/** currDateLabel onCreate event-handler */
	public void onCreateDateLabel(Event event) throws Exception {
		System.out.println("this is Label onCreate");

		System.out.println("Count of components: " + testWin.getChildren().size());
		// helper class for showing the DOM tree
		System.out.println(ZkossComponentTreeUtil.getZulTree(testWin));
	}// end method
}// end class
