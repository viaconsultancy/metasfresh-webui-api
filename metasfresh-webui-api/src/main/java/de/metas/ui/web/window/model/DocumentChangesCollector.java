package de.metas.ui.web.window.model;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;

import de.metas.ui.web.window.datatypes.DocumentPath;

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

public class DocumentChangesCollector implements IDocumentChangesCollector
{
	public static final DocumentChangesCollector newInstance()
	{
		return new DocumentChangesCollector();
	}

	private final Map<DocumentPath, DocumentChanges> documentChangesByPath = new LinkedHashMap<>();

	private DocumentChangesCollector()
	{
		super();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.addValue(documentChangesByPath)
				.toString();
	}

	@Override
	public boolean isEmpty()
	{
		if (documentChangesByPath.isEmpty())
		{
			return true;
		}

		for (final DocumentChanges documentChanges : documentChangesByPath.values())
		{
			if (!documentChanges.isEmpty())
			{
				return false;
			}
		}

		return true;
	}

	@Override
	public Set<String> getFieldNames(final DocumentPath documentPath)
	{
		final DocumentChanges documentChanges = documentChangesIfExists(documentPath);
		if (documentChanges == null)
		{
			return ImmutableSet.of();
		}
		return documentChanges.getFieldNames();
	}

	private DocumentChanges documentChanges(final IDocumentFieldView documentField)
	{
		final DocumentPath documentPath = documentField.getDocumentPath();
		return documentChanges(documentPath);
	}

	private DocumentChanges documentChanges(final DocumentPath documentPath)
	{
		return documentChangesByPath.computeIfAbsent(documentPath, DocumentChanges::new);
	}

	private DocumentChanges documentChangesIfExists(final DocumentPath documentPath)
	{
		return documentChangesByPath.get(documentPath);
	}

	@Override
	public Map<DocumentPath, DocumentChanges> getDocumentChangesByPath()
	{
		return ImmutableMap.copyOf(documentChangesByPath);
	}

	@Override
	public void collectValueChanged(final IDocumentFieldView documentField, final ReasonSupplier reason)
	{
		documentChanges(documentField)
				.collectValueChanged(documentField, reason);
	}

	@Override
	public void collectReadonlyChanged(final IDocumentFieldView documentField, final ReasonSupplier reason)
	{
		documentChanges(documentField)
				.collectReadonlyChanged(documentField, reason);
	}

	@Override
	public void collectMandatoryChanged(final IDocumentFieldView documentField, final ReasonSupplier reason)
	{
		documentChanges(documentField)
				.collectMandatoryChanged(documentField, reason);
	}

	@Override
	public void collectDisplayedChanged(final IDocumentFieldView documentField, final ReasonSupplier reason)
	{
		documentChanges(documentField)
				.collectDisplayedChanged(documentField, reason);
	}

	@Override
	public void collectLookupValuesStaled(final IDocumentFieldView documentField, final ReasonSupplier reason)
	{
		documentChanges(documentField)
				.collectLookupValuesStaled(documentField, reason);
	}

	@Override
	public void collectFrom(final IDocumentChangesCollector fromCollector)
	{
		for (final Map.Entry<DocumentPath, DocumentChanges> e : fromCollector.getDocumentChangesByPath().entrySet())
		{
			final DocumentPath documentPath = e.getKey();
			final DocumentChanges from = e.getValue();

			final DocumentChanges to = documentChanges(documentPath);
			to.collectFrom(from);
		}
	}

	@Override
	public boolean collectFrom(final Document document, final ReasonSupplier reason)
	{
		final DocumentPath documentPath = document.getDocumentPath();
		return documentChanges(documentPath)
				.collectFrom(document, reason);
	}
}