package de.forsthaus.webui.testControllers;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.util.GenericForwardComposer;
import org.zkoss.zul.DefaultTreeModel;
import org.zkoss.zul.DefaultTreeNode;
import org.zkoss.zul.Tree;
import org.zkoss.zul.Treecol;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

public class AutoSortTreeCtrl extends GenericForwardComposer {

	private static final long serialVersionUID = 1L;

	private Treecol col;
	private Tree tree;

	public AutoSortTreeCtrl() {
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		super.doAfterCompose(comp);
		initRenderer();
		initData();
	}

	/**
	 * Ads some dummy data to the tree
	 */
	private void initData() {
		Person steve = new Person(0, "Steve", "Jobs", "Chicago", "street", "phone number", "fax number");
		Person mark = new Person(1, "Mark", "Zuckerberg", "Dallas", "street", "phone number", "fax number");
		Person barack = new Person(2, "Barack", "Obama", "Chicago", "street", "phone number", "fax number");

		List<Person> persons = new ArrayList<Person>();
		persons.add(steve);
		persons.add(mark);
		persons.add(barack);

		List<DefaultTreeNode> nodes = toNode(persons);

		DefaultTreeNode rootNode = new DefaultTreeNode(null, nodes);
		DefaultTreeModel model = new DefaultTreeModel(rootNode);

		tree.setModel(model);
	}

	/**
	 * Sets a renderer for the tree
	 */
	private void initRenderer() {
		tree.setItemRenderer(new TreeitemRenderer() {

			public void render(Treeitem item, Object data) throws Exception {
				String label = "?";
				if (data instanceof DefaultTreeNode) {
					DefaultTreeNode node = (DefaultTreeNode) data;
					Object nodeData = node.getData();
					label = nodeData.toString();
				}
				item.setLabel(label);
			}
		});
	}

	/**
	 * Converts the given list into a list of tree nodes
	 */
	private List<DefaultTreeNode> toNode(List<?> objects) {
		List<DefaultTreeNode> nodes = new ArrayList<DefaultTreeNode>();
		for (Object object : objects) {
			nodes.add(new DefaultTreeNode(object, new ArrayList<DefaultTreeNode>()));
		}
		return nodes;
	}

}
