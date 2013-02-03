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
package de.forsthaus.common.menu.util;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;

import de.forsthaus.UserWorkspace;
import de.forsthaus.common.menu.domain.IMenuDomain;
import de.forsthaus.common.menu.domain.MenuDomain;
import de.forsthaus.common.menu.domain.MetaMenuFactory;

/**
 * @author bbruhns
 * 
 */
abstract public class ZkossMenuFactory implements Serializable {

	private static final long serialVersionUID = 142621423557135573L;
	private final Log loger = LogFactory.getLog(getClass());

	final private LinkedList<Component> stack;
	final private UserWorkspace workspace;

	protected ZkossMenuFactory(Component component) {
		super();
		this.workspace = UserWorkspace.getInstance();

		assert component != null : "Parent component is null!";
		assert this.workspace != null : "No UserWorkspace exists!";

		long t1 = System.nanoTime();

		this.stack = new LinkedList<Component>();
		push(component);

		createMenu(MetaMenuFactory.getRootMenuDomain().getItems());

		// if (getLogger().isTraceEnabled()) {
		// t1 = System.nanoTime() - t1;
		// getLogger().trace("Needed time for inserting the menu: " + t1 /
		// 1000000 + "ms");
		// getLogger().trace("\n" +
		// ZkossComponentTreeUtil.getZulTree(component));
		// }
	}

	private void createMenu(List<IMenuDomain> items) {
		if (items.isEmpty()) {
			return;
		}
		for (final IMenuDomain menuDomain : items) {
			if (menuDomain instanceof MenuDomain) {
				final MenuDomain menu = (MenuDomain) menuDomain;
				if (addSubMenuImpl(menu)) {
					createMenu(menu.getItems());
					ebeneHoch();
				}
			} else {
				addItemImpl(menuDomain);
			}
		}
	}

	private void addItemImpl(IMenuDomain itemDomain) {
		if (isAllowed(itemDomain)) {
			setAttributes(itemDomain, createItemComponent(getCurrentComponent()));
		}
	}

	abstract protected ILabelElement createItemComponent(Component parent);

	private boolean addSubMenuImpl(MenuDomain menu) {
		if (isAllowed(menu)) {
			final MenuFactoryDto dto = createMenuComponent(getCurrentComponent(), menu.isOpen());

			setAttributes(menu, dto.getNode());

			push(dto.getParent());

			return true;
		}
		return false;
	}

	abstract protected MenuFactoryDto createMenuComponent(Component parent, boolean open);

	private boolean isAllowed(IMenuDomain treecellValue) {
		return isAllowed(treecellValue.getRightName());
	}

	private void ebeneHoch() {
		poll();
	}

	private Component getCurrentComponent() {
		return peek();
	}

	private Log getLogger() {
		return this.loger;
	}

	private UserWorkspace getWorkspace() {
		return this.workspace;
	}

	private boolean isAllowed(String rightName) {
		if (StringUtils.isEmpty(rightName)) {
			return true;
		}
		return getWorkspace().isAllowed(rightName);
	}

	private Component peek() {
		return this.stack.peek();
	}

	private Component poll() {
		try {
			return this.stack.poll();
		} finally {
			if (this.stack.isEmpty()) {
				throw new RuntimeException("Root no longer exists!");
			}
		}
	}

	private void push(Component e) {
		this.stack.push(e);
	}

	protected void setAttributes(IMenuDomain treecellValue, ILabelElement defaultTreecell) {
		if (treecellValue.isWithOnClickAction() == null || treecellValue.isWithOnClickAction().booleanValue()) {
			defaultTreecell.setZulNavigation(treecellValue.getZulNavigation());

			if (!StringUtils.isEmpty(treecellValue.getIconName())) {
				defaultTreecell.setImage(treecellValue.getIconName());
			}
		}

		setAttributesWithoutAction(treecellValue, defaultTreecell);
	}

	private void setAttributesWithoutAction(IMenuDomain treecellValue, ILabelElement defaultTreecell) {
		assert treecellValue.getId() != null : "In mainmenu.xml file is a node who's ID is missing!";

		defaultTreecell.setId(treecellValue.getId());
		String label = treecellValue.getLabel();
		if (StringUtils.isEmpty(label)) {
			label = Labels.getLabel(treecellValue.getId());
		} else {
			label = Labels.getLabel(label);
		}
		defaultTreecell.setLabel(" " + label);
	}
}
