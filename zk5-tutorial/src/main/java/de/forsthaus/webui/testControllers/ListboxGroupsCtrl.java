package de.forsthaus.webui.testControllers;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zkplus.databind.AnnotateDataBinder;
import org.zkoss.zul.GroupsModel;
import org.zkoss.zul.SimpleGroupsModel;

@SuppressWarnings("serial")
public class ListboxGroupsCtrl extends GenericForwardComposer {

	// Databinding
	protected transient AnnotateDataBinder binder;

	Object[][] data = { { new Datem("Cell 1-0", "First") }, // Group 1
			{ new Datem("Cell 2-0", "Second"), new Datem("Cell 2-1", "Third") }, // Group
																					// 2
			{ new Datem("Cell 3-0", "Fourth"), new Datem("Cell 3-1", "Fifth"), new Datem("Cell 3-2", "Sixth") } // Group
																												// 3
	};

	Object[] heads = { "Group 1", "Group 2", "Group 3" };

	GroupsModel groupsmodel = new SimpleGroupsModel(data, heads);
	Datem selected;

	@Override
	public void doAfterCompose(Component window) throws Exception {
		super.doAfterCompose(window);

		/**
		 * Set an 'alias' for this composer name to access it in the zul-file.<br>
		 * Set the parameter 'recurse' to 'false' to avoid problems with
		 * managing more than one zul-file in one page. Otherwise it would be
		 * overridden and can ends in curious error messages.
		 */
		this.self.setAttribute("controller", this, false);
	}

	public void onSelect$groupListbox(Event event) {
//		System.out.println("Count Groups : " + getGroupsmodel().getGroupCount());
//		System.out.println("First Group : " + getGroupsmodel().getGroup(0));
//		System.out.println("Second Group : " + getGroupsmodel().getGroup(1));
//		System.out.println("Third Group : " + getGroupsmodel().getGroup(2));
	}

	public void onCreate$winListboxGroups(Event event) throws Exception {
		this.binder = (AnnotateDataBinder) event.getTarget().getAttribute("binder", true);
		this.binder.loadAll();
	}

	class Datem {
		String string1;
		String string2;

		public Datem(String val1, String val2) {
			string1 = val1;
			string2 = val2;
		}

		public String getString1() {
			return string1;
		}

		public void setString1(String val) {
			string1 = val;
		}

		public String getString2() {
			return string2;
		}

		public void setString2(String val) {
			string2 = val;
		}

		public String toString() {
			return string1 + " : " + string2 + "333333333";
		}
	}

	public GroupsModel getGroupsmodel() {
		return groupsmodel;
	}

	public void setGroupsmodel(GroupsModel groupsmodel) {
		this.groupsmodel = groupsmodel;
	}

	public Datem getSelected() {
		return selected;
	}

	public void setSelected(Datem selected) {
		this.selected = selected;
	}

}
