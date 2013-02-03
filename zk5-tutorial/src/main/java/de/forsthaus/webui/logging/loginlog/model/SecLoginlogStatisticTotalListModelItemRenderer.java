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
package de.forsthaus.webui.logging.loginlog.model;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Separator;

import de.forsthaus.backend.bean.DummyBean;

/**
 * Item renderer for listitems in the listbox.
 * 
 * @author bbruhns
 * @author sgerth
 * 
 */
public class SecLoginlogStatisticTotalListModelItemRenderer implements ListitemRenderer, Serializable {

	private static final long serialVersionUID = 1L;
	private transient static final Logger logger = Logger
			.getLogger(SecLoginlogStatisticTotalListModelItemRenderer.class);

	@Override
	public void render(Listitem item, Object data) throws Exception {

		final DummyBean dummyBean = (DummyBean) data;

		Listcell lc;

		lc = new Listcell();
		final Hbox hbox = new Hbox();
		hbox.setParent(lc);

		/* Flag-image */
		final Image img = new Image();
		final String path = "/images/countrycode_flags/";
		final String flag = StringUtils.lowerCase(dummyBean.getCountry()) + ".gif";
		img.setSrc(path + flag);
		hbox.appendChild(img);

		final Separator sep = new Separator();
		hbox.appendChild(sep);

		/* Country */
		final Label label = new Label();
		label.setValue(dummyBean.getCountry());
		hbox.appendChild(label);
		lc.setParent(item);

		lc = new Listcell(dummyBean.getCountryName());
		lc.setParent(item);

		lc = new Listcell(dummyBean.getTotalCount().toString());
		lc.setStyle("text-align: right");
		lc.setParent(item);

		item.setAttribute("data", data);
		// ComponentsCtrl.applyForward(img, "onClick=onImageClicked");
		// ComponentsCtrl.applyForward(item, "onClick=onClicked");
		// ComponentsCtrl.applyForward(item, "onDoubleClick=onDoubleClicked");

	}

}
