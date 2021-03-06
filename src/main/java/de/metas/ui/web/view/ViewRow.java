package de.metas.ui.web.view;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import de.metas.ui.web.exceptions.EntityNotFoundException;
import de.metas.ui.web.window.datatypes.DocumentId;
import de.metas.ui.web.window.datatypes.DocumentPath;
import de.metas.ui.web.window.datatypes.WindowId;
import de.metas.ui.web.window.datatypes.json.JSONLookupValue;
import lombok.NonNull;
import lombok.ToString;

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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public
 * License along with this program. If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

public final class ViewRow implements IViewRow
{
	public static final Builder builder(final WindowId windowId)
	{
		return new Builder(windowId);
	}

	private final DocumentPath documentPath;
	private final DocumentId rowId;
	private final IViewRowType type;
	private final boolean processed;

	private final Map<String, Object> values;

	private final List<IViewRow> includedRows;

	private ViewRow(final Builder builder)
	{
		documentPath = builder.getDocumentPath();
		rowId = documentPath.getDocumentId();
		type = builder.getType();
		processed = builder.isProcessed();

		values = ImmutableMap.copyOf(builder.getValues());

		includedRows = builder.buildIncludedRows();
	}

	@Override
	public String toString()
	{
		return MoreObjects.toStringHelper(this)
				.omitNullValues()
				.add("id", rowId)
				.add("type", type)
				.add("values", values)
				.add("includedRows.count", includedRows.size())
				.add("processed", processed)
				.toString();
	}

	@Override
	public DocumentPath getDocumentPath()
	{
		return documentPath;
	}

	@Override
	public DocumentId getId()
	{
		return rowId;
	}

	@Override
	public IViewRowType getType()
	{
		return type;
	}

	@Override
	public boolean isProcessed()
	{
		return processed;
	}

	@Override
	public Map<String, Object> getFieldNameAndJsonValues()
	{
		return values;
	}

	@Override
	public boolean hasAttributes()
	{
		return false;
	}

	@Override
	public IViewRowAttributes getAttributes()
	{
		throw new EntityNotFoundException("row does not support attributes");
	}

	@Override
	public boolean hasIncludedView()
	{
		return false;
	}

	@Override
	public List<IViewRow> getIncludedRows()
	{
		return includedRows;
	}

	//
	//
	//
	//
	//
	@ToString
	public static final class Builder
	{
		private final WindowId windowId;
		private DocumentId rowId;
		private IViewRowType type;
		private Boolean processed;
		private final Map<String, Object> values = new LinkedHashMap<>(); // preserve the insertion order of fields
		private List<IViewRow> includedRows = null;

		private Builder(@NonNull final WindowId windowId)
		{
			this.windowId = windowId;
		}

		public ViewRow build()
		{
			return new ViewRow(this);
		}

		private DocumentPath getDocumentPath()
		{
			final DocumentId documentId = getRowId();
			return DocumentPath.rootDocumentPath(windowId, documentId);
		}

		public Builder setRowId(final DocumentId rowId)
		{
			this.rowId = rowId;
			return this;
		}

		public Builder setRowIdFromObject(final Object jsonRowIdObj)
		{
			setRowId(convertToRowId(jsonRowIdObj));
			return this;
		}

		private static final DocumentId convertToRowId(@NonNull final Object jsonRowIdObj)
		{
			if (jsonRowIdObj instanceof DocumentId)
			{
				return (DocumentId)jsonRowIdObj;
			}
			else if (jsonRowIdObj instanceof Integer)
			{
				return DocumentId.of((Integer)jsonRowIdObj);
			}
			else if (jsonRowIdObj instanceof String)
			{
				return DocumentId.of(jsonRowIdObj.toString());
			}
			else if (jsonRowIdObj instanceof JSONLookupValue)
			{
				// case: usually this is happening when a view's column which is Lookup is also marked as KEY.
				final JSONLookupValue jsonLookupValue = (JSONLookupValue)jsonRowIdObj;
				return DocumentId.of(jsonLookupValue.getKey());
			}
			else
			{
				throw new IllegalArgumentException("Cannot convert id '" + jsonRowIdObj + "' (" + jsonRowIdObj.getClass() + ") to integer");
			}

		}

		/** @return view row ID */
		private DocumentId getRowId()
		{
			if (rowId == null)
			{
				throw new IllegalStateException("No rowId was provided for " + this);
			}
			return rowId;
		}

		private IViewRowType getType()
		{
			return type;
		}

		public Builder setType(final IViewRowType type)
		{
			this.type = type;
			return this;
		}

		public Builder setProcessed(final boolean processed)
		{
			this.processed = processed;
			return this;
		}

		private boolean isProcessed()
		{
			if (processed == null)
			{
				// NOTE: don't take the "Processed" field if any, because in frontend we will end up with a lot of grayed out completed sales orders, for example.
				// return DisplayType.toBoolean(values.getOrDefault("Processed", false));
				return false;
			}
			else
			{
				return processed.booleanValue();
			}
		}

		public Builder putFieldValue(final String fieldName, final Object jsonValue)
		{
			if (jsonValue == null)
			{
				values.remove(fieldName);
			}
			else
			{
				values.put(fieldName, jsonValue);
			}

			return this;
		}

		private Map<String, Object> getValues()
		{
			return values;
		}

		public Builder addIncludedRow(final IViewRow includedRow)
		{
			if (includedRows == null)
			{
				includedRows = new ArrayList<>();
			}

			includedRows.add(includedRow);

			return this;
		}

		private List<IViewRow> buildIncludedRows()
		{
			if (includedRows == null || includedRows.isEmpty())
			{
				return ImmutableList.of();
			}

			return ImmutableList.copyOf(includedRows);
		}

	}
}
