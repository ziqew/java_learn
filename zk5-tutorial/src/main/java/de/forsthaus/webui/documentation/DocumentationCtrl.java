package de.forsthaus.webui.documentation;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Window;

import de.forsthaus.UserWorkspace;
import de.forsthaus.webui.util.GFCBaseCtrl;

/**
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/documentation/documentation.zul
 * file. <br>
 * +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * 
 * @author bbruhns
 * @author sgerth
 */
public class DocumentationCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(DocumentationCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window windowDocumentation; // autowired

	private Borderlayout borderLayout_documentation; // autowired

	private Iframe iFrameDocumentation; // autowired
	private AMedia media = null;
	// private final String docURL =
	// "http://mesh.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf";
	private final String docURL = "http://aarnet.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf";

	/**
	 * default constructor.<br>
	 */
	public DocumentationCtrl() {
		super();
	}

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * 1. Set an 'alias' for this composer name to access it in the
		 * zul-file.<br>
		 * 2. Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);

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
	public void onCreate$windowDocumentation(Event event) throws Exception {

		doFitSize();

		/**
		 * Calls the long running method that loads the pdf and closes the echo
		 * event message if ready.
		 */
		loadDocument(event);
	}

	/**
	 * when the "refresh" button is clicked. <br>
	 * <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnRefresh(Event event) throws InterruptedException {
		doFitSize();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++++ Business Logic ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	/**
	 * Loads the document and startet an echo event message.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void loadDocument(Event event) {

		// we load the pdf in an Echo-event to show a message to the
		// user for this long running operation.
		Clients.showBusy(Labels.getLabel("message.Information.LongOperationIsRunning"));
		Events.echoEvent("onLoadDocument", DocumentationCtrl.this.windowDocumentation, null);
	}

	public void onLoadDocument() {

		try {
			// media = new AMedia("zksample2-doc.pdf", "pdf", "application/pdf",
			// new
			// java.net.URL("http://ncu.dl.sourceforge.net/project/zksample2/Documentation/zksample2-doc.pdf"),
			// null);
			//
			// if (media != null) {
			// iFrameDocumentation.setContent(media);
			// }
			iFrameDocumentation.setSrc(docURL);

		} finally {
			Clients.clearBusy(); // close the message
		}

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
	 */
	public void doFitSize() {
		// normally 0 ! Or we have a i.e. a toolBar on top of the listBox.
		final int specialSize = 26;
		final int menuOffset = UserWorkspace.getInstance().getMenuOffset();
		int height = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue();
		height = height - menuOffset;
		final int maxListBoxHeight = height - specialSize - 88;
		this.borderLayout_documentation.setHeight(String.valueOf(maxListBoxHeight) + "px");

		borderLayout_documentation.invalidate();

		// if (iFrameDocumentation.getContent() != null) {
		// iFrameDocumentation.setContent(null);
		// iFrameDocumentation.setWidth("100%");
		// iFrameDocumentation.setHeight("100%");
		// this.iFrameDocumentation.invalidate();
		// iFrameDocumentation.setContent(media);
		// }
	}

}
