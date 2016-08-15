package de.metas.ui.web.window_old.descriptor;

import java.util.Collection;

import com.google.common.base.Strings;

/*
 * #%L
 * metasfresh-webui-api
 * %%
 * Copyright (C) 2016 metas GmbH
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

/**
 * Helpers around {@link PropertyDescriptor}.
 * 
 * @author metas-dev <dev@metasfresh.com>
 *
 */
public final class PropertyDescriptors
{
	public static final String toStringRecursivelly(final PropertyDescriptor descriptor)
	{
		final int level = 0;
		return toStringRecursivelly(descriptor, level);
	}

	public static final String toStringRecursivelly(final PropertyDescriptor descriptor, final int level)
	{
		final String indentStr = Strings.repeat("\t", level);
		final StringBuilder sb = new StringBuilder();
		sb.append("\n").append(indentStr).append(descriptor.toString());

		final Collection<PropertyDescriptor> childPropertyDescriptors = descriptor.getChildPropertyDescriptors();
		for (final PropertyDescriptor childPropertyValue : childPropertyDescriptors)
		{
			sb.append(toStringRecursivelly(childPropertyValue, level + 1));
		}

		return sb.toString();
	}

	private PropertyDescriptors()
	{
		super();
	}
}