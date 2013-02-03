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
package de.forsthaus.common.menu.tree;

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Treechildren;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

import de.forsthaus.common.menu.util.ILabelElement;
import de.forsthaus.common.menu.util.MenuFactoryDto;
import de.forsthaus.common.menu.util.ZkossMenuFactory;

/**
 * @author bbruhns
 * 
 */
public class ZkossTreeMenuFactory extends ZkossMenuFactory {

	private static final long serialVersionUID = -1601202637698812546L;

	public static void addMainMenu(Component component) {
		new ZkossTreeMenuFactory(component);
	}

	/**
	 * @param component
	 */
	private ZkossTreeMenuFactory(Component component) {
		super(component);
	}

	@Override
	protected MenuFactoryDto createMenuComponent(Component parent, boolean open) {
		final Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		treeitem.setOpen(open);

		final ILabelElement item = insertTreeCell(treeitem);

		final Treechildren treechildren = new Treechildren();
		treeitem.appendChild(treechildren);

		return new MenuFactoryDto(treechildren, item);
	}

	@Override
	protected ILabelElement createItemComponent(Component parent) {
		final Treeitem treeitem = new Treeitem();
		parent.appendChild(treeitem);

		final ILabelElement item = insertTreeCell(treeitem);

		return item;
	}

	private ILabelElement insertTreeCell(Component parent) {
		final Treerow treerow = new Treerow();
		parent.appendChild(treerow);

		final DefaultTreecell treecell = new DefaultTreecell();
		treerow.appendChild(treecell);

		return treecell;
	}

}
