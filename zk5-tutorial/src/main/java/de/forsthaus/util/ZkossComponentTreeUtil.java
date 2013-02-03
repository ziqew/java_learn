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
package de.forsthaus.util;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.zkoss.zk.ui.AbstractComponent;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.sys.ComponentsCtrl;

/**
 * Helper class for showing the zkoss component tree in the console for a root
 * component.
 * 
 * <pre>
 * Call it with:
 * System.out.println(ZkossComponentTreeUtil.getZulTree(aComponent));
 * </pre>
 * 
 * @author bbruhns
 * 
 */
public class ZkossComponentTreeUtil {

	final private static class FieldListener implements IAddListener {
		private final Field field;

		FieldListener(Field field) {
			super();
			this.field = field;
		}

		@Override
		public void addListener(Component component, StringBuilder result, int depth) {
			try {
				final Map<?, ?> m = (Map<?, ?>) this.field.get(component);
				if (m != null && !m.isEmpty()) {
					for (final Map.Entry<?, ?> entry : m.entrySet()) {
						result.append(StringUtils.leftPad("", depth << 2) + "  " + entry.getKey() + " -> " + entry.getValue() + "\n");
					}
				}
			} catch (final IllegalArgumentException e) {
			} catch (final IllegalAccessException e) {
			}
		}
	}

	private interface IAddListener {
		void addListener(Component component, StringBuilder result, int depth);
	}

	final private static IAddListener ADD_LISTENER;

	static {
		IAddListener tmp = null;
		try {
			final Field field;
			field = AbstractComponent.class.getDeclaredField("_listeners");
			field.setAccessible(true);
			tmp = new FieldListener(field);
		} catch (final SecurityException e) {
		} catch (final NoSuchFieldException e) {
		}

		if (tmp == null) {
			tmp = new IAddListener() {
				@Override
				public void addListener(Component component, StringBuilder result, int depth) {
				}
			};
		}

		ADD_LISTENER = tmp;
	}

	static public CharSequence getZulTree(Component component) {
		return new ZkossComponentTreeUtil().getZulTreeImpl(component);
	}

	private ZkossComponentTreeUtil() {
		super();
	}

	private CharSequence createCompName(Component component) {
		final StringBuilder sb = new StringBuilder();
		sb.append(component.getClass().getSimpleName());

		final String id = component.getId();
		if (StringUtils.isNotBlank(id) && !ComponentsCtrl.isAutoUuid(id)) {
			sb.append(" id=\"" + id + "\"");
		}
		return sb;
	}

	private CharSequence getZulTreeImpl(Component component) {
		if (component == null) {
			return "Component is null!";
		}

		final StringBuilder result = new StringBuilder(6000);
		return getZulTreeImpl(component, result, -1);
	}

	private StringBuilder getZulTreeImpl(Component component, StringBuilder result, int depth) {
		++depth;
		final CharSequence id = createCompName(component);
		if (CollectionUtils.isEmpty(component.getChildren())) {
			result.append(StringUtils.leftPad("", depth << 2) + "<" + id + " />\n");
			return result;
		}

		result.append(StringUtils.leftPad("", depth << 2) + "<" + id + ">\n");

		ADD_LISTENER.addListener(component, result, depth);

		for (final Iterator<?> iterator = component.getChildren().iterator(); iterator.hasNext();) {
			getZulTreeImpl((Component) iterator.next(), result, depth);
		}

		result.append(StringUtils.leftPad("", depth << 2) + "<" + component.getClass().getSimpleName() + " />\n");
		return result;
	}

}
