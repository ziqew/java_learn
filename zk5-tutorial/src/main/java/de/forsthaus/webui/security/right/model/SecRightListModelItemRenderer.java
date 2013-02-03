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
package de.forsthaus.webui.security.right.model;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zkplus.spring.SpringUtil;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

import de.forsthaus.backend.model.SecRight;
import de.forsthaus.backend.model.SecTyp;
import de.forsthaus.backend.service.SecurityService;

/**
 * Item renderer for listitems in the listbox.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class SecRightListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = Logger.getLogger(SecRightListModelItemRenderer.class);

	private transient SecurityService securityService;

	public SecurityService getSecurityService() {
		if (this.securityService == null) {
			this.securityService = (SecurityService) SpringUtil.getBean("securityService");
			setSecurityService(this.securityService);
		}
		return this.securityService;
	}

	public void setSecurityService(SecurityService securityService) {
		this.securityService = securityService;
	}

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final SecRight right = (SecRight) data;
		Listcell lc;

		lc = new Listcell(right.getRigName());
		lc.setStyle("padding-left: 5px");
		lc.setParent(item);

		final SecTyp typ = getSecurityService().getTypById(right.getRigType().intValue());

		lc = new Listcell(String.valueOf(typ.getStpTypname()));
		lc.setStyle("padding-left: 5px");
		lc.setParent(item);

		item.setAttribute("data", data);
		ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClickedRightItem");

	}
}
